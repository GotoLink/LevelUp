package assets.levelup;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import java.awt.*;
import java.util.List;

public final class LevelUpHUD extends Gui {
    private float val = 0.7F, valIncr = 0.005F;

	public void addToText(List<String> left) {
		byte playerClass = PlayerExtendedProperties.getPlayerClass(LevelUp.proxy.getPlayer());
		if (playerClass != 0) {
			if (!LevelUp.renderExpBar) {
				int skillXP = PlayerExtendedProperties.getSkillFromIndex(LevelUp.proxy.getPlayer(), "XP");
				if (skillXP > 0) {
					left.add(StatCollector.translateToLocalFormatted("hud.skill.text1", skillXP));
				}
			}
			left.add(StatCollector.translateToLocalFormatted("hud.skill.text2", StatCollector.translateToLocal("class" + playerClass + ".name")));
		} else if (canSelectClass()) {
			if (!LevelUp.renderExpBar)
				left.add(StatCollector.translateToLocal("hud.skill.select"));
		}
	}

	@SubscribeEvent
	public void renderLvlUpHUD(RenderGameOverlayEvent.Pre event) {
		if (LevelUp.proxy.getPlayer() != null) {
			if (LevelUp.renderTopLeft && event.type == ElementType.TEXT)
				addToText(((RenderGameOverlayEvent.Text) event).left);
			if (LevelUp.renderExpBar && event.type == ElementType.EXPERIENCE) {
				addToExpBar(event.resolution);
			}
		}
	}

	private void addToExpBar(ScaledResolution res) {
		val += valIncr;
		if (val >= 1.0F || val <= 0.4F) {
			valIncr *= -1F;
		}
		if (val > 1.0F) {
			val = 1.0F;
		}
		if (val < 0.4F) {
			val = 0.4F;
		}
		String text = null;
		if (canShowSkills()) {
			int skillXP = PlayerExtendedProperties.getSkillFromIndex(LevelUp.proxy.getPlayer(), "XP");
			if (skillXP > 0)
				text = StatCollector.translateToLocalFormatted("hud.skill.text1", skillXP);
		} else if (canSelectClass())
			text = StatCollector.translateToLocal("hud.skill.select");
		int x = (res.getScaledWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(text)) / 2;
		int y = res.getScaledHeight() - 29;
		if (text != null) {
            int col = Color.HSBtoRGB(0.2929688F, 1.0F, val) & 0xffffff;
            Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, col);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);//Icons texture reset
	}

    public static boolean canSelectClass() {
        if(LevelUp.proxy.getPlayer().experienceLevel >= PlayerEventHandler.minLevel)
            return true;
        else{
            int points = PlayerExtendedProperties.getSkillPoints(LevelUp.proxy.getPlayer());
            return points > PlayerEventHandler.minLevel * PlayerEventHandler.xpPerLevel || points > ClassBonus.getBonusPoints();
        }
    }

    public static boolean canShowSkills(){
        return PlayerExtendedProperties.hasClass(LevelUp.proxy.getPlayer());
    }
}
