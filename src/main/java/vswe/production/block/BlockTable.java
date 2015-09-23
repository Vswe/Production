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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.production.StevesProduction;
import vswe.production.creativetab.CreativeTabProduction;
import vswe.production.reference.Localization;
import vswe.production.tileentity.TileEntityTable;

public class BlockTable extends BlockContainer {
    protected BlockTable() {
        super(Material.rock);
        setCreativeTab(CreativeTabProduction.getTab());
        setHardness(3.5F);
        setStepSound(soundTypePiston);
        setUnlocalizedName(Localization.TABLE_NAME);
    }
    
    @Override
    public String getUnlocalizedName() {
         return Localization.PREFIX + super.getUnlocalizedName();
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
        return getIconFromSideAndMeta(side, 2);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromSideAndMeta(int side, int meta) {
        return icons[getSideFromSideAndMeta(side, meta)];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return getIconFromSideAndMeta(side, world.getBlockMetadata(x, y, z));
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
        world.setBlockMetadataWithNotify(x, y, z, rotation, 2);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof IInventory) {
            IInventory inventory = (IInventory)te;
            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack item = inventory.getStackInSlotOnClosing(i);

                if (item != null) {
                    float offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
                    float offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
                    float offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;

                    EntityItem entityItem = new EntityItem(world, x + offsetX, y + offsetY, z + offsetZ, item.copy());
                    entityItem.motionX = world.rand.nextGaussian() * 0.05F;
                    entityItem.motionY = world.rand.nextGaussian() * 0.05F + 0.2F;
                    entityItem.motionZ = world.rand.nextGaussian() * 0.05F;

                    world.spawnEntityInWorld(entityItem);
                }
            }
        }

        super.breakBlock(world, x, y, z, block, meta);
    }
}
