package assets.levelup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockStem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.ICraftingHandler;

public class PlayerEventHandler implements ICraftingHandler{
	
	@ForgeSubscribe
	public void onPlayerConstruction(EntityEvent.EntityConstructing event)
	{
		if(event.entity instanceof EntityPlayer && !event.isCanceled())
		{
			IExtendedEntityProperties skills = event.entity.getExtendedProperties(ClassBonus.SKILL_ID);
			if(skills == null)
			{
				skills = new PlayerExtendedProperties();
				event.entity.registerExtendedProperties(ClassBonus.SKILL_ID, skills);
			}
		}
	}
	
	@ForgeSubscribe
	public void onBreak(PlayerEvent.BreakSpeed event)
	{
        ItemStack itemstack = event.entityPlayer.getCurrentEquippedItem();
        if (itemstack != null)
	        if (itemstack.getItem() instanceof ItemSpade)
	        {
	            if(event.block instanceof BlockDirt || event.block instanceof BlockGravel)
	            {
	            	event.newSpeed = event.originalSpeed*itemstack.getStrVsBlock(Block.dirt)/0.5F;
	            }
	        }
	        else if (itemstack.getItem() instanceof ItemPickaxe && event.block instanceof BlockRedstoneOre)	
	        {
	            event.newSpeed = event.originalSpeed*itemstack.getStrVsBlock(Block.oreRedstone)/3F;
	        }
	}
	
	/*@ForgeSubscribe
	public void onHarvest(PlayerEvent.HarvestCheck event)
	{
		
	}
	*/
	private static ItemStack lootList[]= (new ItemStack[]
            {
            new ItemStack(Item.bone), new ItemStack(Item.reed), new ItemStack(Item.arrow), new ItemStack(Item.appleRed), new ItemStack(Item.bucketEmpty), new ItemStack(Item.boat), new ItemStack(Item.enderPearl), new ItemStack(Item.fishingRod), new ItemStack(Item.plateChain), new ItemStack(Item.ingotIron)
        });
	private static ItemStack digLoot[] =
        {
            new ItemStack(Item.clay, 8), new ItemStack(Item.bowlEmpty, 2), new ItemStack(Item.coal, 4), new ItemStack(Item.painting), new ItemStack(Item.stick, 4), new ItemStack(Item.silk, 2)
        };
	private static ItemStack digLoot1[] =
        {
            new ItemStack(Item.swordStone), new ItemStack(Item.shovelStone), new ItemStack(Item.pickaxeStone), new ItemStack(Item.axeStone)
        };
	private static ItemStack digLoot2[] =
        {
            new ItemStack(Item.slimeBall, 2), new ItemStack(Item.redstone, 8), new ItemStack(Item.ingotIron), new ItemStack(Item.ingotGold)
        };
	private static ItemStack digLoot3[] = { new ItemStack(Item.diamond) };
	
	@ForgeSubscribe
	public void onInteract(PlayerInteractEvent event)
	{
		if(event.useItem != Event.Result.DENY && event.action==Action.RIGHT_CLICK_AIR && event.entityPlayer.fishEntity!=null)
		{
			EntityFishHook hook = event.entityPlayer.fishEntity;
			int loot = getFishingLoot(event.entityPlayer);
			if(loot>=0)
			{
				ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
				int i = stack.stackSize;
		        int j = stack.getItemDamage();
	            stack.damageItem(loot, event.entityPlayer);
	            event.entityPlayer.swingItem();
	        	event.entityPlayer.inventory.mainInventory[event.entityPlayer.inventory.currentItem] = stack;

	            if (event.entityPlayer.capabilities.isCreativeMode)
	            {
	                stack.stackSize = i;

	                if (stack.isItemStackDamageable())
	                {
	                    stack.setItemDamage(j);
	                }
	            }
	            if (stack.stackSize == 0)
	            {
	            	event.entityPlayer.inventory.mainInventory[event.entityPlayer.inventory.currentItem] = null;
	                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(event.entityPlayer, stack));
	            }
	            if (!event.entityPlayer.isUsingItem())
	            {
	                ((EntityPlayerMP)event.entityPlayer).sendContainerToPlayer(event.entityPlayer.inventoryContainer);
	            }
				event.useItem = Event.Result.DENY;
				if(!hook.worldObj.isRemote)
				{
					EntityItem entityitem = new EntityItem(hook.worldObj, hook.posX, hook.posY, hook.posZ, lootList[loot]);
	                double d5 = hook.angler.posX - hook.posX;
	                double d6 = hook.angler.posY - hook.posY;
	                double d7 = hook.angler.posZ - hook.posZ;
	                double d8 = (double)MathHelper.sqrt_double(d5 * d5 + d6 * d6 + d7 * d7);
	                double d9 = 0.1D;
	                entityitem.motionX = d5 * d9;
	                entityitem.motionY = d6 * d9 + (double)MathHelper.sqrt_double(d8) * 0.08D;
	                entityitem.motionZ = d7 * d9;
	                hook.worldObj.spawnEntityInWorld(entityitem);
	                hook.angler.worldObj.spawnEntityInWorld(new EntityXPOrb(hook.angler.worldObj, hook.angler.posX, hook.angler.posY + 0.5D, hook.angler.posZ + 0.5D, new Random().nextInt(6) + 1));              
				}
			}
		}
		else if(event.action==Action.LEFT_CLICK_BLOCK && event.entityPlayer instanceof EntityPlayerMP)
		{
			World world = event.entityPlayer.worldObj;
			ItemInWorldManager manager = ((EntityPlayerMP)event.entityPlayer).theItemInWorldManager;
			try {
				Field fi = ItemInWorldManager.class.getDeclaredField("receivedFinishDiggingPacket");
				if(!fi.isAccessible())
					fi.setAccessible(true);
				if(!world.isRemote && Boolean.class.cast(fi.get(manager)).booleanValue())
				{
					int id = world.getBlockId(event.x,event.y,event.z);
					int meta = world.getBlockMetadata(event.x, event.y, event.z);
					Block block = Block.blocksList[id];
					int skill;
					Random random = new Random();
					if(block instanceof BlockDirt)
					{
						skill = getSkill(event.entityPlayer, 11);
				        if (random.nextFloat() <= skill / 200F)
				        {
				            ItemStack[] aitemstack4 = digLoot;
				            float f = random.nextFloat();
				            if (f <= 0.1F)
				            {
				                aitemstack4 = digLoot2;
				            }
				            if (f <= 0.4F)
				            {
				                aitemstack4 = digLoot1;
				            }
				            if (random.nextInt(500) == 0)
				            {
				                aitemstack4 = digLoot3;
				            }
				            ItemStack itemstack = aitemstack4[random.nextInt(aitemstack4.length)];
				            ItemStack itemstack1 = itemstack.copy();
				            itemstack1.stackSize = 1;
				            if (aitemstack4 == digLoot1)
				            {
				                itemstack1.setItemDamage(random.nextInt(80) + 20);
				            }
				            world.spawnEntityInWorld(new EntityItem(world, event.x,event.y,event.z, itemstack1));
				            for (int i1 = 0; i1 < itemstack.stackSize - 1; i1++)
				            {
				                if (random.nextFloat() < 0.5F)
				                {
				                	world.spawnEntityInWorld(new EntityItem(world, event.x,event.y,event.z, itemstack1.copy()));
				                }
				            }
				        }
					}
					else if(block instanceof BlockGravel)
					{
						skill = getSkill(event.entityPlayer, 11);
						if(random.nextInt(10)<skill/5)
						{
							world.spawnEntityInWorld(new EntityItem(world, event.x,event.y,event.z, new ItemStack(Item.flint)));
						}
					}
					else if(block instanceof BlockLog)
					{
						skill = getSkill(event.entityPlayer, 3);
						if (meta < 3)
				        {
				            
				            if (random.nextDouble() <= skill / 150D)
				            {
				            	world.spawnEntityInWorld(new EntityItem(world, event.x,event.y,event.z, new ItemStack(Item.stick, 2)));
				            }
				            if (random.nextDouble() <= skill / 150D)
				            {
				            	world.spawnEntityInWorld(new EntityItem(world, event.x,event.y,event.z, new ItemStack(Block.planks, 2)));
				            }
				        }
					}
					else if(block instanceof BlockRedstoneOre)
					{
						skill = getSkill(event.entityPlayer, 0);
						LevelUp.incrementOreCounter(event.entityPlayer, 2);
			            if (random.nextDouble() <= skill / 200D)
			            {
			            	world.spawnEntityInWorld(new EntityItem(world, event.x,event.y,event.z, new ItemStack(block.idDropped(meta, random, meta),block.quantityDropped(random),0)));
			            }
					}
					else if(block instanceof BlockCrops || block instanceof BlockStem)
					{
						skill = getSkill(event.entityPlayer, 9);
						if(meta<7 && random.nextFloat() <= skill / 50F)
						{
		                    world.setBlockMetadataWithNotify(event.x,event.y,event.z, meta+1, 2);		                
						}
						if(random.nextInt(10)<skill/5)
						{
							int ID = block.idDropped(meta, null, 0);
							world.spawnEntityInWorld(new EntityItem(world, event.x,event.y,event.z, new ItemStack(ID,1,0)));
						}
					}
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (NoSuchFieldException e) {
			} catch (SecurityException e) {
			}
		}
	}

	public static int getFishingLoot(EntityPlayer player)
    {
        if (new Random().nextDouble() > (getSkill(player, 10) / 5) * 0.05D)
        {
            return -1;
        }
        else
        {
            return new Random().nextInt(lootList.length);
        }
    }
	
	@ForgeSubscribe
	public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			if(!player.worldObj.isRemote && player.openContainer instanceof ContainerFurnace)
			{
				TileEntityFurnace furnace = null;
				try 
				{
					Field teFurnace = ContainerFurnace.class.getDeclaredField("furnace");
					if(!teFurnace.isAccessible())
						teFurnace.setAccessible(true);
					furnace = TileEntityFurnace.class.cast(teFurnace.get(player.openContainer));
				
					if(furnace!=null && furnace.isBurning())
					{
						Method smelt = TileEntityFurnace.class.getDeclaredMethod("canSmelt");
						if(!smelt.isAccessible())
							smelt.setAccessible(true);
						if(Boolean.class.cast(smelt.invoke(furnace)).booleanValue())
						{
							Field items = TileEntityFurnace.class.getDeclaredField("furnaceItemStacks");				
							if(!items.isAccessible())
								items.setAccessible(true);
							ItemStack stack = ItemStack[].class.cast(items.get(furnace))[0];
							if (stack!=null)
								if (stack.getItem() instanceof ItemFood)
				                {
				                    furnace.furnaceCookTime = MathHelper.floor_float(furnace.furnaceCookTime+(getSkill(player, 7) / 5) * 0.1F);
				                }
				                else
				                {
				                	furnace.furnaceCookTime = MathHelper.floor_float(furnace.furnaceCookTime+(getSkill(player, 4) / 5) * 0.1F);
				                }
						}
					} 
				}catch (ReflectiveOperationException e) {} 
				catch (SecurityException e) {} 
				catch (IllegalArgumentException e) {}
			}
		}
	}
	
	public static int getSkill(EntityPlayer player, int id)
	{
		return PlayerExtendedProperties.getSkillFromIndex(player, id);
	}
	
	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) 
	{
		LevelUp.takenFromCrafting(player, item, craftMatrix);
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) 
	{
		Random random = new Random();
		if (item.getItem() instanceof ItemFood)
        {
            if (random.nextFloat() <= (float)getSkill(player,7) / 200F)
            {
                item.stackSize++;
            }
        }
        else if (random.nextFloat() <= (float)getSkill(player, 4) / 200F)
        {
        	item.stackSize++;
        }
	}
}
