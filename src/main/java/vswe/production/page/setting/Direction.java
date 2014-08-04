package vswe.production.page.setting;


public enum Direction {
    DOWN(1, 2),
    UP(1, 0),
    FRONT(1, 1),
    BACK(3, 1),
    LEFT(2, 1),
    RIGHT(0, 1);

    private int interfaceX;
    private int interfaceY;

    Direction(int interfaceX, int interfaceY) {
        this.interfaceX = interfaceX;
        this.interfaceY = interfaceY;
    }

    public int getInterfaceX() {
        return interfaceX;
    }

    public int getInterfaceY() {
        return interfaceY;
    }
}
