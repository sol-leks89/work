package net.brakingpoint.forza;
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.security.*;
import java.time.zone.*;

import net.brakingpoint.telemetryInterfaces.*;


public class forzaHorizon5 extends forza   {

  static int prevCar = -1;
  static float prevdistanceTraveled = 99999;
  static float prevLapDistance = 0;
  static int prevLap;
  static int TrackOrdinal = -1;
  
  boolean newFile = true;
  
  float lapDistance;

  int  carCategory; // 232
  
  float verticalG; // 236
  
  float steeringAngle; // 240
  
  float positionX; // 244
  float positionY;
  float positionZ; //252
  
  float speed; // 256
  
  float power; // 260
  float torque; // 264
  
  float TireTempFL; // 268
  float TireTempFR; // 272
  float TireTempRL; // 276
  float TireTempRR;

  private final Float boost;
  private final Float fuel;
  private final Float distanceTraveled;
  private final Float bestLap;
  private final Float lastLap;
  private final Float currentLap;
  private final Float currentRaceTime;
  private final int lapNumber;
  private final int racePosition;
  private final int throttle;
  private final int brake;
  private final int clutch;
  private final int handbrake;
  private final int gear;
  private final byte  steer;
  private final int normalizedDrivingLine;
  private final int normalizedAIBrakeDifference;
  
  static String FileName = "fh";
  
  public forzaHorizon5(byte[] ba) {
    super(ba);
    
    carCategory = bytearrayToInt(ba, 232);
    
    verticalG = bytearrayToFloat(ba, 236);
      
    steeringAngle = bytearrayToFloat(ba, 240);
      
    positionX = bytearrayToFloat(ba, 244);
    positionY = bytearrayToFloat(ba, 248);
    positionZ = bytearrayToFloat(ba, 252);
    
    speed = bytearrayToFloat(ba, 256) ;
    
    power = (bytearrayToFloat(ba, 260) * (float) 1.341) / 1000;
    torque = (bytearrayToFloat(ba, 264) / (float) 1.356) ;
    
    TireTempFL = bytearrayToFloat(ba, 268);
    TireTempFR = bytearrayToFloat(ba, 272);
    TireTempRL = bytearrayToFloat(ba, 276);
    TireTempRR = bytearrayToFloat(ba, 280);
    
    boost = bytearrayToFloat(ba, 284);
    fuel = bytearrayToFloat(ba, 288);
    distanceTraveled = bytearrayToFloat(ba, 292);
    bestLap = bytearrayToFloat(ba, 296);
    lastLap = bytearrayToFloat(ba, 300);
    currentLap = bytearrayToFloat(ba, 304);
    currentRaceTime = bytearrayToFloat(ba, 308);

    lapNumber = (int) ba[312] & 0xff;
    racePosition =  ba[314] & 0xff;
    throttle =  ba[315] & 0xff;
    brake =  ba[316] & 0xff;
    clutch =   ba[317] & 0xff;
    handbrake =  ba[318] & 0xff;
    gear =  ba[319] & 0xff;
    steer =  ba[320];
    normalizedDrivingLine =  ba[321] & 0xff;
    normalizedAIBrakeDifference =  ba[322] & 0xff;
    
    
    if (LogThisPacket()) {
      if (CarOrdinal != prevCar || (distanceTraveled < 3 && distanceTraveled < prevdistanceTraveled)) {
        newFile = true;
        prevLap = 0;
        prevLapDistance = 0;
        TrackOrdinal = ((int)(positionX / 5)) * 5;
        
      }
      else 
        newFile = false;
      
      if (lapNumber != prevLap) {
        prevLapDistance =  distanceTraveled;
      }
      lapDistance =  distanceTraveled - prevLapDistance;
      prevLap = lapNumber;
      
      prevCar = CarOrdinal;
      prevdistanceTraveled = distanceTraveled;
    
    }
    
  }
  public   boolean LogThisPacket() {
    return (IsRaceOn == 1 && distanceTraveled > 0);
  }
  public   boolean IsNewFile() {
    return newFile;
  }
  public String CreateFileName() {
    String retVal = "fh-";
    retVal = retVal + CarOrdinal;
    retVal = retVal + "-";
    retVal = retVal + TrackOrdinal + "-";
    retVal = retVal +  TimeStampString() + ".txt";
    retVal = retVal.replace(":", ".");
    retVal = retVal.replace(" ", "_"); 
    FileName = retVal;
    
    return retVal;
    
  }
  public String BuildFirstLine() {
    String retVal = "";
    
    retVal = super.BuildFirstLine();
    retVal = retVal + "carCategory\t";
    
    retVal = retVal + "verticalG\t";
    
    retVal = retVal + "steeringAngle\t";
    
    retVal = retVal + "positionX\t";
    retVal = retVal + "positionY\t";
    retVal = retVal + "positionZ\t";
    
    retVal = retVal + "speed\t";

    retVal = retVal + "power\t";
    retVal = retVal + "Torque\t";
    
    retVal = retVal + "TireTempFL\t";
    retVal = retVal + "TireTempFR\t";
    retVal = retVal + "TireTempRL\t";
    retVal = retVal + "TireTempRR\t";
    
    retVal = retVal + "boost\t";
    retVal = retVal + "fuel\t";
    retVal = retVal + "distanceTraveled\t";
    retVal = retVal + "bestLap\t";
    retVal = retVal + "lastLap\t";
    retVal = retVal + "currentLap\t";
    retVal = retVal + "currentRaceTime\t";
    retVal = retVal + "lapNumber\t";
    retVal = retVal + "racePosition\t";
    retVal = retVal + "throttle\t";
    retVal = retVal + "brake\t";
    retVal = retVal + "clutch\t";
    retVal = retVal + "handbrake\t";
    retVal = retVal + "gear\t";
    retVal = retVal + "steer\t";
    
    retVal = retVal + "CarOrdinal\t";
    retVal = retVal + "TrackOrdinal\t";
    retVal = retVal + "LapDistance\t";
    
    retVal = retVal + "filename\t";
    retVal = retVal + "\n";
    
    return retVal;
}   
  public String BuildDataLine() {
    String retVal = "";
    
    retVal = super.BuildDataLine();
    
    retVal = retVal + carCategory + "\t";
    
    retVal = retVal + verticalG + "\t";
    
    retVal = retVal + steeringAngle + "\t";
    
    retVal = retVal + positionX + "\t";
    retVal = retVal + positionY + "\t";
    retVal = retVal + positionZ + "\t";
    
    retVal = retVal + speed + "\t";
    
    retVal = retVal + power + "\t";
    retVal = retVal + torque + "\t";
    
  retVal = retVal + TireTempFL + "\t";
  retVal = retVal + TireTempFR + "\t";
  retVal = retVal + TireTempRL + "\t";
  retVal = retVal + TireTempRR + "\t";
  
  retVal = retVal + boost +"\t";
  retVal = retVal +  fuel + "\t";
  retVal = retVal +  distanceTraveled + "\t";
  retVal = retVal +  bestLap + "\t";
  retVal = retVal +  lastLap + "\t";
  retVal = retVal +  currentLap + "\t";
  retVal = retVal +  currentRaceTime + "\t";
  retVal = retVal +  lapNumber + "\t";
  retVal = retVal + racePosition + "\t";
  retVal = retVal +  throttle + "\t";
  retVal = retVal +  brake + "\t";
  retVal = retVal +  clutch +"\t";
  retVal = retVal +  handbrake + "\t";
  retVal = retVal +  gear + "\t";
  retVal = retVal +  steer + "\t";
  
  retVal = retVal + CarOrdinal + "\t";
  retVal = retVal + TrackOrdinal + "\t";
  retVal = retVal + lapDistance + "\t";
    
    
  
    retVal = retVal + FileName + "\t";
    retVal = retVal + "\n";
    
    return retVal;
}
  public int getBufSize() {
    return 323;
  }
}
