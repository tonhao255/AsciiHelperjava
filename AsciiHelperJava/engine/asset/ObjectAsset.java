package engine.asset;

public class ObjectAsset {
    public int x;
    public int y;
    public SpriteAsset sprite;
    public int frameIndex;
    public boolean play;
    public boolean loop;
    public int frameCount;
    
    public ObjectAsset(int x, int y, SpriteAsset sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.frameIndex = 0;
        this.play = sprite.autoplaysDefault;
        this.loop = sprite.loopsDefault;
    }

    public void Script() { // That's what the user would override or manipulate

    }

    public void Step() {
        if (this.play) {
            this.frameCount += 1;
            if (this.frameCount > this.sprite.frames[frameIndex].duration) {
                this.frameCount = 0;
                frameIndex += 1;
                if (frameIndex > this.sprite.frames.length) {
                    frameIndex = 0;
                    if (!loop){
                        this.StopAnim();
                    }
                }
            }
        }
    }

    public void StopAnim() {
        this.play = false;
        this.frameCount = 0;
    }

    public void StartAnim() {
        this.play = true;
    }

    public void Main() {
        this.Script();
        this.Step();
    }
}
