package net.brakingpoint.telemetryInterfaces;

public record TelemetrySnapshot(
    double distance,
    float speed,
    float vX, 
    float vZ,
    float gLat,
    float gLong,
    float yawRate, // The missing piece
    float brake,
    float throttle
) {}
