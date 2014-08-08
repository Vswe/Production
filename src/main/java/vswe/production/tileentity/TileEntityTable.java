package vswe.production.tileentity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import vswe.production.block.BlockTable;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.gui.container.slot.SlotFuel;
import vswe.production.gui.menu.GuiMenu;
import vswe.production.gui.menu.GuiMenuItem;
import vswe.production.item.Upgrade;
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
import vswe.production.page.unit.Unit;
import vswe.production.page.unit.UnitCrafting;

import java.util.ArrayList;
import java.util.List;


public class TileEntityTable extends TileEntity implements IInventory, ISidedInventory, IFluidHandler {
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
        onUpgradeChange();
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
                return item;
            }

            ItemStack result = item.splitStack(count);

            if (item.stackSize == 0) {
                setInventorySlotContents(id, null);
            }

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
                onUpgradeChange();
                break;
            case TYPE:
                DataType dataType = dr.readEnum(DataType.class);
                dataType.load(this, dr, false);
                if (dataType == DataType.SIDE_ENABLED) {
                    onSideChange();
                }
                break;
            case UPGRADE_CHANGE:
                onUpgradeChange();
                break;
        }
    }

    private int fuelTick = 0;
    private static final int FUEL_DELAY = 5;
    private int moveTick = 0;
    private static final int MOVE_DELAY = 20;
    private boolean lit;
    private boolean lastLit;
    private int slotTick = 0;
    private static final int SLOT_DELAY = 10;

    @Override
    public void updateEntity() {
        for (Page page : pages) {
            page.onUpdate();
        }

        if (!worldObj.isRemote && ++fuelTick >= FUEL_DELAY) {
            lit = worldObj.getBlockLightValue(xCoord, yCoord + 1, zCoord) == 15;
            if (lastLit != lit) {
                lastLit = lit;
                sendDataToAllPlayer(DataType.LIT);
            }
            fuelTick = 0;
            reloadFuel();
        }


        if (!worldObj.isRemote && ++moveTick >= MOVE_DELAY) {
            moveTick = 0;
            if (getUpgradePage().hasGlobalUpgrade(Upgrade.AUTO_TRANSFER)) {
                int transferSize = (int)Math.pow(2, getUpgradePage().getGlobalUpgradeCount(Upgrade.TRANSFER));
                for (Setting setting : getTransferPage().getSettings()) {
                    for (Side side : setting.getSides()) {
                        transfer(setting, side, side.getInput(), transferSize);
                        transfer(setting, side, side.getOutput(), transferSize);
                    }
                }
            }
        }

        if (!worldObj.isRemote && ++slotTick >= SLOT_DELAY) {
            slotTick = 0;
            for (SlotBase slot : slots) {
                slot.updateServer();
            }
        }
    }

    private void transfer(Setting setting, Side side, Transfer transfer, int transferSize) {
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
                    transfer(inventory, this, slots2, slots1, directionReversed.ordinal(), direction.ordinal(), transferSize);
                }else{
                    transfer(this, inventory, slots1, slots2, direction.ordinal(), directionReversed.ordinal(), transferSize);
                }
            }


        }
    }

    private void transfer(IInventory from, IInventory to, int[] fromSlots, int[] toSlots, int fromSide, int toSide, int maxTransfer) {
        int oldTransfer = maxTransfer;

        try {
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
        }finally {
            if (oldTransfer != maxTransfer) {
                to.markDirty();
                from.markDirty();
            }
        }
    }


    private int lava;
    public static final int MAX_LAVA = 1000;
    private static final int MAX_LAVA_DRAIN = 30;
    private static final int LAVA_EFFICIENCY = 12;
    private static final int SOLAR_GENERATION = 4;

    private int lastPower;
    private int lastLava;
    private void reloadFuel() {
        if (getUpgradePage().hasGlobalUpgrade(Upgrade.SOLAR) && isLitAndCanSeeTheSky()) {
            power += SOLAR_GENERATION;
        }

        if (getUpgradePage().hasGlobalUpgrade(Upgrade.LAVA)) {
            int space = (MAX_POWER - power) / LAVA_EFFICIENCY;
            if (space > 0) {
                int move = Math.max(0, Math.min(MAX_LAVA_DRAIN, Math.min(space, lava)));
                power += move * LAVA_EFFICIENCY;
                lava -= move;
            }
        }

        ItemStack fuel = fuelSlot.getStack();
        if (fuel != null && fuelSlot.isItemValid(fuel)) {
            int fuelLevel = TileEntityFurnace.getItemBurnTime(fuel);
            fuelLevel *= 1 + getUpgradePage().getGlobalUpgradeCount(Upgrade.EFFICIENCY) / 4F;

            if (fuelLevel > 0 && fuelLevel + power <= MAX_POWER) {
                power += fuelLevel;
                if (fuel.getItem().hasContainerItem(fuel)) {
                    fuelSlot.putStack(fuel.getItem().getContainerItem(fuel).copy());
                }else{
                    decrStackSize(fuelSlot.getSlotIndex(), 1);
                }
            }
        }

        if (power > MAX_POWER) {
            power = MAX_POWER;
        }

        if (power != lastPower) {
            lastPower = power;
            sendDataToAllPlayer(DataType.POWER);
        }

        if (lava != lastLava) {
            lastLava = lava;
            sendDataToAllPlayer(DataType.LAVA);
        }
    }

    public boolean isLitAndCanSeeTheSky() {
        return lit &&  worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord);
    }


    public void onUpgradeChangeDistribute() {
        if (!worldObj.isRemote) {
            onUpgradeChange();
            sendToAllPlayers(PacketHandler.getWriter(this, PacketId.UPGRADE_CHANGE));
        }
    }

    public void onUpgradeChange() {
        reloadTransferSides();
        getUpgradePage().onUpgradeChange();
        for (UnitCrafting crafting : getMainPage().getCraftingList()) {
            crafting.onUpgradeChange();
        }
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
        return isItemValidForSlot(slot, item) && slots.get(slot).canAcceptItem(item) && slots.get(slot).isInputValid(getTransferSide(side), item);
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




    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource != null && resource.getFluid() != null && resource.getFluid().equals(FluidRegistry.LAVA)) {
            int space = MAX_LAVA - lava;
            int fill = Math.min(space, resource.amount);
            if (doFill) {
                lava += fill;
            }

            return fill;
        }

        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource != null && resource.getFluid() != null && resource.getFluid().equals(FluidRegistry.LAVA)) {
            return drain(from, resource.amount, doDrain);
        }else{
            return null;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        int drain = Math.min(maxDrain, lava);
        if (doDrain) {
            lava -= drain;
        }

        return drain == 0 ? null : new FluidStack(FluidRegistry.LAVA, drain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid != null && fluid.equals(FluidRegistry.LAVA);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid != null && fluid.equals(FluidRegistry.LAVA);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] {new FluidTankInfo(new FluidStack(FluidRegistry.LAVA, lava), MAX_LAVA)};
    }

    public int getLava() {
        return lava;
    }

    public void setLava(int lava) {
        this.lava = lava;
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }


    private static final String NBT_ITEMS = "Items";
    private static final String NBT_UNITS = "Units";
    private static final String NBT_SETTINGS = "Settings";
    private static final String NBT_SIDES = "Sides";
    private static final String NBT_INPUT = "Input";
    private static final String NBT_OUTPUT = "Output";
    private static final String NBT_SLOT = "Slot";
    private static final String NBT_POWER = "Power";
    private static final String NBT_LAVA = "LavaLevel";
    private static final int COMPOUND_ID = 10;

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                NBTTagCompound slotCompound = new NBTTagCompound();
                slotCompound.setByte(NBT_SLOT, (byte) i);
                items[i].writeToNBT(slotCompound);
                itemList.appendTag(slotCompound);
            }
        }
        compound.setTag(NBT_ITEMS, itemList);

        NBTTagList unitList = new NBTTagList();
        for (Unit unit : getMainPage().getUnits()) {
            NBTTagCompound unitCompound = new NBTTagCompound();
            unit.writeToNBT(unitCompound);
            unitList.appendTag(unitCompound);
        }
        compound.setTag(NBT_UNITS, unitList);

        NBTTagList settingList = new NBTTagList();
        for (Setting setting : getTransferPage().getSettings()) {
            NBTTagCompound settingCompound = new NBTTagCompound();

            NBTTagList sideList = new NBTTagList();
            for (Side side : setting.getSides()) {
                NBTTagCompound sideCompound = new NBTTagCompound();
                NBTTagCompound inputCompound = new NBTTagCompound();
                NBTTagCompound outputCompound = new NBTTagCompound();

                side.getInput().writeToNBT(inputCompound);
                side.getOutput().writeToNBT(outputCompound);

                sideCompound.setTag(NBT_INPUT, inputCompound);
                sideCompound.setTag(NBT_OUTPUT, outputCompound);
                sideList.appendTag(sideCompound);
            }
            settingCompound.setTag(NBT_SIDES, sideList);
            settingList.appendTag(settingCompound);
        }
        compound.setTag(NBT_SETTINGS, settingList);

        compound.setShort(NBT_POWER, (short)power);
        compound.setShort(NBT_LAVA, (byte) lava);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        items = new ItemStack[getSizeInventory()];

        NBTTagList itemList = compound.getTagList(NBT_ITEMS, COMPOUND_ID);
        for (int i = 0; i < itemList.tagCount(); i++) {
            NBTTagCompound slotCompound = itemList.getCompoundTagAt(i);
            int id = slotCompound.getByte(NBT_SLOT);
            if (id < 0) {
                id += 256;
            }

            if (id >= 0 && id < items.length) {
                items[id] = ItemStack.loadItemStackFromNBT(slotCompound);
            }
        }

        NBTTagList unitList = compound.getTagList(NBT_UNITS, COMPOUND_ID);
        List<Unit> units = getMainPage().getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            NBTTagCompound unitCompound = unitList.getCompoundTagAt(i);
            unit.readFromNBT(unitCompound);
        }


        NBTTagList settingList = compound.getTagList(NBT_SETTINGS, COMPOUND_ID);
        List<Setting> settings = getTransferPage().getSettings();
        for (int i = 0; i < settings.size(); i++) {
            Setting setting = settings.get(i);
            NBTTagCompound settingCompound = settingList.getCompoundTagAt(i);
            NBTTagList sideList = settingCompound.getTagList(NBT_SIDES, COMPOUND_ID);
            List<Side> sides = setting.getSides();
            for (int j = 0; j < sides.size(); j++) {
                Side side = sides.get(j);
                NBTTagCompound sideCompound = sideList.getCompoundTagAt(j);
                NBTTagCompound inputCompound = sideCompound.getCompoundTag(NBT_INPUT);
                NBTTagCompound outputCompound = sideCompound.getCompoundTag(NBT_OUTPUT);

                side.getInput().readFromNBT(inputCompound);
                side.getOutput().readFromNBT(outputCompound);
            }
        }

        power = compound.getShort(NBT_POWER);
        lava = compound.getShort(NBT_LAVA);

        onUpgradeChange();
    }

    public void spitOutItem(ItemStack item) {
        float offsetX = worldObj.rand.nextFloat() * 0.8F + 0.1F;
        float offsetY = worldObj.rand.nextFloat() * 0.8F + 0.1F;
        float offsetZ = worldObj.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityItem = new EntityItem(worldObj, xCoord + offsetX, yCoord + offsetY, zCoord + offsetZ, item.copy());
        entityItem.motionX = worldObj.rand.nextGaussian() * 0.05F;
        entityItem.motionY = worldObj.rand.nextGaussian() * 0.05F + 0.2F;
        entityItem.motionZ = worldObj.rand.nextGaussian() * 0.05F;

        worldObj.spawnEntityInWorld(entityItem);
    }
}
