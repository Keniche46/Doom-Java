package doom;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GamePanel extends JPanel implements KeyListener {
    private BufferedImage floor1, roof1, wall1;
    private Raycaster raycaster;
    private Player player;
    private Map map;
    private static final int NUM_RAYS = 120;
    private static final double PROJECTION_PLANE_DISTANCE = 300;

    public GamePanel(Raycaster raycaster, Player player, Map map) {
        this.raycaster = raycaster;
        this.player = player;
        this.map = map;

        try {
            floor1 = ImageIO.read(getClass().getResource("/textures/floor.png"));
            roof1 = ImageIO.read(getClass().getResource("/textures/roof.png"));
            wall1 = ImageIO.read(getClass().getResource("/textures/Wall1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        if (wall1 == null || floor1 == null || roof1 == null) {
            g.setColor(Color.RED);
            g.drawString("Texturas não carregadas!", 10, 20);
            return;
        }

        Ray[] rays = raycaster.castRays();
        if (rays == null || rays.length < NUM_RAYS) {
            g.setColor(Color.RED);
            g.drawString("Erro: rays não gerados corretamente", 10, 40);
            return;
        }

        // Fundo céu e chão
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, screenWidth, screenHeight / 2);
        g.setColor(Color.BLACK);
        g.fillRect(0, screenHeight / 2, screenWidth, screenHeight / 2);

        int columnWidth = screenWidth / NUM_RAYS;

        for (int i = 0; i < NUM_RAYS; i++) {
            Ray ray = rays[i];
            if (ray.getSide() == -1) continue;

            double distance = ray.getDistance();
            if (distance < 0.1) distance = 0.1;

            int side = ray.getSide();
            double wallX = ray.getWallX();

            if (wallX < 0) wallX = 0;
            if (wallX > 1) wallX = 1;

            double wallHeight = (PROJECTION_PLANE_DISTANCE / distance) * screenHeight;
            int drawStart = screenHeight / 2 - (int) (wallHeight / 2);
            int drawEnd = screenHeight / 2 + (int) (wallHeight / 2);

            drawStart = Math.max(drawStart, 0);
            drawEnd = Math.min(drawEnd, screenHeight - 1);

            int texX = (int) (wallX * wall1.getWidth());

            // Inversão da textura
            if (side == 0 && raycaster.getPlayer().x > player.x)
                texX = wall1.getWidth() - texX - 1;
            if (side == 1 && raycaster.getPlayer().y < player.y)
                texX = wall1.getWidth() - texX - 1;

            texX = clamp(texX, 0, wall1.getWidth() - 1);

            for (int y = drawStart; y < drawEnd; y++) {
                int d = y - screenHeight / 2 + (int) (wallHeight / 2);
                int texY = (d * wall1.getHeight()) / (int) wallHeight;
                texY = clamp(texY, 0, wall1.getHeight() - 1);

                int color = wall1.getRGB(texX, texY);
                if (side == 1) color = darkenColor(color, 0.5f);
                g.setColor(new Color(color));
                int x = i * columnWidth;
                g.fillRect(x, y, columnWidth, 1); // 1 pixel de altura, coluna da parede
            }
        }

        // Chão e teto com texturas
        for (int y = screenHeight / 2 + 1; y < screenHeight; y++) {
            double rayDirX0 = Math.cos(player.angle - player.FOV / 2);
            double rayDirY0 = Math.sin(player.angle - player.FOV / 2);
            double rayDirX1 = Math.cos(player.angle + player.FOV / 2);
            double rayDirY1 = Math.sin(player.angle + player.FOV / 2);

            int p = y - screenHeight / 2;
            double rowDistance = PROJECTION_PLANE_DISTANCE / p;

            double floorStepX = rowDistance * (rayDirX1 - rayDirX0) / screenWidth;
            double floorStepY = rowDistance * (rayDirY1 - rayDirY0) / screenWidth;

            double floorX = player.x + rowDistance * rayDirX0;
            double floorY = player.y + rowDistance * rayDirY0;

            for (int x = 0; x < screenWidth; x++) {
                int cellX = (int) floorX;
                int cellY = (int) floorY;

                int texX = (int) ((floorX - cellX) * floor1.getWidth());
                int texY = (int) ((floorY - cellY) * floor1.getHeight());
                texX = clamp(texX, 0, floor1.getWidth() - 1);
                texY = clamp(texY, 0, floor1.getHeight() - 1);

                // Chão
                int floorColor = floor1.getRGB(texX, texY);
                g.setColor(new Color(floorColor));
                g.drawLine(x, y, x, y);

                // Teto (espelhado)
                int roofColor = roof1.getRGB(texX, texY);
                g.setColor(new Color(roofColor));
                g.drawLine(x, screenHeight - y, x, screenHeight - y);

                floorX += floorStepX;
                floorY += floorStepY;
            }
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private int darkenColor(int color, float factor) {
        Color c = new Color(color, true);
        int r = (int) (c.getRed() * factor);
        int g = (int) (c.getGreen() * factor);
        int b = (int) (c.getBlue() * factor);
        return new Color(r, g, b, c.getAlpha()).getRGB();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> player.moveForward(map);
            case KeyEvent.VK_S -> player.moveBackward(map);
            case KeyEvent.VK_A -> player.rotateLeft();
            case KeyEvent.VK_D -> player.rotateRight();
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
