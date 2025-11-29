import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GamePanel extends JPanel {

    private final Game manager;
    private Player[] players;
    private Queue<Player> queue; //handles multiplayer turns

    public GamePanel(Game manager) {
        setFocusable(true);
        requestFocus();
        setPreferredSize(new Dimension(Game.WIDTH / 2, Game.HEIGHT));

        this.manager = manager;
        setBackground(Game.base);
        setBorder(new EmptyBorder(20, 10, 10, 10));

        JLabel pre = new JLabel("Select a mode from the left to begin.");
        pre.setForeground(Game.accent);
        pre.setFont(Game.bold);
        add(pre, BorderLayout.CENTER);
    }

    //gets information needed for the selected game mode
    public void setupSingleplayer(Game.Modes mode) {
        removeAll();
        JLabel modeQ = new JLabel((mode.equals(Game.Modes.MAW))? "Number of Questions: " : "Time Limit: ");
        modeQ.setForeground(Game.accent);
        modeQ.setFont(Game.font);
        add(modeQ);

        JTextField modeInput = new JTextField(10);
        modeInput.setBackground(Game.base_dark);
        modeInput.setForeground(Game.accent);
        modeInput.setFont(Game.font);
        add(modeInput);

        JButton submit = new JButton("Play");
        submit.setBackground(Game.base_light);
        submit.setForeground(Game.accent);
        submit.setFont(Game.font);
        submit.addActionListener(_ -> {
            players = new Player[1];
            //start game
            startGame(mode, modeInput.getText());
        });
        this.add(submit);
        Game.refresh(this);
    }

    //start game mode with requested user inputs
    private void startGame(Game.Modes mode, String input) {
        if (!input.isEmpty()) { //user has entered either num of questions or time limit
            queue = new LinkedList<>();
            queue.addAll(Arrays.asList(players));
            if(mode.equals(Game.Modes.MAW)) { //handle invalid inputs
                try {
                    makeAWish(Integer.parseInt(input));
                } catch (NumberFormatException ex) {System.out.println("Invalid input");}
            }
            else if (mode.equals(Game.Modes.TIME)) {
                try {
                    timeTrials(Integer.parseInt(input));
                } catch (NumberFormatException ex) {System.out.println("Invalid input");}
            }
        } else if (mode.equals(Game.Modes.CHANCES)) {
            queue = new LinkedList<>();
            queue.addAll(Arrays.asList(players)); //add placeholders to queue
            takeChances();
        }
    }

    //user input for number of players and starting multiplayer modes
    public void setupMultiplayer(Game.Modes mode) {
        removeAll();

        //display player select
        JLabel num = new JLabel("Number of Players");
        num.setFont(Game.font);
        num.setForeground(Game.accent);
        this.add(num);

        JTextField field = new JTextField(10);
        field.setBackground(Game.base_dark);
        field.setForeground(Game.accent);
        field.setFont(Game.font);
        add(field);

        JTextField modeInput = new JTextField(10);
        modeInput.setForeground(Game.accent);
        modeInput.setBackground(Game.base_dark);
        modeInput.setFont(Game.font);

        //take chances mode does not require more information (always start with 3 lives)
        if (!mode.equals(Game.Modes.CHANCES)) {
            JLabel modeQ = new JLabel((mode.equals(Game.Modes.MAW)) ? "Number of Questions: " : "Time Limit (seconds): ");
            modeQ.setForeground(Game.accent);
            modeQ.setFont(Game.font);
            add(modeQ);
            add(modeInput);
        }

        //submit button
        JButton submit = new JButton("Play");
        submit.setBackground(Game.base_light);
        submit.setForeground(Game.accent);
        submit.setFont(Game.font);
        submit.addActionListener(_ -> {
            //get user input
            int playerCount = 1;
            if(!(field.getText().isEmpty())) { //only submit once something has been entered into field
                try {
                    playerCount = Integer.parseInt(field.getText());
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid Input");
                }
            }
            players = new Player[(playerCount == 0)? 1 : playerCount]; //set player count

            //start game
            startGame(mode, modeInput.getText());
        });
        this.add(submit);

        Game.refresh(this);
    }

    public void makeAWish(int q) {
        removeAll();
        JLabel name = new JLabel("Player Name:");
        name.setForeground(Game.accent);
        name.setFont(Game.font);
        JTextField field = new JTextField();
        field.setFont(Game.font);
        field.setForeground(Game.accent);
        field.setBackground(Game.base_dark);
        field.setColumns(10);
        JButton enter = new JButton("Start Game");
        enter.setFont(Game.font);
        enter.setForeground(Game.accent);
        enter.setBackground(Game.base_light);
        enter.addActionListener(_ -> makeAWish(q, new Player(field.getText()), 1, System.currentTimeMillis()));

        add(name);
        add(field);
        add(enter);
        Game.refresh(this);

        queue.remove();
    }

    private void makeAWish(int q, Player p, int asked, long start) {
        Map.Entry<String, Number> equation = generateQuestion();

        removeAll();
        JLabel question = new JLabel(equation.getKey());
        question.setForeground(Game.accent);
        question.setFont(Game.bold);
        add(question);
        JTextField answerField = new JTextField(10);
        answerField.setFont(Game.font);
        answerField.setBackground(Game.base_dark);
        answerField.setForeground(Game.accent);
        add(answerField);

        JButton answerButton = new JButton("Answer");
        answerButton.setBackground(Game.base_light);
        answerButton.setForeground(Game.accent);
        answerButton.setFont(Game.font);
        answerButton.addActionListener(_ -> {
            if (!answerField.getText().isEmpty()) {
                if (equation.getValue() instanceof Integer) { //not division question
                    int userAns;
                    try {
                        userAns = Integer.parseInt(answerField.getText());
                    } catch (NumberFormatException ex) {userAns = -1;} //invalid format will always be incorrect
                    boolean correct = checkAnswer(userAns, equation.getValue());
                    p.appendSummary(String.format("\n%s = %d : %b", equation.getKey().substring(7, equation.getKey().indexOf('?')), userAns, correct));
                    if (correct) p.setScore(p.getScore() + 1); //increase score if correct
                } else { //division question
                    double userAns;
                    try {
                        userAns = Double.parseDouble(answerField.getText());
                    } catch (NumberFormatException ex) {userAns = -1f;}
                    boolean correct = checkAnswer(userAns, equation.getValue());
                    p.appendSummary(String.format("\n%s = %f : %b", equation.getKey().substring(7, equation.getKey().indexOf('?')), userAns, correct));
                    if (correct) p.setScore(p.getScore() + 1);
                }
                if (asked < q) makeAWish(q, p, asked + 1, start); //ask more questions
                else
                    endRound(p, String.format("You scored %d/%d.\nTime Taken:%d", p.getScore(), q, (int) ((System.currentTimeMillis() - start) / 1000)), Game.Modes.MAW, q);
            }
        });
        add(answerButton);
        Game.refresh(this);
    }

    public void timeTrials(int limit) {
        removeAll();
        JLabel name = new JLabel("Player Name:");
        name.setFont(Game.font);
        name.setForeground(Game.accent);
        JTextField field = new JTextField();
        field.setForeground(Game.accent);
        field.setBackground(Game.base_dark);
        field.setColumns(10);
        JButton enter = new JButton("Start Game");
        enter.setForeground(Game.accent);
        enter.setBackground(Game.base_light);
        enter.addActionListener(_ -> timeTrials(System.currentTimeMillis() + (limit * 1000L), new Player(field.getText()), 1, limit));

        add(name);
        add(field);
        add(enter);
        Game.refresh(this);

        queue.remove();
    }

    private void timeTrials(long endTime, Player p, int answered, int limit) {
        Map.Entry<String, Number> equation = generateQuestion();

        removeAll();
        JLabel question = new JLabel(equation.getKey());
        question.setForeground(Game.accent);
        add(question);
        JTextField answerField = new JTextField(10);
        answerField.setBackground(Game.base_dark);
        answerField.setForeground(Game.accent);
        add(answerField);

        JButton answerButton = new JButton("Answer");
        answerButton.setBackground(Game.base_light);
        answerButton.setForeground(Game.accent);
        answerButton.addActionListener(_ -> {
            if (!answerField.getText().isEmpty()) {
                if (equation.getValue() instanceof Integer) { //not division question
                    int userAns;
                    try {
                        userAns = Integer.parseInt(answerField.getText());
                    } catch (NumberFormatException ex) {userAns = -1;} //invalid format will always be incorrect
                    boolean correct = checkAnswer(userAns, equation.getValue());
                    p.appendSummary(String.format("\n%s = %d : %b", equation.getKey().substring(7, equation.getKey().indexOf('?')), userAns, correct));
                    if (correct) p.setScore(p.getScore() + 1); //increase score if correct
                } else { //division question
                    double userAns;
                    try {
                        userAns = Double.parseDouble(answerField.getText());
                    } catch (NumberFormatException ex) {userAns = -1f;}
                    boolean correct = checkAnswer(userAns, equation.getValue());
                    p.appendSummary(String.format("\n%s = %.2f : %b", equation.getKey().substring(7, equation.getKey().indexOf('?')), userAns, correct));
                    if (correct) p.setScore(p.getScore() + 1);
                }
                if (System.currentTimeMillis() < endTime)
                    timeTrials(endTime, p, answered + 1, limit);
                else
                    endRound(p, String.format("You scored %d out of %d questions answered", p.getScore(), answered), Game.Modes.TIME, limit);
            }
        });
        add(answerButton);
        Game.refresh(this);
    }

    public void takeChances() {
        removeAll();
        JLabel name = new JLabel("Player Name:");
        name.setForeground(Game.accent);
        JTextField field = new JTextField();
        field.setForeground(Game.accent);
        field.setBackground(Game.base_dark);
        field.setColumns(10);
        JButton enter = new JButton("Start Game");
        enter.setForeground(Game.accent);
        enter.setBackground(Game.base_light);
        enter.addActionListener(_ -> takeChances(new Player(field.getText()), 3, 1));

        add(name);
        add(field);
        add(enter);
        Game.refresh(this);

        queue.remove();
    }

    private void takeChances(Player p, int lives, int answered) {
        Map.Entry<String, Number> equation = generateQuestion();

        removeAll();
        JLabel life = new JLabel("Lives: " + lives);
        life.setForeground(Game.accent);
        add(life);
        JLabel question = new JLabel(equation.getKey());
        question.setForeground(Game.accent);
        add(question);
        JTextField answerField = new JTextField(10);
        answerField.setBackground(Game.base_dark);
        answerField.setForeground(Game.accent);
        add(answerField);

        int scoreCheck = p.getScore();
        JButton answerButton = new JButton("Answer");
        answerButton.addActionListener(_ -> {
            if (!answerField.getText().isEmpty()) {
                if (equation.getValue() instanceof Integer) { //not division question
                    int userAns;
                    try {
                        userAns = Integer.parseInt(answerField.getText());
                    } catch (NumberFormatException ex) {userAns = -1;} //invalid format will always be incorrect
                    if (checkAnswer(userAns, equation.getValue()))
                        p.setScore(p.getScore() + 1); //increase score if correct
                } else { //division question
                    double userAns;
                    try {
                        userAns = Double.parseDouble(answerField.getText());
                    } catch (NumberFormatException ex) {userAns = -1f;}
                    if (checkAnswer(userAns, equation.getValue())) p.setScore(p.getScore() + 1);
                }
                if (lives > 1)
                    takeChances(p, (scoreCheck != p.getScore()) ? lives : lives - 1, answered + 1);
                else if (lives == 1) {
                    if (scoreCheck != p.getScore()) takeChances(p, lives, answered + 1);
                    else endRound(p, String.format("You scored %d out of %d questions answered", p.getScore(), answered), Game.Modes.CHANCES, 0);
                }
                else
                    endRound(p, String.format("You scored %d out of %d questions answered", p.getScore(), answered), Game.Modes.CHANCES, 0);
            }
        });
        add(answerButton);
        Game.refresh(this);
    }

    private void endRound(Player p, String msg, Game.Modes mode, int nextInfo) {
        manager.scoreboard.updateLeaderboard(p);

        removeAll();
        JLabel result = new JLabel("Results for " + p.getName() + ":");
        result.setFont(Game.font);
        result.setForeground(Game.accent);

        JLabel score = new JLabel(msg);
        score.setFont(Game.font);
        score.setForeground(Game.accent);

        JTextArea summary = new JTextArea(p.getSummary());
        summary.setBackground(Game.base_light);
        summary.setFont(Game.font);
        summary.setForeground(Game.accent);

        JButton next = new JButton("Next Player");
        next.setForeground(Game.accent);
        next.setBackground(Game.base_light);
        next.setFont(Game.font);
        next.addActionListener(_ -> {
            if (mode.equals(Game.Modes.MAW)) makeAWish(nextInfo);
            else if (mode.equals(Game.Modes.TIME)) timeTrials(nextInfo);
            else takeChances();
        });

        add(result);
        add(score);
        add(summary);
        if (!queue.isEmpty()) add(next); //only add button if next player is awaiting turn
        Game.refresh(this);
    }

    private boolean checkAnswer(Number userAns, Number ans) {
        if (ans instanceof Integer) {
            return (int)userAns == (int)ans;
        } else {
            //System.out.printf("User: %f, Real: %f\n", (double)userAns, (double)ans);
            return ((int)((double)ans * 100) == (int)((double)userAns * 100));
        }
    }

    private Map.Entry<String, Number> generateQuestion() {
        //return format: <full question, correct answer>
        switch ((int)(Math.random() * 4)) {
            case 1 -> {return generateDivQuestion();}
            case 2 -> {return generateMulQuestion();}
            case 3 -> {return generateSubQuestion();}
            default -> {return generateAddQuestion();}
        }
    }

    private Map.Entry<String, Number> generateDivQuestion() {
        int dividend = (int)(Math.random() * 20);
        int divisor = (int)(Math.random() * 9) + 1;
        double answer = dividend / (double)divisor;

        return Map.entry(String.format("What is %d / %d?\n", dividend, divisor), answer);
    }

    private Map.Entry<String, Number> generateMulQuestion() {
        int num1 = (int)(Math.random() * 20);
        int num2 = (int)(Math.random() * 20);
        return Map.entry(String.format("What is "+num1+" x "+num2+" ?"), num1 * num2);
    }

    private Map.Entry<String, Number> generateSubQuestion() {
        int num1 = (int)(Math.random() * 20);
        int num2 = (int)(Math.random() * 20);

        if (num1 > num2)
            return Map.entry(String.format("What is %d - %d?\n", num1, num2), num1 - num2);
        else
            return Map.entry(String.format("What is %d - %d?\n", num2, num1), num2 - num1);
    }

    private Map.Entry<String, Number> generateAddQuestion() {
        int num1 = (int)(Math.random() * 20);
        int num2 = (int)(Math.random() * 20);
        return Map.entry("What is "+num1+" + "+num2+" ?", num1 + num2);
    }
}
