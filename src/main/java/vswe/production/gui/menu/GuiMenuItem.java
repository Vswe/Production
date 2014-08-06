package vswe.production.gui.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vswe.production.gui.GuiBase;
import vswe.production.gui.component.ArrowScroll;
import vswe.production.gui.component.Button;
import vswe.production.gui.component.TextBox;
import vswe.production.network.data.DataType;
import vswe.production.page.setting.ItemSetting;
import vswe.production.page.setting.TransferMode;
import vswe.production.tileentity.TileEntityTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GuiMenuItem extends GuiMenu {
    private ItemSetting setting;
    private TransferMode mode;
    private ItemStack item;

    private List<ItemStack> playerItems;
    private List<ItemStack> searchItems;
    private boolean showPlayerItems;
    private List<ArrowScroll> arrows;
    private int page;
    private TextBox textBox;

    private static final int ITEMS_PER_ROW = 12;
    private static final int ITEM_ROWS = 7;
    private static final int VISIBLE_ITEMS = ITEMS_PER_ROW * ITEM_ROWS;

    private static final int ITEMS_X = 10;
    private static final int ITEMS_Y = 80;
    private static final int ITEMS_OFFSET = 20;
    private static final int ITEM_SIZE = 18;


    private static final int ITEM_X = 10;
    private static final int ITEM_Y = 10;


    public GuiMenuItem(TileEntityTable table, ItemSetting setting) {
        super(table);

        this.setting = setting;
        this.item = setting.getItem();
        this.mode = setting.getMode();

        textBox = new TextBox(10, 65) {
            @Override
            public boolean isVisible() {
                return !showPlayerItems;
            }

            @Override
            protected void textChanged() {
                loadSearchItems();
            }
        };
        loadSearchItems();
        loadPlayerItems();


        arrows = new ArrayList<ArrowScroll>();
        arrows.add(new ArrowScroll(10, 50, 120) {
            @Override
            public String getText() {
                return showPlayerItems ? "List player items" : "Search for items";
            }

            @Override
            public void setId(int id) {
                showPlayerItems = id == 0;
            }

            @Override
            public int getId() {
                return showPlayerItems ? 0 : 1;
            }

            @Override
            public void onUpdate() {
                page = 0;
            }

            @Override
            protected int getLength() {
                return 2;
            }
        });
        arrows.add(new ArrowScroll(30, 234, 70) {
            @Override
            public String getText() {
                return "Page " + (page + 1);
            }

            @Override
            public void setId(int id) {
                page = id;
            }

            @Override
            public int getId() {
                return page;
            }

            @Override
            public boolean isVisible() {
                return getItemList().size() > VISIBLE_ITEMS;
            }

            @Override
            protected int getLength() {
                return (int)Math.ceil((float)getItemList().size() / VISIBLE_ITEMS);
            }
        });

        arrows.add(new ArrowScroll(10, 30, 120) {
            @Override
            public String getText() {
                return mode.toString();
            }

            @Override
            public void setId(int id) {
                mode = TransferMode.values()[id];
            }

            @Override
            public int getId() {
                return mode.ordinal();
            }

            @Override
            protected int getLength() {
                return TransferMode.values().length;
            }
        });

        buttons.add(new Button("Delete", 30, 11) {
            @Override
            public void clicked() {
                item = null;
            }

            @Override
            public boolean isVisible() {
                return item != null;
            }
        });
    }




    private void loadSearchItems() {
        searchItems = new ArrayList<ItemStack>();

        String search = textBox.getText();
        if (search != null && !search.isEmpty()) {
            search = search.toLowerCase();


            List<ItemStack> itemStacks = new ArrayList<ItemStack>();
            for (Object obj : Item.itemRegistry) {
                Item item = (Item)obj;

                if (item != null && item.getCreativeTab() != null) {
                    item.getSubItems(item, null, itemStacks);
                }
            }

            for (ItemStack itemStack : itemStacks) {
                if (itemStack != null) {
                    searchItems.add(itemStack);
                }
            }

            Iterator<ItemStack> itemIterator = searchItems.iterator();

            while (itemIterator.hasNext()) {
                ItemStack element = itemIterator.next();
                List<String> description;

                try {
                    //noinspection unchecked
                    description = element.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
                }catch (Throwable ex) {
                    itemIterator.remove();
                    continue;
                }

                Iterator<String> descriptionIterator = description.iterator();

                boolean foundSequence = false;

                while (descriptionIterator.hasNext()) {
                    String line = descriptionIterator.next().toLowerCase();
                    if (line.contains(search)) {
                        foundSequence = true;
                        break;
                    }
                }

                if (!foundSequence) {
                    itemIterator.remove();
                }
            }
        }

        fixPage();
    }

    private void loadPlayerItems() {
        playerItems = new ArrayList<ItemStack>();
        IInventory inventory = Minecraft.getMinecraft().thePlayer.inventory;
        int itemLength = inventory.getSizeInventory();
        for (int i = 0; i < itemLength; i++) {
            ItemStack item = inventory.getStackInSlot(i);
            if (item != null) {
                item = item.copy();
                item.stackSize = 1;
                boolean exists = false;
                for (ItemStack other : playerItems) {
                    if (ItemStack.areItemStacksEqual(item, other)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    playerItems.add(item);
                }
            }
        }

        fixPage();
    }

    private void fixPage() {
        int maxPages = (int)Math.ceil((float)getItemList().size() / VISIBLE_ITEMS);
        if (page >= maxPages) {
            page = maxPages - 1;
        }

        if (page < 0) {
            page = 0;
        }
    }


    private List<ItemStack> getItemList() {
        return showPlayerItems ? playerItems : searchItems;
    }

    @Override
    public void draw(GuiBase gui, int mX, int mY) {
        super.draw(gui, mX, mY);

        gui.drawItemWithBackground(item, ITEM_X, ITEM_Y, mX, mY);
        for (ArrowScroll arrow : arrows) {
            arrow.draw(gui, mX, mY);
        }

        int start = page * VISIBLE_ITEMS;
        int end = Math.min (start + VISIBLE_ITEMS, getItemList().size());
        for (int i = start; i < end; i++) {
            int position = i - start;
            int x = position % ITEMS_PER_ROW;
            int y = position / ITEMS_PER_ROW;

            gui.drawItemWithBackground(getItemList().get(i), ITEMS_X + ITEMS_OFFSET * x, ITEMS_Y + ITEMS_OFFSET * y, mX, mY);
        }

        textBox.draw(gui, mX, mY);
    }

    @Override
    public void onClick(GuiBase gui, int mX, int mY) {
        super.onClick(gui, mX, mY);

        for (ArrowScroll arrow : arrows) {
            arrow.onClick(gui, mX, mY);
        }

        int start = page * VISIBLE_ITEMS;
        int end = Math.min (start + VISIBLE_ITEMS, getItemList().size());
        for (int i = start; i < end; i++) {
            int position = i - start;
            int x = position % ITEMS_PER_ROW;
            int y = position / ITEMS_PER_ROW;

            if (gui.inBounds(ITEMS_X + ITEMS_OFFSET * x, ITEMS_Y + ITEMS_OFFSET * y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                item = getItemList().get(i).copy();
                item.stackSize = 1;
                break;
            }
        }
    }

    @Override
    public void onRelease(GuiBase gui, int mX, int mY) {
        super.onRelease(gui, mX, mY);

        for (ArrowScroll arrow : arrows) {
            arrow.onRelease();
        }
    }


    @Override
    public void onKeyStroke(GuiBase gui, char c, int k) {
        super.onKeyStroke(gui, c, k);
        textBox.onKeyStroke(gui, c, k);
    }

    @Override
    protected void save() {
        if (!ItemStack.areItemStacksEqual(item, setting.getItem())) {
            setting.setItem(item);
            table.updateServer(DataType.SIDE_FILTER, table.getTransferPage().getSyncId(setting));
        }

        if (mode != setting.getMode()) {
            setting.setMode(mode);
            table.updateServer(DataType.SIDE_FILTER_MODE, table.getTransferPage().getSyncId(setting));
        }

    }
}
