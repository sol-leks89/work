package net.brakingpoint.UDPTelemetryLogger;

import java.io.IOException;

public interface iTelemetry {

    /**
     * Checks if the packet contains valid data that should be logged.
     * Used by the server to filter out idle or invalid telemetry.
     */
    boolean LogThisPacket();

    /**
     * Determines if the current telemetry state requires a new file to be opened.
     * Likely checks if the CarOrdinal or TrackOrdinal has changed since the last packet.
     */
    boolean IsNewFile();

    /**
     * Generates a unique filename for the telemetry session.
     * Based on CarOrdinal, PerformanceIndex, and TrackOrdinal.
     */
    String CreateFileName();

    /**
     * Returns the CSV header or the first line required for the telemetry file.
     */
    String BuildFirstLine();

    /**
     * Formats the current telemetry object into a string for the data log.
     * @param buf The current forzaDash instance containing telemetry data.
     */
    String BuildDataLine();
    
    int getBufSize();
}

    
