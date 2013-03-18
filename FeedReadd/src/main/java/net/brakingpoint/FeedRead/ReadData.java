package net.brakingpoint.FeedRead;
import java.io.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
public class ReadData {

	private String url;
	private String lastMsg;
	
	public ReadData() {
		url = "http://localhost/leaderboard.json";
		lastMsg = "";
	}
	public ReadData(String urlInit) {
		url = urlInit;
		lastMsg = "";
	}
	public boolean ReadFeed() {
	GetMethod	getter = new GetMethod(url);
	HttpClient client = new HttpClient();
	String msg;	
	boolean retCd = false;
	File f = new File("/home/jimw/FEED.TXT");
	FileWriter fw;
	BufferedWriter bw;
	
	try {
		System.out.println(url);
		if (!f.exists()) {
			System.out.println("Creating new file");
			f.createNewFile();
		}

		fw = new FileWriter(f.getAbsoluteFile(), true);
		bw = new BufferedWriter(fw);
		
		
			client.executeMethod(getter);

		msg = getter.getResponseBodyAsString();
		getter.releaseConnection();
		//System.out.println(getter.getStatusCode());
		if ((!msg.equalsIgnoreCase(lastMsg)) && (getter.getStatusCode() == 200)) {
			System.out.println(msg);
			lastMsg = msg;
			retCd = true;
			bw.append(msg);
			bw.append("~");
		}
		else {
			retCd = false;
			System.out.println(getter.getStatusCode());
		}
		bw.flush();
		bw.close();
	}
	catch(Exception e) {
		System.out.println(e.getStackTrace());
	}
		return retCd;	
		
		
		
	}

}
