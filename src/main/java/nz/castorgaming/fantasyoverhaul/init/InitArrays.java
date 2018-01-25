package nz.castorgaming.fantasyoverhaul.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;
import nz.castorgaming.fantasyoverhaul.powers.brews.effects.InfusionBrewEffect;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.powers.infusions.creature.CreaturePower;
import nz.castorgaming.fantasyoverhaul.powers.playereffect.PlayerEffect;
import nz.castorgaming.fantasyoverhaul.powers.symbols.SymbolEffect;

public class InitArrays {

	public static final List<Item> ITEMS = new ArrayList<Item>();
	public static final List<PotionBase> POTIONS = new ArrayList<PotionBase>();
	public static final List<Block> BLOCKS = new ArrayList<Block>();
	public static final List<PlayerEffect> PLAYER_EFFECTS = new ArrayList<PlayerEffect>();
	public static final List<Infusion> INFUSIONS = new ArrayList<Infusion>();
	public static final List<CreaturePower> CREATURE_POWER = new ArrayList<CreaturePower>();
	public static final List<InfusionBrewEffect> BREW_EFFECT = new ArrayList<InfusionBrewEffect>();
	public static final List<SymbolEffect> SYMBOL_EFFECTS = new ArrayList<SymbolEffect>();

	/*
	 * public static final Map<ResourceLocation, PlayerEffect> PLAYER_EFFECTS = new
	 * HashMap<ResourceLocation, PlayerEffect>(); public static final
	 * Map<ResourceLocation, Infusion> INFUSIONS = new HashMap<ResourceLocation,
	 * Infusion>(); public static final Map<ResourceLocation, CreaturePower>
	 * CREATURE_POWERS = new HashMap<ResourceLocation, CreaturePower>(); public
	 * static final Map<ResourceLocation, InfusionBrewEffect> BREW_EFFECTS = new
	 * HashMap<ResourceLocation, InfusionBrewEffect>();
	 */
}
