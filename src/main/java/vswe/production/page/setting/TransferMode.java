package vswe.production.page.setting;


import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public enum  TransferMode {
    PRECISE("Precise detection") {
        @Override
        public boolean isMatch(ItemStack item1, ItemStack item2) {
            return item1.getItem() == item2.getItem() && item1.getItemDamage() == item2.getItemDamage() && ItemStack.areItemStackTagsEqual(item1, item2);
        }
    },
    NBT_INDEPENDENT("NBT independent detection") {
        @Override
        public boolean isMatch(ItemStack item1, ItemStack item2) {
            return item1.getItem() == item2.getItem() && item1.getItemDamage() == item2.getItemDamage();
        }
    },
    FUZZY("Fuzzy detection") {
        @Override
        public boolean isMatch(ItemStack item1, ItemStack item2) {
            return item1.getItem() == item2.getItem();
        }
    },
    ORE_DICTIONARY("Ore dictionary detection") {
        @Override
        public boolean isMatch(ItemStack item1, ItemStack item2) {
            int[] ids1 = OreDictionary.getOreIDs(item1);
            if (ids1.length > 0) {
                int[] ids2 = OreDictionary.getOreIDs(item2);
                for (int id1 : ids1) {
                    for (int id2 : ids2) {
                        if (id1 == id2) {
                            return true;
                        }
                    }
                }
                return false;
            }else{
                return PRECISE.isMatch(item1, item2);
            }
        }
    };

    private String name;

    TransferMode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract boolean isMatch(ItemStack item1, ItemStack item2);
}
