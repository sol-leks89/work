package net.brakingpoint.telemetryInterfaces;

public interface ICoachFrame {
    TelemetrySnapshot getTelemetrySnapshot();
    boolean LogThisPacket();
    int getCarOrdinal();
    int getTrackOrdinal();
}
