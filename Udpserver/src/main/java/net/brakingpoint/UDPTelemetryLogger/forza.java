package net.brakingpoint.UDPTelemetryLogger;
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.security.*;
import java.time.zone.*;


public abstract class forza  implements iTelemetry {

    int IsRaceOn;
    
    
    
    // Can overflow to 0 eventually
    
    /* unsigned */  int TimestampMS;
    
    float EngineMaxRpm;
    
    float EngineIdleRpm;
    
    float CurrentEngineRpm;
    
    
    
    // In the car's local space; X = right, Y = up, Z = forward
    
    float AccelerationX;
    
    float AccelerationY;
    
    float AccelerationZ;
    
    
    
    // In the car's local space; X = right, Y = up, Z = forward
    
    float VelocityX;
    
    float VelocityY;
    
    float VelocityZ;
    
    
    
    // In the car's local space; X = pitch, Y = yaw, Z = roll
    
    float AngularVelocityX;
    
    float AngularVelocityY;
    
    float AngularVelocityZ;
    
    
    
    float Yaw;
    
    float Pitch;
    
    float Roll;
    
    
    
    // Suspension travel normalized: 0.0f = max stretch; 1.0 = max compression
    
    float NormalizedSuspensionTravelFrontLeft;
    
    float NormalizedSuspensionTravelFrontRight;
    
    float NormalizedSuspensionTravelRearLeft;
    
    float NormalizedSuspensionTravelRearRight;
    
    
    
    // Tire normalized slip ratio, = 0 means 100% grip and |ratio| > 1.0 means loss of grip.
    
    float TireSlipRatioFrontLeft;
    
    float TireSlipRatioFrontRight;
    
    float TireSlipRatioRearLeft;
    
    float TireSlipRatioRearRight;
    
    
    
    // Wheels rotation speed radians/sec. 
    
    float WheelRotationSpeedFrontLeft;
    
    float WheelRotationSpeedFrontRight;
    
    float WheelRotationSpeedRearLeft;
    
    float WheelRotationSpeedRearRight;
    
    
    
    // = 1 when wheel is on rumble strip, = 0 when off.
    
    int WheelOnRumbleStripFrontLeft;
    
    int WheelOnRumbleStripFrontRight;
    
    int WheelOnRumbleStripRearLeft;
    
    int WheelOnRumbleStripRearRight;
    
    
    
    // = from 0 to 1, where 1 is the deepest puddle
    
    float WheelInPuddleDepthFrontLeft;
    
    float WheelInPuddleDepthFrontRight;
    
    float WheelInPuddleDepthRearLeft;
    
    float WheelInPuddleDepthRearRight;
    
    
    
    // Non-dimensional surface rumble values passed to controller force feedback
    
    float SurfaceRumbleFrontLeft;
    
    float SurfaceRumbleFrontRight;
    
    float SurfaceRumbleRearLeft;
    
    float SurfaceRumbleRearRight;
    
    
    
    // Tire normalized slip angle, = 0 means 100% grip and |angle| > 1.0 means loss of grip.
    
    float TireSlipAngleFrontLeft;
    
    float TireSlipAngleFrontRight;
    
    float TireSlipAngleRearLeft;
    
    float TireSlipAngleRearRight;
    
    
    
    // Tire normalized combined slip, = 0 means 100% grip and |slip| > 1.0 means loss of grip.
    
    float TireCombinedSlipFrontLeft;
    
    float TireCombinedSlipFrontRight;
    
    float TireCombinedSlipRearLeft;
    
    float TireCombinedSlipRearRight;
    
    
    
    // Actual suspension travel in meters
    
    float SuspensionTravelMetersFrontLeft;
    
    float SuspensionTravelMetersFrontRight;
    
    float SuspensionTravelMetersRearLeft;
    
    float SuspensionTravelMetersRearRight;
    
    
    
    // Unique ID of the car make/model
    
    int CarOrdinal;
    
    
    
    // Between 0 (D -- worst cars) and 7 (X class -- best cars) inclusive         
        
    int CarClass;
        
        
        
    // Between 100 (worst car) and 999 (best car) inclusive
        
    int CarPerformanceIndex;
        
        
        
    // 0 = FWD, 1 = RWD, 2 = AWD
        
    int DrivetrainType;
        
        
        
    // Number of cylinders in the engine
        
    int NumCylinders;
        
    public forza(byte[] ba) {
        int i = 0;
        
        
        IsRaceOn = bytearrayToInt(ba, 0);  // 0-3
        TimestampMS = bytearrayToInt(ba, 4);
        EngineMaxRpm = bytearrayToFloat(ba, 8);
        EngineIdleRpm = bytearrayToFloat(ba, 12);
        CurrentEngineRpm = bytearrayToFloat(ba, 16);
        AccelerationX = bytearrayToFloat(ba, 20);
        AccelerationY = bytearrayToFloat(ba, 24);
        AccelerationZ = bytearrayToFloat(ba, 28);
        VelocityX = bytearrayToFloat(ba, 32);
        VelocityY = bytearrayToFloat(ba, 36);
        VelocityZ = bytearrayToFloat(ba, 40);
        AngularVelocityX = bytearrayToFloat(ba, 44);
        AngularVelocityY = bytearrayToFloat(ba, 48);
        AngularVelocityZ = bytearrayToFloat(ba, 52);
        Yaw = bytearrayToFloat(ba, 56);
        Pitch = bytearrayToFloat(ba, 60);
        Roll = bytearrayToFloat(ba, 64);
        NormalizedSuspensionTravelFrontLeft = bytearrayToFloat(ba, 68);
        NormalizedSuspensionTravelFrontRight = bytearrayToFloat(ba, 72);
        NormalizedSuspensionTravelRearLeft = bytearrayToFloat(ba, 76);
        NormalizedSuspensionTravelRearRight = bytearrayToFloat(ba, 80);
        TireSlipRatioFrontLeft = bytearrayToFloat(ba, 84);
        TireSlipRatioFrontRight = bytearrayToFloat(ba, 88);
        TireSlipRatioRearLeft = bytearrayToFloat(ba, 92);
        TireSlipRatioRearRight = bytearrayToFloat(ba, 96);
        WheelRotationSpeedFrontLeft = bytearrayToFloat(ba, 100);
        WheelRotationSpeedFrontRight = bytearrayToFloat(ba, 104);
        WheelRotationSpeedRearLeft = bytearrayToFloat(ba, 108);
        WheelRotationSpeedRearRight = bytearrayToFloat(ba, 112); // 112 - 115
        
        WheelOnRumbleStripFrontLeft = bytearrayToInt(ba, 116);
        WheelOnRumbleStripFrontRight = bytearrayToInt(ba, 120);
        WheelOnRumbleStripRearLeft = bytearrayToInt(ba, 124);
        WheelOnRumbleStripRearRight = bytearrayToInt(ba, 128); //  128 - 131
        
        WheelInPuddleDepthFrontLeft = bytearrayToFloat(ba, 132);
        WheelInPuddleDepthFrontRight = bytearrayToFloat(ba, 136);
        WheelInPuddleDepthRearLeft = bytearrayToFloat(ba, 140);
        WheelInPuddleDepthRearRight = bytearrayToFloat(ba, 144);
        SurfaceRumbleFrontLeft = bytearrayToFloat(ba, 148);
        SurfaceRumbleFrontRight = bytearrayToFloat(ba, 152);
        SurfaceRumbleRearLeft = bytearrayToFloat(ba, 156);
        SurfaceRumbleRearRight = bytearrayToFloat(ba, 160);
        TireSlipAngleFrontLeft = bytearrayToFloat(ba, 164);
        TireSlipAngleFrontRight = bytearrayToFloat(ba, 168);
        TireSlipAngleRearLeft = bytearrayToFloat(ba, 172);
        TireSlipAngleRearRight = bytearrayToFloat(ba, 176);
        TireCombinedSlipFrontLeft = bytearrayToFloat(ba, 180);
        TireCombinedSlipFrontRight = bytearrayToFloat(ba, 184);
        TireCombinedSlipRearLeft = bytearrayToFloat(ba, 188);
        TireCombinedSlipRearRight = bytearrayToFloat(ba, 192);
        SuspensionTravelMetersFrontLeft = bytearrayToFloat(ba, 196);
        SuspensionTravelMetersFrontRight = bytearrayToFloat(ba, 200);
        SuspensionTravelMetersRearLeft = bytearrayToFloat(ba, 204);
        SuspensionTravelMetersRearRight = bytearrayToFloat(ba, 208); //208 - 211
        
        CarOrdinal = bytearrayToInt(ba, 212); // This is confirmed to be 212 - 215
        int CarClass = bytearrayToInt(ba, 216);
        CarPerformanceIndex = bytearrayToInt(ba, 220);
        DrivetrainType = bytearrayToInt(ba, 224);
        NumCylinders = bytearrayToInt(ba, 228); // 228 - 231
        
    }
    protected int bytearrayToInt(byte[] ba, int index) {
        int retVal = 0;
        int i;
        
        for (i=3; i >= 0;i--) {
            //System.out.println("i: " +i);
            retVal = (retVal << 8) + (ba[index + i] & 0xff);
        }
                //System.out.println(ba[index] + "\t" + ba[index+1] + "\t" + ba[index+2] + "\t" + ba[index + 3] );
            
        return retVal;
    }
    protected float bytearrayToFloat(byte[] ba, int index) {
        return (Float.intBitsToFloat(bytearrayToInt(ba, index)));
    }
            
            
    public     boolean LogThisPacket() {
            return true;
    }
    public boolean IsNewFile() {
        return true;
    }
    public String CreateFileName() {
        String retCd = "telemetry.txt";
        return retCd;
    }
    public String BuildFirstLine() {
        String retVal = "";
        
        retVal =  "TimestampMS" + ".\t";
        retVal = retVal + "CurrentEngineRpm" + ".\t";
        retVal = retVal + "AccelerationX" + ".\t";
        retVal = retVal + "AccelerationY" + ".\t";
        retVal = retVal + "AccelerationZ" + ".\t";
        
        retVal = retVal +"VelocityX" + ".\t";
        retVal =retVal + "VelocityY" +"\t";
        retVal = retVal + "VelocityZ" + ".\t";
        
        retVal = retVal + "AngularVelocityX" + ".\t";
        retVal = retVal + "AngularVelocityY" + ".\t";
            retVal = retVal + "AngularVelocityZ" + ".\t";
        
        retVal = retVal + "Yaw" + ".\t";
        retVal = retVal + "Pitch" +"\t";
        retVal = retVal + "Roll" + ".\t";
        
        retVal = retVal + "NormalizedSuspensionTravelFrontLeft" + ".\t";
        retVal = retVal + "NormalizedSuspensionTravelFrontRight" + ".\t";
        retVal = retVal + "NormalizedSuspensionTravelRearLeft" + ".\t";
        retVal = retVal + "NormalizedSuspensionTravelRearRight" + ".\t";
        
        retVal = retVal + "TireSlipRatioFrontLeft" + ".\t";
        retVal = retVal + "TireSlipRatioFrontRight" + ".\t";
        retVal = retVal + "TireSlipRatioRearLeft" + ".\t";
        retVal = retVal + "TireSlipRatioRearRight" + ".\t";
        
        retVal = retVal + "WheelRotationSpeedFrontLeft" + ".\t";
        retVal = retVal + "WheelRotationSpeedFrontRight" + ".\t";
        retVal = retVal + "WheelRotationSpeedRearLeft" + ".\t";
        retVal = retVal + "WheelRotationSpeedRearRight" + ".\t";
        
        retVal = retVal + "WheelOnRumbleStripFrontLeft" + ".\t";
        retVal = retVal + "WheelOnRumbleStripFrontRight" + ".\t";
        retVal = retVal + "WheelOnRumbleStripRearLeft" + ".\t";
        retVal = retVal + "WheelOnRumbleStripRearRight" + ".\t";
        
        retVal = retVal + "WheelInPuddleDepthFrontLeft" + ".\t";
        retVal = retVal + "WheelInPuddleDepthFrontRight" + ".\t";
        retVal = retVal + "WheelInPuddleDepthRearLeft" + ".\t";
        retVal = retVal + "WheelInPuddleDepthRearRight" + ".\t";
        
        retVal = retVal +"TireSlipAngleFrontLeft" + ".\t";
        retVal = retVal +"TireSlipAngleFrontRight" + ".\t";
        retVal = retVal +"TireSlipAngleRearLeft" + ".\t";
        retVal = retVal +"TireSlipAngleRearRight" + ".\t";
        
        retVal = retVal +"SuspensionTravelMetersFrontLeft" + ".\t";
        retVal = retVal +"SuspensionTravelMetersFrontRight" + ".\t";
        retVal = retVal +"SuspensionTravelMetersRearLeft" + ".\t";
        retVal = retVal +"SuspensionTravelMetersRearRight" + ".\t";
        
        
        return retVal;
    }
    public String BuildDataLine() {
        String retVal = "";
        
        
        retVal = TimestampMS + "\t";
        retVal = retVal + CurrentEngineRpm + "\t";
        retVal = retVal + AccelerationX + "\t";
        retVal = retVal + AccelerationY + "\t";
        retVal = retVal + AccelerationZ + "\t";
        
        retVal = retVal + VelocityX + "\t";
        retVal =retVal + VelocityY +"\t";
        retVal = retVal + VelocityZ + "\t";
        
        retVal = retVal + AngularVelocityX + "\t";
        retVal = retVal + AngularVelocityY + "\t";
        retVal = retVal + AngularVelocityZ + "\t";
        
        retVal = retVal + Yaw + "\t";
        retVal = retVal + Pitch +"\t";
        retVal = retVal + Roll + "\t";
                retVal = retVal + NormalizedSuspensionTravelFrontLeft + "\t";
        retVal = retVal + NormalizedSuspensionTravelFrontRight + "\t";
        retVal = retVal + NormalizedSuspensionTravelRearLeft + "\t";
        retVal = retVal + NormalizedSuspensionTravelRearRight + "\t";
        
        retVal = retVal + TireSlipRatioFrontLeft + "\t";
        retVal = retVal + TireSlipRatioFrontRight + "\t";
        retVal = retVal + TireSlipRatioRearLeft + "\t";
        retVal = retVal + TireSlipRatioRearRight + "\t";
        
        retVal = retVal + WheelRotationSpeedFrontLeft + "\t";
        retVal = retVal + WheelRotationSpeedFrontRight + "\t";
        retVal = retVal + WheelRotationSpeedRearLeft + "\t";
        retVal = retVal + WheelRotationSpeedRearRight + "\t";
        
        retVal = retVal + WheelOnRumbleStripFrontLeft + "\t";
        retVal = retVal + WheelOnRumbleStripFrontRight + "\t";
        retVal = retVal + WheelOnRumbleStripRearLeft + "\t";
        retVal = retVal + WheelOnRumbleStripRearRight + "\t";
        
        retVal = retVal + WheelInPuddleDepthFrontLeft + "\t";
        retVal = retVal + WheelInPuddleDepthFrontRight + "\t";
        retVal = retVal + WheelInPuddleDepthRearLeft + "\t";
        retVal = retVal + WheelInPuddleDepthRearRight + "\t";
        
        retVal = retVal +TireSlipAngleFrontLeft + "\t";
        retVal = retVal +TireSlipAngleFrontRight + "\t";
        retVal = retVal +TireSlipAngleRearLeft + "\t";
        retVal = retVal +TireSlipAngleRearRight + "\t";
        
        retVal = retVal +SuspensionTravelMetersFrontLeft + "\t";
        retVal = retVal +SuspensionTravelMetersFrontRight + "\t";
        retVal = retVal +SuspensionTravelMetersRearLeft + "\t";
        retVal = retVal +SuspensionTravelMetersRearRight + "\t";
        
        
        return retVal;
    }
    public int getVufSize() {
        return 232;
    }
    protected static String TimeStampString() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString();
            
    }
            
}