import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class wow extends JPanel {

    private static int height = 26, width = 76;

    private final char[][] charMatrix = new char[height][width];
    private final byte[][] fgMatrix  = new byte[height][width];
    private final byte[][] bgMatrix  = new byte[height][width];
    private final boolean[][] dirty = new boolean[height][width];

    private Font cachedFont = new Font("Consolas", Font.PLAIN, 100);
    private int cachedFontSize = -1;
    private int tileHeightPixel = 5;
    private int tileWidthPixel = 3;

    static final Color[] PALETTE = {
        new Color(0,0,0),       new Color(0,0,170),
        new Color(0,170,0),     new Color(0,170,170),
        new Color(170,0,0),     new Color(170,0,170),
        new Color(170,85,0),    new Color(170,170,170),
        new Color(85,85,85),    new Color(85,85,255),
        new Color(85,255,85),   new Color(85,255,255),
        new Color(255,85,85),   new Color(255,85,255),
        new Color(255,255,85),  new Color(255,255,255)
    };

    private Dimension cachedSize = new Dimension(-1, -1);
    private volatile boolean forceFullRedraw = true; 

    private void populateDemo() {
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                setTile(x, y, '$', (byte)2, (byte)1);
            }
        }
        setTile(width / 2, height / 2, '@', (byte)14, (byte)4);
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

    public wow() {
        populateDemo();
        setPreferredSize(new Dimension(width * 12, height * 20));
        setBackground(PALETTE[0]);
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

        int scaleW = Math.max(1 , panelW / basePanelW);
        int scaleH = Math.max(1 , panelH / basePanelH);

        int scale = Math.min(scaleW, scaleH);

        int tileWidth = tileWidthPixel * scale, tileHeight = tileHeightPixel * scale;

        int fontSize = tileHeight;

        int offsetX = (panelW - basePanelW * scale) / 2;
        int offsetY = (panelH - basePanelH * scale) / 2;

        if (fontSize != cachedFontSize) {
            cachedFont = new Font("Consolas", Font.PLAIN, fontSize);
            cachedFontSize = fontSize;
            markAllDirty();
        }

        g2.setFont(cachedFont);
        FontMetrics fm = g2.getFontMetrics();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (!dirty[y][x]) continue;

                int px = x * tileWidth + offsetX;
                int py = y * tileHeight + offsetY;

                Color bg = PALETTE[ bgMatrix[y][x] & 0x0F ];
                g2.setColor(bg);
                g2.fillRect(px, py, tileWidth, tileHeight);

                Color fg = PALETTE[ fgMatrix[y][x] & 0x0F ];
                g2.setColor(fg);

                char c = charMatrix[y][x];
                int textY = py + (tileHeight - fm.getDescent());
                g2.drawString(String.valueOf(c), px, textY);

                dirty[y][x] = false;
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        markAllDirty();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ASCII Graphic Screen");
        wow screen = new wow();

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
