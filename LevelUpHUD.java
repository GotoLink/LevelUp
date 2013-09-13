package assets.levelup;

import java.awt.Color;
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
	private float val=0.7F, valIncr=0.005F;

    public LevelUpHUD(Minecraft minecraft)
    {
        mc = minecraft;
    }

    @ForgeSubscribe
    public void renderLvlUpHUD(RenderGameOverlayEvent.Pre event)
    {
    	if(mc.thePlayer!=null)
        {
	    	if(LevelUp.renderTopLeft && event.type==ElementType.TEXT)
	    		addToText(((RenderGameOverlayEvent.Text)event).left);
	    	if(LevelUp.renderExpBar && event.type==ElementType.EXPERIENCE)
	    	{
            	addToExpBar(event.resolution);
	    	}
        }
    }

    private void addToExpBar(ScaledResolution res) 
    {
    	val += valIncr;
    	if (val >= 1.0F || val <= 0.4F)
        {
            valIncr *= -1F;
        }
    	if (val > 1.0F)
        {
            val = 1.0F;
        }
        if (val < 0.4F)
        {
            val = 0.4F;
        }
        int col = Color.HSBtoRGB(0.2929688F, 1.0F, val) & 0xffffff;
        byte playerClass = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
        String text = null;
        if(playerClass!=0)
        {
        	int skillXP = PlayerExtendedProperties.getSkillFromIndex(mc.thePlayer, "XP");
            if(skillXP > 0)
            	text= "Skill Points: "+skillXP;
        }
        else if(mc.thePlayer.experienceLevel > 3 || PlayerExtendedProperties.getSkillPoints(mc.thePlayer) > 17)
        	text="Choose a Class";
    	int x = (res.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2;
        int y = res.getScaledHeight() - 29;
        if(text!=null)
        	mc.fontRenderer.drawString(text, x, y, col);
        mc.func_110434_K().func_110577_a(Gui.field_110324_m);//Icons texture reset
	}

	public void addToText(List left)
    {
		byte playerClass = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
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
        else if(mc.thePlayer.experienceLevel > 3 || PlayerExtendedProperties.getSkillPoints(mc.thePlayer) > 17)
        {
        	if(!LevelUp.renderExpBar)
        		left.add("Choose a Class");
        }
    }
}
