package doom;

public class Player {
	public double x, y;
	public double angle;
	public final double FOV = Math.toRadians(60);
	public final double moveSpeed = 0.1;
	public final double rotSpeed = Math.toRadians(5);
	
	public Player(double x, double y, double angle) {
		this.x = x;
		this.y = y;
		this.angle = angle;
	};
	
	public void moveForward(Map map) {
		double newX = x + Math.cos(angle) * moveSpeed;
		double newY = y + Math.sin(angle) * moveSpeed;
		
		if (map.map[(int) newY][(int) newX] == 0) {
			x = newX;
			y = newY;
		}
	}
	
	public void moveBackward(Map map) {
		double newX = x - Math.cos(angle) * moveSpeed;
		double newY = y - Math.sin(angle) * moveSpeed;
		
		if (map.map[(int) newY][(int) newX] == 0) {
			x = newX;
			y = newY;
		}
	}
	
	public void rotateLeft() {
	    angle -= rotSpeed;
	    angle = (angle + 2 * Math.PI) % (2 * Math.PI);
	}

	public void rotateRight() {
	    angle += rotSpeed;
	    angle = angle % (2 * Math.PI);
	}
	
}