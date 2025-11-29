import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    private final GridBagConstraints layout;
    private final Game manager;

    public MenuPanel (Game manager) {
        setFocusable(true);
        requestFocus();
        setPreferredSize(new Dimension(Game.WIDTH /2 , Game.HEIGHT / 2));

        this.manager = manager;
        this.setLayout(new GridBagLayout());
        layout = new GridBagConstraints();
        displayMenu();
        this.setBackground(Game.base_dark);
    }

    private void displayMenu() {
        JButton playMAW = new JButton("Play Make a Wish Mode");
        playMAW.setFont(Game.big);
        playMAW.addActionListener(_ -> {
            manager.scoreboard.resetLeaderboard();
            //setup singleplayer game
            manager.game.setupSingleplayer(Game.Modes.MAW);
        });
        JButton playTime = new JButton("Play Time Trials");
        playTime.setFont(Game.big);
        playTime.addActionListener(_ -> {
            manager.scoreboard.resetLeaderboard();
            manager.game.setupSingleplayer(Game.Modes.TIME);
        });
        JButton multMAW = new JButton("Multiplayer Make a Wish");
        multMAW.setFont(Game.big);
        multMAW.addActionListener(_ -> {
            manager.scoreboard.resetLeaderboard();
            //play multiplayer make a wish
            manager.game.setupMultiplayer(Game.Modes.MAW);
        });
        JButton multTime = new JButton("Multiplayer Time Trials");
        multTime.setFont(Game.big);
        multTime.addActionListener(_ -> {
            manager.scoreboard.resetLeaderboard();
            //play multiplayer time trials
            manager.game.setupMultiplayer(Game.Modes.TIME);
        });
        JButton takeChances = new JButton("Take Chances");
        takeChances.setFont(Game.big);
        takeChances.addActionListener(_ -> {
            manager.scoreboard.resetLeaderboard();
            manager.game.setupMultiplayer(Game.Modes.CHANCES);
        });
        JButton quit = new JButton("Quit Game");
        quit.setFont(Game.big);
        quit.addActionListener(_ -> System.exit(0));

        //add components to display the menu

        layout.fill = GridBagConstraints.HORIZONTAL;
        layout.weightx = 0.5;
        layout.weighty = 1;
        layout.ipady = 80;

        layout.gridx = layout.gridy = 0;
        this.add(playMAW, layout);
        layout.gridy = 1;
        this.add(playTime, layout);
        layout.gridy = 2;
        this.add(multMAW, layout);
        layout.gridy = 3;
        this.add(multTime, layout);
        layout.gridy = 4;
        this.add(takeChances, layout);
        layout.gridy = 5;
        this.add(quit, layout);
    }
}
