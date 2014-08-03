package vswe.production.gui.container;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import vswe.production.tileentity.TileEntityTable;

public class ContainerTable extends Container {
    private TileEntityTable table;

    public ContainerTable(TileEntityTable table, EntityPlayer player) {
        this.table = table;

        InventoryPlayer inventory = player.inventory;
        for (int y = 0; y < NORMAL_ROWS; y++) {
            for (int x = 0; x < SLOTS_PER_ROW; x++) {
                addSlotToContainer(new Slot(inventory, x + y * SLOTS_PER_ROW + SLOTS_PER_ROW, PLAYER_X + x * SLOT_SIZE, y * SLOT_SIZE + PLAYER_Y));
            }
        }

        for (int x = 0; x < SLOTS_PER_ROW; x++) {
            addSlotToContainer(new Slot(inventory, x, PLAYER_X + x * SLOT_SIZE, PLAYER_HOT_BAR_Y));
        }
    }

    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = 9;
    private static final int NORMAL_ROWS = 3;
    private static final int PLAYER_X = 48;
    private static final int PLAYER_Y = 174;
    private static final int PLAYER_HOT_BAR_Y = 232;

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return table.isUseableByPlayer(player);
    }
}
