package vswe.production.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class GuiBase extends GuiContainer {
    public GuiBase(Container container) {
        super(container);
    }

    private Slot selectedSlot;
    public boolean shiftMoveRendered;

    @Override
    public void drawScreen(int x, int y, float f) {
        selectedSlot = null;
        shiftMoveRendered = false;
        for (Object obj : inventorySlots.inventorySlots) {
            Slot slot = (Slot)obj;
            if (func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, x, y)) {
                selectedSlot = slot;
                break;
            }
        }

        super.drawScreen(x, y, f);
    }

    public Slot getSelectedSlot() {
        return selectedSlot;
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
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_LIGHTING);
        itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), item, x, y);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
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

    private static final int ITEM_SIZE = 18;
    private static final int ITEM_SRC_X = 68;
    private static final int ITEM_SRC_Y = 62;
    private static final int ITEM_ITEM_OFFSET = 1;

    public void drawItemWithBackground(ItemStack item, int x, int y, int mX, int mY) {
        boolean hover = inBounds(x, y, ITEM_SIZE, ITEM_SIZE, mX, mY);
        int textureIndexX = hover ? 1 : 0;
        int textureIndexY = item != null ? 1 : 0;

        prepare();
        drawRect(x, y, ITEM_SRC_X + textureIndexX * ITEM_SIZE, ITEM_SRC_Y + textureIndexY * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
        drawItem(item, x + ITEM_ITEM_OFFSET, y + ITEM_ITEM_OFFSET);

        if (hover) {
            drawMouseOver(getItemDescription(item));
        }
    }

    public int getStringWidth(String str) {
        return fontRendererObj.getStringWidth(str);
    }

    public void drawCursor(int x, int y, int z, float size, int color) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glScalef(size, size, 0);
        GL11.glTranslatef(-x, -y, 0);
        Gui.drawRect(x, y + 1, x + 1, y + 10, color);
        GL11.glPopMatrix();
    }

    public void drawMouseOver(String str) {
        if (str == null) {
            return;
        }

        List<String> lst = new ArrayList<String>();
        Collections.addAll(lst, str.split("\n"));
        drawMouseOver(lst);
    }

    public void drawMouseOver(List<String> str) {
        this.mouseOver = str;
    }

    private List<String> mouseOver;
    public void clearMouseOverCache() {
        mouseOver = null;
    }

    public void drawCachedMouseOver(int x, int y) {
        if (mouseOver == null || mouseOver.isEmpty()) {
            return;
        }


        int w = 0;

        for (String line : mouseOver) {
            int l = fontRendererObj.getStringWidth(line);

            if (l > w) {
                w = l;
            }
        }

        x += 12;
        y -= 12;
        int h = 8;

        if (mouseOver.size() > 1){
            h += 2 + (mouseOver.size() - 1) * 10;
        }

        if (x + w > this.width) {
            x -= 28 + w;
        }

        if (y + h + 6 > this.height) {
            y = this.height - h - 6;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 300);
        this.zLevel = 300.0F;
        int bg = -267386864;
        this.drawGradientRect(x - 3, y - 4, x + w + 3, y - 3, bg, bg);
        this.drawGradientRect(x - 3, y + h + 3, x + w + 3, y + h + 4, bg, bg);
        this.drawGradientRect(x - 3, y - 3, x + w + 3, y + h + 3, bg, bg);
        this.drawGradientRect(x - 4, y - 3, x - 3, y + h + 3, bg, bg);
        this.drawGradientRect(x + w + 3, y - 3, x + w + 4, y + h + 3, bg, bg);
        int border1 = 1347420415;
        int border2 = (border1 & 16711422) >> 1 | border1 & -16777216;
        this.drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + h + 3 - 1, border1, border2);
        this.drawGradientRect(x + w + 2, y - 3 + 1, x + w + 3, y + h + 3 - 1, border1, border2);
        this.drawGradientRect(x - 3, y - 3, x + w + 3, y - 3 + 1, border1, border1);
        this.drawGradientRect(x - 3, y + h + 2, x + w + 3, y + h + 3, border2, border2);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (int i = 0; i < mouseOver.size(); i++) {
            String line = mouseOver.get(i);
            fontRendererObj.drawStringWithShadow(line, x, y, -1);

            if (i == 0) {
                y += 2;
            }

            y += 10;
        }

        this.zLevel = 0.0F;
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    public String getItemName(ItemStack item) {
        if (item == null || item.getItem() == null) {
            return null;
        }

        try {
            //noinspection unchecked
            return item.getDisplayName();
        }catch (Throwable ignored) {
            return null;
        }
    }

    public List<String> getItemDescription(ItemStack item) {
        if (item == null || item.getItem() == null) {
            return null;
        }

        try {
            //noinspection unchecked
            return item.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        }catch (Throwable ignored) {
            return null;
        }
    }
}
