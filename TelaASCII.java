// \.
// .'¨¨¨'.
///  __   \    `;
// .' .'  /,   ,¨'-
///'.___.'__'__'._____('________ ____
//
// region TESTE

// endregion
// ._____ ____._______
//(  .       (
// '-'

// \.
// .'¨¨¨'.
///  __   \    `;
// .' .'  /,   ,¨'-
///'.___.'__'__'._____('________ ____
//
// region IMPORTS
import javax.swing.*;
import java.awt.*;
// endregion
// ._____ ____._______
//(  .       (
// '-'

// \.
// .'¨¨¨'.
///  __   \    `;
// .' .'  /,   ,¨'-
///'.___.'__'__'._____('________ ____
//
// region CLASS
public class TelaASCII extends JPanel {
    
    // \.
    // .'¨¨¨'.
    ///  __   \    `;
    // .' .'  /,   ,¨'-
    ///'.___.'__'__'._____('________ ____
    //
    // region SETUP
    // not sure but I think 25 x 80 is Stone Story RPG's size
    private static int height = 25, width = 80;

    // initializing the matrices
    private final char[][] charMatrix = new char[height][width];
    private final byte[][] fgMatrix  = new byte[height][width];
    private final byte[][] bgMatrix  = new byte[height][width];
    private final boolean[][] dirty = new boolean[height][width];

    // to not render the same sized font when unneeded
    private Font cachedFont = null;
    private int cachedFontSize = -1;

    // default cmd colors
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
        new Color(85,255,85),   // A LIGHT GREEN
        new Color(85,255,255),  // B LIGHT CYAN
        new Color(255,85,85),   // C LIGHT RED
        new Color(255,85,255),  // D LIGHT MAGENTA
        new Color(255,255,85),  // E YELLOW
        new Color(255,255,255)  // F WHITE
    };
    // endregion
    // ._____ ____._______
    //(  .       (
    // '-'

    // testing purposes, fills the screen. later will be replaced
    private void populateDemo() {
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                setTile(x, y, '$', (byte)2, (byte)1);
            }
        }
        setTile(width / 2, height / 2, '#', (byte)14, (byte)4);
    }

    // helps set everything right, then repaints
    public void setTile(int x, int y, char c, byte fg, byte bg) {
        charMatrix[y][x] = c;
        fgMatrix[y][x] = fg;
        bgMatrix[y][x] = bg;
        dirty[y][x] = true;
        repaint();
    }

    // not sure, but calls population and then sets up the frame
    public TelaASCII() {
        populateDemo(); 
        setPreferredSize(new Dimension(width * 10, height * 20));
        setBackground(PALETTE[0]);
    }

    // overrides the paint function so that it paints the screen
    @Override
    protected void paintComponent(Graphics g) {
        // conserves the functions of the old function
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        // maths
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

        // ignores if the size does not change
        if (fontSize != cachedFontSize) {
            cachedFont = new Font("Consolas", Font.PLAIN, fontSize);
            cachedFontSize = fontSize;
        }

        g2.setFont(cachedFont);

        FontMetrics fm = g2.getFontMetrics();

        // actually paints the characters and boxes
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // if cell dirty, jump to next
                if (!dirty[y][x]) continue;

                // position char while centering
                int px = (int) (x * tileWidth + offsetX);
                int py = (int) (y * tileHeight + offsetY);

                // fills bg
                Color bg = PALETTE[ bgMatrix[y][x] & 0x0F ];
                g2.setColor(bg);
                g2.fillRect(px, py, (int)tileWidth, (int)tileHeight);

                // defines fg color
                Color fg = PALETTE[ fgMatrix[y][x] & 0x0F ];
                g2.setColor(fg);

                // gets char
                char c = charMatrix[y][x];

                // paints char
                int textY = (int) (py + (tileHeight - fm.getDescent()));
                g2.drawString(String.valueOf(c), px, textY);

                // cleans dirty
                dirty[y][x] = false;
            }
        }
    }

    // overrides the invalidation function (whatever that is)
    @Override
    public void invalidate() {
        super.invalidate();
        // marks everything as dirty once you resize or move the screen
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                dirty[y][x] = true;
    }

    // main, creates and sets up the GUI
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
// endregion
// ._____ ____._______
//(  .       (
// '-'