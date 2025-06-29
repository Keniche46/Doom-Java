package doom;

public class Main {
	public static void main(String[] args) {
		Map map = new Map();
		Player player = new Player(5.5, 5.5, Math.toRadians(90));
		Raycaster raycaster = new Raycaster(map, player);
		GamePanel panel = new GamePanel(raycaster, player, map);
		
		new GameFrame(panel);
	}
}
