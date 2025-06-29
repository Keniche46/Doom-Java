package doom;

public class Ray {
    private double distance;
    private int side;
    private double wallX;
    
    public Ray(double distance, int side, double wallX) {
        this.distance = distance;
        this.side = side;
        this.wallX = wallX;
    }
    
    public double getDistance() {
    	return distance;
    }
    
    public int getSide() {
    	return side;
    }
    
    public double getWallX() {
    	return wallX;
    }
    
}
