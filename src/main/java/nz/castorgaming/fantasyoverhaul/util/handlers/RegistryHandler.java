package nz.castorgaming.fantasyoverhaul.util.handlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;
import nz.castorgaming.fantasyoverhaul.powers.brews.effects.InfusionBrewEffect;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.powers.infusions.creature.CreaturePower;
import nz.castorgaming.fantasyoverhaul.powers.playereffect.PlayerEffect;
import nz.castorgaming.fantasyoverhaul.powers.symbols.SymbolEffect;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.interfaces.IHasModel;

@EventBusSubscriber
public class RegistryHandler {

	RegistryHandler() {
	}

	@SubscribeEvent
	public static void onNewRegistry(RegistryEvent.NewRegistry event) {
		buildRegistry("foinfusions", Infusion.class);
		buildRegistry("focreaturepowers", CreaturePower.class);
		buildRegistry("foplayereffects", PlayerEffect.class);
		buildRegistry("fobreweffect", InfusionBrewEffect.class);
		buildRegistry("fosymboleffect", SymbolEffect.class);
	}

	private static void buildRegistry(String string, Class<? extends IForgeRegistryEntry.Impl> class1) {
		RegistryBuilder builder = new RegistryBuilder();
		builder.setName(new ResourceLocation(Reference.MODID, string));
		builder.setType(class1);
		builder.create();
	}

	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(InitArrays.BLOCKS.toArray(new Block[0]));
	}

	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(InitArrays.ITEMS.toArray(new Item[0]));
	}

	@SubscribeEvent
	public static void onPotionRegister(RegistryEvent.Register<Potion> event) {
		IForgeRegistry<Potion> reg = event.getRegistry();
		for (PotionBase potion : InitArrays.POTIONS) {
			reg.register(potion);
			potion.setPotionName(potion.getClass().getName().toLowerCase());
			Potions.register(potion);
		}
	}

	public static void onPlayerEffectRegister(RegistryEvent.Register<PlayerEffect> event) {
		event.getRegistry().registerAll(InitArrays.PLAYER_EFFECTS.toArray(new PlayerEffect[0]));
	}

	public static void onInfusionRegister(RegistryEvent.Register<Infusion> event) {
		event.getRegistry().registerAll(InitArrays.INFUSIONS.toArray(new Infusion[0]));
	}

	public static void onCreaturePowerRegister(RegistryEvent.Register<CreaturePower> event) {
		event.getRegistry().registerAll(InitArrays.CREATURE_POWER.toArray(new CreaturePower[0]));
	}

	public static void onBrewRegister(RegistryEvent.Register<InfusionBrewEffect> event) {
		event.getRegistry().registerAll(InitArrays.BREW_EFFECT.toArray(new InfusionBrewEffect[0]));
	}

	public static void onSymbolEffectRegister(RegistryEvent.Register<SymbolEffect> event) {
		event.getRegistry().registerAll(InitArrays.SYMBOL_EFFECTS.toArray(new SymbolEffect[0]));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		for (Item item : InitArrays.ITEMS) {
			if (item instanceof IHasModel) {
				((IHasModel) item).registerModels();
			}
		}

		for (Block block : InitArrays.BLOCKS) {
			if (block instanceof IHasModel) {
				((IHasModel) block).registerModels();
			}
		}
	}

}
