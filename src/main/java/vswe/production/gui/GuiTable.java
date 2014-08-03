package vswe.production.gui;


import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import vswe.production.gui.container.ContainerTable;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;
import vswe.production.tileentity.data.DataType;


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
        drawSlots();
        drawPower(mX, mY);
        table.getSelectedPage().draw(this, mX, mY);

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
                table.updateServer(DataType.PAGE);
                break;
            }
        }
    }


    private static final int SLOT_SRC_X = 42;
    private static final int SLOT_SRC_Y = 0;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_OFFSET = -1;
    private static final int SLOT_BIG_SIZE = 26;
    private static final int SLOT_BIG_OFFSET = SLOT_OFFSET - (SLOT_BIG_SIZE - SLOT_SIZE) / 2;
    private void drawSlots() {
        prepare();
        for (SlotBase slot : table.getSlots()) {
            boolean visible = slot.isVisible();
            slot.update(visible);
            if (visible) {
                boolean isBig = slot.isBig();
                int srcY = isBig ? SLOT_SIZE + SLOT_SRC_Y : SLOT_SRC_Y;
                int size = isBig ? SLOT_BIG_SIZE : SLOT_SIZE;
                int offset = isBig ? SLOT_BIG_OFFSET : SLOT_OFFSET;

                drawRect(slot.getX() + offset, slot.getY() + offset, SLOT_SRC_X + slot.getTextureIndex() * size, srcY, size, size);
            }
        }
    }


    private static final int POWER_X = 225;
    private static final int POWER_Y = 173;
    private static final int POWER_WIDTH = 18;
    private static final int POWER_HEIGHT = 50;
    private static final int POWER_INNER_WIDTH = 16;
    private static final int POWER_INNER_HEIGHT = 48;

    private static final int POWER_INNER_SRC_X = 0;
    private static final int POWER_INNER_SRC_Y = 64;
    private static final int POWER_SRC_X = 32;
    private static final int POWER_SRC_Y = 62;

    private static final int POWER_INNER_OFFSET_X = (POWER_WIDTH - POWER_INNER_WIDTH) / 2;
    private static final int POWER_INNER_OFFSET_Y = (POWER_HEIGHT - POWER_INNER_HEIGHT) / 2;

    private void drawPower(int mX, int mY) {
        drawRect(POWER_X + POWER_INNER_OFFSET_X, POWER_Y + POWER_INNER_OFFSET_Y, POWER_INNER_SRC_X + POWER_INNER_WIDTH, POWER_INNER_SRC_Y, POWER_INNER_WIDTH, POWER_INNER_HEIGHT);

        int height = POWER_INNER_HEIGHT * table.getPower() / TileEntityTable.MAX_POWER;
        int offset = POWER_INNER_HEIGHT - height;
        drawRect(POWER_X + POWER_INNER_OFFSET_X, POWER_Y + POWER_INNER_OFFSET_Y + offset, POWER_INNER_SRC_X, POWER_INNER_SRC_Y + offset, POWER_INNER_WIDTH, height);
        drawRect(POWER_X, POWER_Y + POWER_INNER_OFFSET_Y + offset - 1, POWER_SRC_X, POWER_SRC_Y - 1, POWER_WIDTH, 1);

        int srcX = POWER_SRC_X;
        if (inBounds(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT, mX, mY)) {
            srcX += POWER_WIDTH;
        }
        drawRect(POWER_X, POWER_Y, srcX, POWER_SRC_Y, POWER_WIDTH, POWER_HEIGHT);
    }

}
