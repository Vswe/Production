package vswe.production.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import vswe.production.creativetab.CreativeTabProduction;

import java.util.List;

public class ItemUpgrade extends Item {
    public ItemUpgrade() {
        setCreativeTab(CreativeTabProduction.getTab());
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack item) {
        Upgrade upgrade = getUpgrade(item);
        return "steves_production:item." + (upgrade != null ? upgrade.getUnlocalizedName() : "unknown") + ".name";
    }

    public Upgrade getUpgrade(int dmg) {
        if (dmg >= 0 && dmg < Upgrade.values().length) {
            return Upgrade.values()[dmg];
        }else{
            return null;
        }
    }

    public Upgrade getUpgrade(ItemStack item) {
        if (item != null && ModItems.upgrade.equals(item.getItem())) {
            return getUpgrade(item.getItemDamage());
        }else{
            return null;
        }
    }


    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int dmg) {
        Upgrade upgrade = getUpgrade(dmg);
        return upgrade != null ? upgrade.getIcon() : null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        for (Upgrade upgrade : Upgrade.values()) {
            upgrade.registerIcon(register);
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List lst) {
        for (Upgrade upgrade : Upgrade.values()) {
            if (upgrade.isEnabled()) {
                //noinspection unchecked
                lst.add(upgrade.getItemStack());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack item, EntityPlayer player, List lst, boolean useExtraInfo) {
        Upgrade upgrade = getUpgrade(item);
        if (upgrade != null) {
            upgrade.addInfo(lst);
        }else{
            lst.add(EnumChatFormatting.RED + "This is not a valid item");
        }
    }
}
