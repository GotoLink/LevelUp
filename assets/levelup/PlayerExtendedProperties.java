package assets.levelup;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerExtendedProperties implements IExtendedEntityProperties {
	private byte playerClass;
	private Map<String, Integer> skillMap = new HashMap<String, Integer>();
	private Map<String, int[]> counterMap = new HashMap<String, int[]>();
	public final static String[] counters = { "ore", "craft", "bonus" };

	public PlayerExtendedProperties() {
		for (String name : ClassBonus.skillNames)
			skillMap.put(name, 0);
		counterMap.put(counters[0], new int[] { 0, 0, 0, 0 });
		counterMap.put(counters[1], new int[] { 0, 0, 0, 0 });
		counterMap.put(counters[2], new int[] { 0, 0, 0 });//ore bonus, craft bonus, kill bonus
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		compound.setByte("Class", playerClass);
		for (String name : ClassBonus.skillNames) {
			compound.setInteger(name, skillMap.get(name));
		}
		for (String cat : counters) {
			compound.setIntArray(cat, counterMap.get(cat));
		}
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		playerClass = compound.getByte("Class");
		for (String name : ClassBonus.skillNames) {
			skillMap.put(name, compound.getInteger(name));
		}
		for (String cat : counters) {
			counterMap.put(cat, compound.getIntArray(cat));
		}
	}

	@Override
	public void init(Entity entity, World world) {
	}

	public static Map<String, Integer> getSkillMap(EntityPlayer player) {
		return ((PlayerExtendedProperties) player.getExtendedProperties(ClassBonus.SKILL_ID)).skillMap;
	}

	public static int getSkillFromIndex(EntityPlayer player, String name) {
		return getSkillMap(player).get(name);
	}

	public static int getSkillFromIndex(EntityPlayer player, int id) {
		return getSkillFromIndex(player, ClassBonus.skillNames[id]);
	}

	public static int getSkillPoints(EntityPlayer player) {
		int total = 0;
		for (String skill : ClassBonus.skillNames) {
			total += getSkillFromIndex(player, skill);
		}
		return total;
	}

	public static byte getPlayerClass(EntityPlayer player) {
		return ((PlayerExtendedProperties) player.getExtendedProperties(ClassBonus.SKILL_ID)).playerClass;
	}

	public static void setPlayerClass(EntityPlayer player, byte newClass) {
		if (newClass != getPlayerClass(player)) {
			ClassBonus.applyBonus(player, getPlayerClass(player), newClass);
			capSkills(player);
			((PlayerExtendedProperties) player.getExtendedProperties(ClassBonus.SKILL_ID)).playerClass = newClass;
		}
	}

	public static Map<String, int[]> getCounterMap(EntityPlayer player) {
		return ((PlayerExtendedProperties) player.getExtendedProperties(ClassBonus.SKILL_ID)).counterMap;
	}

	public static int[] getCounterByName(EntityPlayer player, String name) {
		return getCounterMap(player).get(name);
	}

	public static void capSkills(EntityPlayer player) {
		for (String name : ClassBonus.skillNames) {
			if (name.equals("XP"))
				break;
			int j = getSkillFromIndex(player, name);
			if (j > ClassBonus.maxSkillPoints) {
				getSkillMap(player).put(name, ClassBonus.maxSkillPoints);
			}
		}
	}

	public static void resetSkills(EntityPlayer player) {
		for (String name : ClassBonus.skillNames) {
			if (name.equals("XP"))
				break;
			getSkillMap(player).put(name, 0);
		}
	}

	public static void setPlayerData(EntityPlayer player, int[] data) {
		setPlayerClass(player, (byte) data[data.length - 1]);
		for (int i = 0; i < ClassBonus.skillNames.length; i++) {
			getSkillMap(player).put(ClassBonus.skillNames[i], data[i]);
		}
	}

	public static int[] getPlayerData(EntityPlayer player, boolean withClass) {
		int[] data = new int[ClassBonus.skillNames.length + (withClass ? 1 : 0)];
		for (int i = 0; i < ClassBonus.skillNames.length; i++)
			data[i] = getSkillFromIndex(player, i);
		if (withClass)
			data[data.length - 1] = getPlayerClass(player);
		return data;
	}
}
