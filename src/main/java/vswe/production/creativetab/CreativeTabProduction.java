package vswe.production.creativetab;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;



public class CreativeTabProduction extends CreativeTabs {
    private Item item;

    public CreativeTabProduction() {
        super("steves_production");
        tab = this;
    }

    @Override
    public Item getTabIconItem() {
        return item;
    }

    public void init(Block block) {
        item = Item.getItemFromBlock(block);
    }

    private static CreativeTabs tab;
    public static CreativeTabs getTab() {
        return tab;
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Steve's Production Table";
    }
}
