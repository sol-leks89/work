package net.brakingpoint.forza;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.security.*;
import java.time.zone.*;

import net.brakingpoint.telemetryInterfaces.*;

public class forzaDash extends forza implements Serializable, ICoachFrame  {
  

  static float lapStartDistance = 0;
  static int brakeEventCounter = 0;
  static int previousBrake = 0;
  static int offThrottleCounter = 0;
  static int previousAccel = 255; 
  static int raceLap = 999;

  boolean newFile = true;
  
  float PositionX;
  
float PositionY;
  
float PositionZ;
  
float Speed;
  
float Power;
  
float Torque;
  
float TireTempFrontLeft;
  
float TireTempFrontRight;
  
float TireTempRearLeft;
  
float TireTempRearRight;
  
float Boost;
  
float Fuel;
  
  float DistanceTraveled;
  
  float BestLap;
  
  float LastLap;
  
  float CurrentLap;
  
  float CurrentRaceTime;
  
  int LapNumber0;
  int LapNumber1;
  
  int  RacePosition;
  
  int Accel;
  
  int Brake;
  
  int Clutch;
  
  int HandBrake;
  
int Gear;
  
  byte Steer;
  
  byte NormalizedDrivingLine;
  
  byte NormalizedAIBrakeDifference;
  
  
  
  float TireWearFrontLeft;
  
  float TireWearFrontRight;
  
  float TireWearRearLeft;
  
  float TireWearRearRight;
  
  
  
  // ID for track
  
  int TrackOrdinal;
  float LapDistance;
  int BrakeEvent;
  int OffThrottleEvent;
  static String FileName = "fm";
  
  public forzaDash(byte[] ba) {
    super(ba);
    
    
    int i = 0;
    
    
    
    PositionX = bytearrayToFloat(ba, 232);
    PositionY = bytearrayToFloat(ba, 236);
    PositionZ = bytearrayToFloat(ba, 240);
    Speed = bytearrayToFloat(ba, 244);
    Power = (bytearrayToFloat(ba, 248) * (float) 1.341) / 1000;
    Torque = (bytearrayToFloat(ba, 252)/ (float) 1.356) ;
    TireTempFrontLeft = bytearrayToFloat(ba, 256);
    TireTempFrontRight = bytearrayToFloat(ba, 260);
    TireTempRearLeft = bytearrayToFloat(ba, 264);
    TireTempRearRight = bytearrayToFloat(ba, 268);
    Boost = bytearrayToFloat(ba, 272); // Correct 272 - 275
    Fuel = bytearrayToFloat(ba, 276);
    DistanceTraveled = bytearrayToFloat(ba, 280);
    BestLap = bytearrayToFloat(ba, 284);
    LastLap = bytearrayToFloat(ba, 288);
    CurrentLap = bytearrayToFloat(ba, 292);
    CurrentRaceTime = bytearrayToFloat(ba, 296); // 296 - 299
    // this seems to be where the trouble with the data starts.

    LapNumber0 = ba[300] & 0xff; //300
    LapNumber1 = ba[301]   & 0xff; //301
    RacePosition = ba[302] & 0xff;
    Accel = ba[303] & 0xff;
    Brake = ba[304] & 0xff;
    Clutch = ba[305] & 0xff;
    HandBrake = ba[306] & 0xff;
    Gear = ba[307] & 0xff;
    Steer = ba[308];
    NormalizedDrivingLine = ba[309];
    NormalizedAIBrakeDifference = ba[310];
    i = 312;
    TireWearFrontLeft = bytearrayToFloat(ba, 311);
    TireWearFrontRight = bytearrayToFloat(ba, 315);
    TireWearRearLeft = bytearrayToFloat(ba, 319);
    TireWearRearRight = bytearrayToFloat(ba, 323);
    
    TrackOrdinal = bytearrayToInt(ba, 327);

    // META data dont update the statics unless we are in a race.
    if (IsRaceOn == 1) {
      if (LapNumber0 < raceLap /* && LapNumber0 == 0*/) 
      newFile = true;
      else
      newFile = false;
      
      if (raceLap !=LapNumber0) { // new lap
        System.out.println("Lap Number: " +LapNumber0 + ", raceLap: " + raceLap + ", newFile: " + newFile);
        raceLap = LapNumber0;
        brakeEventCounter = 0;
        offThrottleCounter = 0;
        lapStartDistance = DistanceTraveled;
      }
      if (DistanceTraveled > 0) {
        LapDistance= DistanceTraveled - lapStartDistance;
      }
      else {
        LapDistance = 0;
        lapStartDistance = 0;
        
      }
      
      // look for braking events
      if (previousBrake == 0 && Brake > 0) {
        brakeEventCounter = brakeEventCounter +1;
      }
      if (Brake == 0)
      BrakeEvent = 0;
      else
      BrakeEvent = brakeEventCounter;
      previousBrake = Brake;
      
      // Look for OffThrottle Events
      if (previousAccel == 255 && Accel < 255) {
        offThrottleCounter = offThrottleCounter  + 1;
      }
      if (Accel == 255)
      OffThrottleEvent = 0;
      else
      OffThrottleEvent = offThrottleCounter;
      previousAccel = Accel;
    }

        
    
    
    
  }
  
  
  

public   String BuildDataLine() {
  String retVal = super.BuildDataLine();
  
  retVal = retVal + PositionX + "\t";
  retVal = retVal + PositionY + "\t";
  retVal = retVal + PositionZ + "\t";
  
  
  retVal = retVal + Speed + "\t";
  retVal = retVal + Power + "\t";
  retVal = retVal + Torque + "\t";
  
  retVal = retVal  + TireTempFrontLeft + "\t";
  retVal = retVal  + TireTempFrontRight + "\t";
  retVal = retVal  + TireTempRearLeft + "\t";
  retVal = retVal  + TireTempRearRight + "\t";
  
  retVal = retVal + Fuel + "\t";
  retVal = retVal + DistanceTraveled + "\t";
  
  retVal = retVal + LastLap + "\t";
  retVal = retVal + CurrentLap + "\t";
  retVal = retVal + CurrentRaceTime + "\t";
  retVal = retVal + LapNumber0 + "\t";
  retVal = retVal + LapNumber1 + "\t";
  retVal = retVal + RacePosition + "\t";
  retVal = retVal + Accel + "\t";
  retVal = retVal + Brake + "\t";
  retVal = retVal + Clutch + "\t";
  retVal = retVal + HandBrake + "\t";
  retVal = retVal + Gear + "\t";
  retVal = retVal + Steer + "\t";
  retVal = retVal + NormalizedDrivingLine + "\t";
  retVal = retVal + NormalizedAIBrakeDifference + "\t";
  
  retVal = retVal + TireWearFrontLeft + "\t";
  retVal = retVal + TireWearFrontRight + "\t";
  retVal = retVal + TireWearRearLeft + "\t";
  retVal = retVal + TireWearRearRight + "\t";
  retVal = retVal + TrackOrdinal + "\t";
  retVal = retVal + CarOrdinal + "\t";
  
  retVal = retVal + LapDistance  + "\t";
  retVal = retVal + BrakeEvent + "\t";
  retVal = retVal + OffThrottleEvent + "\t";
  retVal = retVal + FileName + "\t";
  
  retVal = retVal + "\n";
  return retVal;
}
  public  String BuildFirstLine() {
    
    String retVal = super.BuildFirstLine();
    
    retVal = retVal + "PositionX" + ".\t";
    retVal = retVal + "PositionY" + ".\t";
    retVal = retVal + "PositionZ" + ".\t";
    
    
    retVal = retVal + "Speed" + ".\t";
    retVal = retVal + "Power" + ".\t";
    retVal = retVal + "Torque" + ".\t";
    
    retVal = retVal  + "TireTempFrontLeft" + ".\t";
    retVal = retVal  + "TireTempFrontRight" + ".\t";
    retVal = retVal  + "TireTempRearLeft" + ".\t";
    retVal = retVal  + "TireTempRearRight" + ".\t";
    
    retVal = retVal + "Fuel" + ".\t";
    retVal = retVal + "DistanceTraveled" + ".\t";
    
    retVal = retVal + "LastLap" + ".\t";
    retVal = retVal + "CurrentLap" + ".\t";
    retVal = retVal + "CurrentRaceTime" + ".\t";
    retVal = retVal + "LapNumber0" + ".\t";
    retVal = retVal + "LapNumber1" + ".\t";
    retVal = retVal + "RacePosition" + ".\t";
    retVal = retVal + "Accel" + ".\t";
    retVal = retVal + "Brake" + ".\t";
    retVal = retVal + "Clutch" + ".\t";
    retVal = retVal + "HandBrake" + ".\t";
    retVal = retVal + "Gear" + ".\t";
    retVal = retVal + "Steer" + ".\t";
    retVal = retVal + "NormalizedDrivingLine" + ".\t";
    retVal = retVal + "NormalizedAIBrakeDifference" + ".\t";
    
    retVal = retVal + "TireWearFrontLeft" + ".\t";
    retVal = retVal + "TireWearFrontRight" + ".\t";
    retVal = retVal + "TireWearRearLeft" + ".\t";
    retVal = retVal + "TireWearRearRight" + ".\t";
    
    retVal = retVal + "TrackOrdinal" + ".\t";
    retVal = retVal + "CarOrdinal" + ".\t";
    
    retVal = retVal + "LapDistance" + "\t";
    retVal = retVal + "BrakeEvent" + "\t";
    retVal = retVal + "OffThrottleEvent" + "\t";
    retVal = retVal + "FileName" + "\t";
    
    retVal = retVal + "\n";
    return retVal;
  }
  public int getBufSize() {
    return 331;
  }
  public boolean IsNewFile() {
    System.out.println("IsNewFile: " + newFile);
    return newFile;
  }
  public String CreateFileName() {
    String retVal;
    
    retVal = "fm23-" + CarOrdinal + "-" + CarPerformanceIndex + "-" +TrackOrdinal + "-" + TimeStampString() + ".txt";
    retVal = retVal.replace(":", ".");
    retVal = retVal.replace(" ", "_"); 
    FileName = retVal;
    
    return retVal;
  }
  public boolean LogThisPacket() {
    return (IsRaceOn == 1);
  }
  public TelemetrySnapshot getTelemetrySnapshot() {
    TelemetrySnapshot retVal = new TelemetrySnapshot(
      LapDistance,
      Speed,
      VelocityX, 
      VelocityZ,
      AccelerationX,
      AccelerationZ,
      Yaw, // The missing piece
      (float) Brake / 255 ,
      (float) Accel / 255
      
    );
    
    return retVal;
  }
  public int getCarOrdinal() {
    return CarOrdinal;
  }
  public int getTrackOrdinal() {
    return TrackOrdinal;
  }
}
