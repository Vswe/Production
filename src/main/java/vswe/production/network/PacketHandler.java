package vswe.production.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.production.gui.container.ContainerTable;
import vswe.production.tileentity.TileEntityTable;


public class PacketHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        onPacket(event, FMLClientHandler.instance().getClient().thePlayer, false);
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        onPacket(event, ((NetHandlerPlayServer)event.handler).playerEntity, true);
    }

    private void onPacket(FMLNetworkEvent.CustomPacketEvent event, EntityPlayer player, boolean onServer) {
        DataReader dr = new DataReader(event.packet.payload());
        PacketId id = dr.readEnum(PacketId.class);
        TileEntityTable table = null;

        if (id.isInInterface()) {
            if (player.openContainer instanceof ContainerTable) {
                table = ((ContainerTable)player.openContainer).getTable();
            }
        }else{
            int x = dr.readSignedInteger();
            int y = dr.readSignedInteger();
            int z = dr.readSignedInteger();
            World world = player.worldObj;
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityTable) {
                table = (TileEntityTable)te;
            }
        }

        if (table != null) {
            if (onServer) {
                table.receiveServerPacket(dr, id, player);
            }else{
                table.receiveClientPacket(dr, id);
            }
        }
    }

    public static DataWriter getWriter(TileEntityTable table, PacketId id) {
        DataWriter dw = new DataWriter();
        dw.writeEnum(id);
        if (!id.isInInterface()) {
            dw.writeInteger(table.xCoord);
            dw.writeInteger(table.yCoord);
            dw.writeInteger(table.zCoord);
        }
        return dw;
    }

    public static void sendToPlayer(DataWriter dw, EntityPlayer player) {
        dw.sendToPlayer((EntityPlayerMP)player);
    }

    public static void sendToServer(DataWriter dw) {
        dw.sendToServer();
    }

}
