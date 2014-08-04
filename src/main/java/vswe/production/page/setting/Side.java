package vswe.production.page.setting;

public class Side {
    private int x;
    private int y;
    private Direction direction;
    private boolean input;
    private boolean output;

    public Side(Direction direction, int x, int y) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOutputEnabled() {
        return output;
    }

    public boolean isInputEnabled() {
        return input;
    }

    public void setOutputEnabled(boolean value) {
        output = value;
    }

    public void setInputEnabled(boolean value) {
        input = value;
    }

    public Direction getDirection() {
        return direction;
    }
}
