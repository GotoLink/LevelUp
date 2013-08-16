package assets.levelup;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class SkillKeyHandler extends KeyHandler {

	public static final KeyBinding[] keys = {new KeyBinding("LvlUpClassesGUI", Keyboard.KEY_K),new KeyBinding("LvlUpSkillsGUI",Keyboard.KEY_L)};
	private Minecraft mc;
	
	public SkillKeyHandler(Minecraft minecraft)
	{
		super(keys, new boolean[]{false});
		mc = minecraft;
	}

	@Override
	public String getLabel() 
	{
		return "LevelUpKeys";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) 
	{
		if(tickEnd)
		{
			EntityClientPlayerMP player = mc.thePlayer;
			if(kb.keyCode==keys[0].keyCode)
			{
				player.openGui(LevelUp.instance, 0, mc.theWorld,(int) player.posX,(int) player.posY,(int) player.posZ);
			}
			else if(kb.keyCode==keys[1].keyCode)
			{
				player.openGui(LevelUp.instance, 1, mc.theWorld,(int) player.posX,(int) player.posY,(int) player.posZ);
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) 
	{
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.CLIENT);
	}

}
