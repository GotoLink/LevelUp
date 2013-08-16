package assets.levelup;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

public class ClassBonus
{
	public final static String SKILL_ID = "LevelUpSkills";
	public final static String[] skillNames = 
    	{"Mining","Sword","Defense","WoodCutting","Smelting","Archery",
    	"Athletics","Cooking","Sneaking","Farming","Fishing","Digging","XP"};
    public static void applyBonus(EntityPlayer entityplayer, byte playerClass, boolean isNew)
    {
        switch (playerClass)
        {
        	case 0:
        		break;
        		
            case 1:
            	addBonusToSkill(entityplayer,skillNames[0], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[11], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[4], 5, isNew);
                break;

            case 2:
            	addBonusToSkill(entityplayer,skillNames[1], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[2], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[5], 5, isNew);
                break;

            case 3:
            	addBonusToSkill(entityplayer,skillNames[4], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[3], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[7], 5, isNew);
                break;

            case 4:
            	addBonusToSkill(entityplayer,skillNames[2], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[6], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[0], 5, isNew);
                break;

            case 5:
            	addBonusToSkill(entityplayer,skillNames[5], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[8], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[6], 5, isNew);
                break;

            case 6:
            	addBonusToSkill(entityplayer,skillNames[9], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[10], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[3], 5, isNew);
                break;

            case 7:
            	addBonusToSkill(entityplayer,skillNames[11], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[0], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[3], 5, isNew);
                break;

            case 8:
            	addBonusToSkill(entityplayer,skillNames[8], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[1], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[5], 5, isNew);
                break;

            case 9:
            	addBonusToSkill(entityplayer,skillNames[3], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[2], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[6], 5, isNew);
                break;

            case 10:
            	addBonusToSkill(entityplayer,skillNames[7], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[0], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[11], 5, isNew);
                break;

            case 11:
            	addBonusToSkill(entityplayer,skillNames[6], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[1], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[2], 5, isNew);
                break;

            case 12:
            	addBonusToSkill(entityplayer,skillNames[10], 10, isNew);
            	addBonusToSkill(entityplayer,skillNames[7], 5, isNew);
            	addBonusToSkill(entityplayer,skillNames[3], 5, isNew);
                break;

            case 13:
            	addBonusToSkill(entityplayer,skillNames[12], 20, isNew);
                break;
        }
    }
	
	public static void addBonusToSkill(EntityPlayer player, String name, int bonus, boolean isNew)
	{
		Map<String,Integer> skill = PlayerExtendedProperties.getSkillMap(player);
		skill.put(name, skill.get(name).intValue()+bonus*(isNew?1:-1));
	}

	public static void applyBonus(EntityPlayer player, byte oldClass, byte newClass) 
	{
		applyBonus(player,oldClass,false);
		applyBonus(player,newClass,true);
	}
}
