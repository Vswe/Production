package vswe.production.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import vswe.production.block.BlockTable;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.gui.container.slot.SlotFuel;
import vswe.production.gui.container.slot.SlotValidity;
import vswe.production.gui.menu.GuiMenu;
import vswe.production.gui.menu.GuiMenuItem;
import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.network.PacketHandler;
import vswe.production.network.PacketId;
import vswe.production.page.Page;
import vswe.production.page.PageMain;
import vswe.production.page.PageTransfer;
import vswe.production.page.PageUpgrades;
import vswe.production.page.setting.Setting;
import vswe.production.page.setting.Side;
import vswe.production.page.setting.Transfer;
import vswe.production.network.data.DataType;

import java.util.ArrayList;
import java.util.List;


public class TileEntityTable extends TileEntity implements IInventory, ISidedInventory {
    private List<Page> pages;
    private Page selectedPage;
    private List<SlotBase> slots;
    private ItemStack[] items;

    private GuiMenu menu;

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

    public ItemStack[] getItems() {
        return items;
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
        ItemStack item = getStackInSlot(id);
        if (item != null) {
            if (item.stackSize <= count) {
                setInventorySlotContents(id, null);
                markDirty();
                return item;
            }

            ItemStack result = item.splitStack(count);

            if (item.stackSize == 0) {
                setInventorySlotContents(id, null);
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
                if (dataType == DataType.SIDE_ENABLED) {
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
                if (dataType == DataType.SIDE_ENABLED) {
                    onSideChange();
                }
                break;
        }
    }

    private int moveTick = 0;
    private static final int MOVE_DELAY = 20;

    @Override
    public void updateEntity() {
        reloadFuel();
        for (Page page : pages) {
            page.onUpdate();
        }

        if (!worldObj.isRemote && ++moveTick >= MOVE_DELAY) {
            moveTick = 0;
            for (Setting setting : getTransferPage().getSettings()) {
                for (Side side : setting.getSides()) {
                    transfer(setting, side, side.getInput());
                    transfer(setting, side, side.getOutput());
                }
            }
        }
    }

    private void transfer(Setting setting, Side side, Transfer transfer) {
        if (transfer.isEnabled() && transfer.isAuto()) {
            ForgeDirection direction = ForgeDirection.values()[BlockTable.getSideFromSideAndMetaReversed(side.getDirection().ordinal(), getBlockMetadata())];

            TileEntity te = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
            if (te instanceof IInventory) {
                IInventory inventory = (IInventory)te;

                List<SlotBase> transferSlots = setting.getSlots();
                int[] slots1 = new int[transferSlots.size()];
                for (int i = 0; i < transferSlots.size(); i++) {
                    slots1[i] = transferSlots.get(i).getSlotIndex();
                }

                int[] slots2;

                ForgeDirection directionReversed = direction.getOpposite();
                if (inventory instanceof ISidedInventory) {
                    slots2 = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(directionReversed.ordinal());
                }else{
                    slots2 = new int[inventory.getSizeInventory()];
                    for (int i = 0; i < slots2.length; i++) {
                        slots2[i] = i;
                    }
                }


                if (slots2 == null ||slots2.length == 0) {
                    return;
                }

                if (transfer.isInput()) {
                    transfer(inventory, this, slots2, slots1, directionReversed.ordinal(), direction.ordinal(), 1);
                }else{
                    transfer(this, inventory, slots1, slots2, direction.ordinal(), directionReversed.ordinal(), 1);
                }
            }


        }
    }

    private void transfer(IInventory from, IInventory to, int[] fromSlots, int[] toSlots, int fromSide, int toSide, int maxTransfer) {
        ISidedInventory fromSided = from instanceof ISidedInventory ? (ISidedInventory)from : null;
        ISidedInventory toSided = to instanceof ISidedInventory ? (ISidedInventory)to : null;

        for (int fromSlot : fromSlots) {
            ItemStack fromItem = from.getStackInSlot(fromSlot);
            if (fromItem != null && fromItem.stackSize > 0) {
                if (fromSided == null || fromSided.canExtractItem(fromSlot, fromItem, fromSide)) {
                    if (fromItem.isStackable()) {
                        for (int toSlot : toSlots) {
                            ItemStack toItem = to.getStackInSlot(toSlot);
                            if (toItem != null && toItem.stackSize > 0) {
                                if (toSided == null || toSided.canInsertItem(toSlot, fromItem, toSide)) {
                                    if (fromItem.isItemEqual(toItem) && ItemStack.areItemStackTagsEqual(toItem, fromItem)) {
                                        int maxSize = Math.min(toItem.getMaxStackSize(), to.getInventoryStackLimit());
                                        int maxMove = Math.min(maxSize - toItem.stackSize, Math.min(maxTransfer, fromItem.stackSize));

                                        toItem.stackSize += maxMove;
                                        maxTransfer -= maxMove;
                                        fromItem.stackSize -= maxMove;
                                        if (fromItem.stackSize == 0) {
                                            from.setInventorySlotContents(fromSlot, null);
                                        }

                                        if (maxTransfer == 0) {
                                            return;
                                        } else if (fromItem.stackSize == 0) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (fromItem.stackSize > 0) {
                        for (int toSlot : toSlots) {
                            ItemStack toItem = to.getStackInSlot(toSlot);
                            if (toItem == null && to.isItemValidForSlot(toSlot, fromItem)) {
                                if (toSided == null || toSided.canInsertItem(toSlot, fromItem, toSide)) {
                                    toItem = fromItem.copy();
                                    toItem.stackSize = Math.min(maxTransfer, fromItem.stackSize);
                                    to.setInventorySlotContents(toSlot, toItem);
                                    maxTransfer -= toItem.stackSize;
                                    fromItem.stackSize -= toItem.stackSize;

                                    if (fromItem.stackSize == 0) {
                                        from.setInventorySlotContents(fromSlot, null);
                                    }

                                    if (maxTransfer == 0) {
                                        return;
                                    } else if (fromItem.stackSize == 0) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

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
                slot.resetValidity(i);
            }

            List<SlotBase> slotsForSide = new ArrayList<SlotBase>();

            for (Setting setting : getTransferPage().getSettings()) {
                Transfer input = setting.getSides().get(i).getInput();
                Transfer output = setting.getSides().get(i).getOutput();

                if (input.isEnabled() || output.isEnabled()) {
                    List<SlotBase> unitSlots = setting.getSlots();
                    if (unitSlots != null) {
                        slotsForSide.addAll(unitSlots);
                        for (SlotBase unitSlot : unitSlots) {
                            boolean isSlotInput = input.isEnabled() && unitSlot.canAcceptItems();
                            boolean isSlotOutput = output.isEnabled() && unitSlot.canSupplyItems();

                            unitSlot.setValidity(i, isSlotInput ? input : null, isSlotOutput ? output : null);
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
        return sideSlots[getTransferSide(side)];
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side) {
        return isItemValidForSlot(slot, item) && slots.get(slot).isInputValid(getTransferSide(side), item);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side) {
        return slots.get(slot).isOutputValid(getTransferSide(side), item);
    }


    private int getTransferSide(int side) {
        return BlockTable.getSideFromSideAndMeta(side, getBlockMetadata());
    }

    public GuiMenu getMenu() {
        return menu;
    }

    public void setMenu(GuiMenuItem menu) {
        this.menu = menu;
    }
}
