package vswe.production.block;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vswe.production.StevesProduction;
import vswe.production.creativetab.CreativeTabProduction;
import vswe.production.tileentity.TileEntityTable;


public class BlockTable extends BlockContainer {
    protected BlockTable() {
        super(Material.iron);
        setCreativeTab(CreativeTabProduction.getTab());
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTable();
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;


    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        icons = new IIcon[]  {
                register.registerIcon("production:bot"),
                register.registerIcon("production:top"),
                register.registerIcon("production:front"),
                register.registerIcon("production:back"),
                register.registerIcon("production:left"),
                register.registerIcon("production:right"),
        };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[getSideFromSideAndMeta(side, meta)];
    }

    public static int getSideFromSideAndMeta(int side, int meta) {
        if (side <= 1) {
            return side;
        }else{
            int index = SIDES_INDICES[side - 2] - meta;
            if (index < 0) {
                index += SIDES.length;
            }
            return SIDES[index] + 2;
        }
    }

    public static int getSideFromSideAndMetaReversed(int side, int meta) {
        if (side <= 1) {
            return side;
        }else{
            int index = SIDES_INDICES[side - 2] + meta;
            index %= SIDES.length;

            return SIDES[index] + 2;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, StevesProduction.instance, 0, world, x, y, z);
        }

        return true;
    }

    private static final int[] SIDES_INDICES = {0, 2, 3, 1};
    private static final int[] SIDES = {0, 3, 1, 2};

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack item) {
        int rotation = MathHelper.floor_double((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        System.out.println(rotation);
        world.setBlockMetadataWithNotify(x, y, z, rotation, 2);
    }


}
