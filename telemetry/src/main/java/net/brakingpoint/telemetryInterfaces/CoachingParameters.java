package net.brakingpoint.telemetryInterfaces;

public record CoachingParameters(
    float lapDistance,
    float velocityMps,
    // 3D Coordinates for future "Gate" logic
    float x, 
    float y, 
    float z,
    boolean isInPits,
    long timestamp
) {}
