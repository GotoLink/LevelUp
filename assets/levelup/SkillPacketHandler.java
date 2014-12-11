package assets.levelup;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.config.Property;

import java.util.Map;

public final class SkillPacketHandler {
    public static final String[] CHAN = {"LEVELUPINIT", "LEVELUPCLASSES", "LEVELUPSKILLS", "LEVELUPCFG"};
    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        if(event.packet.channel().equals(CHAN[1]))
            handleClassChange(event.packet.payload().readByte(), ((NetHandlerPlayServer)event.handler).playerEntity);
        else if(event.packet.channel().equals(CHAN[2]))
            handlePacket(event.packet, ((NetHandlerPlayServer)event.handler).playerEntity);
    }

    private void handleClassChange(byte newClass, EntityPlayerMP entityPlayerMP){
        if(newClass>=0){
            PlayerExtendedProperties.setPlayerClass(entityPlayerMP, newClass);
            FMLEventHandler.INSTANCE.loadPlayer(entityPlayerMP);
        }
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event){
        if(event.packet.channel().equals(CHAN[0]))
            handlePacket(event.packet, LevelUp.proxy.getPlayer());
        else if(event.packet.channel().equals(CHAN[3]))
            handleConfig(event.packet);
    }

    private void handlePacket(FMLProxyPacket packet, EntityPlayer player) {
		ByteBuf buf = packet.payload();
		byte button = buf.readByte();
        int[] data = null;
        int sum = 0;
        if (packet.channel().equals(CHAN[0]) || button == -1) {
            data = new int[ClassBonus.skillNames.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = buf.readInt();
                sum += data[i];
            }
        }
        if (packet.channel().equals(CHAN[2])) {
            if (PlayerExtendedProperties.hasClass(player))
                if (data != null && button == -1 && sum == 0) {
                    if (data[data.length - 1] != 0 && -data[data.length - 1] <= PlayerExtendedProperties.getSkillFromIndex(player, "XP")) {
                        for (int index = 0; index < data.length; index++) {
                            if(data[index]!=0) {
                                ClassBonus.addBonusToSkill(player, ClassBonus.skillNames[index], data[index], true);
                            }
                        }
                        FMLEventHandler.INSTANCE.loadPlayer(player);
                    }
                }
        } else if (packet.channel().equals(CHAN[0]) && data != null) {
            PlayerExtendedProperties.setPlayerClass(player, button);
            Map<String, Integer> skillMap = PlayerExtendedProperties.getSkillMap(player);
            for (int index = 0; index < data.length; index++) {
                skillMap.put(ClassBonus.skillNames[index], data[index]);
            }
        }
	}

	public static FMLProxyPacket getPacket(Side side, int channel, byte id, int... dat) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(id);
        if ((id < 0 || channel == 0) && dat != null) {
            for (int da : dat)
                buf.writeInt(da);
        }
		FMLProxyPacket pkt = new FMLProxyPacket(buf, CHAN[channel]);
        pkt.setTarget(side);
		return pkt;
	}

    public static FMLProxyPacket getConfigPacket(Property... dat) {
        ByteBuf buf = Unpooled.buffer();
        for(int i = 0; i < dat.length; i++){
            if(i==2){
                buf.writeDouble(dat[i].getDouble());
            }else if(i<3){
                buf.writeInt(dat[i].getInt());
            }else{
                buf.writeBoolean(dat[i].getBoolean());
            }
        }
        FMLProxyPacket pkt = new FMLProxyPacket(buf, CHAN[3]);
        pkt.setTarget(Side.CLIENT);
        return pkt;
    }


    private void handleConfig(FMLProxyPacket packet) {
        ByteBuf buf = packet.payload();
        Property[] properties = LevelUp.instance.getServerProperties();
        for(int i = 0; i < properties.length; i++){
            if(i==2){
                properties[i].set(buf.readDouble());
            }else if(i<3){
                properties[i].set(buf.readInt());
            }else{
                properties[i].set(buf.readBoolean());
            }
        }
        LevelUp.instance.useServerProperties();
    }
}
