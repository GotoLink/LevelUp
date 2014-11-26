package assets.levelup;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.Set;

@SuppressWarnings("UnusedDeclaration")
public final class ConfigLevelUp extends GuiScreen implements IModGuiFactory{
    private GuiScreen parent;
    private final boolean[] toggles = new boolean[3];
    public ConfigLevelUp(){}

    public ConfigLevelUp(GuiScreen guiScreen){
        this.parent = guiScreen;
    }

    @Override
    public void initialize(Minecraft minecraftInstance) {
        this.mc = minecraftInstance;
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return this.getClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        toggles[0] = LevelUp.allowHUD;
        toggles[1] = LevelUp.renderTopLeft;
        toggles[2] = LevelUp.renderExpBar;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
        for(int i = 0; i< toggles.length; i++)
            this.buttonList.add(new GuiButton(1+i, this.width / 2 - 75, this.height - 98 - i*60, I18n.format("config.levelup.option"+i, toggles[i])));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 0) {
                FMLClientHandler.instance().showGuiScreen(parent);
            }else if(button.id - 1 < toggles.length){
                toggles[button.id-1] = !toggles[button.id-1];
                button.displayString = I18n.format("config.levelup.option"+(button.id-1), toggles[button.id-1]);
            }
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3){
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("config.levelup.title"), this.width / 2, this.height / 2 - 115, 0xFFFFFF);
        super.drawScreen(par1, par2, par3);
    }

    @Override
    public void onGuiClosed(){
        super.onGuiClosed();
        LevelUp.refreshValues(toggles);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
}
