package assets.levelup;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public final class ItemRespecBook extends Item {
    public ItemRespecBook() {
        super();
        setHasSubtypes(true);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!world.isRemote) {
            PlayerExtendedProperties.from(entityplayer).convertPointsToXp(itemstack.getItemDamage() > 0);
            FMLEventHandler.INSTANCE.loadPlayer(entityplayer);
        }
        if (!entityplayer.capabilities.isCreativeMode)
            itemstack.stackSize--;
        return itemstack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        super.getSubItems(item, tab, list);
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean isAdvanced) {
        if (itemStack.getItemDamage() > 0) {
            list.add(StatCollector.translateToLocal("respecbook.canresetclass"));
        }
    }
}
