package vswe.production.nei;

import codechicken.nei.recipe.DefaultOverlayHandler;


public class Overlay extends DefaultOverlayHandler {
    private int offsetX;
    private int offsetY;
    Overlay(int x, int y) {
        super(x, y);
        this.offsetX = x;
        this.offsetY = y;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }
}
