package net.brakingpoint.GoddardsBrain;

import java.io.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

public class PrimitiveMovements {
		private String url;
		public PrimitiveMovements() {

		}
		public PrimitiveMovements(String maestroUrl) {
			url = maestroUrl;

		}
		public String getUrl() {
			return url;

		}
		public void setUrl(String maestroUrl) {
			url = maestroUrl;
		}
		public void MoveServo(int channel, int position, boolean isWaiting) {
			try {
				if (url != null && !url.equalsIgnoreCase("")) {
					PostMethod poster = new PostMethod(url);
					HttpClient client = new HttpClient();
					String channelParam = "";
					String positionParam = "";

					channelParam = channelParam + channel;
					positionParam = positionParam + position;
					
					poster.addParameter("channel", channelParam);
					poster.addParameter("position", positionParam);

					int statusCode = client.executeMethod(poster);
					System.out.println("Status: " + statusCode);
				}
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}


			
	
		}
	
}