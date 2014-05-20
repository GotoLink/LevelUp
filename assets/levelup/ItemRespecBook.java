package assets.levelup;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemRespecBook extends Item {
	public static boolean resClass = false;

	public ItemRespecBook() {
		super();
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if(!world.isRemote){
            PlayerExtendedProperties.getSkillMap(entityplayer).put("XP", PlayerExtendedProperties.getSkillPoints(entityplayer));
            if (resClass)
                PlayerExtendedProperties.setPlayerClass(entityplayer, (byte) 0);
            PlayerExtendedProperties.resetSkills(entityplayer, false);
            PlayerEventHandler.loadPlayer(entityplayer);
        }
		if (!entityplayer.capabilities.isCreativeMode)
			itemstack.stackSize--;
		return itemstack;
	}
}
