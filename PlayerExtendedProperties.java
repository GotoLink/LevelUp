package assets.levelup;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerExtendedProperties implements IExtendedEntityProperties{

    public byte playerClass;
    public int deathLevel;
    private Map<String,Integer> skillMap = new HashMap();
    private Map<String,int[]> counterMap = new HashMap();
    private final static String[] counters = {"ore","craft","bonus"};
    
    public PlayerExtendedProperties()
    {
    	for(String name : ClassBonus.skillNames)
			skillMap.put(name, 0);
		counterMap.put(counters[0], new int[]{0,0,0,0});
		counterMap.put(counters[1], new int[]{0,0,0,0});
		counterMap.put(counters[2], new int[]{0,0,0});//ore bonus, craft bonus, kill bonus
    }
	@Override
	public void saveNBTData(NBTTagCompound compound) 
	{
		compound.setByte("Class", playerClass);
		for(String name : ClassBonus.skillNames)
		{
            compound.setInteger(name, skillMap.get(name));
        }
		for(String cat : counters)
		{
			compound.setIntArray(cat, counterMap.get(cat));
		}
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) 
	{
		playerClass = compound.getByte("Class");
		for(String name : ClassBonus.skillNames)
		{
            skillMap.put(name, compound.getInteger(name));
        }
		for(String cat : counters)
		{
			counterMap.put(cat, compound.getIntArray(cat));
		}
	}

	@Override
	public void init(Entity entity, World world) 
	{
	}
	
	public static Map<String,Integer> getSkillMap(EntityPlayer player)
	{
		return ((PlayerExtendedProperties)player.getExtendedProperties(ClassBonus.SKILL_ID)).skillMap;
	}
	
	public static int getSkillFromIndex(EntityPlayer player, String name)
    {
		return getSkillMap(player).get(name).intValue();
    }
	
	public static int getSkillFromIndex(EntityPlayer player, int id)
    {
		return getSkillFromIndex(player, ClassBonus.skillNames[id]);
    }
	
	public static byte getPlayerClass(EntityPlayer player)
	{
		return ((PlayerExtendedProperties)player.getExtendedProperties(ClassBonus.SKILL_ID)).playerClass;
	}
	
	public static void setPlayerClass(EntityPlayer player, byte newClass)
	{
		if(newClass != getPlayerClass(player))
		{
			applyClassBonuses(player, newClass);
			((PlayerExtendedProperties)player.getExtendedProperties(ClassBonus.SKILL_ID)).playerClass = newClass;
		}
	}
	
	public static int getPlayerDeathLevel(EntityPlayer player)
	{
		return ((PlayerExtendedProperties)player.getExtendedProperties(ClassBonus.SKILL_ID)).deathLevel;
	}
	
	public static Map<String,int[]> getCounterMap(EntityPlayer player)
	{
		return ((PlayerExtendedProperties)player.getExtendedProperties(ClassBonus.SKILL_ID)).counterMap;
	}
	
	public static int[] getCounterByName(EntityPlayer player, String name)
	{
		return getCounterMap(player).get(name);
	}

    public static void applyClassBonuses(EntityPlayer player, byte newClass)
    {
        ClassBonus.applyBonus(player, getPlayerClass(player), newClass);
        capSkills(player);
    }

    public static void capSkills(EntityPlayer player)
    {
    	for(String name : ClassBonus.skillNames)
        {
            int j = getSkillFromIndex(player, name);
            if (j > 50)
            {
            	getSkillMap(player).put(name, 50);
            }
        }
    }
    
    public static void resetSkills(EntityPlayer player)
    {
    	for(String name : ClassBonus.skillNames)
    		getSkillMap(player).put(name, 0);
    }
}
