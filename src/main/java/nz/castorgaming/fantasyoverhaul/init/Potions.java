package nz.castorgaming.fantasyoverhaul.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.potion.Potion;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.EnderInhibition;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Enslaved;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.KeepEffects;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.MortalCoil;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Paralysis;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Queasy;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Resizing;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleEnderTeleport;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleHarvestDrops;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingAttack;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingDeath;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingHurt;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingJump;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingSetAttackTarget;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingUpdate;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandlePlayerDrops;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandlePreRenderLiving;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleRenderLiving;

public class Potions {

	public static final List<IHandleHarvestDrops> HARVEST_DROPS_HANDLERS = new ArrayList<IHandleHarvestDrops>();
	public static final List<IHandleLivingAttack> LIVING_ATTACK_HANDLERS = new ArrayList<IHandleLivingAttack>();
	public static final List<IHandleLivingDeath> LIVING_DEATH_HANDLERS = new ArrayList<IHandleLivingDeath>();
	public static final List<IHandleLivingHurt> LIVING_HURT_HANDLERS = new ArrayList<IHandleLivingHurt>();
	public static final List<IHandleLivingJump> LIVING_JUMP_HANDLERS = new ArrayList<IHandleLivingJump>();
	public static final List<IHandleLivingSetAttackTarget> LIVING_SET_ATTACK_TARGET_HANDLERS = new ArrayList<IHandleLivingSetAttackTarget>();
	public static final List<IHandleLivingUpdate> LIVING_UPDATE_HANDLERS = new ArrayList<IHandleLivingUpdate>();
	public static final List<IHandlePlayerDrops> PLAYER_DROPS_HANDLERS = new ArrayList<IHandlePlayerDrops>();
	public static final List<IHandlePreRenderLiving> LIVING_PRERENDER_HANDLERS = new ArrayList<IHandlePreRenderLiving>();
	public static final List<IHandleRenderLiving> LIVING_RENDER_HANDLERS = new ArrayList<IHandleRenderLiving>();
	public static final List<IHandleEnderTeleport> ENDER_TELEPORT_HANDLERS = new ArrayList<IHandleEnderTeleport>();

	public static final PotionBase WORSHIP = new PotionBase("worship".hashCode());
	public static final Enslaved ENSLAVED = new Enslaved("enslaved".hashCode());
	public static final KeepEffects KEEP_EFFECTS = new KeepEffects("keepInventory".hashCode());
	public static final Paralysis PARALYSED = new Paralysis("paralysed".hashCode());
	public static final Queasy QUEASY = new Queasy("queasy".hashCode());
	public static final Resizing RESIZING = new Resizing("resizing".hashCode());
	public static final MortalCoil MORTAL_COIL = new MortalCoil("mortal_coil".hashCode());
	public static final EnderInhibition ENDER_INHIBITION = new EnderInhibition("ender_inhibition".hashCode());

	public static Potion register(PotionBase potion) {
		if (potion instanceof PotionBase) {
			if (potion instanceof IHandleHarvestDrops) {
				HARVEST_DROPS_HANDLERS.add((IHandleHarvestDrops) potion);
			}
			if (potion instanceof IHandleLivingAttack) {
				LIVING_ATTACK_HANDLERS.add((IHandleLivingAttack) potion);
			}
			if (potion instanceof IHandleLivingDeath) {
				LIVING_DEATH_HANDLERS.add((IHandleLivingDeath) potion);
			}
			if (potion instanceof IHandleLivingJump) {
				LIVING_JUMP_HANDLERS.add((IHandleLivingJump) potion);
			}
			if (potion instanceof IHandleLivingSetAttackTarget) {
				LIVING_SET_ATTACK_TARGET_HANDLERS.add((IHandleLivingSetAttackTarget) potion);
			}
			if (potion instanceof IHandleLivingUpdate) {
				LIVING_UPDATE_HANDLERS.add((IHandleLivingUpdate) potion);
			}
			if (potion instanceof IHandlePlayerDrops) {
				PLAYER_DROPS_HANDLERS.add((IHandlePlayerDrops) potion);
			}
			if (potion instanceof IHandlePreRenderLiving) {
				LIVING_PRERENDER_HANDLERS.add((IHandlePreRenderLiving) potion);
			}
			if (potion instanceof IHandleRenderLiving) {
				LIVING_RENDER_HANDLERS.add((IHandleRenderLiving) potion);
			}
			if (potion instanceof IHandleLivingHurt) {
				LIVING_HURT_HANDLERS.add((IHandleLivingHurt) potion);
			}
			if (potion instanceof IHandleEnderTeleport) {
				ENDER_TELEPORT_HANDLERS.add((IHandleEnderTeleport) potion);
			}
		}
		return potion;
	}
}
