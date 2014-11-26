package assets.levelup;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public final class SkillKeyHandler {
	public final static KeyBinding keys = new KeyBinding("LvlUpGUI", Keyboard.KEY_L, "key.categories.gui");

    public SkillKeyHandler(){
        ClientRegistry.registerKeyBinding(keys);
    }

    @SubscribeEvent
	public void keyDown(InputEvent.KeyInputEvent event) {
        if (keys.getIsKeyPressed() && Minecraft.getMinecraft().currentScreen == null && Minecraft.getMinecraft().thePlayer!=null) {
            if (LevelUpHUD.canShowSkills()) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiSkills());
            } else if (LevelUpHUD.canSelectClass())
                Minecraft.getMinecraft().displayGuiScreen(new GuiClasses());
        }
	}
}
