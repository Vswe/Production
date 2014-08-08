package vswe.production.item;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import java.util.EnumSet;
import java.util.List;

public enum Upgrade {
    BLANK("Blank Upgrade", "Crafting components for other upgrades", 0, (ParentType)null),
    AUTO_CRAFTER("Auto Crafter", "Convert a crafting table into an auto crafting table", 1, ParentType.CRAFTING),
    STORAGE("Extra Storage", "Adds extra storage", 1, ParentType.CRAFTING),
    CHARGED("Charger", "Let idle components charge up for later", 8),
    SPEED("Production Speed", "Increase the production speed", 8),
    QUEUE("Input Queue", "Adds an input queue", 3, ParentType.SMELTING),
    EFFICIENCY("Fuel Efficiency", "Improves the fuel efficiency of solid fuel types", 4, ParentType.GLOBAL),
    LAVA("Lava Generator", "Allows lava to be used as fuel", 1, ParentType.GLOBAL),
    SOLAR("Solar Generator", "Allows the table to be charged by solar power", 1,  ParentType.GLOBAL),
    AUTO_TRANSFER("Auto Transfer", "Enables auto transfer to and from the table", 1, ParentType.GLOBAL),
    FILTER("Filter", "Enables transfer filters", 1,  ParentType.GLOBAL),
    TRANSFER("Transfer Capacity", "Increases the automatic transfer capacity", 6, ParentType.GLOBAL);

    //PATTERN("Pattern Crafting", "Remembers old recipes", 4, ParentType.CRAFTING), //TODO
    //RESTOCK("Restock Control", "Only produce more items when there isn't enough of them", 1), //TODO

    private String unlocalizedName;
    private String name;
    private String description;
    private int maxCount;
    private EnumSet<ParentType> validParents;

    Upgrade(String name, String description, int maxCount, EnumSet<ParentType> validParents) {
        this.name = name;
        this.validParents = validParents;
        this.unlocalizedName = toString().toLowerCase();
        this.description = description;
        this.maxCount = maxCount;
    }

    Upgrade(String name, String description, int maxCount, ParentType type) {
        this(name, description, maxCount, type == null ? EnumSet.noneOf(ParentType.class) : EnumSet.of(type));
    }

    Upgrade(String name, String description, int maxCount) {
        this(name, description, maxCount, EnumSet.of(ParentType.CRAFTING, ParentType.SMELTING));
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }


    public String getName() {
        return name;
    }

    @SideOnly(Side.CLIENT)
    private IIcon icon;

    @SideOnly(Side.CLIENT)
    public IIcon getIcon() {
        return icon;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcon(IIconRegister register) {
        icon = register.registerIcon("production:" + unlocalizedName);
    }


    public ItemStack getItemStack() {
        return new ItemStack(ModItems.upgrade, 1, ordinal());
    }

    public static ItemStack getInvalidItemStack() {
        return new ItemStack(ModItems.upgrade, 1, values().length);
    }

    public void addInfo(List<String> info) {
        info.add(EnumChatFormatting.GRAY + description);
        if (GuiScreen.isShiftKeyDown()) {
            if (maxCount == 1) {
                info.add(EnumChatFormatting.YELLOW + "Doesn't stack well");
            }else if (maxCount > 1) {
                info.add(EnumChatFormatting.YELLOW + "Stacks well up to " + maxCount + " items");
            }

            for (ParentType validParent : validParents) {
                info.add(EnumChatFormatting.GOLD + validParent.description);
            }
        }
    }

    public boolean isValid(ItemStack parent) {
        for (ParentType validParent : validParents) {
            if (validParent.isValidParent(parent)) {
                return true;
            }
        }

        return false;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public enum ParentType {
        CRAFTING("Works with Crafting Tables") {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return item != null && Item.getItemFromBlock(Blocks.crafting_table).equals(item.getItem());
            }
        },
        SMELTING("Works with Furnaces") {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return item != null && Item.getItemFromBlock(Blocks.furnace).equals(item.getItem());
            }
        },
        GLOBAL("Upgrades the entire Production Table") {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return item == null;
            }
        };

        private String description;

        ParentType(String description) {
            this.description = description;
        }

        protected abstract boolean isValidParent(ItemStack item);
    }
}
