package net.brakingpoint.coachingHelpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.brakingpoint.telemetryInterfaces.*;
import net.brakingpoint.trackInfo.BrakingZone;
import net.brakingpoint.audio.AudioMixer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Arrays;

public class CoachPhysics {
    private static final Logger logger = LogManager.getLogger(CoachPhysics.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private List<BrakingZone> trackMap;
    private int nextZoneIndex = 0;
    private boolean lapSavePending = false;
    private boolean logZoneStart = true;
    
    private double lastTuningNoteDistance = -1000.0;

    // Filtering Constants
    private final int SUSTAINED_PACKET_THRESHOLD = 10;
    private final float G_WINDOW_TOLERANCE = 0.06f;
    private final float LAT_RESET_THRESHOLD = 0.5f; 
    private final double IMPACT_G_JUMP_CUTOFF = 3.5; // effectively disable the threshold.

    // Dynamic Accumulators
    private int sustainedLongCount = 0;
    private float pendingHighLong = 0.0f;
    
    private int sustainedLatCount = 0;
    private float pendingHighLat = 0.0f;
    private float lapLatSum = 0.0f;
    private int lapLatValidEvents = 0;

    // Limits & Profile Data
    private final double limitUnderDriven;
    private final double limitOverDriven;
    private final float limitBrakeThreshold;
    private final double limitInstability;
    
    private double refMaxLatG;
    private double refMaxLongG;
    private int trackOrdinal;
    private int carOrdinal;

    private float learnedMaxLongG;
    private float learnedMaxLatG;

    private final double G_TO_MS2 = 9.80665;
    private final String PROFILE_DIR = "maps/profiles/";

    // Musical Sonar Constants
    private final double TARGET_PITCH = 440.0; 
    private final double MAX_DISPARITY = 80.0; 

    public CoachPhysics(int trackId, int carId, String masterMapPath, float brakeThreshold, double under, double over, double instability) {
        this.limitUnderDriven = under;
        this.limitOverDriven = over;
        this.limitInstability = instability;
        this.limitBrakeThreshold = brakeThreshold;
        this.trackOrdinal = trackId;
        this.carOrdinal = carId;

        new File(PROFILE_DIR).mkdirs();
        String profileName = String.format("profile_%d_%d.json", trackOrdinal, carOrdinal);
        File profileFile = new File(Paths.get(PROFILE_DIR, profileName).toString());

        if (profileFile.exists()) {
            loadMap(profileFile.getAbsolutePath());
        } else {
            loadMap(masterMapPath);
        }
    }

    private void loadMap(String path) {
        try {
            JsonNode root = mapper.readTree(new File(path));
            this.refMaxLatG = root.get("referenceMaxLatG").asDouble();
            this.refMaxLongG = root.get("referenceMaxLongG").asDouble();
            this.learnedMaxLatG = (float) refMaxLatG;
            this.learnedMaxLongG = (float) refMaxLongG;
            BrakingZone[] zones = mapper.readerFor(BrakingZone[].class).readValue(root.get("zones"));
            this.trackMap = Arrays.asList(zones);
        } catch (Exception e) {
            logger.error("Mapping Load Failed: {}", e.getMessage());
        }
    }

    public void updateGripLimits(TelemetrySnapshot snapshot) {
        float currentLongG = Math.abs(snapshot.gLong()) / (float)G_TO_MS2;
        float currentLatG = Math.abs(snapshot.gLat()) / (float)G_TO_MS2;
        double bodySlip = Math.abs(calculateBodySlip(snapshot));

        // 1. BRAKING LOGIC (Longitudinal)
        if (snapshot.brake() > limitBrakeThreshold) {
            sustainedLongCount++;
            pendingHighLong += currentLongG;
        }

        // 2. CORNERING LOGIC (Lateral)
        // Reset counters if we are on a straight to ensure "sustained" means a single corner
        if (currentLatG < LAT_RESET_THRESHOLD) {
            sustainedLatCount = 0;
            pendingHighLat = 0.0f;
        }

        // Filter for valid cornering (Ignore shunts/slides)
        // Only consider Gs if we aren't sliding past our overdriven limit
        if (currentLatG > 0.7f && bodySlip < limitOverDriven) {
            if (Math.abs(currentLatG - pendingHighLat) < G_WINDOW_TOLERANCE) {
                sustainedLatCount++;
            } else {
                pendingHighLat = currentLatG;
                sustainedLatCount = 0;
            }

            // If Gs are sustained, we mark this as a valid peak for the lap
            if (sustainedLatCount == SUSTAINED_PACKET_THRESHOLD) {
                // Final safety: don't let a single impact jump the G-limit by more than the cutoff
                if (pendingHighLat < learnedMaxLatG + IMPACT_G_JUMP_CUTOFF) {
                    lapLatSum += pendingHighLat;
                    lapLatValidEvents++;
                }
            }
        }
    }

    public boolean shouldChirp(TelemetrySnapshot snapshot, float reactionTime) {
        // Lap End / Start Detection
        if (snapshot.distance() < 10.0) {
            if (lapSavePending) {
                nextZoneIndex = 0;
                
                // Update Longitudinal (Braking) via EMA
                if (sustainedLongCount > SUSTAINED_PACKET_THRESHOLD) {
                    float lapLongAverage = pendingHighLong / (float)sustainedLongCount;
                    learnedMaxLongG = (learnedMaxLongG * 0.3f) + (lapLongAverage * 0.7f);
                    logger.info(String.format("Lap End - New Max Brake G: %7.2f", learnedMaxLongG));
                }

                // Update Lateral (Cornering) via EMA
                if (lapLatValidEvents > 0) {
                    float lapLatAverage = lapLatSum / (float)lapLatValidEvents;
                    // Slightly more conservative weight for Lat G to handle track variations
                    //this might be a mistake, but if going up, go up quickly, if going down go down slowly
                    if (learnedMaxLatG < lapLatAverage)
                        learnedMaxLatG = (learnedMaxLatG * 0.6f) + (lapLatAverage * 0.4f);
                        
                    logger.info(String.format("Lap End - New Max Lateral G: %7.2f", learnedMaxLatG));
                } 

                // Reset per-lap accumulators
                sustainedLongCount = 0;
                pendingHighLong = 0.0f;
                lapLatSum = 0.0f;
                lapLatValidEvents = 0;
                sustainedLatCount = 0;

                saveCurrentProfile();
                lapSavePending = false; 
            }
        } else { 
            lapSavePending = true; 
        }
            
        if (trackMap == null || nextZoneIndex >= trackMap.size()) return false;
        
        BrakingZone zone = trackMap.get(nextZoneIndex);
        
        // Calculate Apex Speed based on learned Lateral capability
        double latMultiplier = (double)learnedMaxLatG / refMaxLatG;
        double adjApexSpeed = zone.getApexSpeed() * Math.sqrt(latMultiplier);
        
        // Zone Exit Logic
        if (snapshot.distance() > zone.getLocationMeters() || snapshot.speed() <= adjApexSpeed + 1.0) {
            if (snapshot.distance() > zone.getLocationMeters() - 50.0) {
                nextZoneIndex++;
                logger.info(String.format("Zone %s finished at %7.2f meters", zone.getId(), snapshot.distance()));
                logZoneStart = true;
                return false;
            }
        }
        
        // Calculate Braking Trigger Point
        double localDecel = (zone.getBaselineLongG() * learnedMaxLongG) * 9.80665;
        double brakingDist = (Math.pow(snapshot.speed(), 2) - Math.pow(adjApexSpeed, 2)) / (2 * localDecel);
        double triggerPoint = zone.getLocationMeters() - (brakingDist + (snapshot.speed() * reactionTime));

        if ((snapshot.distance() >= triggerPoint) && logZoneStart) {
            logZoneStart = false;
            logger.info(String.format("Zone: %s started chirp at: %7.2f meters, speed: %7.2f target speed %7.2f target braking distance %7.2f meters", zone.getId(), snapshot.distance(), snapshot.speed(), adjApexSpeed, brakingDist));
        }
        return (snapshot.distance() >= triggerPoint);
    }

    public void prepareChirp(TelemetrySnapshot snapshot, AudioMixer mixer) {
        if (trackMap != null && nextZoneIndex < trackMap.size()) {
            BrakingZone zone = trackMap.get(nextZoneIndex);
            double latMultiplier = (double)learnedMaxLatG / refMaxLatG;
            double adjApexSpeed = zone.getApexSpeed() * Math.sqrt(latMultiplier);
            double speedDelta = snapshot.speed() - adjApexSpeed;
            
            if (speedDelta > 0) {
                double freq = TARGET_PITCH * Math.pow(2, (speedDelta / MAX_DISPARITY));
                mixer.setChirpFreq(freq);
            }
        }
    }
    
    public void saveCurrentProfile() {
        String fileName = String.format("profile_%d_%d.json", trackOrdinal, carOrdinal);
        File saveFile = new File(Paths.get(PROFILE_DIR, fileName).toString());
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("referenceCarOrdinal", this.carOrdinal);
            root.put("referenceTrackOrdinal", this.trackOrdinal);
            root.put("referenceMaxLatG", (double)this.learnedMaxLatG);
            root.put("referenceMaxLongG", (double)this.learnedMaxLongG);
            root.set("zones", mapper.valueToTree(this.trackMap));
            mapper.writerWithDefaultPrettyPrinter().writeValue(saveFile, root);
        } catch (Exception e) { logger.error("Save failed: {}", e.getMessage()); }
    }

    public float getUnderDrivenVolume(TelemetrySnapshot snapshot) {
        if (snapshot.throttle() > 0.95 || snapshot.speed() < 10.0f) return 0.0f;
        double slip = Math.abs(calculateBodySlip(snapshot));
        if (Math.abs(snapshot.gLat() / G_TO_MS2) < 0.5 || slip >= limitUnderDriven) return 0.0f;
        return (float) ((limitUnderDriven - slip) / limitUnderDriven);
    }
    
    public float getOverDrivenVolume(TelemetrySnapshot snapshot) {
        double slip = Math.abs(calculateBodySlip(snapshot));
        if (slip <= limitOverDriven) return 0.0f;
        if (slip >= limitInstability) return 1.0f;
        return (float) ((slip - limitOverDriven) / (limitInstability - limitOverDriven));
    }

    public double calculateBodySlip(TelemetrySnapshot snapshot) {
        if (snapshot.speed() < 1.0f) return 0.0;
        // atan2 returns radians, converting to degrees for easier threshold comparison
        return Math.toDegrees(Math.atan2(snapshot.vX(), snapshot.vZ()));
    }

    public void analyzeDrivingStyle(TelemetrySnapshot snapshot) {
        double slip = Math.abs(calculateBodySlip(snapshot));
        double currentDist = snapshot.distance();
        if (snapshot.throttle() > 0.95 && slip < (limitUnderDriven * 0.5) && Math.abs(snapshot.gLat() / G_TO_MS2) > 0.5) {
            if (Math.abs(currentDist - lastTuningNoteDistance) > 100.0) {
                lastTuningNoteDistance = currentDist;
            }
        }
    }
}
