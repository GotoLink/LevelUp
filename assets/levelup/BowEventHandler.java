package assets.levelup;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

public final class BowEventHandler {
    public static final BowEventHandler INSTANCE = new BowEventHandler();

    private BowEventHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onSpawn(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.entity;
            if (arrow.shootingEntity instanceof EntityPlayer) {
                int archer = getArcherSkill((EntityPlayer) arrow.shootingEntity);
                if (archer != 0) {
                    arrow.motionX *= 1.0F + archer / 100F;
                    arrow.motionY *= 1.0F + archer / 100F;
                    arrow.motionZ *= 1.0F + archer / 100F;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onBowUse(PlayerUseItemEvent.Start event) {
        if (event.item != null && event.item.getMaxStackSize() == 1 && event.item.getItemUseAction() == EnumAction.bow) {
            int archer = getArcherSkill(event.entityPlayer);
            if (archer != 0 && event.duration > archer / 5)
                event.duration -= (archer / 5);
        }
    }

    public static int getArcherSkill(EntityPlayer player) {
        return PlayerExtendedProperties.getSkillFromIndex(player, 5);
    }
}
