package assets.levelup;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

public class GuiSkills extends GuiScreen {
	private boolean closedWithButton;
	private final static int offset = 80;
	private int[] skills = new int[ClassBonus.skillNames.length];
	private int[] skillsPrev = null;
	byte cl = -1;

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
		} else if (guibutton.field_146127_k < 21) {
			if (skills[skills.length - 1] > 0 && skills[guibutton.field_146127_k - 1] < 50) {
				FMLProxyPacket packet = SkillPacketHandler.getPacket(Side.SERVER, "LEVELUPSKILLS", field_146297_k.thePlayer.func_145782_y(), (byte) guibutton.field_146127_k);
				LevelUp.skillChannel.sendToServer(packet);
			}
		} else if (guibutton.field_146127_k > 20 && skills[guibutton.field_146127_k - 21] > skillsPrev[guibutton.field_146127_k - 21]) {
			FMLProxyPacket packet = SkillPacketHandler.getPacket(Side.SERVER, "LEVELUPSKILLS", field_146297_k.thePlayer.func_145782_y(), (byte) guibutton.field_146127_k);
            LevelUp.skillChannel.sendToServer(packet);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int i, int j, float f) {
        func_146276_q_();
		String s = "";
		String s1 = "";
		for (Object button: field_146292_n) {
			int l = ((GuiButton) button).field_146127_k;
			if (l < 1 || l > 99) {
				continue;
			}
			if (l > 20) {
				l -= 20;
			}
			if (((GuiButton) button).func_146116_c(field_146297_k, i, j)) {
				s = StatCollector.translateToLocal("skill" + l + ".tooltip1");
				s1 = StatCollector.translateToLocal("skill" + l + ".tooltip2");
			}
		}
		updateSkillList();
		if (cl < 0)
			cl = PlayerExtendedProperties.getPlayerClass(field_146297_k.thePlayer);
		if (cl > 0) {
			drawCenteredString(field_146289_q, StatCollector.translateToLocal("hud.skill.text2") + StatCollector.translateToLocal("class" + cl + ".name"), field_146294_l / 2, 2, 0xffffff);
		}
		for (int x = 0; x < 6; x++) {
			drawCenteredString(field_146289_q, StatCollector.translateToLocal("skill" + (x + 1) + ".name") + ": " + skills[x], field_146294_l / 2 - offset, 20 + 32 * x, 0xffffff);
			drawCenteredString(field_146289_q, StatCollector.translateToLocal("skill" + (x + 7) + ".name") + ": " + skills[x + 6], field_146294_l / 2 + offset, 20 + 32 * x, 0xffffff);
		}
		drawCenteredString(field_146289_q, s, field_146294_l / 2, field_146295_m / 6 + 168, 0xffffff);
		drawCenteredString(field_146289_q, s1, field_146294_l / 2, field_146295_m / 6 + 180, 0xffffff);
		super.drawScreen(i, j, f);
	}

	@Override
	public void initGui() {
		closedWithButton = false;
		field_146292_n.clear();
		field_146292_n.add(new GuiButton(0, field_146294_l / 2 + 96, field_146295_m / 6 + 168, 96, 20, StatCollector.translateToLocal("gui.done")));
		field_146292_n.add(new GuiButton(100, field_146294_l / 2 - 192, field_146295_m / 6 + 168, 96, 20, StatCollector.translateToLocal("gui.cancel")));
		for (int index = 0; index < 6; index++) {
			field_146292_n.add(new GuiButton(1 + index, (field_146294_l / 2 + 44) - offset, 15 + 32 * index, 20, 20, "+"));
			field_146292_n.add(new GuiButton(7 + index, field_146294_l / 2 + 44 + offset, 15 + 32 * index, 20, 20, "+"));
			field_146292_n.add(new GuiButton(21 + index, field_146294_l / 2 - 64 - offset, 15 + 32 * index, 20, 20, "-"));
			field_146292_n.add(new GuiButton(27 + index, (field_146294_l / 2 - 64) + offset, 15 + 32 * index, 20, 20, "-"));
		}
	}

	@Override
	public void func_146281_b() {
		if (!closedWithButton) {
			FMLProxyPacket packet = SkillPacketHandler.getPacket(Side.SERVER, "LEVELUPSKILLS", field_146297_k.thePlayer.func_145782_y(), (byte) -1, skillsPrev);
            LevelUp.skillChannel.sendToServer(packet);
		}
	}

	private void updateSkillList() {
		for (int i = 0; i < skills.length; i++) {
			skills[i] = PlayerExtendedProperties.getSkillFromIndex(field_146297_k.thePlayer, i);
		}
		if (skillsPrev == null) {
			skillsPrev = new int[skills.length];
			System.arraycopy(skills, 0, skillsPrev, 0, skills.length);
		}
	}
}
