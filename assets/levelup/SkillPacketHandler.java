package assets.levelup;

import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.config.Property;

public final class SkillPacketHandler {
    public static final String[] CHAN = {"LEVELUPINIT", "LEVELUPCLASSES", "LEVELUPSKILLS", "LEVELUPCFG"};

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        final ByteBuf in = event.packet.payload();
        final EntityPlayerMP player = ((NetHandlerPlayServer) event.handler).playerEntity;
        if (event.packet.channel().equals(CHAN[1])) {
            addTask(event.handler, new Runnable() {
                @Override
                public void run() {
                    handleClassChange(in.readByte(), player);
                }
            });
        }else if (event.packet.channel().equals(CHAN[2])) {
            addTask(event.handler, new Runnable() {
                @Override
                public void run() {
                    handlePacket(in, player);
                }
            });
        }
    }

    private void addTask(INetHandler netHandler, Runnable runnable){
        FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(runnable);
    }

    private void handleClassChange(byte newClass, EntityPlayerMP entityPlayerMP) {
        if (newClass >= 0) {
            PlayerExtendedProperties.from(entityPlayerMP).setPlayerClass(newClass);
            FMLEventHandler.INSTANCE.loadPlayer(entityPlayerMP);
        }
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        final ByteBuf in = event.packet.payload();
        if (event.packet.channel().equals(CHAN[0])) {
            addTask(event.handler, new Runnable() {
                @Override
                public void run() {
                    handlePacket(in, LevelUp.proxy.getPlayer());
                }
            });
        } else if (event.packet.channel().equals(CHAN[3])) {
            addTask(event.handler, new Runnable() {
                @Override
                public void run() {
                    handleConfig(in);
                }
            });
        }
    }

    private void handlePacket(ByteBuf buf, EntityPlayer player) {
        boolean isInit = player.worldObj.isRemote;
        byte button = buf.readByte();
        int[] data = null;
        int sum = 0;
        if (isInit || button == -1) {
            data = new int[ClassBonus.skillNames.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = buf.readInt();
                sum += data[i];
            }
        }
        PlayerExtendedProperties properties = PlayerExtendedProperties.from(player);
        if (!isInit) {
            if (properties.hasClass())
                if (data != null && button == -1 && sum == 0) {
                    if (data[data.length - 1] != 0 && -data[data.length - 1] <= properties.getSkillFromIndex("XP")) {
                        for (int index = 0; index < data.length; index++) {
                            if (data[index] != 0) {
                                properties.addToSkill(ClassBonus.skillNames[index], data[index]);
                            }
                        }
                        FMLEventHandler.INSTANCE.loadPlayer(player);
                    }
                }
        } else if (data != null) {
            properties.setPlayerClass(button);
            properties.setPlayerData(data);
        }
    }

    public static FMLProxyPacket getPacket(Side side, int channel, byte id, int... dat) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(id);
        if ((id < 0 || channel == 0) && dat != null) {
            for (int da : dat)
                buf.writeInt(da);
        }
        FMLProxyPacket pkt = new FMLProxyPacket(new PacketBuffer(buf), CHAN[channel]);
        pkt.setTarget(side);
        return pkt;
    }

    public static FMLProxyPacket getConfigPacket(Property... dat) {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < dat.length; i++) {
            if (i == 2) {
                buf.writeDouble(dat[i].getDouble());
            } else if (i < 4) {
                buf.writeInt(dat[i].getInt());
            } else {
                buf.writeBoolean(dat[i].getBoolean());
            }
        }
        FMLProxyPacket pkt = new FMLProxyPacket(new PacketBuffer(buf), CHAN[3]);
        pkt.setTarget(Side.CLIENT);
        return pkt;
    }


    private void handleConfig(ByteBuf buf) {
        Property[] properties = LevelUp.instance.getServerProperties();
        for (int i = 0; i < properties.length; i++) {
            if (i == 2) {
                properties[i].set(buf.readDouble());
            } else if (i < 4) {
                properties[i].set(buf.readInt());
            } else {
                properties[i].set(buf.readBoolean());
            }
        }
        LevelUp.instance.useServerProperties();
    }
}
