package assets.levelup;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class SkillClientProxy extends SkillProxy {
	@Override
	public void registerGui() {
		if (LevelUp.allowHUD)
			MinecraftForge.EVENT_BUS.register(new LevelUpHUD(Minecraft.getMinecraft()));
        FMLCommonHandler.instance().bus().register(new SkillKeyHandler());
	}
}
