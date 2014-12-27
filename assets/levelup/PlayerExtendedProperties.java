package assets.levelup;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import java.util.HashMap;
import java.util.Map;

public final class PlayerExtendedProperties implements IExtendedEntityProperties {
    private byte playerClass;
    private Map<String, Integer> skillMap = new HashMap<String, Integer>();
    private Map<String, int[]> counterMap = new HashMap<String, int[]>();
    public final static String[] counters = {"ore", "craft", "bonus"};

    public PlayerExtendedProperties() {
        for (String name : ClassBonus.skillNames)
            skillMap.put(name, 0);
        counterMap.put(counters[0], new int[]{0, 0, 0, 0});
        counterMap.put(counters[1], new int[]{0, 0, 0, 0});
        counterMap.put(counters[2], new int[]{0, 0, 0});//ore bonus, craft bonus, kill bonus
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

    public static PlayerExtendedProperties from(EntityPlayer player) {
        return ((PlayerExtendedProperties) player.getExtendedProperties(ClassBonus.SKILL_ID));
    }

    public void addToSkill(String name, int value) {
        skillMap.put(name, skillMap.get(name) + value);
    }

    public int getSkillFromIndex(String name) {
        return skillMap.get(name);
    }

    public static int getSkillFromIndex(EntityPlayer player, int id) {
        return from(player).getSkillFromIndex(ClassBonus.skillNames[id]);
    }

    public int getSkillPoints() {
        int total = 0;
        for (String skill : ClassBonus.skillNames) {
            total += getSkillFromIndex(skill);
        }
        return total;
    }

    public boolean hasClass() {
        return playerClass != 0;
    }

    public static byte getPlayerClass(EntityPlayer player) {
        return from(player).playerClass;
    }

    public void setPlayerClass(byte newClass) {
        if (newClass != playerClass) {
            ClassBonus.applyBonus(this, playerClass, newClass);
            capSkills();
            playerClass = newClass;
        }
    }

    public static Map<String, int[]> getCounterMap(EntityPlayer player) {
        return from(player).counterMap;
    }

    public void capSkills() {
        for (String name : ClassBonus.skillNames) {
            if (name.equals("XP"))
                continue;
            int j = skillMap.get(name);
            if (j > ClassBonus.getMaxSkillPoints()) {
                skillMap.put(name, ClassBonus.getMaxSkillPoints());
            }
        }
    }

    public void takeSkillFraction(float ratio) {
        final byte clas = playerClass;
        if (clas != 0) {
            ClassBonus.applyBonus(this, clas, (byte) 0);
            playerClass = 0;
        }
        for (String name : ClassBonus.skillNames) {
            final int value = skillMap.get(name);
            int remove = (int) (value * ratio);
            if (remove > 0) {
                skillMap.put(name, value - remove);
            }
        }
        if (clas != 0) {
            ClassBonus.applyBonus(this, (byte) 0, clas);
            playerClass = clas;
        }
        capSkills();
    }

    public void convertPointsToXp(boolean resetClass) {
        final byte clas = playerClass;
        setPlayerClass((byte) 0);
        skillMap.put("XP", getSkillPoints());
        setPlayerData(new int[ClassBonus.skillNames.length - 1]);
        if (!resetClass)
            setPlayerClass(clas);
    }

    void setPlayerData(int[] data) {
        for (int i = 0; i < ClassBonus.skillNames.length && i < data.length; i++) {
            skillMap.put(ClassBonus.skillNames[i], data[i]);
        }
    }

    int[] getPlayerData(boolean withClass) {
        int[] data = new int[ClassBonus.skillNames.length + (withClass ? 1 : 0)];
        for (int i = 0; i < ClassBonus.skillNames.length; i++)
            data[i] = getSkillFromIndex(ClassBonus.skillNames[i]);
        if (withClass)
            data[data.length - 1] = playerClass;
        return data;
    }
}
