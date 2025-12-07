package engine.asset;

public class LayerAsset {
    public ObjectAsset[] objects;
    public int zIndex;
    public SpriteAsset bgSprite;
    public int offsetX;
    public int offsetY;

    public LayerAsset(ObjectAsset[] objects, int zIndex, SpriteAsset bgSprite, int offsetX, int offsetY) {
        this.objects = objects;
        this.zIndex = zIndex;
        this.bgSprite = bgSprite;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
