package nz.castorgaming.fantasyoverhaul.powers.infusions.player;

import java.lang.reflect.Field;

import jline.internal.Log;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Enslaved;
import nz.castorgaming.fantasyoverhaul.objects.potions.tasks.EntityAIAttackOnCollide2;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.powers.infusions.creature.CreaturePower;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class InfusionInfernal extends Infusion {

	public static final int MAX_CHARGES = 20;

	public InfusionInfernal(String name) {
		super(name);
	}

	public InfusionInfernal() {
		super("infernal");
	}

	@Override
	public ResourceLocation getPowerBarIcon(EntityPlayer player, int index) {
		return new ResourceLocation("netherrack");
	}

	@Override
	public void onLeftClickEntity(ItemStack stack, World world, EntityPlayer player, Entity otherEntity) {
		if (!world.isRemote && otherEntity instanceof EntityLivingBase) {
			EntityLivingBase livingEnt = (EntityLivingBase) otherEntity;
			if (player.isSneaking()) {
				if (Enslaved.canCreatureBeEnslaved(livingEnt)) {
					if (Enslaved.isMobEnslavedBy((EntityLiving) livingEnt, player)) {
						if (consumeCharges(world, player, 1, true)) {
							trySacrificeCreature(world, player, (EntityLiving) livingEnt);
						}
					} else if (consumeCharges(world, player, 5, true)) {
						Enslaved.setEnslaverForMob((EntityLiving) livingEnt, player);
						EntityUtil.dropAttackTarget((EntityLiving) otherEntity);
						ParticleEffect.SPELL.send(SoundEffect.MOB_ZOMBIE_INFECT, livingEnt, 1.0, 2.0, 16);
					}
				} else {
					int r = 50;
					if (consumeCharges(world, player, 1, true)) {
						int minionCount = 0;
						AxisAlignedBB bounds = new AxisAlignedBB(player.posX - 50, player.posY - 15.0, player.posZ - 50,
								player.posX + 50, player.posY + 15.0, player.posZ + 50);
						for (Object obj : world.getEntitiesWithinAABB(EntityLiving.class, bounds)) {
							EntityLiving nearbyEnt = (EntityLiving) obj;
							if (Enslaved.isMobEnslavedBy(nearbyEnt, player)) {
								++minionCount;
								nearbyEnt.setAttackTarget(livingEnt);
								if (nearbyEnt instanceof EntityGhast) {
									try {
										EntityGhast ghast = (EntityGhast) nearbyEnt;
										Field[] fields = EntityGhast.class.getDeclaredFields();
										Field fieldTargetedEntity = fields[4];
										fieldTargetedEntity.setAccessible(true);
										fieldTargetedEntity.set(ghast, livingEnt);

										Field fieldAggroCooldown = fields[5];
										fieldAggroCooldown.setAccessible(true);
										fieldAggroCooldown.set(ghast, 20000);
									} catch (IllegalAccessException e) {
										Log.warn(e, "Excpetion Occurred setting ghast target.");
									} catch (Exception e2) {
										Log.debug(String.format("Exception occured setting ghast target. %s",
												e2.toString()));
									}
								}
								if (!(nearbyEnt instanceof EntityCreature)) {
									continue;
								}
								EntityCreature nearbyCreature = (EntityCreature) obj;
								nearbyCreature.setAttackTarget(livingEnt);
								nearbyCreature.setRevengeTarget(livingEnt);
								if (!(nearbyCreature instanceof EntityCreature)
										&& !(nearbyCreature instanceof EntityCreeper)) {
									continue;
								}
								nearbyCreature.tasks.addTask(2,
										new EntityAIAttackOnCollide2(nearbyCreature, livingEnt.getClass(), 1.0, false));
							}
						}
						if (minionCount > 0) {
							ParticleEffect.CRIT.send(SoundEffect.RANDOM_BREATH, livingEnt, 0.5, 2.0, 16);
						}
					}
				}
			}
		}
	}

	private void trySacrificeCreature(World world, EntityPlayer player, EntityLiving livingEnt) {
		CreaturePower power = CreaturePower.get(livingEnt);
		if (power != null) {
			String currentCreaturePowerID = CreaturePower.getCreaturePowerID(player);
			if (currentCreaturePowerID.equals(power.getCreaturePowerID())) {
				int currentCharges = CreaturePower.getCreaturePowerCharges(player);
				CreaturePower.setCreaturePowerCharges(player, MathHelper
						.floor_double(Math.min(currentCharges + power.getChargesPerSacrifice(), MAX_CHARGES)));

			} else {
				CreaturePower.setCreaturePowerID(player, power.getCreaturePowerID(), power.getChargesPerSacrifice());
			}
			Infusion.syncPlayer(world, player);
			livingEnt.attackEntityFrom(DamageSource.causeIndirectMagicDamage(player, null),
					livingEnt.getHealth() + 1.0f);
		} else {
			playFailSound(world, player);
		}
	}

	@Override
	public void onHurt(World world, EntityPlayer player, LivingHurtEvent event) {
		String creaturePowerID = CreaturePower.getCreaturePowerID(player);
		if (creaturePowerID != null) {
			CreaturePower.get(creaturePowerID).onDamage(player.worldObj, player, event);
		}
	}

	@Override
	public void onFalling(World world, EntityPlayer player, LivingFallEvent event) {
		String creaturePowerID = CreaturePower.getCreaturePowerID(player);
		if (creaturePowerID != null) {
			CreaturePower.get(creaturePowerID).onFalling(player.worldObj, player, event);
		}
	}

	@Override
	public void onUsingItemTick(ItemStack stack, World world, EntityPlayer player, int countdown) {
		if (!world.isRemote) {
			int elapsedTicks = getMaxItemUseDuration(stack) - countdown;
			double MAX_TARGET_RANGE = 15.0;
			RayTraceResult rtr = InfusionOtherwhere.doCustomRayTrace(world, player, true, MAX_TARGET_RANGE);
			if (player.isSneaking()) {
				if (rtr != null) {
					switch (rtr.typeOfHit) {
					case ENTITY: {
						playFailSound(world, player);
					}
					case BLOCK: {
						if (EnumFacing.UP.equals(rtr.sideHit)) {
							int minionCount = 0;
							int r = 50;
							AxisAlignedBB bound = new AxisAlignedBB(player.posX - 50.0, player.posY - 15.0,
									player.posZ - 50.0, player.posX + 50.0, player.posY + 15.0, player.posZ + 50.0);
							for (Object obj : world.getEntitiesWithinAABB(EntityLiving.class, bound)) {
								EntityLiving living = (EntityLiving) obj;
								if (Enslaved.isMobEnslavedBy(living, player)) {
									++minionCount;
									living.setAttackTarget(null);
									living.setRevengeTarget(null);
									if ((!(living instanceof EntitySpider)
											&& living.getNavigator().tryMoveToXYZ(rtr.getBlockPos().getX(),
													rtr.getBlockPos().getY() + 1, rtr.getBlockPos().getZ(), 1.0))) {
										continue;
									}
								}
							}
							if (minionCount > 0) {
								ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_POP, world,
										rtr.getBlockPos().getX(), rtr.getBlockPos().getY() + 1,
										rtr.getBlockPos().getZ(), 0.5, 2.0, 16);
							}
							break;
						}
						break;
					}
					default:
						break;
					}
				} else {
					playFailSound(world, player);
				}
			} else {
				String powerID = CreaturePower.getCreaturePowerID(player);
				if (powerID != null) {
					CreaturePower power = CreaturePower.get(powerID);
					int chargesRequired = power.activateCost(world, player, elapsedTicks, rtr);
					int currentCharges = CreaturePower.getCreaturePowerCharges(player);
					if (currentCharges - chargesRequired >= 0 && consumeCharges(world, player, 1, true)) {
						power.onActivate(world, player, elapsedTicks, rtr);
						if (!player.capabilities.isCreativeMode) {
							CreaturePower.setCreaturePowerCharges(player, currentCharges - chargesRequired);
							Infusion.syncPlayer(world, player);
						}
					} else {
						playFailSound(world, player);
					}
				} else {
					playFailSound(world, player);
				}
			}
		}
	}

}
