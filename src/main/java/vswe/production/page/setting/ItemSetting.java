package vswe.production.page.setting;


import net.minecraft.item.ItemStack;

public class ItemSetting {
    public static final int ITEM_COUNT = 10;

    private int id;
    private ItemStack item;
    private TransferMode mode = TransferMode.PRECISE;


    public ItemSetting(int id) {
        this.id = id;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public TransferMode getMode() {
        return mode;
    }

    public void setMode(TransferMode mode) {
        this.mode = mode;
    }
}
