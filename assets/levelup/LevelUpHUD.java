package assets.levelup;

import java.awt.Color;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class LevelUpHUD extends Gui {
	private final Minecraft mc;
	private float val = 0.7F, valIncr = 0.005F;
	private static final int minLevel = 3;
	private static final int minXp = ClassBonus.bonusPoints - PlayerEventHandler.xpPerLevel;

	public LevelUpHUD(Minecraft minecraft) {
		mc = minecraft;
	}

	public void addToText(List<String> left) {
		byte playerClass = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
		if (playerClass != 0) {
			if (!LevelUp.renderExpBar) {
				int skillXP = PlayerExtendedProperties.getSkillFromIndex(mc.thePlayer, "XP");
				if (skillXP > 0) {
					left.add(StatCollector.translateToLocal("hud.skill.text1") + skillXP);
				}
			}
			left.add(StatCollector.translateToLocal("hud.skill.text2") + StatCollector.translateToLocal("class" + playerClass + ".name"));
		} else if (mc.thePlayer.experienceLevel > minLevel || PlayerExtendedProperties.getSkillPoints(mc.thePlayer) > minXp) {
			if (!LevelUp.renderExpBar)
				left.add(StatCollector.translateToLocal("hud.skill.select"));
		}
	}

	@SubscribeEvent
	public void renderLvlUpHUD(RenderGameOverlayEvent.Pre event) {
		if (mc.thePlayer != null) {
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
		int col = Color.HSBtoRGB(0.2929688F, 1.0F, val) & 0xffffff;
		byte playerClass = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
		String text = null;
		if (playerClass != 0) {
			int skillXP = PlayerExtendedProperties.getSkillFromIndex(mc.thePlayer, "XP");
			if (skillXP > 0)
				text = StatCollector.translateToLocal("hud.skill.text1") + skillXP;
		} else if (mc.thePlayer.experienceLevel > minLevel || PlayerExtendedProperties.getSkillPoints(mc.thePlayer) > minXp)
			text = StatCollector.translateToLocal("hud.skill.select");
		int x = (res.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2;
		int y = res.getScaledHeight() - 29;
		if (text != null)
			mc.fontRenderer.drawString(text, x, y, col);
		mc.getTextureManager().bindTexture(Gui.icons);//Icons texture reset
	}
}
