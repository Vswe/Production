package vswe.production.gui;


import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vswe.production.gui.container.ContainerTable;
import vswe.production.tileentity.TileEntityTable;

public class GuiTable extends GuiContainer {

    public GuiTable(TileEntityTable table, EntityPlayer player) {
        super(new ContainerTable(table, player));
        xSize = 256;
        ySize = 256;
    }

    private static final ResourceLocation BACKGROUND = new ResourceLocation("production", "textures/gui/background.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft, guiTop, 0);
        x -= guiLeft;
        y -= guiTop;

        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);

        GL11.glPopMatrix();
    }
}
