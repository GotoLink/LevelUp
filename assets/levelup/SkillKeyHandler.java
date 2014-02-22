package assets.levelup;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

public class SkillKeyHandler {
	public final static KeyBinding keys = new KeyBinding("LvlUpGUI", Keyboard.KEY_L, "game");
	public final static int minLevel = 3;
	private final static int minXP = ClassBonus.bonusPoints - PlayerEventHandler.xpPerLevel;
	private final Minecraft mc = Minecraft.getMinecraft();

    public SkillKeyHandler(){
        ClientRegistry.registerKeyBinding(keys);
    }
    @SubscribeEvent
	public void keyDown(InputEvent.KeyInputEvent event) {
        if (keys.getIsKeyPressed() && mc.currentScreen == null && mc.thePlayer!=null) {
            EntityClientPlayerMP player = mc.thePlayer;
            if (PlayerExtendedProperties.getPlayerClass(player) != 0) {
                player.openGui(LevelUp.instance, SkillProxy.SKILLGUI, mc.theWorld, (int) player.posX, (int) player.posY, (int) player.posZ);
            } else if (player.experienceLevel > minLevel || PlayerExtendedProperties.getSkillPoints(player) > minXP)
                player.openGui(LevelUp.instance, SkillProxy.CLASSGUI, mc.theWorld, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
	}
}
