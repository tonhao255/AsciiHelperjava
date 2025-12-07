import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TelaASCII_Uh extends JPanel {

    private static int height = 26, width = 76;

    private final char[][] charMatrix = new char[height][width];
    private final byte[][] fgMatrix  = new byte[height][width];
    private final byte[][] bgMatrix  = new byte[height][width];
    private final boolean[][] dirty = new boolean[height][width];

    private Font cachedFont = new Font("Consolas", Font.PLAIN, 100);
    private int cachedFontSize = -1;
    private int tileHeightPixel = 5;
    private int tileWidthPixel = 3;
    private int targetX;
    private int targetY;

    private int backgroundColor = 1;

    static final Color[] PALETTE = {
        new Color(0,0,0),       // 0 BLACK
        new Color(0,0,170),     // 1 BLUE
        new Color(0,170,0),     // 2 GREEN
        new Color(0,170,170),   // 3 CYAN
        new Color(170,0,0),     // 4 RED
        new Color(170,0,170),   // 5 MAGENTA
        new Color(170,0,0),    // 6 BROWN
        new Color(170,170,170), // 7 LIGHT GRAY

        new Color(85,85,85),    // 8 DARK GRAY
        new Color(85,85,255),   // 9 LIGHT BLUE
        new Color(85,255,85),   // 10 LIGHT GREEN
        new Color(85,255,255),  // 11 LIGHT CYAN
        new Color(255,85,85),   // 12 LIGHT RED
        new Color(255,85,255),  // 13 LIGHT MAGENTA
        new Color(255,255,85),  // 14 YELLOW
        new Color(255,255,255)  // 15 WHITE
    };

    private Dimension cachedSize = new Dimension(-1, -1);
    private volatile boolean forceFullRedraw = true; 

    private void populateDemo() {
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                if (x == 0 || y == 0 || x == width -1|| y == height -1){
                    setTile(x, y, '#', (byte)0, (byte)15);
                }else setTile(x, y, '$', (byte)10, (byte)0);
            }
        }
        setTile(width / 2, height / 2, '@', (byte)12, (byte)0);
    }

    public void setTile(int x, int y, char c, byte fg, byte bg) {
        charMatrix[y][x] = c;
        fgMatrix[y][x] = fg;
        bgMatrix[y][x] = bg;
        dirty[y][x] = true;
        repaint();
    }

    public void markAllDirty() {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                dirty[y][x] = true;
    }

    public TelaASCII_Uh() {
        populateDemo();
        setPreferredSize(new Dimension(width * 12, height * 20));
        setBackground(PALETTE[backgroundColor]);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension now = getSize();
        if (forceFullRedraw || !now.equals(cachedSize)) {
            cachedSize = new Dimension(now);
            markAllDirty();
            forceFullRedraw = false;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        int panelW = getWidth();
        int panelH = getHeight();

        int basePanelW = tileWidthPixel * width;
        int basePanelH = tileHeightPixel * height;

        // int scaleW = Math.max(1 , panelW / basePanelW);
        // int scaleH = Math.max(1 , panelH / basePanelH);

        // int scale = Math.min(scaleW, scaleH);

        // int tileWidth = tileWidthPixel * scale, tileHeight = tileHeightPixel * scale;

        // int fontSize = tileHeight;

        if (basePanelH * panelW < basePanelW * panelH){
            targetX = panelW;
            targetY = (int)Math.floor(basePanelH * panelW / basePanelW);
        }else{
            targetY = panelH;
            targetX = (int)Math.floor(basePanelW * panelH / basePanelH);
        }

        targetX = Math.max(basePanelW, targetX);
        targetY = Math.max(basePanelH, targetY);

        // int offsetX = (panelW - basePanelW * scale) / 2;
        // int offsetY = (panelH - basePanelH * scale) / 2;
        int offsetX = (panelW - targetX) / 2;
        int offsetY = (panelH - targetY) / 2;

        int fontSize = (int)Math.round(targetY/(float)height);

        if (fontSize != cachedFontSize) {
            cachedFont = new Font("Consolas", Font.PLAIN, fontSize);
            cachedFontSize = fontSize;
            markAllDirty();
        }

        g2.setFont(cachedFont);
        FontMetrics fm = g2.getFontMetrics();

        int accX = 0;
        int accY = 0;
        int px = 0;
        int py = 0;
        int posX = 0;
        int posY = 0;

        for (int y = 0; y < height; y++) {

            accY += targetY;
            int tileHeight = accY / height;
            accY = accY % height;

            accX = 0;
            px = 0;

            for (int x = 0; x < width; x++) {

                accX += targetX;
                int tileWidth = accX / width;
                accX = accX % width;
                
                if (!dirty[y][x]) {
                    px += tileWidth;
                    continue;
                }

                posX = px + offsetX;
                posY = py + offsetY;
                
                Color bg = PALETTE[ bgMatrix[y][x] & 0x0F ];
                g2.setColor(bg);
                g2.fillRect(posX, posY, tileWidth, tileHeight);

                Color fg = PALETTE[ fgMatrix[y][x] & 0x0F ];
                g2.setColor(fg);

                char c = charMatrix[y][x];
                int textY = posY + (tileHeight - fm.getDescent());
                g2.drawString(String.valueOf(c), posX, textY);                                
                
                // int px = x * tileWidth + offsetX;
                // int py = y * tileHeight + offsetY;

                // Color bg = PALETTE[ bgMatrix[y][x] & 0x0F ];
                // g2.setColor(bg);
                // g2.fillRect(px, py, tileWidth, tileHeight);

                // Color fg = PALETTE[ fgMatrix[y][x] & 0x0F ];
                // g2.setColor(fg);

                // char c = charMatrix[y][x];
                // int textY = py + (tileHeight - fm.getDescent());
                // g2.drawString(String.valueOf(c), px, textY);

                dirty[y][x] = false;
                px += tileWidth;
            }
            py += tileHeight;
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        markAllDirty();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ASCII Graphic Screen");
        TelaASCII_Uh screen = new TelaASCII_Uh();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(screen);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // painel: resize/show
        screen.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                screen.forceFullRedraw = true;
                screen.repaint();
            }
            @Override
            public void componentShown(ComponentEvent e) {
                screen.forceFullRedraw = true;
                screen.repaint();
            }
        });

        // frame: resize/move
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                screen.forceFullRedraw = true;
                screen.repaint();
            }
            @Override
            public void componentMoved(ComponentEvent e) {
                screen.forceFullRedraw = true;
                screen.repaint();
            }
        });

        // window state: maximize/restore
        frame.addWindowStateListener(e -> {
            screen.forceFullRedraw = true;
            screen.repaint();
        });

        // window listener: minimize/restore/activate
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeiconified(WindowEvent e) {
                screen.forceFullRedraw = true;
                screen.repaint();
            }
            @Override
            public void windowActivated(WindowEvent e) {
                screen.forceFullRedraw = true;
                screen.repaint();
            }
        });

        // hierarchy: when panel is reparented or its displayability changes
        screen.addHierarchyListener(e -> {
            long flags = e.getChangeFlags();
            if ((flags & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0
             || (flags & HierarchyEvent.SHOWING_CHANGED) != 0) {
                screen.forceFullRedraw = true;
                screen.repaint();
            }
        });
    }
}
