package vswe.production.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import vswe.production.gui.container.ContainerTable;
import vswe.production.tileentity.TileEntityTable;


public class PacketHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        if (player.openContainer instanceof ContainerTable) {
            TileEntityTable table = ((ContainerTable)player.openContainer).getTable();
            DataReader dr = new DataReader(event.packet.payload());
            PacketId id = dr.readEnum(PacketId.class);
            table.receiveClientPacket(dr, id);
        }
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        EntityPlayer player = ((NetHandlerPlayServer)event.handler).playerEntity;
        if (player.openContainer instanceof ContainerTable) {
            TileEntityTable table = ((ContainerTable)player.openContainer).getTable();
            DataReader dr = new DataReader(event.packet.payload());
            PacketId id = dr.readEnum(PacketId.class);
            table.receiveServerPacket(dr, id, player);
        }
    }

    public static DataWriter getWriter(PacketId id) {
        DataWriter dw = new DataWriter();
        dw.writeEnum(id);
        return dw;
    }

    public static void sendToPlayer(DataWriter dw, EntityPlayer player) {
        dw.sendToPlayer((EntityPlayerMP)player);
    }

    public static void sendToServer(DataWriter dw) {
        dw.sendToServer();
    }
}
