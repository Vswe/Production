package vswe.production.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import vswe.production.block.BlockTable;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.gui.container.slot.SlotFuel;
import vswe.production.gui.container.slot.SlotValidity;
import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.network.PacketHandler;
import vswe.production.network.PacketId;
import vswe.production.page.Page;
import vswe.production.page.PageMain;
import vswe.production.page.PageTransfer;
import vswe.production.page.PageUpgrades;
import vswe.production.page.setting.Setting;
import vswe.production.tileentity.data.DataType;

import java.util.ArrayList;
import java.util.List;


public class TileEntityTable extends TileEntity implements IInventory, ISidedInventory {
    private List<Page> pages;
    private Page selectedPage;
    private List<SlotBase> slots;
    private ItemStack[] items;

    private int power;
    public static final int MAX_POWER = 20000;
    private SlotFuel fuelSlot;

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public TileEntityTable() {
        pages = new ArrayList<Page>();
        pages.add(new PageMain(this, "Main"));
        pages.add(new PageTransfer(this, "Transfer"));
        pages.add(new PageUpgrades(this, "Upgrades"));


        slots = new ArrayList<SlotBase>();
        int id = 0;
        addSlot(fuelSlot = new SlotFuel(this, null, id++, 226, 226));
        for (Page page : pages) {
            id = page.createSlots(id);
        }
        items = new ItemStack[slots.size()];

        setSelectedPage(pages.get(0));
        reloadTransferSides();
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
    }

    @Override
    public int getSizeInventory() {
        return items.length;
    }

    public PageMain getMainPage() {
        return (PageMain)pages.get(0);
    }

    public PageTransfer getTransferPage() {
        return (PageTransfer)pages.get(1);
    }

    public PageUpgrades getUpgradePage() {
        return (PageUpgrades)pages.get(2);
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

    private List<EntityPlayer> players = new ArrayList<EntityPlayer>();
    public void addPlayer(EntityPlayer player) {
        if (!players.contains(player)) {
            players.add(player);
            sendAllDataToPlayer(player);
        }else{
            System.err.println("Trying to add a listening player: " + player.toString());
        }
    }

    private void sendAllDataToPlayer(EntityPlayer player) {
        DataWriter dw = PacketHandler.getWriter(this, PacketId.ALL);
        for (DataType dataType : DataType.values()) {
            dataType.save(this, dw, -1);
        }
        PacketHandler.sendToPlayer(dw, player);
    }

    public void sendDataToAllPlayer(DataType dataType) {
        sendDataToAllPlayer(dataType, 0);
    }

    public void sendDataToAllPlayer(DataType dataType, int id) {
        sendToAllPlayers(getWriterForType(dataType, id));
    }

    private void sendDataToAllPlayersExcept(DataType dataType, int id, EntityPlayer ignored) {
        sendToAllPlayersExcept(getWriterForType(dataType, id), ignored);
    }

    private void sendToAllPlayers(DataWriter dw) {
        sendToAllPlayersExcept(dw, null);
    }

    private void sendToAllPlayersExcept(DataWriter dw, EntityPlayer ignored) {
        for (EntityPlayer player : players) {
            if (!player.equals(ignored)) {
                PacketHandler.sendToPlayer(dw, player);
            }
        }
    }

    public void removePlayer(EntityPlayer player) {
        if (!players.remove(player)) {
            System.err.println("Trying to remove non-listening player: " + player.toString());
        }
    }

    public void updateServer(DataType dataType) {
        updateServer(dataType, 0);
    }

    public void updateServer(DataType dataType, int id) {
        PacketHandler.sendToServer(getWriterForType(dataType, id));
    }

    private DataWriter getWriterForType(DataType dataType, int id) {
        DataWriter dw = PacketHandler.getWriter(this, PacketId.TYPE);
        dw.writeEnum(dataType);
        dataType.save(this, dw, id);

        return dw;
    }

    public void receiveServerPacket(DataReader dr, PacketId id, EntityPlayer player) {
        switch (id) {
            case TYPE:
                DataType dataType = dr.readEnum(DataType.class);
                int index = dataType.load(this, dr, false);
                if (index != -1 && dataType.shouldBounce(this)) {
                    sendDataToAllPlayersExcept(dataType, index, dataType.shouldBounceToAll(this) ? null : player);
                }
                if (dataType == DataType.SIDE) {
                    onSideChange();
                }
                break;
            case CLOSE:
                removePlayer(player);
                break;
        }
    }

    public void receiveClientPacket(DataReader dr, PacketId id) {
        switch (id) {
            case ALL:
                for (DataType dataType : DataType.values()) {
                    dataType.load(this, dr, true);
                }
                break;
            case TYPE:
                DataType dataType = dr.readEnum(DataType.class);
                dataType.load(this, dr, false);
                if (dataType == DataType.SIDE) {
                    onSideChange();
                }
                break;
        }
    }

    @Override
    public void updateEntity() {
        reloadFuel();
        for (Page page : pages) {
            page.onUpdate();
        }
    }

    private int lastPower;
    //TODO handle container items
    private void reloadFuel() {
        if (!worldObj.isRemote) {
            ItemStack fuel = fuelSlot.getStack();
            if (fuel != null) {
                int fuelLevel = TileEntityFurnace.getItemBurnTime(fuel);
                if (fuelLevel > 0 && fuelLevel + power <= MAX_POWER) {
                    power += fuelLevel;
                    decrStackSize(fuelSlot.getSlotIndex(), 1);
                }
            }

            if (power != lastPower) {
                lastPower = power;
                sendDataToAllPlayer(DataType.POWER);
            }
        }
    }


    public void onUpgradeChange() {
        reloadTransferSides();
    }

    public void onSideChange() {
        reloadTransferSides();
    }

    private void reloadTransferSides() {
        for (int i = 0; i < sideSlots.length; i++) {
            for (SlotBase slot : slots) {
                slot.setValid(SlotValidity.NONE, i);
            }

            List<SlotBase> slotsForSide = new ArrayList<SlotBase>();

            for (Setting setting : getTransferPage().getSettings()) {
                boolean isInput = setting.getSides().get(i).isInputEnabled();
                boolean isOutput = setting.getSides().get(i).isOutputEnabled();

                if (isInput || isOutput) {
                    List<SlotBase> unitSlots = setting.getSlots();
                    if (unitSlots != null) {
                        slotsForSide.addAll(unitSlots);
                        for (SlotBase unitSlot : unitSlots) {
                            boolean isSlotInput = isInput && unitSlot.canAcceptItems();
                            boolean isSlotOutput = isOutput && unitSlot.canSupplyItems();

                            SlotValidity validity = SlotValidity.getValidity(isSlotInput, isSlotOutput);
                            unitSlot.setValid(validity, i);
                        }
                    }
                }
            }


            sideSlots[i] = getSlotIndexArray(slotsForSide);
        }
    }

    private int[] getSlotIndexArray(List<SlotBase> slots) {
        int[] result = new int[slots.size()];
        for (int j = 0; j < slots.size(); j++) {
            result[j] = slots.get(j).getSlotIndex();
        }
        return result;
    }


    private int[][] sideSlots = new int[6][];

    @Override
    public boolean isItemValidForSlot(int id, ItemStack item) {
        return slots.get(id).isItemValid(item);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return sideSlots[BlockTable.getSideFromSideAndMeta(side, getBlockMetadata())];
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side) {
        return isItemValidForSlot(slot, item) && slots.get(slot).getValid(BlockTable.getSideFromSideAndMeta(side, getBlockMetadata())).isInput();
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side) {
        return slots.get(slot).getValid(BlockTable.getSideFromSideAndMeta(side, getBlockMetadata())).isOutput();
    }

}
