import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Test extends JFrame{

    private BufferedImage baseImage;
    private BufferedImage scaledImage;

    private int lastW = -1;
    private int lastH = -1;
    private int targetW;
    private int targetH;
    
    public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight){
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, 2);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    public Test () {

        ImageIcon temp = new ImageIcon("assets\\pixilart-drawing.png");
        Image rankS = temp.getImage();
        baseImage = new BufferedImage(64, 128, 2);
        Graphics2D g2 = baseImage.createGraphics();
        g2.drawImage(rankS, 0, 0, this);
        g2.drawImage(rankS, 0, 64, this);
        g2.dispose();
        int baseW = baseImage.getWidth();
        int baseH = baseImage.getHeight();

        JPanel panel = new JPanel(){         
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);

                int width = getWidth();
                int height = getHeight();

                if (width != lastW || height != lastH){
                    if (baseH * width < baseW * height) {
                        targetW = width;
                        targetH = (int)Math.floor(baseH * width / baseW);
                    }else{
                        targetH = height;
                        targetW = (int)Math.floor(baseW * height / baseH);
                    }
                    targetW = Math.max(baseW, targetW);
                    targetH = Math.max(baseH, targetH);
                }         
              

                scaledImage = resizeImage(baseImage, targetW, targetH);

                if (scaledImage != null){
                    int x = (getWidth() - scaledImage.getWidth(null))/2;
                    int y = (getHeight() - scaledImage.getHeight(null))/2;
                    g.drawImage(scaledImage, x, y, this);
                }
            }
        };

        this.getContentPane().add(panel);

        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        panel.setBackground(new Color(0,0,0));
        setVisible(true);
    }
    
    public static void main(String args[]){
        new Test();
    }
}
