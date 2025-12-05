import javax.swing.*;

public class Game {

    private final int WIDTH = 40;
    private final int HEIGHT = 10;

    private char[][] chars  = new char[HEIGHT][WIDTH];
    private byte[][] fg     = new byte[HEIGHT][WIDTH];
    private byte[][] bg     = new byte[HEIGHT][WIDTH];
    private boolean[][] dirty = new boolean[HEIGHT][WIDTH];

    private int frameCount = 0;
    private int pos1 = 0;
    private int pos2 = 0;
    private int pos3 = 0;

    private TelaASCII screen;
    private JFrame frame;

    public void start() {

        // inicializa matrizes
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                chars[y][x] = ' ';
                fg[y][x] = 15;
                bg[y][x] = 0;
                dirty[y][x] = true;
            }
        }

        // cria tela
        screen = new TelaASCII();
        screen.initScreen(WIDTH, HEIGHT, 3, 5);

        // janela
        frame = new JFrame("ASCII Loop Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(screen);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // LOOP — 30 FPS
        Timer timer = new Timer(33, e -> update());
        timer.start();
    }

    private void update() {

        frameCount++;

        // limpa
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++) {
                chars[y][x] = ' ';
                dirty[y][x] = true;
            }

        // MOVIMENTO 1 — todo frame
        pos1 = (pos1 + 1) % WIDTH;
        chars[2][pos1] = '>';

        // MOVIMENTO 2 — a cada 10 frames
        if (frameCount % 10 == 0) {
            pos2 = (pos2 + 1) % WIDTH;
        }
        chars[4][pos2] = '*';

        // MOVIMENTO 3 — a cada 30 frames
        if (frameCount % 30 == 0) {
            pos3 = (pos3 + 1) % WIDTH;
        }
        chars[6][pos3] = '@';

        // cores
        fg[2][pos1] = 14;
        fg[4][pos2] = 11;
        fg[6][pos3] = 12;

        // render
        screen.render(chars, fg, bg, dirty);
    }
}
