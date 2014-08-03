package vswe.production.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.page.Page;
import vswe.production.page.PageUpgrades;

import java.util.ArrayList;
import java.util.List;


public class TileEntityTable extends TileEntity implements IInventory {
    private List<Page> pages;
    private Page selectedPage;
    private List<SlotBase> slots;
    private ItemStack[] items;

    public TileEntityTable() {
        pages = new ArrayList<Page>();
        pages.add(new Page(this, "Main"));
        pages.add(new Page(this, "Transfer"));
        pages.add(new PageUpgrades(this, "Upgrades"));


        slots = new ArrayList<SlotBase>();
        int id = 0;
        for (Page page : pages) {
            id = page.createSlots(id);
        }
        items = new ItemStack[slots.size()];

        setSelectedPage(pages.get(0));
    }

    public List<SlotBase> getSlots() {
        return slots;
    }

    public List<Page> getPages() {
        return pages;
    }

    public Page getSelectedPage() {
        return selectedPage;
    }

    public void setSelectedPage(Page selectedPage) {
        this.selectedPage = selectedPage;
        for (SlotBase slot : slots) {
            slot.update();
        }
    }

    @Override
    public int getSizeInventory() {
        return items.length;
    }

    @Override
    public ItemStack getStackInSlot(int id) {
        return items[id];
    }

    @Override
    public ItemStack decrStackSize(int id, int count) {
        if (items[id] != null) {
            if (items[id].stackSize <= count) {
                ItemStack itemstack = items[id];
                items[id] = null;
                markDirty();
                return itemstack;
            }

            ItemStack result = items[id].splitStack(count);

            if (items[id].stackSize == 0) {
                items[id] = null;
            }

            markDirty();
            return result;
        }else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int id) {
        ItemStack item = getStackInSlot(id);
        setInventorySlotContents(id, null);
        return item;
    }

    @Override
    public void setInventorySlotContents(int id, ItemStack item) {
        items[id] = item;
    }

    @Override
    public String getInventoryName() {
        return "Production Table";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot(int id, ItemStack item) {
        return true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    public void addSlot(SlotBase slot) {
        slots.add(slot);
    }
}
