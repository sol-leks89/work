// UDPServer.java
package net.brakingpoint.UDPTelemetryLogger;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.security.*;
import java.time.zone.*;


public class UDPServer {
  static  int dumpnum = 0;
  
  public static void main(String[] args) throws IOException {
    DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt("5002"));
    iTelemetry forzaBuf ;
    System.out.println("Server Started. Listening for Clients on port 5002" + "...");
    byte[]  receiveData = new byte[336];
    DatagramPacket receivePacket;
    String line;
    BufferedWriter writer = null;
  String filename = "Telemetry.txt"; 
    while (true) 
    {
      // Server waiting for clients message
       receivePacket = new DatagramPacket( receiveData, 336);
       serverSocket.receive(receivePacket);
      // send it on down the line
      // Define the destination (127.0.0.1 : 5001)
      InetAddress echoAddress = InetAddress.getByName("127.0.0.1");
      int echoPort = 5001;
      
      // Create a new packet using the data just received
      DatagramPacket echoPacket = new DatagramPacket(
        receivePacket.getData(), 
        receivePacket.getLength(), 
        echoAddress, 
        echoPort
      );
      
      // Send it using the existing serverSocket
      serverSocket.send(echoPacket);
      
        
        if (receivePacket.getLength() < 330)
        forzaBuf = new forzaHorizon5(receivePacket.getData());
        else
          forzaBuf = new forzaDash(receivePacket.getData());
       if (forzaBuf.LogThisPacket()) 
        {
          if (writer == null) {
            // is this a new race or a continuation?
            if (forzaBuf.IsNewFile() == true) {
              // new race 
              //PrintByteArray(ba, forzaBuf);
              filename = forzaBuf.CreateFileName();
                writer = new BufferedWriter(new FileWriter(filename));   
                writer.write(forzaBuf.BuildFirstLine());
              
            }
            else {// continuation 
              writer = new BufferedWriter(new FileWriter(filename, true));   
            }
              
        }
          line = forzaBuf.BuildDataLine();
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
private  static String TimeStampString() {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    return timestamp.toString();
    
  }
  
  private static void PrintByteArray(byte arr[], iTelemetry buf) throws IOException {
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
  writer.write(buf.BuildDataLine());
  writer.close();
}
  
}