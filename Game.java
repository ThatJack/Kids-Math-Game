import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {
    JPanel leftPanel;
    MenuPanel menu;
    GamePanel game;
    ScorePanel scoreboard;
    public static final int WIDTH = 600, HEIGHT = 600;

    public enum Modes {MAW, TIME, CHANCES}

    public static final Color base = new Color(38, 42, 43);
    public static final Color base_dark = new Color(25, 27, 28);
    public static final Color base_light = new Color(66, 73, 74);
    public static final Color accent = new Color(135, 245, 255);

    public static final Font big = new Font("Arial", Font.PLAIN, 20);
    public static final Font font = new Font("Arial", Font.PLAIN, 14);
    public static final Font bold = new Font("Arial", Font.BOLD, 14);

    private Game () {
        super("Game");

        menu = new MenuPanel(this);
        game = new GamePanel(this);
        scoreboard = new ScorePanel(this);

        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(WIDTH / 2, HEIGHT));

        leftPanel.add(menu, BorderLayout.PAGE_START);
        leftPanel.add(scoreboard, BorderLayout.PAGE_END);

        add(leftPanel, BorderLayout.WEST);
        add(game);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();

        setVisible(true);
    }

    public static void main(String[] args) {
        new Game();
    }

    static void refresh (JPanel panel) {
        panel.revalidate();
        panel.repaint();
    }
}
