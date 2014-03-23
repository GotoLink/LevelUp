package assets.levelup;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class SkillProxy implements IGuiHandler {
	public final static int CLASSGUI = 0, SKILLGUI = 1;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case CLASSGUI:
			return new GuiClasses();
		case SKILLGUI:
			return new GuiSkills();
		}
		return null;
	}

	public void registerGui() {
	}

    public EntityPlayer getPlayer(){
        return null;
    }
}
