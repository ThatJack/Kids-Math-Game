import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class ScorePanel extends JPanel {
    GridBagConstraints layout = new GridBagConstraints();
    JTable board = new JTable(1, 2); //name column and score column
    DefaultTableModel model = (DefaultTableModel) board.getModel();
    ArrayList<Player> leaderboard = new ArrayList<>();

    public ScorePanel (Game manager) {
        setFocusable(true);
        requestFocus();
        setPreferredSize(new Dimension(Game.WIDTH / 2, Game.HEIGHT / 2));

        setBackground(new Color(25, 221, 255));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        resetLeaderboard();
    }

    public void resetLeaderboard() {
        leaderboard = new ArrayList<>();
        refreshLeaderboard();
    }

    private void refreshLeaderboard() {
        removeAll();
        JLabel lb = new JLabel("Leaderboard:");
        lb.setForeground(Game.base_dark);
        lb.setFont(Game.bold);
        lb.setAlignmentX(CENTER_ALIGNMENT);
        add(lb);

        board = new JTable(1, 2);
        model = (DefaultTableModel) board.getModel();
        model.setValueAt("Name:",0, 0);
        model.setValueAt("Score/Time:", 0, 1);

        add(board, layout);
        Game.refresh(this);
    }

    public void updateLeaderboard(Player p) {
        leaderboard.add(p);
        leaderboard.sort(new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return Integer.compare(o2.getScore(), o1.getScore());
            }
        });
        refreshLeaderboard();
        for (Player entry : leaderboard) model.addRow(new Object[] {entry.getName(), entry.getScore()});
    }
}
