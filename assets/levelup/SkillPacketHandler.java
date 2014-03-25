package assets.levelup;

import java.util.Map;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

public class SkillPacketHandler {
    public static final String[] CHAN = {"LEVELUPINIT", "LEVELUPCLASSES", "LEVELUPSKILLS"};
    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        handlePacket(event.packet, ((NetHandlerPlayServer)event.handler).playerEntity);
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event){
        handlePacket(event.packet, LevelUp.proxy.getPlayer());
    }

	private static void handlePacket(FMLProxyPacket packet, EntityPlayer fake) {
		ByteBuf buf = packet.payload();
		int id= buf.readInt();
		byte button= buf.readByte();
		int[] data = null;
        if (packet.channel().equals(CHAN[0]) || button < 0) {
            data = new int[ClassBonus.skillNames.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = buf.readInt();
            }
        }
        Entity ent = fake.worldObj.getEntityByID(id);
		if (ent instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) ent;
            boolean valid = false;
			if (packet.channel().equals(CHAN[1])) {
				PlayerExtendedProperties.setPlayerClass(player, button);
                valid = true;
			} else if (packet.channel().equals(CHAN[2])) {
				if (data != null) {
					Map<String, Integer> skillMap = PlayerExtendedProperties.getSkillMap(player);
					for (int index = 0; index < data.length; index++) {
						skillMap.put(ClassBonus.skillNames[index], data[index]);
					}
                    valid = true;
				} else if(PlayerExtendedProperties.getSkillFromIndex(player, "XP")>0){
                    String skill = ClassBonus.skillNames[button < 21 ? button - 1 : button - 21];
                    if(PlayerExtendedProperties.getSkillFromIndex(player, skill)<ClassBonus.maxSkillPoints){
                        ClassBonus.addBonusToSkill(player, "XP", 1, !(button < 21));
					    ClassBonus.addBonusToSkill(player, skill, 1, button < 21);
                        valid = true;
                    }
				}
			} else if (packet.channel().equals(CHAN[0])) {
				PlayerExtendedProperties.setPlayerClass(player, button);
				Map<String, Integer> skillMap = PlayerExtendedProperties.getSkillMap(player);
				for (int index = 0; index < data.length; index++) {
					skillMap.put(ClassBonus.skillNames[index], data[index]);
				}
                valid = true;
			}
			if (valid && player instanceof EntityPlayerMP) {
				PlayerEventHandler.loadPlayer(player);
			}
		}
	}

	public static FMLProxyPacket getPacket(Side side, int channel, int user, byte id, int... dat) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(user);
        buf.writeByte(id);
        if ((id < 0 || channel == 0) && dat != null) {
            for (int da : dat)
                buf.writeInt(da);
        }
		FMLProxyPacket pkt = new FMLProxyPacket(buf, CHAN[channel]);
        pkt.setTarget(side);
		return pkt;
	}
}
