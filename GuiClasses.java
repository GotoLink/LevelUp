package assets.levelup;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiClasses extends GuiScreen
{
    private final static String toolTips[] =
    {
        "", "Miner: +10 Mining, +5 Digging, +5 Smelting | Extra XP from mining",
        "Warrior: +10 Melee, +5 Defense, +5 Marksman | Extra XP from combat",
        "Artisan: +10 Smelting, +5 Woodcutting, +5 Cooking | Extra XP from crafting",
        "Spelunker: +10 Defense, +5 Athletics, +5 Mining | Extra XP from mining",
        "Scout: +10 Marksman, +5 Stealth, +5 Athletics | Extra XP from combat",
        "Farmer: +10 Farming, +5 Fishing, +5 Woodcutting | Extra XP from crafting",
        "Archeologist: +10 Digging, +5 Mining, +5 Woodcutting | Extra XP from mining",
        "Assassin: +10 Stealth, +5 Melee, +5 Marksman | Extra XP from combat",
        "Lumberjack: +10 Woodcutting, +5 Defense, +5 Athletics | Extra XP from crafting",
        "Hermit: +10 Cooking, +5 Mining, +5 Digging | Extra XP from mining",
        "Zealot: +10 Athletics, +5 Melee, +5 Defense | Extra XP from combat",
        "Fisherman: +10 Fishing, +5 Cooking, +5 Woodcutting | Extra XP from crafting",
        "Freelancer: +20 Skill Points | No Extra XP"
    };
    public final static String classList[] =
    {
        "<None>", "Miner", "Warrior", "Artisan", "Spelunker", "Scout", "Farmer", "Archeologist", "Assassin", "Lumberjack",
        "Hermit", "Zealot", "Fisherman", "Freelancer"
    };
    private boolean closedWithButton= false;

	@Override
    public void initGui()
    {
        closedWithButton = false;
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 + 96, height / 6 + 168, 96, 20, "Select"));
        buttonList.add(new GuiButton(100, width / 2 - 192, height / 6 + 168, 96, 20, "Cancel"));
        buttonList.add(new GuiButton(1, width / 2 - 160, 18, 96, 20, "Miner"));
        buttonList.add(new GuiButton(2, width / 2 - 48, 18, 96, 20, "Warrior"));
        buttonList.add(new GuiButton(3, width / 2 + 64, 18, 96, 20, "Artisan"));
        buttonList.add(new GuiButton(4, width / 2 - 160, 50, 96, 20, "Spelunker"));
        buttonList.add(new GuiButton(5, width / 2 - 48, 50, 96, 20, "Scout"));
        buttonList.add(new GuiButton(6, width / 2 + 64, 50, 96, 20, "Farmer"));
        buttonList.add(new GuiButton(7, width / 2 - 160, 82, 96, 20, "Archeologist"));
        buttonList.add(new GuiButton(8, width / 2 - 48, 82, 96, 20, "Assassin"));
        buttonList.add(new GuiButton(9, width / 2 + 64, 82, 96, 20, "Lumberjack"));
        buttonList.add(new GuiButton(10, width / 2 - 160, 114, 96, 20, "Hermit"));
        buttonList.add(new GuiButton(11, width / 2 - 48, 114, 96, 20, "Zealot"));
        buttonList.add(new GuiButton(12, width / 2 + 64, 114, 96, 20, "Fisherman"));
        buttonList.add(new GuiButton(classList.length - 1, width / 2 - 48, 146, 96, 20, "Freelancer"));
    }
    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.id == 0)
        {
            closedWithButton = true;
            mc.displayGuiScreen((GuiScreen)null);
            mc.setIngameFocus();
        }
        else if (guibutton.id == 100)
        {
            closedWithButton = false;
            mc.displayGuiScreen((GuiScreen)null);
            mc.setIngameFocus();
        }
        else
        {
            PlayerExtendedProperties.setPlayerClass(mc.thePlayer, (byte)guibutton.id);
        }
    }
    @Override
    public void onGuiClosed()
    {
        if (!closedWithButton)
        {
        	PlayerExtendedProperties.setPlayerClass(mc.thePlayer, (byte) 0);
        }
    }
    @Override
    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        byte cl = PlayerExtendedProperties.getPlayerClass(mc.thePlayer);
        drawCenteredString(fontRenderer, toolTips[cl], width / 2, height / 6 + 148, 0xffffff);
        drawCenteredString(fontRenderer, (new StringBuilder()).append("Your Class: ").append(classList[cl]).toString(), width / 2, height / 6 + 174, 0xffffff);
        super.drawScreen(i, j, f);
    }
}
