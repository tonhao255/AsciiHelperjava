package engine.asset;

public class FrameAsset {
    public char[][] charMatrix;
    public byte[][] fgMatrix;
    public byte[][] bgMatrix;
    public boolean[][] hitMatrix;
    public int duration;

    public FrameAsset(char[][] charMatrix, byte[][] fgMatrix, byte[][] bgMatrix, boolean[][] hitMatrix, int duration) {
        this.charMatrix = charMatrix;
        this.fgMatrix = fgMatrix;
        this.bgMatrix = bgMatrix;
        this.hitMatrix = hitMatrix;
        this.duration = duration;
    }
}
