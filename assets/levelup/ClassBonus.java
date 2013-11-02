package assets.levelup;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

public class ClassBonus {
	public final static String SKILL_ID = "LevelUpSkills";
	public final static String[] skillNames = { "Mining", "Sword", "Defense", "WoodCutting", "Smelting", "Archery", "Athletics", "Cooking", "Sneaking", "Farming", "Fishing", "Digging", "XP" };
	public final static int bonusPoints = 20;

	public static void addBonusToSkill(EntityPlayer player, String name, int bonus, boolean isNew) {
		Map<String, Integer> skill = PlayerExtendedProperties.getSkillMap(player);
		skill.put(name, skill.get(name).intValue() + bonus * (isNew ? 1 : -1));
	}

	public static void applyBonus(EntityPlayer entityplayer, byte playerClass, boolean isNew) {
		int main = 0, sec1 = 0, sec2 = 0;
		switch (playerClass) {
		case 0:
			return;
		case 1:
			sec1 = 11;
			sec2 = 4;
			break;
		case 2:
			main = 1;
			sec1 = 2;
			sec2 = 5;
			break;
		case 3:
			main = 4;
			sec1 = 3;
			sec2 = 7;
			break;
		case 4:
			main = 2;
			sec1 = 6;
			break;
		case 5:
			main = 5;
			sec1 = 8;
			sec2 = 6;
			break;
		case 6:
			main = 9;
			sec1 = 10;
			sec2 = 3;
			break;
		case 7:
			main = 11;
			sec1 = 3;
			break;
		case 8:
			main = 8;
			sec1 = 1;
			sec2 = 5;
			break;
		case 9:
			main = 3;
			sec1 = 2;
			sec2 = 6;
			break;
		case 10:
			main = 7;
			sec1 = 11;
			break;
		case 11:
			main = 6;
			sec1 = 1;
			sec2 = 2;
			break;
		case 12:
			main = 10;
			sec1 = 7;
			sec2 = 3;
			break;
		case 13:
			addBonusToSkill(entityplayer, skillNames[12], bonusPoints, isNew);
			return;
		}
		addBonusToSkill(entityplayer, skillNames[main], bonusPoints / 2, isNew);
		addBonusToSkill(entityplayer, skillNames[sec1], bonusPoints / 4, isNew);
		addBonusToSkill(entityplayer, skillNames[sec2], bonusPoints / 4, isNew);
	}

	public static void applyBonus(EntityPlayer player, byte oldClass, byte newClass) {
		applyBonus(player, oldClass, false);
		applyBonus(player, newClass, true);
	}
}
