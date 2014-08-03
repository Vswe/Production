package vswe.production.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public abstract class GuiBase extends GuiContainer {
    public GuiBase(Container container) {
        super(container);
    }

    protected static final ResourceLocation BACKGROUND = new ResourceLocation("production", "textures/gui/background.png");
    protected static final ResourceLocation ELEMENTS = new ResourceLocation("production", "textures/gui/elements.png");

    public void prepare() {
        mc.getTextureManager().bindTexture(ELEMENTS);
        GL11.glColor4f(1, 1, 1, 1);
    }

    public boolean inBounds(int x, int y, int w, int h, int mX, int mY) {
        return x <= mX && mX < x + w && y <= mY && mY < y + h;
    }

    public void drawRect(int x, int y, int u, int v, int w, int h) {
        drawTexturedModalRect(x, y, u, v, w, h);
    }

    public void drawString(String str, int x, int y, int color) {
        drawString(str, x, y, 1F, color);
    }

    public void drawString(String str, int x, int y, float multiplier, int color) {
        GL11.glPushMatrix();
        GL11.glScalef(multiplier, multiplier, 1F);
        fontRendererObj.drawString(str, (int)(x / multiplier), (int)(y / multiplier), color);

        GL11.glPopMatrix();
    }

    public void drawCenteredString(String str, int x, int y, int width, float multiplier, int color) {
        drawString(str, x + (width - (int)(fontRendererObj.getStringWidth(str) * multiplier)) / 2, y, multiplier, color);
    }
}