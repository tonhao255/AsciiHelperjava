package engine.asset;

public class SpriteAsset {
    public FrameAsset[] frames;
    public boolean loopsDefault;
    public boolean autoplaysDefault;
    public int offsetX;
    public int offsetY;

    public SpriteAsset(FrameAsset[] frames, boolean loopsDefault, boolean autoplaysDefault, int offsetX, int offsetY){
        this.frames = frames;
        this.loopsDefault = loopsDefault;
        this.autoplaysDefault = autoplaysDefault;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }    
}
