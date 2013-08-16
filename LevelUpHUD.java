package assets.levelup;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class LevelUpHUD extends Gui
{
    private Minecraft mc;
	private int skillXP;
	private int deathLevel;
	private byte playerClass;

    public LevelUpHUD(Minecraft minecraft)
    {
        mc = minecraft;
    }

    @ForgeSubscribe
    public void renderLvlUpHUD(RenderGameOverlayEvent.Text event)
    {
    	renderGameOverlay(event.left);
    }

    public void renderGameOverlay(ArrayList<String> left)
    {
    	if(mc.thePlayer!=null)
        {
	        skillXP = PlayerExtendedProperties.getSkillFromIndex(mc.thePlayer, "XP");
	        deathLevel = PlayerExtendedProperties.getPlayerDeathLevel(mc.thePlayer);
	        playerClass = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
            boolean flag = (mc.thePlayer.experienceLevel > 3 || deathLevel > 3) && playerClass == 0;
            if (skillXP > 0 && !flag)
            {
                left.add("Skill Points: "+skillXP);
            }
            if (flag)
            {
                left.add("You can choose a Class");
            }
            if(playerClass!=0)
            {
            	left.add("Class: "+GuiClasses.classList[playerClass]);
            }
        }
    }
}
