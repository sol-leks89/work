package net.brakingpoint.audioRaceCoach;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.brakingpoint.telemetryInterfaces.*;
import net.brakingpoint.trackInfo.*;
import net.brakingpoint.coachingHelpers.*;
import net.brakingpoint.forza.*;
import net.brakingpoint.ACC.*;

import net.brakingpoint.audio.*;


public class audioRaceCoach {
    
    private static String DATfilename  = "";
    private static String dumpFilename = "";
    private static final Logger logger = LogManager.getLogger(audioRaceCoach.class);
    
    public static void main(String[] args) throws IOException {
        
        CoachPhysics coachPhysics = null;
        int carOrdinal = -1;
        int trackOrdinal = -1;
        int chirpCount = 0;
        String game = "forza";
        
        if (args.length >= 1)
            game = args[0];
        if (args.length >= 2)
            trackOrdinal = Integer.parseInt(args[1]);
        
        
        logger.info(String.format("Starting audioRaceCoach game: %s track: %d...", game, trackOrdinal));
        
        // 1. Load configuration
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
        } catch (IOException ex) {
            logger.error("Could not find config.properties, using defaults.");
        }

        // 2. Assign variables from properties (with defaults)
        int listenPort = Integer.parseInt(prop.getProperty("listen.port", "5002"));
        String echoAddr = prop.getProperty("echo.address", "127.0.0.1");
        int echoPort = Integer.parseInt(prop.getProperty("echo.port", "5001"));
        int packetSize = Integer.parseInt(prop.getProperty("packet.size", "336"));
        String xbox = prop.getProperty("xbox", "127.0.0.1");
        int writeTest = Integer.parseInt(prop.getProperty("debug.writeDAT", "0"));
        int writeBytes = Integer.parseInt(prop.getProperty("debug.writeBytes", "0"));
        int writeTelemetry = Integer.parseInt(prop.getProperty("debug.writeTelemetry", "0"));
        
        float reactionTime = Float.parseFloat(prop.getProperty("physics.reactionTime", "0.250"));
        float brakeThreshold = Float.parseFloat(prop.getProperty("physics.brakeThreshold", "0.250"));
        double under = Double.parseDouble(prop.getProperty("physics.underDriven", "4"));
        double over = Double.parseDouble(prop.getProperty("physics.overDriven", "10"));
        double sliding = Double.parseDouble(prop.getProperty("physics.sliding", "12"));
        
        boolean soundUnder = Boolean.parseBoolean(prop.getProperty("sound.under", "false"));
        boolean soundOver = Boolean.parseBoolean(prop.getProperty("sound.over", "false"));
            boolean soundBrake = Boolean.parseBoolean(prop.getProperty("sound.brake", "false"));
                
        AudioMixer mixer = null;
        try {
            mixer = new AudioMixer(soundUnder, soundBrake, soundOver);
            mixer.setHumVolume(0);
            mixer.setHissVolume(0);
            mixer.setBrakeHumVolume(0);
            
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        

    
        DatagramSocket serverSocket = new DatagramSocket(listenPort);
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket;
        
        // Move address resolution outside the loop for performance
        InetAddress echoAddress = InetAddress.getByName(echoAddr);

        logger.info("Relay started: Listening on " + listenPort + " -> Forwarding to " + echoAddr + ":" + echoPort);
        ACCConnectionManager  accMgr = new ACCConnectionManager();
        //accMgr.startConnectionLoop(xbox);

        while (true) {
            receivePacket = new DatagramPacket(receiveData, packetSize);
            serverSocket.receive(receivePacket);
            //accMgr.setConnected();
            DatagramPacket echoPacket = new DatagramPacket(
                receivePacket.getData(),
                receivePacket.getLength(),
                echoAddress,
                echoPort
            );

            serverSocket.send(echoPacket);
            ICoachFrame cf = null;
            switch (game) {
                case "ACC":
                    cf = new ACCController(receivePacket.getData()); 
                    break;
                case "fm7":
                    cf = new FM7(receivePacket.getData(), trackOrdinal);
                    break;
                default:
                    cf = (ICoachFrame) new forzaDash(receivePacket.getData());
                    break;
            }
            if (cf.LogThisPacket() && (writeTelemetry != 0)) {
                TelemetrySnapshot snapshot = cf.getTelemetrySnapshot();
                
                if (trackOrdinal != cf.getTrackOrdinal() || carOrdinal != cf.getCarOrdinal()) {
                    trackOrdinal = cf.getTrackOrdinal();
                    carOrdinal = cf.getCarOrdinal();
                    coachPhysics = new CoachPhysics(trackOrdinal, carOrdinal, "profile_" + trackOrdinal + ".json", brakeThreshold, under, over, sliding);
                        
                }
                // 2. Update the "Learned" G-Limits for the GT3 scaling
                coachPhysics.updateGripLimits(snapshot);

                // Check if we have reached a calculated braking trigger point
                if (coachPhysics.shouldChirp(snapshot, reactionTime)) {
                    // 1. Calculate and set the musical pitch based on how much 
                    //    overspeed we have relative to the adjusted apex speed.
                    coachPhysics.prepareChirp(snapshot, mixer);
                    
                    // 2. Trigger the actual audio playback in the mixer thread.
                    mixer.playChirp();
                }
                // 2. Continuous Grip Feedback
                // The 55Hz Hum is loud when you aren't trying hard enough
                mixer.setHumVolume(coachPhysics.getUnderDrivenVolume(snapshot));
                            
                // The 1760Hz Hiss starts when you exceed the over-driven limit
                mixer.setHissVolume(coachPhysics.getOverDrivenVolume(snapshot));
                
                // set braking volume
                mixer.setBrakeHumVolume(snapshot.brake());
                            
                // 3. Learning
                coachPhysics.updateGripLimits(snapshot);
                            
                // 5. Log the state (Optional, for debugging the 'Push')
                coachPhysics.analyzeDrivingStyle(snapshot);
            }
            else {
                mixer.setHissVolume(0);
                mixer.setHumVolume(0);
                mixer.setBrakeHumVolume(0);
            }
            
        
            if (writeBytes == 1) {
                PrintByteArray(receivePacket.getData(), receivePacket.getLength());
            }   
            if (writeTest == 1) {
                writeDAT(receivePacket.getData(), receivePacket.getLength());
            }
        }
    }

    private static String TimeStampString() {
        return new Timestamp(System.currentTimeMillis()).toString();
    }
    private static void PrintByteArray(byte arr[], int len) throws IOException {
    int i;
    
    if (dumpFilename == "") {
        dumpFilename = "dump-" + TimeStampString() + ".txt";
    }
        
    BufferedWriter writer = new BufferedWriter(new FileWriter(dumpFilename, true));   
        
    for (i = 0; i < len; i++) {
        if (i % 16 == 0)
            writer.write("\n");
        else if (i % 8 == 0)
            writer.write("\t");
        else 
            writer.write(" ");
        
        writer.write(String.format("%02x", arr[i]));
        
    }
    writer.write("\n\n");
    writer.close();
}
private static void writeDAT(byte arr[], int len) throws IOException {
    if (DATfilename == "") {
        DATfilename = "debug-" + len + "-" + TimeStampString() + ".dat";
    }
    
    
    // The 'true' flag in FileOutputStream enables "append" mode
    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(DATfilename, true))) {
        bos.write(arr, 0, len);
        bos.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    
}
    
    
}
    