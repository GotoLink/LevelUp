package assets.levelup;

import com.google.common.collect.Sets;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.*;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

public final class PlayerEventHandler {
    /**
     * Configurable flags related to breaking speed
     */
    public static boolean oldSpeedDigging = true, oldSpeedRedstone = true;
    /**
     * Configurable flags related to player death
     */
    public static boolean resetSkillOnDeath = false, resetClassOnDeath = false;
    /**
     * How much each level give in skill points
     */
    public static double xpPerLevel = 3.0D;
    /**
     * Level at which a player can choose a class, and get its first skill points
     */
    public final static int minLevel = 4;
    /**
     * Movement data for Athletics
     */
	public final static UUID speedID = UUID.fromString("4f7637c8-6106-4050-96cb-e47f83bfa415");
    /**
     * Movement data for Sneaking
     */
	public final static UUID sneakID = UUID.fromString("a4dc0b04-f78a-43f6-8805-5ebfbab10b18");
    /**
     * Number of ticks a furnace run
     */
    public final static int maxFurnaceCookTime = 200;
    /**
     * Recently dead players data
     */
	private static Map<UUID, int[]> deathNote = new HashMap<UUID, int[]>();
    /**
     * Random additional loot for Fishing
     */
	public static ItemStack[] lootList = new ItemStack[] { new ItemStack(Items.bone), new ItemStack(Items.reeds), new ItemStack(Items.arrow), new ItemStack(Items.apple),
			new ItemStack(Items.bucket), new ItemStack(Items.boat), new ItemStack(Items.ender_pearl), new ItemStack(Items.fishing_rod), new ItemStack(Items.chainmail_chestplate), new ItemStack(Items.iron_ingot) };
    /**
     * Internal ore counter
     */
    private static Map<Block, Integer> blockToCounter = new IdentityHashMap<Block, Integer>();
    static {
        blockToCounter.put(Blocks.coal_ore, 0);
        blockToCounter.put(Blocks.lapis_ore, 1);
        blockToCounter.put(Blocks.redstone_ore, 2);
        blockToCounter.put(Blocks.iron_ore, 3);
        blockToCounter.put(Blocks.gold_ore, 4);
        blockToCounter.put(Blocks.emerald_ore, 5);
        blockToCounter.put(Blocks.diamond_ore, 6);
        blockToCounter.put(Blocks.quartz_ore, 7);
    }
    /**
     * Blocks that could be crops, but should be left alone by Farming skill
     */
    private static List<IPlantable> blackListedCrops;
    /**
     * Items given by Digging ground
     */
    private static ItemStack digLoot[] = { new ItemStack(Items.clay_ball, 8), new ItemStack(Items.bowl, 2), new ItemStack(Items.coal, 4), new ItemStack(Items.painting), new ItemStack(Items.stick, 4),
            new ItemStack(Items.string, 2) };
    private static ItemStack digLoot1[] = { new ItemStack(Items.stone_sword), new ItemStack(Items.stone_shovel), new ItemStack(Items.stone_pickaxe), new ItemStack(Items.stone_axe) };
    private static ItemStack digLoot2[] = { new ItemStack(Items.slime_ball, 2), new ItemStack(Items.redstone, 8), new ItemStack(Items.iron_ingot), new ItemStack(Items.gold_ingot) };
    private static ItemStack digLoot3[] = { new ItemStack(Items.diamond) };
    /**
     * Internal ores list for Mining
     */
    private static Set<Block> ores = Sets.newIdentityHashSet();
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onBreak(PlayerEvent.BreakSpeed event) {
		ItemStack itemstack = event.entityPlayer.getCurrentEquippedItem();
		if (itemstack != null)
			if (oldSpeedDigging && itemstack.getItem() instanceof ItemSpade) {
				if (event.block instanceof BlockDirt || event.block instanceof BlockGravel) {
					event.newSpeed = event.newSpeed * itemstack.func_150997_a(event.block) / 0.5F;
				}
			} else if (oldSpeedRedstone && itemstack.getItem() instanceof ItemPickaxe && event.block instanceof BlockRedstoneOre) {
				event.newSpeed = event.newSpeed * itemstack.func_150997_a(event.block) / 3F;
			}
		if (event.block instanceof BlockStone || event.block == Blocks.cobblestone || event.block == Blocks.obsidian || (event.block instanceof BlockOre)) {
			event.newSpeed = event.newSpeed + getSkill(event.entityPlayer, 0) / 5 * 0.2F;
		} else if (event.block.getMaterial() == Material.wood) {
			event.newSpeed = event.newSpeed + getSkill(event.entityPlayer, 3) / 5 * 0.2F;
		}
	}

	@SubscribeEvent
	public void onCrafting(cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
		LevelUp.takenFromCrafting(event.player, event.crafting, event.craftMatrix);
	}

    /**
     * Track player deaths to reset values when appropriate,
     * and player final strikes on mobs to give bonus xp
     */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDeath(LivingDeathEvent event) {
		if (event.entityLiving instanceof EntityPlayerMP) {
            if(resetClassOnDeath){
                PlayerExtendedProperties.setPlayerClass((EntityPlayer) event.entityLiving, (byte) 0);
            }
            if(resetSkillOnDeath){
                byte clas = PlayerExtendedProperties.getPlayerClass((EntityPlayer) event.entityLiving);
                PlayerExtendedProperties.setPlayerClass((EntityPlayer) event.entityLiving, (byte) 0);
                PlayerExtendedProperties.resetSkills((EntityPlayer) event.entityLiving, true);
                PlayerExtendedProperties.setPlayerClass((EntityPlayer) event.entityLiving, clas);
            }
			deathNote.put(event.entityLiving.getUniqueID(), PlayerExtendedProperties.getPlayerData((EntityPlayer) event.entityLiving, true));
		} else if (event.entityLiving instanceof EntityMob && event.source.getEntity() instanceof EntityPlayer) {
			LevelUp.giveBonusFightingXP((EntityPlayer) event.source.getEntity());
		}
	}

    /**
     * Change fishing by adding some loots
     * Prevent flagged block placement
     */
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent event) {
		if (event.useItem != Event.Result.DENY)
            if(event.action == Action.RIGHT_CLICK_AIR) {
                EntityFishHook hook = event.entityPlayer.fishEntity;
                if(hook!=null && hook.field_146043_c==null && hook.field_146045_ax>0) {//Not attached to some random stuff, and within the time frame for catching
                    int loot = getFishingLoot(event.entityPlayer);
                    if (loot >= 0) {
                        ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
                        int i = stack.stackSize;
                        int j = stack.getItemDamage();
                        stack.damageItem(loot, event.entityPlayer);
                        event.entityPlayer.swingItem();
                        event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, stack);
                        if (event.entityPlayer.capabilities.isCreativeMode) {
                            stack.stackSize = i;
                            if (stack.isItemStackDamageable()) {
                                stack.setItemDamage(j);
                            }
                        }
                        if (stack.stackSize <= 0) {
                            event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, null);
                            MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(event.entityPlayer, stack));
                        }
                        if (!event.entityPlayer.isUsingItem() && event.entityPlayer instanceof EntityPlayerMP) {
                            ((EntityPlayerMP) event.entityPlayer).sendContainerToPlayer(event.entityPlayer.inventoryContainer);
                        }
                        event.useItem = Event.Result.DENY;
                        if (!hook.worldObj.isRemote) {
                            EntityItem entityitem = new EntityItem(hook.worldObj, hook.posX, hook.posY, hook.posZ, lootList[loot]);
                            double d5 = hook.field_146042_b.posX - hook.posX;
                            double d6 = hook.field_146042_b.posY - hook.posY;
                            double d7 = hook.field_146042_b.posZ - hook.posZ;
                            double d8 = MathHelper.sqrt_double(d5 * d5 + d6 * d6 + d7 * d7);
                            double d9 = 0.1D;
                            entityitem.motionX = d5 * d9;
                            entityitem.motionY = d6 * d9 + MathHelper.sqrt_double(d8) * 0.08D;
                            entityitem.motionZ = d7 * d9;
                            hook.worldObj.spawnEntityInWorld(entityitem);
                            hook.field_146042_b.worldObj.spawnEntityInWorld(new EntityXPOrb(hook.field_146042_b.worldObj, hook.field_146042_b.posX, hook.field_146042_b.posY + 0.5D, hook.field_146042_b.posZ + 0.5D, event.entityPlayer.getRNG().nextInt(6) + 1));
                        }
                    }
                }
            }else if(event.action == Action.RIGHT_CLICK_BLOCK){
                ItemStack itemStack = event.entityPlayer.inventory.getCurrentItem();
                if(itemStack!=null && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("NoPlacing")){
                    event.useItem = Event.Result.DENY;
                }
            }
	}

    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event){
        if(event.harvester!=null){
            int skill;
            Random random = event.harvester.getRNG();
            if (event.block instanceof BlockOre || event.block instanceof BlockRedstoneOre || ores.contains(event.block)) {
                skill = getSkill(event.harvester, 0);
                if (!blockToCounter.containsKey(event.block)) {
                    blockToCounter.put(event.block, blockToCounter.size());
                }
                if(!event.isSilkTouching)
                    LevelUp.incrementOreCounter(event.harvester, blockToCounter.get(event.block));
                if (random.nextDouble() <= skill / 200D) {
                    boolean foundBlock = false;
                    for(ItemStack stack:event.drops) {
                        if(stack != null && event.block == Block.getBlockFromItem(stack.getItem())){
                            writeNoPlacing(stack);
                            stack.stackSize += 1;
                            foundBlock = true;
                            break;
                        }
                    }
                    if(!foundBlock){
                        Item ID = event.block.getItemDropped(event.blockMetadata, random, 0);
                        if(ID!=null){
                            int qutity = event.block.quantityDropped(event.blockMetadata, 0, random);
                            if(qutity>0)
                                event.drops.add(new ItemStack(ID, qutity, event.block.damageDropped(event.blockMetadata)));
                        }
                    }
                }
            } else if (event.block instanceof BlockLog) {
                skill = getSkill(event.harvester, 3);
                if (random.nextDouble() <= skill / 150D) {
                    ItemStack planks = null;
                    for(ItemStack stack:event.drops) {
                        if (stack != null && event.block == Block.getBlockFromItem(stack.getItem())) {
                            planks = getPlanks(event.harvester, event.block, event.blockMetadata, stack.copy());
                            break;
                        }
                    }
                    if(planks != null)
                        event.drops.add(planks);
                }
                if (random.nextDouble() <= skill / 150D) {
                    event.drops.add(new ItemStack(Items.stick, 2));
                }
            } else if (event.block.getMaterial() == Material.ground) {
                skill = getSkill(event.harvester, 11);
                if (random.nextFloat() <= skill / 200F) {
                    ItemStack[] aitemstack4 = digLoot;
                    float f = random.nextFloat();
                    if(f <= 0.002F){
                        aitemstack4 = digLoot3;
                    }
                    else {
                        if (f <= 0.1F) {
                            aitemstack4 = digLoot2;
                        } else if (f <= 0.4F) {
                            aitemstack4 = digLoot1;
                        }
                    }
                    removeFromList(event.drops, event.block);
                    ItemStack itemstack = aitemstack4[random.nextInt(aitemstack4.length)];
                    final int size = itemstack.stackSize;
                    ItemStack toDrop = itemstack.copy();
                    toDrop.stackSize = 1;
                    if (toDrop.getMaxDamage()>20) {
                        toDrop.setItemDamage(random.nextInt(80) + 20);
                    }
                    else {
                        for (int i1 = 0; i1 < size - 1; i1++) {
                            if (random.nextFloat() < 0.5F) {
                                event.drops.add(toDrop.copy());
                            }
                        }
                    }
                    event.drops.add(toDrop);
                }
            } else if (event.block instanceof BlockGravel) {
                skill = getSkill(event.harvester, 11);
                if (random.nextInt(10) < skill / 5) {
                    removeFromList(event.drops, event.block);
                    event.drops.add(new ItemStack(Items.flint));
                }
            }
        }
    }

    private void removeFromList(ArrayList<ItemStack> drops, Block block){
        Iterator<ItemStack> itr = drops.iterator();
        while(itr.hasNext()){
            ItemStack drop = itr.next();
            if(drop!=null && block == Block.getBlockFromItem(drop.getItem())){
                itr.remove();
            }
        }
    }

    /**
     * Convenience method to write the "no-placement" flag onto a block
     */
    private void writeNoPlacing(ItemStack toDrop) {
        NBTTagCompound tagCompound = toDrop.getTagCompound();
        if(tagCompound==null)
            tagCompound = new NBTTagCompound();
        tagCompound.setBoolean("NoPlacing", true);
        toDrop.setTagCompound(tagCompound);
    }

    /**
     * Converts a log block into craftable planks, if possible
     *
     * @return default planks if no crafting against the log is possible
     */
    private ItemStack getPlanks(EntityPlayer player, Block block, int meta, ItemStack drop) {
        if (block != Blocks.log) {
            InventoryCrafting craft = new ContainerPlayer(player.inventory, !player.worldObj.isRemote, player).craftMatrix;
            craft.setInventorySlotContents(1, drop);
            ItemStack planks = CraftingManager.getInstance().findMatchingRecipe(craft, player.worldObj);
            if (planks != null) {
                planks.stackSize = 2;
                return planks;
            }
        }
        return new ItemStack(Blocks.planks, 2, meta & 3);
    }

    /**
     * Adds additional drops for Farming when breaking crops
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBroken(BlockEvent.BreakEvent event){
        if(!event.world.isRemote && event.getPlayer()!=null && event.block!=null){
            if (event.block instanceof BlockCrops || event.block instanceof BlockStem) {//BlockNetherWart ?
                Random random = event.getPlayer().getRNG();
                int skill = getSkill(event.getPlayer(), 9);
                if (random.nextInt(10) < skill / 5) {
                    Item ID = event.block.getItemDropped(event.blockMetadata, random, 0);
                    if(ID!=null)
                        event.world.spawnEntityInWorld(new EntityItem(event.world, event.x, event.y, event.z, new ItemStack(ID, 1, event.block.damageDropped(event.blockMetadata))));
                }
            }
        }
    }

    /**
     * Track player changing dimension to update skill points data
     */
	@SubscribeEvent
	public void onPlayerChangedDimension(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
		loadPlayer(event.player);
	}

    /**
     * Register base skill data to players
     */
	@SubscribeEvent
	public void onPlayerConstruction(EntityEvent.EntityConstructing event) {
		if (event.entity instanceof EntityPlayer) {
			IExtendedEntityProperties skills = event.entity.getExtendedProperties(ClassBonus.SKILL_ID);
			if (skills == null) {
				skills = new PlayerExtendedProperties();
				event.entity.registerExtendedProperties(ClassBonus.SKILL_ID, skills);
			}
		}
	}

    /**
     * Track player login to update skill points data and some configuration values
     */
	@SubscribeEvent
	public void onPlayerLogin(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if(event.player instanceof EntityPlayerMP){
            loadPlayer(event.player);
            LevelUp.configChannel.sendTo(SkillPacketHandler.getConfigPacket(LevelUp.instance.getServerProperties()), (EntityPlayerMP) event.player);
        }
	}

	@SubscribeEvent
	public void onPlayerRespawn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
		if (deathNote.containsKey(event.player.getUniqueID())) {
			PlayerExtendedProperties.setPlayerData(event.player, deathNote.remove(event.player.getUniqueID()));
		}
		loadPlayer(event.player);
	}

	@SubscribeEvent
	public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entityLiving;
            //Furnace speed bonus for Smelting / Cooking
			if (!player.worldObj.isRemote && player.openContainer instanceof ContainerFurnace) {
                TileEntityFurnace furnace = ((ContainerFurnace)player.openContainer).tileFurnace;
                if (furnace != null && furnace.isBurning()) {//isBurning
                    if (furnace.canSmelt()) {//canCook
                        ItemStack stack = furnace.getStackInSlot(0);
                        if (stack != null){
                            int bonus;
                            if (stack.getItem().getItemUseAction(stack) == EnumAction.eat) {
                                bonus = getSkill(player, 7);
                            }else{
                                bonus = getSkill(player, 4);
                            }
                            if (bonus > 10) {
                                int time = player.getRNG().nextInt(bonus / 10);
                                if (time != 0 && furnace.furnaceCookTime + time < maxFurnaceCookTime) {
                                    furnace.furnaceCookTime += time;
                                }
                            }
                        }
                    }
                }
            }
            //Give points on levelup
			if (PlayerExtendedProperties.getPlayerClass(player) != 0){
                double diff = xpPerLevel * (player.experienceLevel - minLevel) + ClassBonus.getBonusPoints() - PlayerExtendedProperties.getSkillPoints(player);
				if(diff >= 1.0D)
                    ClassBonus.addBonusToSkill(player, "XP", (int)Math.floor(diff), true);
			}
            //Farming grow crops
			int skill = getSkill(player, 9);
			if (skill != 0 && !player.worldObj.isRemote && player.getRNG().nextFloat() <= skill / 2500F) {
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
     * Add more output when smelting food for Cooking and other items for Smelting
     */
	@SubscribeEvent
	public void onSmelting(cpw.mods.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent event) {
        if (!event.player.worldObj.isRemote) {
            Random random = event.player.getRNG();
            ItemStack add = null;
            if (event.smelting.getItem().getItemUseAction(event.smelting) == EnumAction.eat) {
                if (random.nextFloat() <= getSkill(event.player, 7) / 200F) {
                    add = event.smelting.copy();
                }
            } else if (random.nextFloat() <= getSkill(event.player, 4) / 200F) {
                add = event.smelting.copy();
            }
            EntityItem entityitem = ForgeHooks.onPlayerTossEvent(event.player, add, true);
            if (entityitem != null) {
                entityitem.delayBeforeCanPickup = 0;
                entityitem.func_145797_a(event.player.getCommandSenderName());
            }
        }
	}

    /**
     * Keep track of registered ores blocks, for mining xp compatibility
     */
    @SubscribeEvent
    public void onOreRegister(OreDictionary.OreRegisterEvent event){
        if(event.Name.startsWith("ore") && event.Ore!=null && event.Ore.getItem()!=null){
            Block ore = Block.getBlockFromItem(event.Ore.getItem());
            if(ore!=Blocks.air && !(ore instanceof BlockOre || ore instanceof BlockRedstoneOre)){
                ores.add(ore);
            }
        }
    }

    /**
     * Helper to get a random slot value for the fish drop list
     *
     * @return -1 if no drop is required
     */
	public static int getFishingLoot(EntityPlayer player) {
		if (player.getRNG().nextDouble() > (getSkill(player, 10) / 5) * 0.05D) {
			return -1;
		} else {
			return player.getRNG().nextInt(lootList.length);
		}
	}

    /**
     * Helper to retrieve skill points from the index
     */
	public static int getSkill(EntityPlayer player, int id) {
		return PlayerExtendedProperties.getSkillFromIndex(player, id);
	}

    /**
     * Help build the packet to send to client for updating skill point data
     */
	public static void loadPlayer(EntityPlayer player) {
		byte cl = PlayerExtendedProperties.getPlayerClass(player);
		int[] data = PlayerExtendedProperties.getPlayerData(player, false);
        LevelUp.initChannel.sendTo(SkillPacketHandler.getPacket(Side.CLIENT, 0, cl, data), (EntityPlayerMP) player);
	}

    /**
     * Apply bonemeal on non-black-listed blocks around player
     */
	private static void growCropsAround(World world, int range, EntityPlayer player) {
		int posX = (int) player.posX;
		int posY = (int) player.posY;
		int posZ = (int) player.posZ;
		int dist = range / 2 + 2;
		for (int x = posX - dist; x < posX + dist + 1; x++) {
			for (int z = posZ - dist; z < posZ + dist + 1; z++) {
				for (int y = posY - dist; y < posY + dist + 1; y++) {
					if (world.isAirBlock(x, y + 1, z)) {
						Block block = world.getBlock(x, y, z);
						if (block instanceof IPlantable && !blackListedCrops.contains(block)) {
							Block soil = world.getBlock(x, y - 1, z);
							if (!soil.isAir(world, x, y - 1, z) && soil.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (IPlantable) block)) {
								ItemDye.applyBonemeal(new ItemStack(Items.dye, 1, 15), world, x, y, z, player);
							}
						}
						break;
					}
				}
			}
		}
	}

    /**
     * Converts given black-listed names into blocks for the internal black-list
     */
    public static void addCropsToBlackList(List<String> blackList){
        if(blackListedCrops==null)
            blackListedCrops = new ArrayList<IPlantable>(blackList.size());
        for(String txt:blackList){
            Object crop = GameData.getBlockRegistry().getObject(txt);
            if(crop instanceof IPlantable)
                blackListedCrops.add((IPlantable)crop);
        }
    }
}
