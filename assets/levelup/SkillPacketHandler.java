package assets.levelup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class SkillPacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		handlePacket(packet, (EntityPlayer) player);
	}

	private static void handlePacket(Packet250CustomPayload packet, EntityPlayer fake) {
		DataInputStream inStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int id;
		byte button;
		int[] data = null;
		try {
			id = inStream.readInt();
			button = inStream.readByte();
			if (packet.channel.equals("LEVELUPINIT") || button < 0) {
				data = new int[ClassBonus.skillNames.length];
				for (int i = 0; i < data.length; i++) {
					data[i] = inStream.readInt();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
        Entity ent = fake.worldObj.getEntityByID(id);
		if (ent instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) ent;
            boolean valid = false;
			if (packet.channel.equals("LEVELUPCLASSES")) {
				PlayerExtendedProperties.setPlayerClass(player, button);
                valid = true;
			} else if (packet.channel.equals("LEVELUPSKILLS")) {
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
			} else if (packet.channel.equals("LEVELUPINIT")) {
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

	public static Packet getPacket(String channel, int user, byte id, int... dat) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1 + 4 + (dat != null ? 4 * dat.length : 0));
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(user);
			dos.write(id);
			if ((id < 0 || channel.equals("LEVELUPINIT")) && dat != null) {
				for (int da : dat)
					dos.writeInt(da);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		pkt.channel = channel;
		pkt.data = bos.toByteArray();
		pkt.length = bos.size();
		return pkt;
	}
}
