package assets.levelup;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class LevelUpHUD extends Gui
{
    private Minecraft mc;

    public LevelUpHUD(Minecraft minecraft)
    {
        mc = minecraft;
    }

    @ForgeSubscribe
    public void renderLvlUpHUD(RenderGameOverlayEvent.Text event)
    {
    	renderGameOverlay(event.left);
    }

    public void renderGameOverlay(List left)
    {
    	if(mc.thePlayer!=null)
        {
	        byte playerClass = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
            if(playerClass!=0)
            {
            	int skillXP = PlayerExtendedProperties.getSkillFromIndex(mc.thePlayer, "XP");
	            if(skillXP > 0)
	            {
	                left.add("Skill Points: "+skillXP);
	            }
            	left.add("Class: "+GuiClasses.classList[playerClass]);
            }
            else if(mc.thePlayer.experienceLevel > 3 || PlayerExtendedProperties.getPlayerDeathLevel(mc.thePlayer) > 3)
            {
                left.add("You can choose a Class");
            }
        }
    }
}
