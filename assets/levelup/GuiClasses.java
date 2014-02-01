package assets.levelup;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

public class GuiClasses extends GuiScreen {
	private boolean closedWithButton = false;
	private byte cl = -1;

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int i, int j, float f) {
        func_146276_q_();
		updateClass();
		drawCenteredString(field_146289_q, StatCollector.translateToLocal("class" + cl + ".tooltip"), field_146294_l / 2, field_146295_m / 6 + 148, 0xffffff);
		drawCenteredString(field_146289_q, StatCollector.translateToLocal("gui.class.title") + StatCollector.translateToLocal("class" + cl + ".name"), field_146294_l / 2, field_146295_m / 6 + 174, 0xffffff);
		super.drawScreen(i, j, f);
	}

	@Override
	public void initGui() {
		closedWithButton = false;
        field_146292_n.clear();
        field_146292_n.add(new GuiButton(0, field_146294_l / 2 + 96, field_146295_m / 6 + 168, 96, 20, StatCollector.translateToLocal("gui.done")));
        field_146292_n.add(new GuiButton(100, field_146294_l / 2 - 192, field_146295_m / 6 + 168, 96, 20, StatCollector.translateToLocal("gui.cancel")));
		for (int j = 1; j < 13; j = j + 3) {
			for (int i = 0; i < 3; i++) {
                field_146292_n.add(new GuiButton(i + j, field_146294_l / 2 - 160 + i * 112, 18 + 32 * (j - 1) / 3, 96, 20, StatCollector.translateToLocal("class" + (i + j) + ".name")));
			}
		}
        field_146292_n.add(new GuiButton(13, field_146294_l / 2 - 48, 146, 96, 20, StatCollector.translateToLocal("class13.name")));
	}

	@Override
	public void func_146281_b() {
		if (!closedWithButton) {
			FMLProxyPacket packet = SkillPacketHandler.getPacket(Side.SERVER, "LEVELUPCLASSES", field_146297_k.thePlayer.func_145782_y(), (byte) 0);
            LevelUp.classChannel.sendToServer(packet);
		}
	}

	@Override
	protected void func_146284_a(GuiButton guibutton) {
		if (guibutton.field_146127_k == 0) {
			closedWithButton = true;
            field_146297_k.func_147108_a(null);
            field_146297_k.setIngameFocus();
		} else if (guibutton.field_146127_k == 100) {
			closedWithButton = false;
            field_146297_k.func_147108_a(null);
            field_146297_k.setIngameFocus();
		} else {
			FMLProxyPacket packet = SkillPacketHandler.getPacket(Side.SERVER, "LEVELUPCLASSES", field_146297_k.thePlayer.func_145782_y(), (byte) guibutton.field_146127_k);
            LevelUp.classChannel.sendToServer(packet);
		}
	}

	private void updateClass() {
		cl = PlayerExtendedProperties.getPlayerClass(field_146297_k.thePlayer);
	}
}
