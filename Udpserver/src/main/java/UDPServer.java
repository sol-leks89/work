// UDPServer.java

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.security.*;
import java.time.zone.*;


public class UDPServer {
  static  int dumpnum = 0;
  
  public static void main(String[] args) throws IOException {
    DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt("5001"));
    forzaDash forzaBuf ;
    System.out.println("Server Started. Listening for Clients on port 5001" + "...");
    byte[]  receiveData = new byte[336];
    DatagramPacket receivePacket;
    int CurrentLap = 999;
    String line;
    BufferedWriter writer = null;
  String filename = "Telemetry.txt"; 
  float lapStartDistance = 0;
  int brakeEventCounter = 0;
  int previousBrake = 0;
  int offThrottleCounter = 0;
  int previousAccel = 255;
    while (true) 
    {
      // Server waiting for clients message
       receivePacket = new DatagramPacket( receiveData, 336);
       serverSocket.receive(receivePacket);

        forzaBuf = new forzaDash(receivePacket.getData());
       if (forzaBuf.IsRaceOn  != 0) 
        {
          if (writer == null) {
            // is this a new race or a continuation?
            if (forzaBuf.LapNumber0 < CurrentLap) {
                // new race
                filename = "telemetry-" + forzaBuf.CarOrdinal + "-" + forzaBuf.CarPerformanceIndex + "-" +forzaBuf.TrackOrdinal + "-" + TimeStampString() + ".txt";
                filename = filename.replace(":", ".");
                filename = filename.replace(" ", "_");
                writer = new BufferedWriter(new FileWriter(filename));   
                writer.write(BuildFirstLine());
              
            }
            else {// continuation 
              writer = new BufferedWriter(new FileWriter(filename, true));   
            }
              
        }
        if (CurrentLap !=forzaBuf.LapNumber0) { // new lap
            CurrentLap = forzaBuf.LapNumber0;
            brakeEventCounter = 0;
            offThrottleCounter = 0;
            System.out.println("Current Lap: " + CurrentLap);
            //PrintByteArray(receivePacket.getData(), forzaBuf);
            lapStartDistance = forzaBuf.DistanceTraveled;
          
        }
          forzaBuf.LapDistance= forzaBuf.DistanceTraveled - lapStartDistance;
          
          // look for braking events
          if (previousBrake == 0 && forzaBuf.Brake > 0) {
            brakeEventCounter = brakeEventCounter +1;
          }
          if (forzaBuf.Brake == 0)
            forzaBuf.BrakeEvent = 0;
          else
            forzaBuf.BrakeEvent = brakeEventCounter;
        previousBrake = forzaBuf.Brake;
        
        // Look for OffThrottle Events
        if (previousAccel == 255 && forzaBuf.Accel < 255) {
          offThrottleCounter = offThrottleCounter  + 1;
        }
          if (forzaBuf.Accel == 255)
            forzaBuf.OffThrottleEvent = 0;
          else
            forzaBuf.OffThrottleEvent = offThrottleCounter;
            previousAccel = forzaBuf.Accel;
forzaBuf.FileName = filename;

          line = BuildDataLine(forzaBuf);
        writer.write(line);
  
      }  
      else { //the race is no longer on
        if (writer != null)
          writer.close();
        writer = null;
      
      }
      forzaBuf = null;
      
    }
  }
  private static String TimeStampString() {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    return timestamp.toString();
    
  }
  private static void PrintByteArray(byte arr[], forzaDash buf) throws IOException {
  int i;

  BufferedWriter writer = new BufferedWriter(new FileWriter("dump-" + TimeStampString() + ".txt"));   
  
  for (i = 0; i < arr.length; i++) {
    if (i % 16 == 0)
      writer.write("\n");
    else if (i % 8 == 0)
      writer.write("\t");
    else 
      writer.write(" ");
    
    writer.write(String.format("%02x", arr[i]));
    
  }
  writer.write("\n\n");
  writer.write(BuildDataLine(buf));
  writer.close();
}
  private  static String BuildDataLine(forzaDash buf) {
    String retVal = new String();
    
    retVal = buf.TimestampMS + "\t";
    retVal = retVal + buf.CurrentEngineRpm + "\t";
    retVal = retVal + buf.AccelerationX + "\t";
    retVal = retVal + buf.AccelerationY + "\t";
    retVal = retVal + buf.AccelerationZ + "\t";
    
    retVal = retVal +buf.VelocityX + "\t";
    retVal =retVal + buf.VelocityY +"\t";
    retVal = retVal + buf.VelocityZ + "\t";
    
    retVal = retVal + buf.AngularVelocityX + "\t";
    retVal = retVal + buf.AngularVelocityY + "\t";
    retVal = retVal + buf.AngularVelocityZ + "\t";
    
    retVal = retVal + buf.Yaw + "\t";
    retVal = retVal + buf.Pitch +"\t";
    retVal = retVal + buf.Roll + "\t";
    
    retVal = retVal + buf.NormalizedSuspensionTravelFrontLeft + "\t";
    retVal = retVal + buf.NormalizedSuspensionTravelFrontRight + "\t";
    retVal = retVal + buf.NormalizedSuspensionTravelRearLeft + "\t";
    retVal = retVal + buf.NormalizedSuspensionTravelRearRight + "\t";
    
    retVal = retVal + buf.TireSlipRatioFrontLeft + "\t";
    retVal = retVal + buf.TireSlipRatioFrontRight + "\t";
    retVal = retVal + buf.TireSlipRatioRearLeft + "\t";
    retVal = retVal + buf.TireSlipRatioRearRight + "\t";
    
    retVal = retVal + buf.WheelRotationSpeedFrontLeft + "\t";
    retVal = retVal + buf.WheelRotationSpeedFrontRight + "\t";
    retVal = retVal + buf.WheelRotationSpeedRearLeft + "\t";
    retVal = retVal + buf.WheelRotationSpeedRearRight + "\t";
    
    retVal = retVal + buf.WheelOnRumbleStripFrontLeft + "\t";
    retVal = retVal + buf.WheelOnRumbleStripFrontRight + "\t";
    retVal = retVal + buf.WheelOnRumbleStripRearLeft + "\t";
    retVal = retVal + buf.WheelOnRumbleStripRearRight + "\t";
    
    retVal = retVal + buf.WheelInPuddleDepthFrontLeft + "\t";
    retVal = retVal + buf.WheelInPuddleDepthFrontRight + "\t";
    retVal = retVal + buf.WheelInPuddleDepthRearLeft + "\t";
    retVal = retVal + buf.WheelInPuddleDepthRearRight + "\t";
    
    retVal = retVal +buf.TireSlipAngleFrontLeft + "\t";
    retVal = retVal +buf.TireSlipAngleFrontRight + "\t";
    retVal = retVal +buf.TireSlipAngleRearLeft + "\t";
    retVal = retVal +buf.TireSlipAngleRearRight + "\t";
    
    retVal = retVal +buf.SuspensionTravelMetersFrontLeft + "\t";
    retVal = retVal +buf.SuspensionTravelMetersFrontRight + "\t";
    retVal = retVal +buf.SuspensionTravelMetersRearLeft + "\t";
    retVal = retVal +buf.SuspensionTravelMetersRearRight + "\t";
    
    retVal = retVal + buf.PositionX + "\t";
    retVal = retVal + buf.PositionY + "\t";
    retVal = retVal + buf.PositionZ + "\t";
    
    
    retVal = retVal + buf.Speed + "\t";
    retVal = retVal + buf.Power + "\t";
    retVal = retVal + buf.Torque + "\t";
    
    retVal = retVal  + buf.TireTempFrontLeft + "\t";
    retVal = retVal  + buf.TireTempFrontRight + "\t";
    retVal = retVal  + buf.TireTempRearLeft + "\t";
    retVal = retVal  + buf.TireTempRearRight + "\t";
    
    retVal = retVal + buf.Fuel + "\t";
    retVal = retVal + buf.DistanceTraveled + "\t";
    
    retVal = retVal + buf.LastLap + "\t";
    retVal = retVal + buf.CurrentLap + "\t";
    retVal = retVal + buf.CurrentRaceTime + "\t";
    retVal = retVal + buf.LapNumber0 + "\t";
    retVal = retVal + buf.LapNumber1 + "\t";
    retVal = retVal + buf.RacePosition + "\t";
    retVal = retVal + buf.Accel + "\t";
    retVal = retVal + buf.Brake + "\t";
    retVal = retVal + buf.Clutch + "\t";
    retVal = retVal + buf.HandBrake + "\t";
    retVal = retVal + buf.Gear + "\t";
    retVal = retVal + buf.Steer + "\t";
    retVal = retVal + buf.NormalizedDrivingLine + "\t";
    retVal = retVal + buf.NormalizedAIBrakeDifference + "\t";
    
    retVal = retVal + buf.TireWearFrontLeft + "\t";
    retVal = retVal + buf.TireWearFrontRight + "\t";
    retVal = retVal + buf.TireWearRearLeft + "\t";
    retVal = retVal + buf.TireWearRearRight + "\t";
    
    retVal = retVal + buf.TrackOrdinal + "\t";
    retVal = retVal + buf.CarOrdinal + "\t";
    
    retVal = retVal + buf.LapDistance  + "\t";
    retVal = retVal + buf.BrakeEvent + "\t";
    retVal = retVal + buf.OffThrottleEvent + "\t";
    retVal = retVal + buf.FileName + "\t";
    
    retVal = retVal + "\n";
    return retVal;
  }
  private static String BuildFirstLine() {
    
    String retVal;
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
}