package doom;

public class Raycaster {
    private Map map;
    private Player player;
    private final int numRays = 120;

    public Raycaster(Map map, Player player) {
        this.map = map;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Ray[] castRays() {
        Ray[] rays = new Ray[numRays];
        double startAngle = player.angle - player.FOV / 2;
        double angleStep = player.FOV / numRays;

        for (int i = 0; i < numRays; i++) {
            double rayAngle = startAngle + i * angleStep;
            rays[i] = performDDA(rayAngle, rayAngle - player.angle);
        }
        return rays;
    }

    private Ray performDDA(double rayAngle, double angleDiff) {
        double rayDirX = Math.cos(rayAngle);
        double rayDirY = Math.sin(rayAngle);

        int mapX = (int) player.x;
        int mapY = (int) player.y;

        double deltaDistX = Math.abs(1 / rayDirX);
        double deltaDistY = Math.abs(1 / rayDirY);

        int stepX, stepY;
        double sideDistX, sideDistY;

        if (rayDirX < 0) {
            stepX = -1;
            sideDistX = (player.x - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0 - player.x) * deltaDistX;
        }

        if (rayDirY < 0) {
            stepY = -1;
            sideDistY = (player.y - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0 - player.y) * deltaDistY;
        }

        boolean hit = false;
        int side = -1;
        double distance = Double.MAX_VALUE;

        while (!hit) {
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = 0;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = 1;
            }

            if (mapX < 0 || mapX >= map.width || mapY < 0 || mapY >= map.height) {
                break;
            }

            if (map.map[mapY][mapX] != 0) hit = true;
        }

        if (distance == Double.MAX_VALUE) {
            return new Ray(distance, -1, 0);
        }

        if (side == 0) {
            distance = (mapX - player.x + (1 - stepX) / 2.0) / rayDirX;
        } else {
            distance = (mapY - player.y + (1 - stepY) / 2.0) / rayDirY;
        }

        distance *= Math.cos(angleDiff);

        double wallX;

        if (side == 0) {
            wallX = player.y + distance * rayDirY;
        } else {
            wallX = player.x + distance * rayDirX;
        }

        wallX -= Math.floor(wallX);

        return new Ray(distance, side, wallX);
    }
}
