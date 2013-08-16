package assets.levelup;

import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiSkills extends GuiScreen
{
    private boolean closedWithButton;
    private final static int offset = 80;
    private final static String toolTips[] =
    {
        "Every 1: + chance: 2x ore drops", "Every 1: + chance: random crit",
        "Every 1: + chance: 'super block'", "Every 1: + chance: more wood drops",
        "Every 1: + chance: 2x smelt yield", "Every 1: + arrow speed", "Every 1: + sprint speed",
        "Every 1: + chance: 2x cook yield", "Every 1: + sneak speed", "Every 1: + crop grow speed",
        "Every 1: + chance: getting a bite", "Every 1: + chance: digging up loot"
    };
    private final static String toolTips2[] =
    {
        "Every 5: + mining speed", "Every 5: + damage with items",
        "Every 5: - damage from mobs", "Every 5: + chopping speed",
        "Every 5: + smelting speed", "Every 5: + bow drawback speed",
        "Every 5: - falling damage", "Every 5: + cooking speed",
        "Every 5: - mob sight range with sneak", "Every 5: + chance: 2x wheat drops",
        "Every 5: + chance: fish up loot", "Every 5: + chance: flint from gravel"
    };
    private int[] skills;
	private int[] skillsPrev;

    private boolean mouseOverButton(GuiButton guibutton, int i, int j)
    {
        return guibutton.mousePressed(mc, i, j);
    }

    private void updateSkillList()
    {
    	for(int i=0; i< skills.length; i++)
        {
        	skills[i] = PlayerExtendedProperties.getSkillFromIndex(mc.thePlayer, i);
        }
    }
    
    @Override
    public void initGui()
    {
    	skills = new int[ClassBonus.skillNames.length];
        for(int i=0; i< skills.length; i++)
        {
        	skills[i] = PlayerExtendedProperties.getSkillFromIndex(mc.thePlayer, i);
        }
        skillsPrev = new int[skills.length];
        System.arraycopy(skills, 0, skillsPrev, 0, skills.length);
        closedWithButton = false;
        updateSkillList();
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 + 96, height / 6 + 168, 96, 20, "Done"));
        buttonList.add(new GuiButton(100, width / 2 - 192, height / 6 + 168, 96, 20, "Cancel"));
        if (skills[12]> 0)
        {
        	for(int index=0;index<6;index++)
        	{
        		buttonList.add(new GuiButton(1+index, (width / 2 + 44) - offset, 15+32*index, 20, 20, "+"));
        		buttonList.add(new GuiButton(7+index, width / 2 + 44 + offset, 15+32*index, 20, 20, "+"));
        		buttonList.add(new GuiButton(21+index, width / 2 - 64 - offset, 15+32*index, 20, 20, "-"));
                buttonList.add(new GuiButton(27+index, (width / 2 - 64) + offset, 15+32*index, 20, 20, "-"));
            }
        }
    }
    @Override
    public void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.id == 0)
        {
            closedWithButton = true;
            mc.displayGuiScreen((GuiScreen)null);
            mc.setIngameFocus();
        }
        else if (guibutton.id == 100)
        {
            closedWithButton = false;
            mc.displayGuiScreen((GuiScreen)null);
            mc.setIngameFocus();
        }
        else if (guibutton.id < 21)	
        {
        	if(skills[12] > 0 && skills[guibutton.id - 1] < 50)
        	{
		    	ClassBonus.addBonusToSkill(mc.thePlayer, ClassBonus.skillNames[guibutton.id-1], 1, true);
		    	ClassBonus.addBonusToSkill(mc.thePlayer, "XP", 1, false);
		        updateSkillList();
        	}
        }
        else if (skills[guibutton.id - 21] > 0)
        {
        	ClassBonus.addBonusToSkill(mc.thePlayer, ClassBonus.skillNames[guibutton.id-21], 1, true);
        	ClassBonus.addBonusToSkill(mc.thePlayer, "XP", 1, false);
            updateSkillList();
        }
    }
    @Override
    public void onGuiClosed()
    {
        if (!closedWithButton)
        {
        	Map<String,Integer> skillMap = PlayerExtendedProperties.getSkillMap(mc.thePlayer);
        	for(int index=0;index<skillsPrev.length;index++)
        	{
        		skillMap.put(ClassBonus.skillNames[index], skillsPrev[index]);
        	}
        }
    }
    @Override
    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        String s = "";
        String s1 = "";
        for (int k = 0; k < buttonList.size(); k++)
        {
            int l = ((GuiButton)buttonList.get(k)).id;
            if (l < 1 || l > 99)
            {
                continue;
            }
            if (l > 20)
            {
                l -= 20;
            }
            if (mouseOverButton((GuiButton)buttonList.get(k), i, j))
            {
                s = toolTips[l - 1];
                s1 = toolTips2[l - 1];
            }
        }
        byte cl = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
        if (cl != 0)
        {
            drawCenteredString(fontRenderer, (new StringBuilder()).append("Class: ").append(GuiClasses.classList[cl]).toString(), width / 2, 2, 0xffffff);
        }
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Mining: ").append(skills[0]).toString(), width / 2 - offset, 20, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Melee: ").append(skills[1]).toString(), width / 2 - offset, 52, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Defense: ").append(skills[2]).toString(), width / 2 - offset, 84, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Woodcutting: ").append(skills[3]).toString(), width / 2 - offset, 116, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Smelting: ").append(skills[4]).toString(), width / 2 - offset, 148, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Marksman: ").append(skills[5]).toString(), width / 2 - offset, 180, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Athletics: ").append(skills[6]).toString(), width / 2 + offset, 20, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Cooking: ").append(skills[7]).toString(), width / 2 + offset, 52, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Stealth: ").append(skills[8]).toString(), width / 2 + offset, 84, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Farming: ").append(skills[9]).toString(), width / 2 + offset, 116, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Fishing: ").append(skills[10]).toString(), width / 2 + offset, 148, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Digging: ").append(skills[11]).toString(), width / 2 + offset, 180, 0xffffff);
        drawCenteredString(fontRenderer, s, width / 2, height / 6 + 168, 0xffffff);
        drawCenteredString(fontRenderer, s1, width / 2, height / 6 + 180, 0xffffff);
        super.drawScreen(i, j, f);
    }
}
