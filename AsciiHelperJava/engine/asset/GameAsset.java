package engine.asset;

public class GameAsset {
    public int width;
    public int height;
    public int fps;
    public RoomAsset[] rooms;
    public int startRoom;

    public GameAsset(int width, int height, int fps, RoomAsset[] rooms, int startRoom) {
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.rooms = rooms;
        this.startRoom = startRoom;
    }
}
