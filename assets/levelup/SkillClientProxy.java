package assets.levelup;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("UnusedDeclaration")
public final class SkillClientProxy extends SkillProxy {
    @Override
    public void tryUseMUD(){
        try {
            Class.forName("mods.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, String.class, String.class).invoke(null,
                    FMLCommonHandler.instance().findContainerFor(LevelUp.instance),
                    "https://raw.github.com/GotoLink/LevelUp/master/update.xml",
                    "https://raw.github.com/GotoLink/LevelUp/master/changelog.md"
            );
        } catch (Throwable ignored) {
        }
    }

	@Override
	public void registerGui() {
		if (LevelUp.allowHUD)
			MinecraftForge.EVENT_BUS.register(LevelUpHUD.INSTANCE);
        FMLCommonHandler.instance().bus().register(SkillKeyHandler.INSTANCE);
	}

    @Override
    public EntityPlayer getPlayer(){
        return FMLClientHandler.instance().getClient().thePlayer;
    }
}
