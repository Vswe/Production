package vswe.production.gui.container;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.gui.container.slot.SlotPlayer;
import vswe.production.tileentity.TileEntityTable;

public class ContainerTable extends Container {
    private TileEntityTable table;

    public ContainerTable(TileEntityTable table, EntityPlayer player) {
        this.table = table;

        for (SlotBase slot : table.getSlots()) {
            addSlotToContainer(slot);
        }

        InventoryPlayer inventory = player.inventory;
        for (int y = 0; y < NORMAL_ROWS; y++) {
            for (int x = 0; x < SLOTS_PER_ROW; x++) {
                addSlotToContainer(new SlotPlayer(inventory, table, x + y * SLOTS_PER_ROW + SLOTS_PER_ROW, PLAYER_X + x * SLOT_SIZE, y * SLOT_SIZE + PLAYER_Y));
            }
        }

        for (int x = 0; x < SLOTS_PER_ROW; x++) {
            addSlotToContainer(new SlotPlayer(inventory, table, x, PLAYER_X + x * SLOT_SIZE, PLAYER_HOT_BAR_Y));
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


    @Override
    public void addCraftingToCrafters(ICrafting player) {
        super.addCraftingToCrafters(player);

        if (player instanceof EntityPlayer) {
            table.addPlayer((EntityPlayer)player);
        }
    }


    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        ItemStack itemstack = null;
        Slot slot = (SlotBase)inventorySlots.get(i);
        if(slot != null && slot.getHasStack() && (!(slot instanceof SlotBase) || ((SlotBase)slot).isVisible())) {
            ItemStack slotItem = slot.getStack();
            itemstack = slotItem.copy();
            if(i < table.getSizeInventory()) {
                if(!mergeItemStack(slotItem, table.getSizeInventory() + 28, table.getSizeInventory() + 36, false)) {
                    if(!mergeItemStack(slotItem, table.getSizeInventory(), table.getSizeInventory() + 28, false)) {
                        return null;
                    }
                }
            }else if(!mergeItemStack(slotItem, 0, table.getSizeInventory(), false)){
                return null;
            }
            if(slotItem.stackSize == 0){
                slot.putStack(null);
            }else{
                slot.onSlotChanged();
            }
            if(slotItem.stackSize != itemstack.stackSize){
                slot.onPickupFromSlot(player,slotItem);
            }else{
                return null;
            }
        }
        return itemstack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack item, int start, int end, boolean invert){
        boolean result = false;
        int id = start;

        if (invert) {
            id = end - 1;
        }

        Slot slot;
        ItemStack slotItem;

        if (item.isStackable()) {
            while (item.stackSize > 0 && (!invert && id < end || invert && id >= start)) {
                slot = (SlotBase)this.inventorySlots.get(id);
                if (!(slot instanceof SlotBase) || ((SlotBase)slot).isVisible()) {
                    slotItem = slot.getStack();

                    if (slotItem != null && slotItem.stackSize > 0 && slotItem.getItem() == item.getItem() && (!item.getHasSubtypes() || item.getItemDamage() == slotItem.getItemDamage()) && ItemStack.areItemStackTagsEqual(item, slotItem)) {
                        int size = slotItem.stackSize + item.stackSize;

                        int maxLimit = Math.min(item.getMaxStackSize(), slot.getSlotStackLimit());
                        if (size <= maxLimit) {
                            item.stackSize = 0;
                            slotItem.stackSize = size;
                            slot.onSlotChanged();
                            result = true;
                        }else if (slotItem.stackSize < maxLimit) {
                            item.stackSize -= maxLimit - slotItem.stackSize;
                            slotItem.stackSize = maxLimit;
                            slot.onSlotChanged();
                            result = true;
                        }
                    }
                }

                if (invert) {
                    --id;
                }else{
                    ++id;
                }
            }
        }

        if (item.stackSize > 0){
            if (invert){
                id = end - 1;
            }else{
                id = start;
            }

            while (!invert && id < end || invert && id >= start){
                slot = (SlotBase)this.inventorySlots.get(id);
                slotItem = slot.getStack();

                if (!(slot instanceof SlotBase) || ((SlotBase)slot).isVisible()) {
                    if (slotItem == null && slot.isItemValid(item)) {
                        int stackSize = Math.min(slot.getSlotStackLimit(), item.stackSize);
                        ItemStack newItem = item.copy();
                        newItem.stackSize = stackSize;
                        item.stackSize -= stackSize;
                        slot.putStack(newItem);
                        slot.onSlotChanged();

                        result = item.stackSize == 0;
                        break;
                    }
                }

                if (invert){
                    --id;
                }else{
                    ++id;
                }
            }
        }

        return result;
    }

    public TileEntityTable getTable() {
        return table;
    }
}
