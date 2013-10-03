package assets.levelup;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class BowEventHandler {
	
	@ForgeSubscribe(receiveCanceled=true)
	public void onSpawn(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof EntityArrow )
		{
			EntityArrow arrow = (EntityArrow) event.entity;
			if(arrow.shootingEntity instanceof EntityPlayer)
			{
				int archer = getArcherSkill((EntityPlayer) arrow.shootingEntity);
				if(archer!=0)
				{
					arrow.motionX *= 1.0F + archer / 100F;
					arrow.motionY *= 1.0F + archer / 100F;
					arrow.motionZ *= 1.0F + archer / 100F;
				}
			}
		}
	}
	
	@ForgeSubscribe(receiveCanceled=true)
	public void onBowUse(ArrowNockEvent event)
	{
		int archer = getArcherSkill(event.entityPlayer);
		if(archer!=0)
		{
			if (event.entityPlayer.capabilities.isCreativeMode || event.entityPlayer.inventory.hasItem(Item.arrow.itemID))
	        {
				event.entityPlayer.setItemInUse(event.result, event.result.getItem().getMaxItemUseDuration(event.result)-archer/5);
	        }
			event.setCanceled(true);
		}
	}
	
	public static int getArcherSkill(EntityPlayer player)
	{
		return PlayerExtendedProperties.getSkillFromIndex(player, 5);
	}
}
