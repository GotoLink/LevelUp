package assets.levelup;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("UnusedDeclaration")
public final class SkillClientProxy extends SkillProxy {
    @Override
    public void tryUseMUD() {
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
        MinecraftForge.EVENT_BUS.register(LevelUpHUD.INSTANCE);
        FMLCommonHandler.instance().bus().register(SkillKeyHandler.INSTANCE);
    }

    @Override
    public EntityPlayer getPlayer() {
        return FMLClientHandler.instance().getClient().thePlayer;
    }

    @Override
    public void register(Item item, String id){
        final ModelResourceLocation model = new ModelResourceLocation(id, "inventory");
        ItemModelMesher mesher = FMLClientHandler.instance().getClient().getRenderItem().getItemModelMesher();
        mesher.register(item, 0, model);
        if(item.getHasSubtypes())
            mesher.register(item, new ItemMeshDefinition() {
                    @Override
                    public ModelResourceLocation getModelLocation(ItemStack stack) {
                        return model;
                    }
                });
    }
}
