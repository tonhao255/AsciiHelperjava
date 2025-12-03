import javax.swing.*;
import java.awt.*;

public class TelaASCII extends JPanel {
    
    // not sure but I think 25 x 80 is Stone Story RPG's size
    private static int height = 25, width = 80;

    private final char[][] charMatrix = new char[height][width];
    private final byte[][] fgMatrix  = new byte[height][width];
    private final byte[][] bgMatrix  = new byte[height][width];
    private final boolean[][] dirty = new boolean[height][width];

    private Font cachedFont = null;
    private int cachedFontSize = -1;

    // region CORES

    static final Color[] PALETTE = {
        new Color(0,0,0),       // 0 BLACK
        new Color(0,0,170),     // 1 BLUE
        new Color(0,170,0),     // 2 GREEN
        new Color(0,170,170),   // 3 CYAN
        new Color(170,0,0),     // 4 RED
        new Color(170,0,170),   // 5 MAGENTA
        new Color(170,85,0),    // 6 BROWN
        new Color(170,170,170), // 7 LIGHT GRAY

        new Color(85,85,85),    // 8 DARK GRAY
        new Color(85,85,255),   // 9 LIGHT BLUE
        new Color(85,255,85),   // A LIGHT GREEN
        new Color(85,255,255),  // B LIGHT CYAN
        new Color(255,85,85),   // C LIGHT RED
        new Color(255,85,255),  // D LIGHT MAGENTA
        new Color(255,255,85),  // E YELLOW
        new Color(255,255,255)  // F WHITE
    };

    // endregion

    private void populateDemo() {
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                setTile(x, y, '$', (byte)2, (byte)1);
            }
        }
        setTile(width / 2, height / 2, '#', (byte)14, (byte)4);
    }

    public void setTile(int x, int y, char c, byte fg, byte bg) {
        charMatrix[y][x] = c;
        fgMatrix[y][x] = fg;
        bgMatrix[y][x] = bg;
        dirty[y][x] = true;
        repaint();
    }

    public TelaASCII() {
        populateDemo(); 
        setPreferredSize(new Dimension(width * 10, height * 20));
        setBackground(Color.BLACK);        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        int panelW = getWidth();
        int panelH = getHeight();

        int tWidth = (int)(panelW) / (int)(width);
        int tHeight = (int)(panelH) / (int)(height * 2);
        int fontSize = (int)(Math.max(1, Math.min(tWidth, tHeight))) * 2;

        int tileWidth = fontSize / 2;
        int tileHeight = fontSize;

        int displayW = width * tileWidth;
        int displayH = height * tileHeight;

        int offsetX = (panelW - displayW) / 2;
        int offsetY = (panelH - displayH) / 2;

        // int tw = (int) Math.floor(panelW / width);
        // int th = (int) Math.floor(panelH / height); 

        // float tileWidth  = Math.min(tw, th / 2f);
        // float tileHeight = tileWidth * 2f;

        // int fontSize = Math.max(1, (int)Math.floor(tileHeight));

        if (fontSize != cachedFontSize) {
            cachedFont = new Font("Consolas", Font.PLAIN, fontSize);
            cachedFontSize = fontSize;
        }

        g2.setFont(cachedFont);

        FontMetrics fm = g2.getFontMetrics();


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (!dirty[y][x]) continue;

                int px = (int) (x * tileWidth + offsetX);
                int py = (int) (y * tileHeight + offsetY);

                Color bg = PALETTE[ bgMatrix[y][x] & 0x0F ];
                g2.setColor(bg);
                g2.fillRect(px, py, (int)tileWidth, (int)tileHeight);

                Color fg = PALETTE[ fgMatrix[y][x] & 0x0F ];
                g2.setColor(fg);

                char c = charMatrix[y][x];

                int textY = (int) (py + (tileHeight - fm.getDescent()));
                g2.drawString(String.valueOf(c), px, textY);

                // limpa o dirty
                dirty[y][x] = false;
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        // marca tudo como dirty
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                dirty[y][x] = true;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tela ASCII Gráfica");
        TelaASCII tela = new TelaASCII();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(tela);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
