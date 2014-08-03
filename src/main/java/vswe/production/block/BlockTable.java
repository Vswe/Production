package vswe.production.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
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
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        topIcon = register.registerIcon("production:top");
        sideIcon = register.registerIcon("production:side");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? topIcon : sideIcon;
    }
}
