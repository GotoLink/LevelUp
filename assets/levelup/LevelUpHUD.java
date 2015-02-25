package assets.levelup;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import java.awt.*;
import java.util.List;

public final class LevelUpHUD extends Gui {
    public static final LevelUpHUD INSTANCE = new LevelUpHUD();
    private float val, valIncr;

    private LevelUpHUD() {
        val = 0.7F;
        valIncr = 0.005F;
    }

    public void addToText(List<String> left) {
        byte playerClass = PlayerExtendedProperties.getPlayerClass(LevelUp.proxy.getPlayer());
        if (playerClass != 0) {
            if (!LevelUp.renderExpBar) {
                int skillXP = PlayerExtendedProperties.from(LevelUp.proxy.getPlayer()).getSkillFromIndex("XP");
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
        if (LevelUp.allowHUD && LevelUp.proxy.getPlayer() != null) {
            if (LevelUp.renderTopLeft && event.type == ElementType.TEXT)
                addToText(((RenderGameOverlayEvent.Text) event).left);
            if (LevelUp.renderExpBar && event.type == ElementType.EXPERIENCE)
                addToExpBar(event.resolution);
        }
    }

    @SubscribeEvent
    public void onFOV(FOVUpdateEvent event){
        if(!LevelUp.changeFOV && !event.entity.isUsingItem()) {
            int skill = 0;
            if(event.entity.isSneaking()){
                skill = 2 * FMLEventHandler.getSkill(event.entity, 8);
            }else if(event.entity.isSprinting()){
                skill = FMLEventHandler.getSkill(event.entity, 6);
            }
            if(skill > 0){
                event.newfov -= 0.5F;
                event.newfov *=  1/(1.0F + skill / 100F);
                event.newfov += 0.5F;
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
            int skillXP = PlayerExtendedProperties.from(LevelUp.proxy.getPlayer()).getSkillFromIndex("XP");
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
        if (LevelUp.proxy.getPlayer().experienceLevel >= PlayerEventHandler.minLevel)
            return true;
        else {
            int points = PlayerExtendedProperties.from(LevelUp.proxy.getPlayer()).getSkillPoints();
            return points > PlayerEventHandler.minLevel * PlayerEventHandler.xpPerLevel || points > ClassBonus.getBonusPoints();
        }
    }

    public static boolean canShowSkills() {
        return PlayerExtendedProperties.from(LevelUp.proxy.getPlayer()).hasClass();
    }
}
