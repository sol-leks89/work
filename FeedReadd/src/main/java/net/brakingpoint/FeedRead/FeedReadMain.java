
package net.brakingpoint.FeedRead;

public class FeedReadMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReadData rd;
		if (args.length > 0)
			//ReadData rd = new ReadData("http://www.nascar.com/leaderboard/Series_1/2013/4116/3/leaderboard.json");
			rd = new ReadData(args[0]);
		else
			rd = new ReadData();
		System.out.println("FeedReadd starting!");
		
		for (int i=0; i < 500; i=i) {
				System.out.print(i);
				System.out.print(" - ");
				if (rd.ReadFeed()) {
					i=0;
				}
				else {
					i++;
				}
				try {
					Thread.sleep(250);
				}
				catch (Exception e) {
					System.out.println("Thread exception");
				}
			
		}

	}

}
