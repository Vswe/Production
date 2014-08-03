package vswe.production.network;


import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import static vswe.production.StevesProduction.CHANNEL;
import static vswe.production.StevesProduction.packetHandler;

import java.io.IOException;

public class DataWriter {
    private ByteBuf stream;
    private int byteBuffer;
    private int bitCountBuffer;

    DataWriter() {
        stream = Unpooled.buffer();
    }

    public void writeData(int data, IBitCount bitCount) {
        writeData(data, bitCount.getBitCount());
    }


    private void writeData(int data, int bitCount) {
        long mask = (long)Math.pow(2, bitCount) - 1;
        data &= mask;

        while (true) {
            if (bitCountBuffer + bitCount >= 8) {
                int bitsToAdd = 8 - bitCountBuffer;
                int addMask = (int)Math.pow(2, bitsToAdd) - 1;
                int addData = data & addMask;
                data >>>= bitsToAdd;
                addData <<= bitCountBuffer;
                byteBuffer |= addData;
                stream.writeByte(byteBuffer);
                byteBuffer = 0;
                bitCount -= bitsToAdd;
                bitCountBuffer = 0;
            }else{
                byteBuffer |= data << bitCountBuffer;
                bitCountBuffer += bitCount;
                break;
            }
        }
    }

    public void writeString(String str) {
        writeString(str, StandardCounts.DEFAULT_STRING);
    }

    public void writeString(String str, IBitCount bits) {
        if (str != null) {
            byte[] bytes = str.getBytes();
            int l = Math.min(bytes.length, (int)Math.pow(2, bits.getBitCount()) - 1);
            writeData(l, bits);
            for (int i = 0; i < l; i++) {
                writeByte(bytes[i]);
            }
        }else{
            writeData(0, bits);
        }
    }

    public void writeNBT(NBTTagCompound nbtTagCompound){
        byte[] bytes = null;

        if (nbtTagCompound != null) {
            try {
                bytes = CompressedStreamTools.compress(nbtTagCompound);
            }catch (IOException ex) {
                bytes = null;
            }
        }

        writeBoolean(bytes != null);
        if (bytes != null) {
            writeData(bytes.length, StandardCounts.NBT_LENGTH);
            for (byte b : bytes) {
                writeByte(b);
            }
        }
    }

    void writeFinalBits() {
        if (bitCountBuffer > 0) {
            stream.writeByte(byteBuffer);
            bitCountBuffer = 0;
        }

    }

    private FMLProxyPacket createPacket() {
        writeFinalBits();
        return new FMLProxyPacket(stream, CHANNEL);
    }



    void sendToPlayer(EntityPlayerMP player) {
        packetHandler.sendTo(createPacket(), player);
    }

    void sendToServer() {
        packetHandler.sendToServer(createPacket());
    }


    void sendToAllPlayers() {
        packetHandler.sendToAll(createPacket());
    }

    void sendToAllPlayersAround(TileEntity te, double range) {
        sendToAllPlayersAround(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, range);
    }

    void sendToAllPlayersAround(World world, int x, int y, int z, double range) {
        packetHandler.sendToAllAround(createPacket(), new NetworkRegistry.TargetPoint(world.provider.dimensionId, x + 0.5,y + 0.5, z, range));
    }



    /**
     * Easy access methods
     */

    public void writeBoolean(boolean data) {
        writeData(data ? 1 : 0, StandardCounts.BOOLEAN);
    }

    public void writeByte(int data) {
        writeData(data, StandardCounts.BYTE);
    }

    public void writeShort(int data) {
        writeData(data, StandardCounts.SHORT);
    }

    public void writeInteger(int data) {
        writeData(data, StandardCounts.INTEGER);
    }

    public void writeEnum(Enum data) {
        try {
            Class<? extends Enum> clazz = data.getDeclaringClass();
            int length = ((Object[])clazz.getMethod("values").invoke(null)).length;
            if (length == 0) {
                return;
            }
            int bitCount = (int)(Math.log10(length) / Math.log10(2)) + 1;

            writeData(data.ordinal(), bitCount);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}