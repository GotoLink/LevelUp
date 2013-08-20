package assets.levelup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="levelup",name="Level Up!",version="alpha")
@NetworkMod(clientSideRequired=true,channels={"LEVELUPCLASSES","LEVELUPSKILLS"},packetHandler=SkillPacketHandler.class)
public class LevelUp
{
	@Instance(value="levelup")
	public static LevelUp instance;
	@SidedProxy(clientSide="assets.levelup.SkillClientProxy",serverSide="assets.levelup.SkillProxy")
	public static SkillProxy proxy;
	
    public static Item respecBook,xpTalisman;
    public static int respecBookID = 11113,xpTalismanID = 11114;
    public static boolean allowHUD;
    
    private static Map<Integer,Integer> towItems = new HashMap();
    private static int[] ingrTier1,ingrTier2,ingrTier3,ingrTier4;
	
    
    @EventHandler
    public void load(FMLPreInitializationEvent event)
    {
    	Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    	config.load();
    	respecBookID = config.getItem("item", "respecbookid", respecBookID).getInt();
    	xpTalismanID = config.getItem("item", "xptalismanid", xpTalismanID).getInt();
    	allowHUD = config.get("general", "allowHUD", true).getBoolean(true);
    	if(config.hasChanged())
    		config.save();
        ingrTier1 = (new int[]
                {
                    Item.stick.itemID, Item.leather.itemID, Block.stone.blockID
                });
        ingrTier2 = (new int[]
                {
                    Item.ingotIron.itemID, Item.ingotGold.itemID, Item.paper.itemID, Item.slimeBall.itemID
                });
        ingrTier3 = (new int[]
                {
                    Item.redstone.itemID, Item.glowstone.itemID, Item.enderPearl.itemID
                });
        ingrTier4 = (new int[]
                {
                    Item.diamond.itemID
                });
        towItems.put(Integer.valueOf(Block.wood.blockID), Integer.valueOf(2));
        towItems.put(Integer.valueOf(Item.coal.itemID), Integer.valueOf(4));
        towItems.put(Integer.valueOf(Item.brick.itemID), Integer.valueOf(4));
        towItems.put(Integer.valueOf(Item.book.itemID), Integer.valueOf(4));
        towItems.put(Integer.valueOf(Block.oreIron.blockID), Integer.valueOf(8));
        towItems.put(Integer.valueOf(Item.dyePowder.itemID), Integer.valueOf(8));
        towItems.put(Integer.valueOf(Item.redstone.itemID), Integer.valueOf(8));
        towItems.put(Integer.valueOf(Item.bread.itemID), Integer.valueOf(10));
        towItems.put(Integer.valueOf(Item.melon.itemID), Integer.valueOf(10));
        towItems.put(Integer.valueOf(Block.pumpkin.blockID), Integer.valueOf(10));
        towItems.put(Integer.valueOf(Item.porkCooked.itemID), Integer.valueOf(12));
        towItems.put(Integer.valueOf(Item.beefCooked.itemID), Integer.valueOf(12));
        towItems.put(Integer.valueOf(Item.chickenCooked.itemID), Integer.valueOf(12));
        towItems.put(Integer.valueOf(Item.fishCooked.itemID), Integer.valueOf(12));
        towItems.put(Integer.valueOf(Item.ingotIron.itemID), Integer.valueOf(16));
        towItems.put(Integer.valueOf(Block.oreGold.blockID), Integer.valueOf(20));
        towItems.put(Integer.valueOf(Item.ingotGold.itemID), Integer.valueOf(24));
        towItems.put(Integer.valueOf(Item.diamond.itemID), Integer.valueOf(40));
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	respecBook = new ItemRespecBook(respecBookID).setUnlocalizedName("respecBook").func_111206_d("levelup:RespecBook");
        xpTalisman = new Item(xpTalismanID).setUnlocalizedName("xpTalisman").func_111206_d("levelup:XPTalisman");
    	LanguageRegistry.instance().addName(respecBook, "Book of Unlearning");
    	GameRegistry.addRecipe(new ItemStack(respecBook, 1), new Object[]
            {
                "OEO", "DBD", "ODO", 
                Character.valueOf('O'), Block.obsidian,
                Character.valueOf('D'), new ItemStack(Item.dyePowder, 1, 0),
                Character.valueOf('E'), Item.enderPearl,
                Character.valueOf('B'), Item.book
            });
    	LanguageRegistry.instance().addName(xpTalisman, "Talisman of Wonder");
    	ItemStack talisman = new ItemStack(xpTalisman, 1);
        GameRegistry.addRecipe(talisman, new Object[]
                {
                    "GG ", " R ", " GG", 
                    Character.valueOf('G'), Item.ingotGold,
                    Character.valueOf('R'), Item.redstone
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.coal
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Block.oreGold
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Block.oreIron
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.diamond
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Block.wood
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.brick
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.book
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, new ItemStack(Item.dyePowder, 1, 4)
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.redstone
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.bread
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.melon
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.porkCooked
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.beefCooked
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.chickenCooked
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.fishCooked
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.ingotIron
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Item.ingotGold
                });
        GameRegistry.addShapelessRecipe(talisman, new Object[]
                {
                    xpTalisman, Block.pumpkin
                });
        GameRegistry.addRecipe(new ItemStack(Item.pumpkinSeeds, 4), new Object[]
                {
                    "#", Character.valueOf('#'), Block.pumpkin
                });
        GameRegistry.addRecipe(new ItemStack(Block.gravel, 4), new Object[]
                {
                    "##", "##", Character.valueOf('#'), Item.flint
                });
        
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(new BowEventHandler());
        MinecraftForge.EVENT_BUS.register(new FightEventHandler());
        NetworkRegistry.instance().registerGuiHandler(this, proxy);
        TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
        proxy.registerGui();
    }
    
    public static void takenFromCrafting(EntityPlayer player, ItemStack itemstack, IInventory iinventory)
    {
        if (isTalismanRecipe(iinventory))
        {
            for (int i = 0; i < iinventory.getSizeInventory(); i++)
            {
                ItemStack itemstack1 = iinventory.getStackInSlot(i);
                if (itemstack1 != null)
                {
                    int k = itemstack1.itemID;
                    if (towItems.containsKey(Integer.valueOf(k)))
                    {
                        player.addExperience((int)Math.floor((double)(itemstack1.stackSize * Integer.parseInt(String.valueOf(towItems.get(Integer.valueOf(k))))) / 4D));
                        iinventory.getStackInSlot(i).stackSize = 0;
                    }
                }
            }
        }
        else
        {
            for (int j = 0; j < iinventory.getSizeInventory(); j++)
            {
                ItemStack itemstack2 = iinventory.getStackInSlot(j);
                if (itemstack2 != null && itemstack.itemID != Block.blockGold.blockID && itemstack.itemID != Block.blockIron.blockID && itemstack.itemID != Block.blockDiamond.blockID)
                {
                    giveCraftingXP(player, itemstack2);
                    giveBonusCraftingXP(player);
                }
            }
        }
    }
    
    public static void giveCraftingXP(EntityPlayer player, ItemStack itemstack)
    {
        int ai[][] =
        {
            ingrTier1, ingrTier2, ingrTier3, ingrTier4
        };
        for (int i = 0; i < 4; i++)
        {
            if (Arrays.asList(ai[i]).contains(itemstack.itemID))
            {
                incrementCraftCounter(player, i);
            }
        }
    }
    
    public static void giveBonusCraftingXP(EntityPlayer player)
    {
    	byte pClass = PlayerExtendedProperties.getPlayerClass(player);
        if (pClass == 3 || pClass == 6 || pClass == 9 || pClass == 12)
        {
        	Map<String,int[]> counters = PlayerExtendedProperties.getCounterMap(player);
        	int[] bonus = counters.get("bonus");
	        if (bonus[1] < 4)
	        {
	        	bonus[1]++;
	        }
	        else
	        {
	            bonus[1] = 0;
	            player.addExperience(2);
	        }
	        counters.put("bonus", bonus);
        }
    }
    
    public static void giveBonusMiningXP(EntityPlayer player)
    {
    	byte pClass = PlayerExtendedProperties.getPlayerClass(player);
        if (pClass == 1 || pClass == 4 || pClass == 7 || pClass == 10)
        {
        	Map<String,int[]> counters = PlayerExtendedProperties.getCounterMap(player);
        	int[] bonus = counters.get("bonus");
	        if (bonus[0] < 2)
	        {
	            bonus[0]++;
	        }
	        else
	        {
	            bonus[0] = 0;
	            player.addExperience(2);
	        }
	        counters.put("bonus", bonus);
        }
    }

    public static boolean isTalismanRecipe(IInventory iinventory)
    {
        for (int i = 0; i < iinventory.getSizeInventory(); i++)
        {
            if (iinventory.getStackInSlot(i) != null && iinventory.getStackInSlot(i).itemID == xpTalisman.itemID)
            {
                return true;
            }
        }

        return false;
    }

    public static void incrementOreCounter(EntityPlayer player, int i)
    {
    	Map<String,int[]> counters = PlayerExtendedProperties.getCounterMap(player);
    	int[] ore = counters.get("ore");
        ore[i]++;
        giveBonusMiningXP(player);
        float f = (float)Math.pow(2D, 3 - i) / 2.0F;
        boolean flag;
        for (flag = false; f <= (float)ore[i]; flag = true)
        {
            player.addExperience(1);
            f += 0.5F;
        }

        if (flag)
        {
            ore[i] = 0;
        }
        counters.put("ore", ore);
    }

    public static void incrementCraftCounter(EntityPlayer player, int i)
    {
    	Map<String,int[]> counters = PlayerExtendedProperties.getCounterMap(player);
    	int[] craft = counters.get("craft");
        craft[i]++;
        float f = (float)Math.pow(2D, 3 - i);
        boolean flag;
        for (flag = false; f <= (float)craft[i]; flag = true)
        {
            player.addExperience(1);
            f += 0.5F;
        }

        if (flag)
        {
            craft[i] = 0;
        }
        counters.put("craft", craft);
    }
}
