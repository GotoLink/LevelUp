package assets.levelup;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeSubscribe;

public class LevelUpHUD extends Gui
{
    private Minecraft mc;

    public LevelUpHUD(Minecraft minecraft)
    {
        mc = minecraft;
    }

    @ForgeSubscribe
    public void renderLvlUpHUD(RenderGameOverlayEvent.Pre event)
    {
    	if(mc.thePlayer!=null)
        {
    		byte playerClass = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
	    	if(LevelUp.renderTopLeft && event.type==ElementType.TEXT)
	    		addToText(((RenderGameOverlayEvent.Text)event).left, playerClass);
	    	if(LevelUp.renderExpBar && event.type==ElementType.EXPERIENCE && playerClass!=0)
	    	{
	    		int skillXP = PlayerExtendedProperties.getSkillFromIndex(mc.thePlayer, "XP");
	            if(skillXP > 0)
	            	addToExpBar(event.resolution,skillXP);
	    	}
        }
    }

    private void addToExpBar(ScaledResolution res, int skillXP) 
    {
    	String text= "Skill Points: "+skillXP;
    	int x = (res.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2;
        int y = res.getScaledHeight() - 29;
        mc.fontRenderer.drawString(text, x, y, 16777215);//White text
        mc.func_110434_K().func_110577_a(Gui.field_110324_m);//Icons texture reset
	}

	public void addToText(List left,byte playerClass)
    {
        if(playerClass!=0)
        {
        	if(!LevelUp.renderExpBar)
        	{
	        	int skillXP = PlayerExtendedProperties.getSkillFromIndex(mc.thePlayer, "XP");
	            if(skillXP > 0)
	            {
	                left.add("Skill Points: "+skillXP);
	            }
        	}
        	left.add("Class: "+GuiClasses.classList[playerClass]);
        }
        else if(mc.thePlayer.experienceLevel > 3 || PlayerExtendedProperties.getPlayerDeathLevel(mc.thePlayer) > 3)
        {
            left.add("You can choose a Class");
        }
    }
}
