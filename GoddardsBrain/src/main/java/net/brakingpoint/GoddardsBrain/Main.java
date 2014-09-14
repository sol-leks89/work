
package net.brakingpoint.GoddardsBrain;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting GoddardsBrain");
		PrimitiveMovements pm = new PrimitiveMovements("http://192.168.0.52:8900/set-channel-position/");

		pm.MoveServo(0, 2000, false);
		pm.MoveServo(1, 8000,false);
		pm.MoveServo(0, 8000,false);
		pm.MoveServo(1, 2000, false);

	}

}
