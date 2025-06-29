package doom;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
    public GameFrame(GamePanel panel) {
        this.setTitle("Doom - Java");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(panel);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
