package nz.castorgaming.fantasyoverhaul.util.classes;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import nz.castorgaming.fantasyoverhaul.capabilities.PlayerCapabilityMaster.QuestState;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer.PlayerType;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.IPlayerWerewolf;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.PlayerWerewolf;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Resizing;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketSyncEntitySize;
import nz.castorgaming.fantasyoverhaul.util.packets.PushTarget;

public class ShapeShift {

	public static final ShapeShift INSTANCE;
	public final StatBoost[] boostWolfman, boostWolf, boostVampire, boostBat;
	public static final AttributeModifier SPEED_MODIFIER, DAMAGE_MODIFIER, HEALTH_MODIFIER;
	private static Field fieldExperienceValue;

	public ShapeShift() {
		boostWolfman = new StatBoost[] { new StatBoost(0.0f, 0.0, 0.0, 0, 0.0f, 0.0f, 0, 4.0f),
				new StatBoost(0.0f, 0.0, 0.0, 0, 0.0f, 0.0f, 0, 4.0f),
				new StatBoost(0.0f, 0.0, 0.0, 0, 0.0f, 0.0f, 0, 3.0f),
				new StatBoost(0.0f, 0.0, 0.0, 0, 0.0f, 0.0f, 0, 3.0f),
				new StatBoost(0.0f, 0.0, 0.0, 0, 0.0f, 0.0f, 0, 3.0f),
				new StatBoost(0.2f, 0.20000000298023224, 0.20000000298023224, 20, 4.0f, 3.0f, 3, 2.0f),
				new StatBoost(0.2f, 0.30000001192092896, 0.20000000298023224, 20, 4.0f, 3.0f, 4, 2.0f),
				new StatBoost(0.4f, 0.4000000059604645, 0.4000000059604645, 20, 5.0f, 4.0f, 5, 2.0f),
				new StatBoost(0.4f, 0.5, 0.4000000059604645, 30, 6.0f, 4.0f, 6, 2.0f),
				new StatBoost(0.5f, 0.6000000238418579, 0.6000000238418579, 40, 7.0f, 5.0f, 7, 2.0f),
				new StatBoost(0.5f, 0.6000000238418579, 0.6000000238418579, 40, 7.0f, 5.0f, 7, 2.0f) };
		boostWolf = new StatBoost[] { new StatBoost(0.0f, 0.0, 0.0, 0, 0.0f, 0.0f, 0, 4.0f),
				new StatBoost(0.5f, 0.20000000298023224, 0.20000000298023224, 0, 1.0f, 0.0f, 2, 4.0f),
				new StatBoost(0.5f, 0.20000000298023224, 0.20000000298023224, 0, 1.0f, 0.0f, 2, 3.0f),
				new StatBoost(0.75f, 0.20000000298023224, 0.30000001192092896, 0, 2.0f, 0.0f, 2, 3.0f),
				new StatBoost(0.75f, 0.20000000298023224, 0.4000000059604645, 0, 2.0f, 0.0f, 3, 3.0f),
				new StatBoost(0.75f, 0.20000000298023224, 0.5, 0, 2.0f, 0.0f, 3, 2.0f),
				new StatBoost(1.0f, 0.20000000298023224, 0.6000000238418579, 0, 2.0f, 1.0f, 3, 2.0f),
				new StatBoost(1.25f, 0.30000001192092896, 0.699999988079071, 4, 2.0f, 1.0f, 4, 2.0f),
				new StatBoost(1.5f, 0.30000001192092896, 0.800000011920929, 8, 3.0f, 2.0f, 4, 2.0f),
				new StatBoost(1.75f, 0.30000001192092896, 0.8999999761581421, 12, 3.0f, 3.0f, 5, 2.0f),
				new StatBoost(1.75f, 0.30000001192092896, 1.0, 12, 3.0f, 3.0f, 5, 2.0f) };
		boostVampire = new StatBoost[] { new StatBoost(0.0f), new StatBoost(1.0f), new StatBoost(1.0f),
				new StatBoost(1.0f), new StatBoost(2.0f), new StatBoost(2.0f), new StatBoost(2.0f), new StatBoost(3.0f),
				new StatBoost(3.0f), new StatBoost(3.0f), new StatBoost(3.0f) };
		boostBat = new StatBoost[] { new StatBoost(0.0f), new StatBoost(-6.0f).setFlying(true),
				new StatBoost(-6.0f).setFlying(true), new StatBoost(-6.0f).setFlying(true),
				new StatBoost(-6.0f).setFlying(true), new StatBoost(-6.0f).setFlying(true),
				new StatBoost(-6.0f).setFlying(true), new StatBoost(-6.0f).setFlying(true),
				new StatBoost(-6.0f).setFlying(true), new StatBoost(-6.0f).setFlying(true),
				new StatBoost(-6.0f).setFlying(true) };

	}

	public void initCurrentShift(EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			EntitySizeInfo sizeInfo = new EntitySizeInfo(player);
			Resizing.setEntitySize(player, sizeInfo.defaultWidth, sizeInfo.defaultHeight);
			player.stepHeight = sizeInfo.stepSize;
			player.eyeHeight = sizeInfo.eyeHeight;

			AbstractAttributeMap playerAttributes = player.getAttributeMap();
			StatBoost boost = getStatBoost(player);
			if (boost != null) {
				applyModifier(SharedMonsterAttributes.MOVEMENT_SPEED, ShapeShift.SPEED_MODIFIER, boost.speed,
						playerAttributes);
				applyModifier(SharedMonsterAttributes.ATTACK_DAMAGE, ShapeShift.DAMAGE_MODIFIER, boost.damage,
						playerAttributes);
				applyModifier(SharedMonsterAttributes.MAX_HEALTH, ShapeShift.HEALTH_MODIFIER, boost.health,
						playerAttributes);
			} else {
				removeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, ShapeShift.SPEED_MODIFIER, playerAttributes);
				removeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, ShapeShift.DAMAGE_MODIFIER, playerAttributes);
				removeModifier(SharedMonsterAttributes.MAX_HEALTH, ShapeShift.HEALTH_MODIFIER, playerAttributes);
			}
			if (!player.capabilities.isCreativeMode) {
				player.capabilities.allowFlying = (boost != null && boost.flying);
				if (!player.capabilities.allowFlying && player.capabilities.isFlying) {
					player.capabilities.isFlying = false;
				} else if (player.capabilities.allowFlying) {
					player.capabilities.isFlying = true;
				}
				player.sendPlayerAbilities();
			}
			Reference.PACKET_HANDLER.sendToAll(new PacketSyncEntitySize(player));
			Reference.PACKET_HANDLER.sendTo(new PacketSyncEntitySize(player), player);
		}
	}

	public void updatePlayerState(EntityPlayer player, ExtendedPlayer playerEx) {
		if (playerEx.getCreatureType() == TransformCreatures.BAT) {
			if (player.capabilities.isFlying) {
				player.fallDistance = 0.0f;
			}
			if (!player.capabilities.allowFlying && !player.capabilities.isCreativeMode) {
				player.capabilities.allowFlying = true;
				player.sendPlayerAbilities();
			}
		}
	}

	public float updateFallState(EntityPlayer player, float distance) {
		StatBoost boost = getStatBoost(player);

		if (boost == null) {
			return distance;
		}
		if (boost.fall == -1) {
			return 0.0f;
		}
		return Math.max(0.0f, distance - boost.fall);
	}

	public float getDamageCap(EntityPlayer player, ExtendedPlayer playerEx) {
		StatBoost boost = getStatBoost(player);
		if (boost != null) {
			return boost.damageCap;
		}
		return 0.0f;
	}

	public float getResistance(EntityPlayer player, ExtendedPlayer playerEx) {
		final StatBoost boost = getStatBoost(player);
		if (boost != null) {
			return boost.resistance;
		}
		return 0.0f;
	}

	public void updateJump(final EntityPlayer player) {
		final StatBoost boost = this.getStatBoost(player);
		if (boost != null) {
			player.motionY += boost.jump;
			if (player.isSprinting()) {
				final float f = player.rotationYaw * 0.017453292f;
				player.motionX -= MathHelper.sin(f) * boost.leap;
				player.motionZ += MathHelper.cos(f) * boost.leap;
			}
		}
	}

	public void updateChargeDamage(LivingHurtEvent event, EntityPlayer player, ExtendedPlayer playerEx) {
		PlayerVampire playerVamp = IPlayerVampire.get(player);
		if (isWolfAnimalForm(playerEx)) {
			if (itemHasDamageAttribute(player.getHeldItemMainhand())) {
				event.setAmount(2.0f);
			} else {
				StatBoost boost = getStatBoost(player);
				if (boost != null && player.isSprinting()) {
					event.setAmount(event.getAmount() + boost.damage);
				}
			}
		}
		if (playerVamp.getVampireLevel() >= 3 && playerEx.getCreatureType() == TransformCreatures.NONE
				&& player.isSneaking()) {
			Vec3d look = player.getLookVec();
			double motionX = look.xCoord * 0.6 * 3.0;
			double motionY = 0.8999999999999999;
			double motionZ = look.zCoord * 0.6 * 3.0;
			if (event.getEntityLiving() instanceof EntityPlayer) {
				EntityPlayer targetPlayer = (EntityPlayer) event.getEntityLiving();
				Reference.PACKET_HANDLER.sendTo(new PushTarget(motionX, motionY, motionZ), targetPlayer);
			} else {
				EntityLivingBase living = event.getEntityLiving();
				living.motionX = motionX;
				living.motionY = motionY;
				living.motionZ = motionZ;
			}
		}
	}

	private boolean itemHasDamageAttribute(ItemStack item) {
		if (item == null) {
			return false;
		}
		Multimap<String, AttributeModifier> modifiers = item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
		if (modifiers == null) {
			return false;
		}
		return modifiers.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName());
	}

	public void rendArmor(EntityLivingBase victim, EntityPlayer player) {
		ExtendedPlayer playerEx = IExtendPlayer.get(player);
		if (playerEx.getCreatureType() == TransformCreatures.WOLFMAN
				&& IPlayerWerewolf.get(player).getWerewolfLevel() >= 9) {
			EntityEquipmentSlot slot = EntityEquipmentSlot.values()[1 + victim.worldObj.rand.nextInt(4)];
			ItemStack armor = victim.getItemStackFromSlot(slot);
			if (armor != null) {
				boolean ripOffArmor = !armor.isItemStackDamageable();
				if (!ripOffArmor) {
					int damage = armor.getItemDamage();
					int rendAmount = (int) Math.ceil(armor.getMaxDamage() * 0.25f);
					armor.damageItem(rendAmount, player);
					if (victim instanceof EntityPlayer && armor.getItem() instanceof ItemArmor) {
						ItemArmor armorItem = (ItemArmor) armor.getItem();
						armorItem.onArmorTick(victim.worldObj, (EntityPlayer) victim, armor);
					}
					ripOffArmor = (armor.getItemDamage() <= damage);
				}
				if (ripOffArmor && victim instanceof EntityPlayer) {
					victim.setItemStackToSlot(slot, null);
					EntityItem droppedItem = victim.entityDropItem(armor, 1.0f);
					if (droppedItem != null) {
						droppedItem.setPickupDelay(TimeUtilities.secsToTicks(5));
					}
				}
			}
		}
	}

	public void processCreatureKilled(LivingDeathEvent event, EntityPlayer attacker) {
		PlayerWerewolf playerWere = IPlayerWerewolf.get(attacker);
		if (isWolfAnimalForm(IExtendPlayer.get(attacker)) && playerWere.getWerewolfLevel() >= 4
				&& !CreatureUtilities.isUndead(event.getEntityLiving())) {
			ParticleEffect.REDDUST.send(
					(attacker.worldObj.rand.nextInt(3) == 0) ? SoundEffect.MOB_WOLFMAN_EAT : SoundEffect.NONE,
					event.getEntityLiving(), 1.0, 2.0, 16);
			attacker.getFoodStats().addStats(8, 0.8f);
		}
	}

	public void processDigging(BlockEvent.HarvestDropsEvent event, EntityPlayer player) {
		ExtendedPlayer playerEx = IExtendPlayer.get(player);
		PlayerWerewolf playerWere = IPlayerWerewolf.get(player);
		if (playerEx.getCreatureType() == TransformCreatures.WOLF && playerWere.getWerewolfLevel() >= 3
				&& event.getDrops().size() == 1 && event.getDrops().get(0) != null) {
			long lastFind = playerWere.getLastBoneFind();
			long serverTime = MinecraftServer.getCurrentTimeMillis();
			if (lastFind + TimeUtilities.secsToMillisecs(60) < serverTime && player.worldObj.rand.nextInt(20) == 0) {
				playerWere.setLastBoneFind(serverTime);
				event.getDrops().add(new ItemStack(Items.BONE, (player.worldObj.rand.nextInt(5) == 0) ? 2 : 1));
			}
		}
	}

	public void checkForHowling(EntityPlayer player) {
		ExtendedPlayer playerEx = IExtendPlayer.get(player);
		PlayerWerewolf playerWere = IPlayerWerewolf.get(player);
		if (playerWere.getWerewolfLevel() == 6 && isWolfAnimalForm(playerEx)
				&& playerWere.getWolfmanQuestState() == QuestState.STARTED && !player.worldObj.isDaytime()) {
			int x = MathHelper.floor_double(player.posX) >> 4;
			int z = MathHelper.floor_double(player.posZ) >> 4;
			SoundEffect.MOB_WOLFMAN_HOWL.playAtPlayer(player.worldObj, player, 1.0f);
			if (playerWere.storeWolfmanQuestChunk(x, z)) {
				playerWere.increaseWolfmanQuestCounter();
			} else {
				ChatUtilities.sendTranslated(TextFormatting.RED, player, Reference.WERE_CHUNK_VISITED, new Object[0]);
			}
		} else if (playerEx.getCreatureType() == TransformCreatures.WOLF && playerWere.getWerewolfLevel() >= 8) {
			long lastHowl = playerWere.getLastHowl();
			long serverTime = MinecraftServer.getCurrentTimeMillis();
			if (player.capabilities.isCreativeMode || lastHowl + TimeUtilities.secsToMillisecs(60) < serverTime) {
				SoundEffect.MOB_WOLFMAN_HOWL.playAtPlayer(player.worldObj, player, 1.0f);
				playerWere.setLastHowl(serverTime);
				for (int i = 0; i < 2 + player.worldObj.rand.nextInt(playerWere.getWerewolfLevel() - 7); ++i) {
					EntityCreature creature = Infusion.spawnCreature(player.worldObj, EntityWolf.class,
							(int) player.posX, (int) player.posY, (int) player.posZ, player.getLastAttacker(), 1, 6,
							ParticleEffect.SMOKE, SoundEffect.NONE);
					if (creature != null) {
						creature.addPotionEffect(new PotionEffect(Potions.MORTAL_COIL, TimeUtilities.secsToTicks(10)));
						EntityWolf wolf = (EntityWolf) creature;
						wolf.setTamed(true);
						wolf.setOwnerId(player.getUniqueID());
						if (ShapeShift.fieldExperienceValue == null) {
							ShapeShift.fieldExperienceValue = ReflectionHelper.findField(EntityLiving.class,
									new String[] { "experienceValue", "experienceValue", "aV" });
						}

						if (ShapeShift.fieldExperienceValue != null) {
							try {
								ShapeShift.fieldExperienceValue.set(wolf, 0);
							} catch (IllegalAccessException e) {
							}
						}

						EntityUtil.setNoDrops(wolf);
					}
				}
			} else {
				SoundEffect.NOTE_SNARE.playAtPlayer(player.worldObj, player);
			}
		} else if (playerEx.getCreatureType() == TransformCreatures.WOLFMAN && playerWere.getWerewolfLevel() >= 7) {
			long lastHowl = playerWere.getLastHowl();
			long serverTime = MinecraftServer.getCurrentTimeMillis();
			if (player.capabilities.isCreativeMode || lastHowl + TimeUtilities.secsToMillisecs(60) < serverTime) {
				SoundEffect.MOB_WOLFMAN_HOWL.playAtPlayer(player.worldObj, player, 1.0f);
				playerWere.setLastHowl(serverTime);
				double radius = 16.0;
				List<EntityLivingBase> entities = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
						player.getEntityBoundingBox().expand(radius, radius, radius));
				for (EntityLivingBase entity : entities) {
					if (!CreatureUtilities.isWerewolf(entity, true) && !CreatureUtilities.isVampire(entity)) {
						entity.addPotionEffect(
								new PotionEffect(Potions.PARALYSED,
										TimeUtilities.secsToTicks(
												4 + player.worldObj.rand.nextInt(playerWere.getWerewolfLevel() - 6)),
										3));
					}
				}
			} else {
				SoundEffect.NOTE_SNARE.playAtPlayer(player.worldObj, player);
			}
		}
	}

	public void processWolfInfection(EntityLivingBase entity, EntityPlayer attackingPlayer, float health) {
		PlayerWerewolf werewolf = IPlayerWerewolf.get(attackingPlayer);
		if (werewolf.getWerewolfLevel() >= 10 && isWolfAnimalForm(attackingPlayer)) {
			if (entity instanceof EntityVillager) {
				infectVillagerWere(entity, health);
			} else if (entity instanceof EntityPlayer && Config.instance().allowPlayerToPlayerWolfInfection) {
				infectPlayerWere(entity, health);
			}
		}
	}

	public void processWolfInfection(EntityLivingBase entity, EntityWolfman attackingEntity, float health) {
		if (attackingEntity.isInfectious()) {
			if (entity instanceof EntityVillager) {
				infectVillagerWere(entity, health);
			} else if (entity instanceof EntityPlayer) {
				infectPlayerWere(entity, health);
			}
		}
	}

	private void infectPlayerWere(EntityLivingBase entity, float health) {
		EntityPlayer victim = (EntityPlayer) entity;
		PlayerWerewolf vicWere = IPlayerWerewolf.get(victim);
		PlayerVampire vicVamp = IPlayerVampire.get(victim);

		if (health < victim.getMaxHealth() * 0.25 && health > 0.0f
				&& !ItemHunterClothes.isWolfProtectionActive(victim)) {
			if (Config.instance().allowVampireWerewolfHybrids || !vicVamp.isVampire()) {
				if (vicWere.getWerewolfLevel() == 0) {
					vicWere.setWerewolfLevel(1);
					ChatUtilities.sendTranslated(TextFormatting.DARK_PURPLE, victim, Reference.WERE_INFECTION,
							new Object[0]);
				}
			}
		}
	}

	private void infectVillagerWere(EntityLivingBase entity, float health) {
		if (health < entity.getMaxHealth() * 0.25f && health > 0.0f && entity.worldObj.rand.nextInt(4) == 1) {
			EntityVillager villager = (EntityVillager) entity;
			EntityWolfman.convertToVillager(villager, villager.getProfessionForge(), false, villager.getHealth(),
					villager.buyingList);
		}
	}

	public boolean isWolfAnimalForm(EntityPlayer player) {
		return isWolfAnimalForm(IExtendPlayer.get(player));
	}

	public boolean isWolfAnimalForm(IExtendPlayer player) {
		return TransformCreatures.isWolfForm(player);
	}

	public boolean isWolfmanAllowed(EntityPlayer player) {
		return IPlayerWerewolf.get(player).getWerewolfLevel() >= 5;
	}

	public boolean canControlTransformation(EntityPlayer player) {
		return IPlayerWerewolf.get(player).getWerewolfLevel() >= 2;
	}

	public StatBoost getStatBoost(EntityPlayer player) {
		ExtendedPlayer extend = IExtendPlayer.get(player);
		Map<PlayerType, Integer> levels = extend.getLevels();
		TransformCreatures creature = extend.getCreatureType();
		switch (creature) {
		case BAT:
			return boostBat[levels.get(PlayerType.VAMPIRE)];
		case WOLF:
			return boostWolf[levels.get(PlayerType.WEREWOLF)];
		case WOLFMAN:
			return boostWolfman[levels.get(PlayerType.WEREWOLF)];
		default:
			return levels.get(PlayerType.VAMPIRE) > 0 ? boostVampire[levels.get(PlayerType.VAMPIRE)] : null;
		}
	}

	public void applyModifier(IAttribute attribute, AttributeModifier modifier, double mod,
			AbstractAttributeMap playerAttributes) {
		IAttributeInstance attributeInstance = playerAttributes.getAttributeInstance(attribute);
		AttributeModifier speedModifier = new AttributeModifier(modifier.getID(), modifier.getName(), mod,
				modifier.getOperation());
		attributeInstance.removeModifier(speedModifier);
		attributeInstance.applyModifier(speedModifier);
	}

	public void removeModifier(IAttribute attribute, AttributeModifier modifier,
			AbstractAttributeMap playerAttributes) {
		IAttributeInstance instance = playerAttributes.getAttributeInstance(attribute);
		instance.removeModifier(modifier);
	}

	public void shiftTo(EntityPlayer player, TransformCreatures creature) {
		IExtendPlayer.get(player).setCreatureType(creature);
		initCurrentShift(player);
	}

	static {
		INSTANCE = new ShapeShift();
		SPEED_MODIFIER = new AttributeModifier(UUID.fromString("10536417-7AA6-4033-A598-8E934CA77D98"),
				"witcheryWolfSpeed", 0.5, 2);
		DAMAGE_MODIFIER = new AttributeModifier(UUID.fromString("46C5271C-193B-4D41-9CAB-D071AAEE9D4A"),
				"witcheryWolfDamage", 6.0, 2);
		HEALTH_MODIFIER = new AttributeModifier(UUID.fromString("615920F9-6675-4779-8B18-6A62A3671E94"),
				"witcheryWolfHealth", 40.0, 0);
	}

}
