package nz.castorgaming.fantasyoverhaul;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.proxy.CommonProxy;
import nz.castorgaming.fantasyoverhaul.tabs.witcheryTab;
import nz.castorgaming.fantasyoverhaul.util.Reference;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPEND_STRING)
public class FantasyOverhaul {

	public enum LoadedMod {

		THAUMCRAFT("thaumcraft"), MINEFACTORY("minefactoryreloaded"), ARS("arsmagica2"), FORESTRY("forestry"), JEI(
				"jei"), BACKPACK("wearablebackpacks");

		private boolean loaded;
		public String id;

		LoadedMod(String id) {
			loaded = false;
			this.id = id;
		}

		public void setLoaded() {
			loaded = true;
		}

		public boolean getLoaded() {
			return loaded;
		}
	}

	public static final CreativeTabs foItems = new witcheryTab("foItems",
			new ItemStack(ItemInit.CHAIN_VAMPIRE_MALE_CHESTPLATE));
	public static final CreativeTabs foBlocks = new witcheryTab("foBlocks",
			new ItemStack(ItemInit.CLOTH_VAMPIRE_FEMALE_CHESTPLATE));

	@Instance
	public static FantasyOverhaul instance;

	@SidedProxy(clientSide = Reference.CLIENT, serverSide = Reference.COMMON)
	public static CommonProxy proxy;

	@EventHandler
	public static void Init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@EventHandler
	public static void preInit(FMLPreInitializationEvent e) {
		proxy.preInit(e);
	}
}
