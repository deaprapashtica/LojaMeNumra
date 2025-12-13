import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

class LojaMeNumra extends JPanel implements ActionListener, KeyListener {

    private static final int W = 800;
    private static final int H = 600;

    private int px = W / 2 - 45;
    private final int py = H - 80;
    private final int pw = 90;
    private final int ph = 18;

    private int dx = 0;
    private final int playerSpeed = 7;

    private final ArrayList<Fall> falling = new ArrayList<>();
    private final Random rnd = new Random();

    private int score = 0;
    private int lives = 3;
    private int target = pickTarget();
    private boolean gameOver = false;

    private int tick = 0;
    private final int spawnRate = 30;
    private final int fallSpeed = 3;

    private final Timer timer = new Timer(16, this);

    public LojaMeNumra() {
        setPreferredSize(new Dimension(W, H));
        setBackground(Color.PINK);
        setFocusable(true);
        addKeyListener(this);
        timer.start();
    }

    private int pickTarget() {
        return 1 + rnd.nextInt(9);
    }

    private void spawnNumber() {
        int x = 30 + rnd.nextInt(W - 60);
        int y = -30;
        int value = 1 + rnd.nextInt(9);
        falling.add(new Fall(x, y, value, fallSpeed));
    }

    private void resetGame() {
        score = 0;
        lives = 3;
        tick = 0;
        dx = 0;
        gameOver = false;

        target = pickTarget();
        px = W / 2 - 45;
        falling.clear();
    }

    private void endGame() {
        gameOver = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) updateGame();
        repaint();
    }

    private void updateGame() {
        tick++;
        if (tick >= spawnRate) {
            spawnNumber();
            tick = 0;
        }

        px += dx;
        if (px < 0) px = 0;
        if (px + pw > W) px = W - pw;

        Rectangle playerRect = new Rectangle(px, py, pw, ph);

        for (int i = falling.size() - 1; i >= 0; i--) {
            Fall n = falling.get(i);
            n.y += n.speed;

            Rectangle bubbleRect = new Rectangle(n.x - 30, n.y - 30, 60, 60);

            if (n.y - 30 > H) {
                if (n.value == target) {
                    lives--;
                    target = pickTarget();
                    if (lives <= 0) endGame();
                }
                falling.remove(i);
                continue;
            }

            if (playerRect.intersects(bubbleRect)) {
                if (n.value == target) {
                    score += 10;
                    target = pickTarget();
                } else {
                    lives--;
                    if (lives <= 0) endGame();
                }
                falling.remove(i);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(255, 182, 193));
        g2.fillRect(0, 0, W, H);

        g2.setColor(new Color(147, 112, 219));
        g2.fillRect(px, py, pw, ph);

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        for (Fall n : falling) {
            g2.setColor(new Color(255, 105, 180));
            g2.fillOval(n.x - 30, n.y - 30, 60, 60);

            g2.setColor(Color.BLACK);
            String text = String.valueOf(n.value);
            int tw = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, n.x - tw / 2, n.y + 6);
        }

        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 20, 30);
        g2.drawString("Lives: " + lives, 20, 55);
        g2.drawString("Target: " + target, W - 160, 30);

        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, W, H);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString("GAME OVER", 240, 250);

            g2.setFont(new Font("Arial", Font.PLAIN, 22));
            g2.drawString("Final Score: " + score, 300, 300);
            g2.drawString("ENTER = Restart", 290, 340);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

        if (gameOver && k == KeyEvent.VK_ENTER) {
            resetGame();
            return;
        }

        if (k == KeyEvent.VK_LEFT) {
            dx = -playerSpeed;
        } else if (k == KeyEvent.VK_RIGHT) {
            dx = playerSpeed;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    private static class Fall {
        int x, y, value, speed;

        Fall(int x, int y, int value, int speed) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.speed = speed;
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Loja Me Numra");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);

        LojaMeNumra game = new LojaMeNumra();
        f.add(game);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
