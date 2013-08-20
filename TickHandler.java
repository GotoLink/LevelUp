package assets.levelup;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockStem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler{

	public static Map<String,BlockPosition> blockClicked = new HashMap();
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
		ItemInWorldManager manager;
		EntityPlayerMP player = null;
		WorldServer world;
		BlockPosition block;
		for(String name :blockClicked.keySet())
		{
			if(name!=null)
			{
				block = blockClicked.get(name);
				world = MinecraftServer.getServer().worldServers[block.position[0]];
				Iterator itr = world.playerEntities.iterator();
				while(itr.hasNext())
				{
					player = (EntityPlayerMP) itr.next();
					if(player.username.equals(name))
						break;
				}
				if(player!=null)
				{
					manager = player.theItemInWorldManager;
					boolean playerDestroys = false;
					try {
						Field fi = ItemInWorldManager.class.getDeclaredField("isDestroyingBlock"/*"durabilityRemainingOnBlock"*/);
						if(!fi.isAccessible())
							fi.setAccessible(true);
						playerDestroys = Boolean.class.cast(fi.get(manager)).booleanValue();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					if(!playerDestroys) //Changes to false right after block breaks
						if(world.getBlockId(block.position[1], block.position[2], block.position[3])!=block.position[4] && player.isSwingInProgress)
						{
							onBlockBreak(world,player,block);
							blockClicked.remove(name);
						}
						else
						{
							blockClicked.remove(name);
						}
				}
			}
		}
		
	}

	private static void onBlockBreak(WorldServer world, EntityPlayerMP player, BlockPosition info)
	{
		Block block = Block.blocksList[info.position[4]];
		int meta = info.position[5];
		//System.out.println("Broken "+block.getUnlocalizedName());
		int skill;
		Random random = new Random();
		if(block instanceof BlockDirt)
		{
			skill = getSkill(player, 11);
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
	            world.spawnEntityInWorld(new EntityItem(world, info.position[1], info.position[2], info.position[3], itemstack1));
	            for (int i1 = 0; i1 < itemstack.stackSize - 1; i1++)
	            {
	                if (random.nextFloat() < 0.5F)
	                {
	                	world.spawnEntityInWorld(new EntityItem(world, info.position[1], info.position[2], info.position[3], itemstack1.copy()));
	                }
	            }
	        }
		}
		else if(block instanceof BlockGravel)
		{
			skill = getSkill(player, 11);
			if(random.nextInt(10)<skill/5)
			{
				world.spawnEntityInWorld(new EntityItem(world, info.position[1], info.position[2], info.position[3], new ItemStack(Item.flint)));
			}
		}
		else if(block instanceof BlockLog)
		{
			skill = getSkill(player, 3);
			if (meta < 3)
	        {
	            if (random.nextDouble() <= skill / 150D)
	            {
	            	world.spawnEntityInWorld(new EntityItem(world, info.position[1], info.position[2], info.position[3], new ItemStack(Item.stick, 2)));
	            }
	            if (random.nextDouble() <= skill / 150D)
	            {
	            	world.spawnEntityInWorld(new EntityItem(world, info.position[1], info.position[2], info.position[3], new ItemStack(Block.planks, 2)));
	            }
	        }
		}
		else if(block instanceof BlockRedstoneOre)
		{
			skill = getSkill(player, 0);
			LevelUp.incrementOreCounter(player, 2);
            if (random.nextDouble() <= skill / 200D)
            {
            	world.spawnEntityInWorld(new EntityItem(world, info.position[1], info.position[2], info.position[3], new ItemStack(block.idDropped(meta, random, meta),block.quantityDropped(random),0)));
            }
		}
		else if(block instanceof BlockCrops || block instanceof BlockStem)
		{
			skill = getSkill(player, 9);
			if(meta<7 && random.nextFloat() <= skill / 50F)
			{
                world.setBlockMetadataWithNotify(info.position[1], info.position[2], info.position[3], meta+1, 2);		                
			}
			if(random.nextInt(10)<skill/5)
			{
				int ID = block.idDropped(meta, null, 0);
				world.spawnEntityInWorld(new EntityItem(world, info.position[1], info.position[2], info.position[3], new ItemStack(ID,1,0)));
			}
		}
	}
	public static int getSkill(EntityPlayer player, int id)
	{
		return PlayerExtendedProperties.getSkillFromIndex(player, id);
	}
	
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
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
		
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() 
	{
		return "LevelUpTick";
	}

}
