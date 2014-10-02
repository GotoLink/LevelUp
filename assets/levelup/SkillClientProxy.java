package assets.levelup;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class SkillClientProxy extends SkillProxy {
	@Override
	public void registerGui() {
		if (LevelUp.allowHUD)
			MinecraftForge.EVENT_BUS.register(new LevelUpHUD());
        FMLCommonHandler.instance().bus().register(new SkillKeyHandler());
	}

    @Override
    public EntityPlayer getPlayer(){
        return FMLClientHandler.instance().getClient().thePlayer;
    }
}
