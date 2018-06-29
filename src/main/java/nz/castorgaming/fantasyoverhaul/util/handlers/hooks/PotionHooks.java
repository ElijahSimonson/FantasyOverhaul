package nz.castorgaming.fantasyoverhaul.util.handlers.hooks;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Enslaved;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleEnderTeleport;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleHarvestDrops;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingAttack;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingDeath;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingHurt;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingJump;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingSetAttackTarget;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingUpdate;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandlePlayerDrops;

@EventBusSubscriber
public class PotionHooks {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerDrops(final PlayerDropsEvent event) {
		for (final IHandlePlayerDrops handler : Potions.PLAYER_DROPS_HANDLERS) {
			if (event.isCanceled()) {
				break;
			}
			if (!event.getEntityLiving().isPotionActive(handler.getPotion())) {
				continue;
			}
			final PotionEffect effect = event.getEntityLiving().getActivePotionEffect(handler.getPotion());
			handler.onPlayerDrops(event.getEntityPlayer().getEntityWorld(), event.getEntityPlayer(), event,
					effect.getAmplifier());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onBlockHarvest(final BlockEvent.HarvestDropsEvent event) {
		for (final IHandleHarvestDrops handler : Potions.HARVEST_DROPS_HANDLERS) {
			if (event.isCanceled()) {
				break;
			}
			if (event.getHarvester() == null || !event.getHarvester().isPotionActive(handler.getPotion())) {
				continue;
			}
			final PotionEffect effect = event.getHarvester().getActivePotionEffect(handler.getPotion());
			handler.onHarvestDrops(event.getWorld(), event.getHarvester(), event, effect.getAmplifier());
		}
	}

	@SubscribeEvent
	public void onLivingHurt(final LivingHurtEvent event) {
		for (final IHandleLivingHurt handler : Potions.LIVING_HURT_HANDLERS) {
			if (event.isCanceled()) {
				break;
			}
			if (!handler.handleAllHurtEvents() && !event.getEntityLiving().isPotionActive(handler.getPotion())) {
				continue;
			}
			final PotionEffect effect = event.getEntityLiving().getActivePotionEffect(handler.getPotion());
			handler.onLivingHurt(event.getEntityLiving().worldObj, event.getEntityLiving(), event,
					(effect != null) ? effect.getAmplifier() : -1);
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
		for (final IHandleLivingUpdate handler : Potions.LIVING_UPDATE_HANDLERS) {
			if (event.isCanceled()) {
				break;
			}
			if (!event.getEntityLiving().isPotionActive(handler.getPotion())) {
				continue;
			}
			final PotionEffect effect = event.getEntityLiving().getActivePotionEffect(handler.getPotion());
			handler.onLivingUpdate(event.getEntityLiving().worldObj, event.getEntityLiving(), event,
					effect.getAmplifier(), effect.getDuration());
		}
	}

	@SubscribeEvent
	public void onLivingAttack(final LivingAttackEvent event) {
		for (final IHandleLivingAttack handler : Potions.LIVING_ATTACK_HANDLERS) {
			if (event.isCanceled()) {
				break;
			}
			if (!event.getEntityLiving().isPotionActive(handler.getPotion())) {
				continue;
			}
			final PotionEffect effect = event.getEntityLiving().getActivePotionEffect(handler.getPotion());
			handler.onLivingAttack(event.getEntityLiving().worldObj, event.getEntityLiving(), event,
					effect.getAmplifier());
		}
		if (Reference.modHooks.isAM2Present && !event.isCanceled() && !event.getEntity().worldObj.isRemote
				&& event.getSource() == DamageSource.inWall && event.getEntity() instanceof EntityPlayer
				&& (IExtendPlayer.get((EntityPlayer) event.getEntity()).getCreatureType() == TransformCreatures.WOLF
						|| IExtendPlayer.get((EntityPlayer) event.getEntity())
								.getCreatureType() == TransformCreatures.BAT)
				&& !event.getEntity().worldObj.getBlockState(event.getEntity().getPosition()).isNormalCube()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onLivingJump(final LivingEvent.LivingJumpEvent event) {
		for (final IHandleLivingJump handler : Potions.LIVING_JUMP_HANDLERS) {
			if (event.isCanceled()) {
				break;
			}
			if (!event.getEntityLiving().isPotionActive(handler.getPotion())) {
				continue;
			}
			final PotionEffect effect = event.getEntityLiving().getActivePotionEffect(handler.getPotion());
			handler.onLivingJump(event.getEntityLiving().worldObj, event.getEntityLiving(), event,
					effect.getAmplifier());
		}
	}

	@SubscribeEvent
	public void onEnderTeleport(final EnderTeleportEvent event) {
		if (event.getEntityLiving() != null && (event.getEntityLiving().worldObj.provider
				.getDimension() == Config.instance().dimensionTormentID
				|| event.getEntityLiving().worldObj.provider.getDimension() == Config.instance().dimensionMirrorID)) {
			event.setCanceled(true);
			return;
		}
		for (final IHandleEnderTeleport handler : Potions.ENDER_TELEPORT_HANDLERS) {
			if (event.isCanceled()) {
				break;
			}
			if (!event.getEntityLiving().isPotionActive(handler.getPotion())) {
				continue;
			}
			final PotionEffect effect = event.getEntityLiving().getActivePotionEffect(handler.getPotion());
			handler.onEnderTeleport(event.getEntityLiving().worldObj, event.getEntityLiving(), event,
					effect.getAmplifier());
		}
	}

	@SubscribeEvent
	public void onLivingSetAttackTarget(final LivingSetAttackTargetEvent event) {
		if (event.getEntityLiving() instanceof EntityLiving) {
			final EntityLiving livingEntity = (EntityLiving) event.getEntityLiving();
			if (livingEntity != null && Potions.ENSLAVED != null && event.getTarget() != null
					&& event.getTarget() instanceof EntityPlayer) {
				final EntityPlayer player = (EntityPlayer) event.getTarget();
				if (!livingEntity.isPotionActive(Potions.ENSLAVED) && Enslaved.isMobEnslavedBy(livingEntity, player)) {
					livingEntity.setAttackTarget((EntityLivingBase) null);
				}
			}
			for (final IHandleLivingSetAttackTarget handler : Potions.LIVING_SET_ATTACK_TARGET_HANDLERS) {
				if (event.isCanceled()) {
					break;
				}
				if (!event.getEntityLiving().isPotionActive(handler.getPotion())) {
					continue;
				}
				final PotionEffect effect = event.getEntityLiving().getActivePotionEffect(handler.getPotion());
				handler.onLivingSetAttackTarget(event.getEntityLiving().worldObj, livingEntity, event,
						effect.getAmplifier());
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onLivingDeath(final LivingDeathEvent event) {
		for (final IHandleLivingDeath handler : Potions.LIVING_DEATH_HANDLERS) {
			if (event.isCanceled()) {
				break;
			}
			if (!event.getEntityLiving().isPotionActive(handler.getPotion())) {
				continue;
			}
			final PotionEffect effect = event.getEntityLiving().getActivePotionEffect(handler.getPotion());
			handler.onLivingDeath(event.getEntityLiving().worldObj, event.getEntityLiving(), event,
					effect.getAmplifier());
		}
		if (!event.getEntityLiving().worldObj.isRemote && event.getEntityLiving() instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			final Collection<PotionEffect> activeEffects = player.getActivePotionEffects();
			if (activeEffects.size() > 0) {
				final ArrayList<PotionEffect> permenantEffectList = new ArrayList<PotionEffect>();
				int allPermentantLevel = -1;
				if (player.isPotionActive(Potions.KEEP_EFFECTS)) {
					final PotionEffect permAll = player.getActivePotionEffect(Potions.KEEP_EFFECTS);
					allPermentantLevel = permAll.getAmplifier();
				}
				for (final PotionEffect effect2 : activeEffects) {
					final Potion potionID = effect2.getPotion();
					if (potionID != null && potionID instanceof PotionBase) {
						final PotionBase potion = (PotionBase) potionID;
						if (potion.isPermanent()) {
							permenantEffectList.add(effect2);
							continue;
						}
					}
					if (PotionBase.isDebuff(potionID) || allPermentantLevel < effect2.getAmplifier()) {
						continue;
					}
					permenantEffectList.add(effect2);
				}
				if (permenantEffectList.size() > 0) {
					final NBTTagList nbtEffectList = new NBTTagList();
					for (final PotionEffect permenantEffect : permenantEffectList) {
						final NBTTagCompound nbtEffect = new NBTTagCompound();
						permenantEffect.writeCustomPotionEffectToNBT(nbtEffect);
						nbtEffectList.appendTag(nbtEffect);
					}
					final NBTTagCompound nbtPlayer = Infusion.getNBT(player);
					nbtPlayer.setTag("WITCPoSpawn", nbtEffectList);
				}
			}
		}
	}
}
