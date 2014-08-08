package vswe.production.page.setting;


public enum Direction {
    BOTTOM(1, 2),
    TOP(1, 0),
    FRONT(1, 1),
    BACK(3, 1),
    LEFT(2, 1, "Your right side when facing the front"),
    RIGHT(0, 1, "Your left side when facing the front");

    private int interfaceX;
    private int interfaceY;
    private String name;
    private String description;

    Direction(int interfaceX, int interfaceY) {
        this(interfaceX, interfaceY, null);
    }

    Direction(int interfaceX, int interfaceY, String description) {
        this.interfaceX = interfaceX;
        this.interfaceY = interfaceY;

        this.name = toString().charAt(0) + toString().substring(1).toLowerCase() + " Side";
        this.description = description;
    }

    public int getInterfaceX() {
        return interfaceX;
    }

    public int getInterfaceY() {
        return interfaceY;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
