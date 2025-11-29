import java.util.*;

public class Player {
    private int score;
    private final String name;
    private String summary = "Summary:";

    public Player (String name) {
        score = 0;
        this.name = name;
    }

    public void setScore(int score) {this.score = score;}
    public void appendSummary(String summary) {this.summary += summary;}
    public String getSummary() {return summary;}
    public int getScore () {return score;}
    public String getName() {return name;}

    public static void displayLeaderboard (Player[] players) {
        //sort by comparing scores
        Arrays.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return Integer.compare(p2.score, p1.score); //sorts highest to lowest score
            }
        });

        System.out.println("\n-- Leaderboard (Score only) --");
        for (Player player : players) System.out.printf("%s : %d\n", player.name, player.score);
    }
}