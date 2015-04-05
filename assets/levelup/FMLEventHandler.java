package assets.levelup;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;

import java.util.*;

public final class FMLEventHandler {
    /**
     * Movement data for Athletics
     */
    private static final UUID speedID = UUID.fromString("4f7637c8-6106-4050-96cb-e47f83bfa415");
    /**
     * Movement data for Sneaking
     */
    private static final UUID sneakID = UUID.fromString("a4dc0b04-f78a-43f6-8805-5ebfbab10b18");
    public static final FMLEventHandler INSTANCE = new FMLEventHandler();
    /**
     * Blocks that could be crops, but should be left alone by Farming skill
     */
    private List<IPlantable> blackListedCrops;

    private FMLEventHandler() {
    }

    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            EntityPlayer player = event.player;
            //Furnace speed bonus for Smelting / Cooking
            if (!player.worldObj.isRemote && player.openContainer instanceof ContainerFurnace) {
                TileEntityFurnace furnace = (TileEntityFurnace)((ContainerFurnace) player.openContainer).tileFurnace;
                if (furnace != null && furnace.isBurning()) {//isBurning
                    if (furnace.canSmelt()) {//canCook
                        ItemStack stack = furnace.getStackInSlot(0);
                        if (stack != null) {
                            int bonus;
                            if (stack.getItem().getItemUseAction(stack) == EnumAction.EAT) {
                                bonus = getSkill(player, 7);
                            } else {
                                bonus = getSkill(player, 4);
                            }
                            if (bonus > 10) {
                                int time = player.getRNG().nextInt(bonus / 10);
                                if (time != 0 && furnace.getField(2) + time < furnace.getField(3)) {//Increase burn time
                                    furnace.setField(2, furnace.getField(2) + time);
                                }
                            }
                        }
                    }
                }
            }
            //Give points on levelup
            if (PlayerExtendedProperties.getPlayerClass(player) != 0) {
                double diff = PlayerEventHandler.xpPerLevel * (player.experienceLevel - PlayerEventHandler.minLevel) + ClassBonus.getBonusPoints() - PlayerExtendedProperties.from(player).getSkillPoints();
                if (diff >= 1.0D)
                    PlayerExtendedProperties.from(player).addToSkill("XP", (int) Math.floor(diff));
            }
            //Farming grow crops
            int skill;
            if (!player.worldObj.isRemote && player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof ItemHoe && (skill = getSkill(player, 9)) != 0 && player.getRNG().nextFloat() <= skill / 2500F) {
                growCropsAround(player.worldObj, skill / 4, player);
            }
            //Athletics speed
            IAttributeInstance atinst = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            AttributeModifier mod;
            skill = getSkill(player, 6);
            if (skill != 0) {
                mod = new AttributeModifier(speedID, "SprintingSkillSpeed", skill / 100F, 2);
                if (player.isSprinting()) {
                    if (atinst.getModifier(speedID) == null) {
                        atinst.applyModifier(mod);
                    }
                } else if (atinst.getModifier(speedID) != null) {
                    atinst.removeModifier(mod);
                }
                if (player.fallDistance > 0) {
                    player.fallDistance *= 1 - skill / 5 / 100F;
                }
            }
            //Sneaking speed
            skill = getSkill(player, 8);
            if (skill != 0) {
                mod = new AttributeModifier(sneakID, "SneakingSkillSpeed", 2 * skill / 100F, 2);
                if (player.isSneaking()) {
                    if (atinst.getModifier(sneakID) == null) {
                        atinst.applyModifier(mod);
                    }
                } else if (atinst.getModifier(sneakID) != null) {
                    atinst.removeModifier(mod);
                }
            }
        }
    }

    /**
     * Apply bonemeal on non-black-listed blocks around player
     */
    private void growCropsAround(World world, int range, EntityPlayer player) {
        int posX = (int) player.posX;
        int posY = (int) player.posY;
        int posZ = (int) player.posZ;
        int dist = range / 2 + 2;
        for (Object o : BlockPos.getAllInBox(new BlockPos(posX - dist, posY - dist, posZ - dist), new BlockPos(posX + dist + 1, posY + dist + 1, posZ + dist + 1))) {
            BlockPos pos = (BlockPos) o;
            if (world.isAirBlock(pos.up())) {
                Block block = world.getBlockState(pos).getBlock();
                if (block instanceof IPlantable && !blackListedCrops.contains(block)) {
                    Block soil = world.getBlockState(pos.down()).getBlock();
                    if (!soil.isAir(world, pos.down()) && soil.canSustainPlant(world, pos.down(), EnumFacing.UP, (IPlantable) block)) {
                        ItemDye.applyBonemeal(new ItemStack(Items.dye, 1, 15), world, pos, player);
                    }
                }
                break;
            }
        }
    }

    /**
     * Converts given black-listed names into blocks for the internal black-list
     */
    public void addCropsToBlackList(List<String> blackList) {
        if (blackListedCrops == null)
            blackListedCrops = new ArrayList<IPlantable>(blackList.size());
        for (String txt : blackList) {
            Object crop = GameData.getBlockRegistry().getObject(txt);
            if (crop instanceof IPlantable)
                blackListedCrops.add((IPlantable) crop);
        }
    }

    /**
     * Helper to retrieve skill points from the index
     */
    public static int getSkill(EntityPlayer player, int id) {
        return PlayerExtendedProperties.getSkillFromIndex(player, id);
    }

    /**
     * Add more output when smelting food for Cooking and other items for Smelting
     */
    @SubscribeEvent
    public void onSmelting(PlayerEvent.ItemSmeltedEvent event) {
        if (!event.player.worldObj.isRemote) {
            Random random = event.player.getRNG();
            ItemStack add = null;
            if (event.smelting.getItemUseAction() == EnumAction.EAT) {
                if (random.nextFloat() <= getSkill(event.player, 7) / 200F) {
                    add = event.smelting.copy();
                }
            } else if (random.nextFloat() <= getSkill(event.player, 4) / 200F) {
                add = event.smelting.copy();
            }
            EntityItem entityitem = ForgeHooks.onPlayerTossEvent(event.player, add, true);
            if (entityitem != null) {
                entityitem.setNoPickupDelay();
                entityitem.setOwner(event.player.getCommandSenderName());
            }
        }
    }

    /**
     * Track player crafting to give additional XP
     */
    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        LevelUp.takenFromCrafting(event.player, event.crafting, event.craftMatrix);
    }

    /**
     * Track player changing dimension to update skill points data
     */
    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        loadPlayer(event.player);
    }

    /**
     * Track player respawn to update skill points data
     */
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        loadPlayer(event.player);
    }

    /**
     * Track player login to update skill points data and some configuration values
     */
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            loadPlayer(event.player);
            LevelUp.configChannel.sendTo(SkillPacketHandler.getConfigPacket(LevelUp.instance.getServerProperties()), (EntityPlayerMP) event.player);
        }
    }

    /**
     * Help build the packet to send to client for updating skill point data
     */
    public void loadPlayer(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            byte cl = PlayerExtendedProperties.getPlayerClass(player);
            int[] data = PlayerExtendedProperties.from(player).getPlayerData(false);
            LevelUp.initChannel.sendTo(SkillPacketHandler.getPacket(Side.CLIENT, 0, cl, data), (EntityPlayerMP) player);
        }
    }
}
