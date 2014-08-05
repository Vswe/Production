package vswe.production.page.setting;

public class Side {
    private int x;
    private int y;
    private Direction direction;
    private Setting setting;
    private Transfer input;
    private Transfer output;

    public Side(Setting setting, Direction direction, int x, int y) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.setting = setting;

        input = new Transfer(true);
        output = new Transfer(false);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOutputEnabled() {
        return output.isEnabled();
    }

    public boolean isInputEnabled() {
        return input.isEnabled();
    }

    public void setOutputEnabled(boolean value) {
        output.setEnabled(value);
    }

    public void setInputEnabled(boolean value) {
        input.setEnabled(value);
    }

    public Direction getDirection() {
        return direction;
    }

    public Setting getSetting() {
        return setting;
    }

    public Transfer getOutput() {
        return output;
    }

    public Transfer getInput() {
        return input;
    }
}
