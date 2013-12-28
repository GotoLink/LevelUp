package assets.levelup;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiSkills extends GuiScreen {
	private boolean closedWithButton;
	private final static int offset = 80;
	private int[] skills = new int[ClassBonus.skillNames.length];
	private int[] skillsPrev = null;
	byte cl = -1;

	@Override
	public void actionPerformed(GuiButton guibutton) {
		if (guibutton.id == 0) {
			closedWithButton = true;
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		} else if (guibutton.id == 100) {
			closedWithButton = false;
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		} else if (guibutton.id < 21) {
			if (skills[skills.length - 1] > 0 && skills[guibutton.id - 1] < 50) {
				Packet packet = SkillPacketHandler.getPacket("LEVELUPSKILLS", mc.thePlayer.entityId, (byte) guibutton.id);
				PacketDispatcher.sendPacketToServer(packet);
			}
		} else if (guibutton.id > 20 && skills[guibutton.id - 21] > skillsPrev[guibutton.id - 21]) {
			Packet packet = SkillPacketHandler.getPacket("LEVELUPSKILLS", mc.thePlayer.entityId, (byte) guibutton.id);
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		String s = "";
		String s1 = "";
		for (Object button: buttonList) {
			int l = ((GuiButton) button).id;
			if (l < 1 || l > 99) {
				continue;
			}
			if (l > 20) {
				l -= 20;
			}
			if (((GuiButton) button).mousePressed(mc, i, j)) {
				s = StatCollector.translateToLocal("skill" + l + ".tooltip1");
				s1 = StatCollector.translateToLocal("skill" + l + ".tooltip2");
			}
		}
		updateSkillList();
		if (cl < 0)
			cl = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
		if (cl > 0) {
			drawCenteredString(fontRenderer, StatCollector.translateToLocal("hud.skill.text2") + StatCollector.translateToLocal("class" + cl + ".name"), width / 2, 2, 0xffffff);
		}
		for (int x = 0; x < 6; x++) {
			drawCenteredString(fontRenderer, StatCollector.translateToLocal("skill" + (x + 1) + ".name") + ": " + skills[x], width / 2 - offset, 20 + 32 * x, 0xffffff);
			drawCenteredString(fontRenderer, StatCollector.translateToLocal("skill" + (x + 7) + ".name") + ": " + skills[x + 6], width / 2 + offset, 20 + 32 * x, 0xffffff);
		}
		drawCenteredString(fontRenderer, s, width / 2, height / 6 + 168, 0xffffff);
		drawCenteredString(fontRenderer, s1, width / 2, height / 6 + 180, 0xffffff);
		super.drawScreen(i, j, f);
	}

	@Override
	public void initGui() {
		closedWithButton = false;
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 + 96, height / 6 + 168, 96, 20, StatCollector.translateToLocal("gui.done")));
		buttonList.add(new GuiButton(100, width / 2 - 192, height / 6 + 168, 96, 20, StatCollector.translateToLocal("gui.cancel")));
		for (int index = 0; index < 6; index++) {
			buttonList.add(new GuiButton(1 + index, (width / 2 + 44) - offset, 15 + 32 * index, 20, 20, "+"));
			buttonList.add(new GuiButton(7 + index, width / 2 + 44 + offset, 15 + 32 * index, 20, 20, "+"));
			buttonList.add(new GuiButton(21 + index, width / 2 - 64 - offset, 15 + 32 * index, 20, 20, "-"));
			buttonList.add(new GuiButton(27 + index, (width / 2 - 64) + offset, 15 + 32 * index, 20, 20, "-"));
		}
	}

	@Override
	public void onGuiClosed() {
		if (!closedWithButton) {
			Packet packet = SkillPacketHandler.getPacket("LEVELUPSKILLS", mc.thePlayer.entityId, (byte) -1, skillsPrev);
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	private void updateSkillList() {
		for (int i = 0; i < skills.length; i++) {
			skills[i] = PlayerExtendedProperties.getSkillFromIndex(mc.thePlayer, i);
		}
		if (skillsPrev == null) {
			skillsPrev = new int[skills.length];
			System.arraycopy(skills, 0, skillsPrev, 0, skills.length);
		}
	}
}
