package vswe.production.gui;


import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import vswe.production.gui.container.ContainerTable;
import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;


public class GuiTable extends GuiBase {

    private TileEntityTable table;


    public GuiTable(TileEntityTable table, EntityPlayer player) {
        super(new ContainerTable(table, player));
        xSize = 256;
        ySize = 256;

        this.table = table;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mX, int mY) {
        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft, guiTop, 0);
        mX -= guiLeft;
        mY -= guiTop;

        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);

        drawPageHeaders(mX, mY);

        GL11.glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mX, int mY, int button) {
        super.mouseClicked(mX, mY, button);
        mX -= guiLeft;
        mY -= guiTop;

        clickPageHeader(mX, mY);
    }

    private static final int HEADER_SRC_X = 0;
    private static final int HEADER_SRC_Y = 0;
    private static final int HEADER_FULL_WIDTH = 42;
    private static final int HEADER_WIDTH = 38;
    private static final int HEADER_HEIGHT = 17;
    private static final int HEADER_X = 3;
    private static final int HEADER_Y = 173;
    private static final int HEADER_TEXT_Y = 7;

    private void drawPageHeaders(int mX, int mY) {
        for (int i = 0; i < table.getPages().size(); i++) {
            Page page = table.getPages().get(i);

            boolean selected = page.equals(table.getSelectedPage());
            int srcY = selected ? HEADER_SRC_Y + HEADER_HEIGHT : HEADER_SRC_Y;
            int y = HEADER_Y + HEADER_HEIGHT * i;


            boolean hover = inBounds(HEADER_X, y, HEADER_FULL_WIDTH, HEADER_HEIGHT, mX, mY);
            int width = hover ? HEADER_FULL_WIDTH : HEADER_WIDTH;
            int offset = HEADER_FULL_WIDTH - width;

            prepare();
            drawRect(HEADER_X, y, HEADER_SRC_X + offset, srcY, width, HEADER_HEIGHT);

            int invertedOffset = (HEADER_FULL_WIDTH - HEADER_WIDTH) - offset;
            drawCenteredString(page.getName(), HEADER_X + invertedOffset, y + HEADER_TEXT_Y, HEADER_WIDTH, 0.7F, 0x404040);
        }
    }

    private void clickPageHeader(int mX, int mY) {
        for (int i = 0; i < table.getPages().size(); i++) {
            Page page = table.getPages().get(i);
            int y = HEADER_Y + HEADER_HEIGHT * i;
            if (inBounds(HEADER_X, y, HEADER_FULL_WIDTH, HEADER_HEIGHT, mX, mY)) {
                table.setSelectedPage(page);
                break;
            }
        }
    }


}
