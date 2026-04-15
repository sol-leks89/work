package net.brakingpoint.audio;

import javax.sound.sampled.*;

public class AudioMixer {
    private SourceDataLine masterLine;
    
    // Toggle Flags
    private final boolean useHum;
    private final boolean useBrakeHum;
    private final boolean useHiss;

    // Volume States
    private float humVol = 0.0f;        // 110Hz (Under-driven)
    private float brakeHumVol = 0.0f;   // 165Hz (Braking - Perfect 5th)
    private float hissVol = 0.0f;       // White Noise (Over-driven)
    
    private float chirpActive = 0.0f; 
    private double currentChirpFreq = 440.0; 
    private static long lastChirpMS = 0;

    public AudioMixer(boolean enableUnderHum, boolean enableBrakeHum, boolean enableOverHiss) throws LineUnavailableException {
        this.useHum = enableUnderHum;
        this.useBrakeHum = enableBrakeHum;
        this.useHiss = enableOverHiss;

        AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
        masterLine = AudioSystem.getSourceDataLine(format);
        masterLine.open(format, 4096); 
        masterLine.start();
        
        Thread audioThread = new Thread(this::mixingLoop);
        audioThread.setPriority(Thread.MAX_PRIORITY);
        audioThread.start();
    }

    public void setHumVolume(float vol) { this.humVol = vol; }
    public void setBrakeHumVolume(float vol) { this.brakeHumVol = vol; }
    public void setHissVolume(float vol) { this.hissVol = vol; }
    
    public void setChirpFreq(double freq) { this.currentChirpFreq = freq; }
    
    public void playChirp() { 
        long nowMs = System.currentTimeMillis();
        if (nowMs - lastChirpMS >= 120) {
            this.chirpActive = 0.150f; 
            lastChirpMS = nowMs;
        }
    }

    public boolean isChirpFinished() { return this.chirpActive <= 0; }
    
    private void mixingLoop() {
        double phaseHum = 0;
        double phaseBrakeHum = 0;
        double phaseChirp = 0;
        byte[] buffer = new byte[512]; 

        // Frequency Constants
        final double FREQ_HUM = 110.0;
        final double FREQ_BRAKE = 165.0; // 110 * 1.5 (Perfect 5th)
        final double SAMPLE_RATE = 44100.0;

        while (true) {
            for (int i = 0; i < buffer.length / 2; i++) {
                double sHum = 0;
                double sBrakeHum = 0;
                double sHiss = 0;
                double sChirp = 0;

                // 1. Under-driven Hum (110Hz)
                if (useHum) {
                    sHum = Math.sin(phaseHum) * humVol;
                    phaseHum += 2.0 * Math.PI * FREQ_HUM / SAMPLE_RATE;
                    if (phaseHum > 2.0 * Math.PI) phaseHum -= 2.0 * Math.PI;
                }

                // 2. Brake Hum (165Hz - Perfect 5th)
                if (useBrakeHum) {
                    sBrakeHum = Math.sin(phaseBrakeHum) * brakeHumVol;
                    phaseBrakeHum += 2.0 * Math.PI * FREQ_BRAKE / SAMPLE_RATE;
                    if (phaseBrakeHum > 2.0 * Math.PI) phaseBrakeHum -= 2.0 * Math.PI;
                }

                // 3. Over-driven Hiss
                if (useHiss) {
                    sHiss = (Math.random() * 2 - 1) * hissVol;
                }

                // 4. Chirp (Always enabled as it's the primary signal)
                if (chirpActive > 0) {
                    sChirp = Math.sin(phaseChirp) * 0.9;
                    phaseChirp += 2.0 * Math.PI * currentChirpFreq / SAMPLE_RATE;
                    chirpActive -= (1.0 / SAMPLE_RATE);
                } else { 
                    phaseChirp = 0; 
                }

                // Final Mix Calculation
                // Short.MAX_VALUE = 32767. Divisor of 3.0 prevents clipping if multiple signals peak.
                short mixed = (short) ((sHum + sBrakeHum + sHiss + sChirp) * 32767 / 3.0);
                
                buffer[2 * i] = (byte) (mixed >> 8);
                buffer[2 * i + 1] = (byte) (mixed & 0xFF);
            }
            masterLine.write(buffer, 0, buffer.length);
        }
    }
}
