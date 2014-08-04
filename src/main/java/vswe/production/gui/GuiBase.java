package vswe.production.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
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
        GL11.glDisable(GL11.GL_LIGHTING);
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

    public void drawItem(ItemStack item, int x, int y) {
        itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), item, x, y);
    }

    private static final ResourceLocation BLOCK_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");


    public void drawBlockIcon(IIcon icon, int x, int y) {
        mc.getTextureManager().bindTexture(BLOCK_TEXTURE);
        drawIcon(icon, x, y);
    }

    public void drawIcon(IIcon icon, int x, int y) {
        drawIcon(icon, x, y, 1F, 1F, 0F, 0F);
    }

    public void drawIcon(IIcon icon, int targetX, int targetY, float sizeX, float sizeY, float offsetX, float offsetY) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        float x = icon.getMinU() + offsetX * (icon.getMaxU() - icon.getMinU());
        float y = icon.getMinV() + offsetY * (icon.getMaxV() - icon.getMinV());
        float width = (icon.getMaxU() - icon.getMinU()) * sizeX;
        float height = (icon.getMaxV() - icon.getMinV()) * sizeY;

        tessellator.addVertexWithUV(targetX, 			    targetY + 16 * sizeY, 	this.zLevel, 	x, 			    y + height);
        tessellator.addVertexWithUV(targetX + 16 * sizeX, 	targetY + 16 * sizeY, 	this.zLevel, 	x + width, 		y + height);
        tessellator.addVertexWithUV(targetX + 16 * sizeX, 	targetY, 			    this.zLevel, 	x + width, 		y);
        tessellator.addVertexWithUV(targetX, 			    targetY, 			    this.zLevel, 	x, 			    y);
        tessellator.draw();
    }
}
