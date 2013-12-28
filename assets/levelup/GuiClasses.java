package assets.levelup;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiClasses extends GuiScreen {
	private boolean closedWithButton = false;
	private byte cl = -1;

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		updateClass();
		drawCenteredString(fontRenderer, StatCollector.translateToLocal("class" + cl + ".tooltip"), width / 2, height / 6 + 148, 0xffffff);
		drawCenteredString(fontRenderer, StatCollector.translateToLocal("gui.class.title") + StatCollector.translateToLocal("class" + cl + ".name"), width / 2, height / 6 + 174, 0xffffff);
		super.drawScreen(i, j, f);
	}

	@Override
	public void initGui() {
		closedWithButton = false;
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 + 96, height / 6 + 168, 96, 20, StatCollector.translateToLocal("gui.done")));
		buttonList.add(new GuiButton(100, width / 2 - 192, height / 6 + 168, 96, 20, StatCollector.translateToLocal("gui.cancel")));
		for (int j = 1; j < 13; j = j + 3) {
			for (int i = 0; i < 3; i++) {
				buttonList.add(new GuiButton(i + j, width / 2 - 160 + i * 112, 18 + 32 * (j - 1) / 3, 96, 20, StatCollector.translateToLocal("class" + (i + j) + ".name")));
			}
		}
		buttonList.add(new GuiButton(13, width / 2 - 48, 146, 96, 20, StatCollector.translateToLocal("class13.name")));
	}

	@Override
	public void onGuiClosed() {
		if (!closedWithButton) {
			Packet packet = SkillPacketHandler.getPacket("LEVELUPCLASSES", mc.thePlayer.entityId, (byte) 0);
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if (guibutton.id == 0) {
			closedWithButton = true;
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		} else if (guibutton.id == 100) {
			closedWithButton = false;
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		} else {
			Packet packet = SkillPacketHandler.getPacket("LEVELUPCLASSES", mc.thePlayer.entityId, (byte) guibutton.id);
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	private void updateClass() {
		cl = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
	}
}
