package assets.levelup;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class SkillClientProxy extends SkillProxy {
	@Override
	public void registerGui()
	{
		if(LevelUp.allowHUD)
			MinecraftForge.EVENT_BUS.register(new LevelUpHUD(Minecraft.getMinecraft()));
		KeyBindingRegistry.registerKeyBinding(new SkillKeyHandler(Minecraft.getMinecraft()));
	}
}
