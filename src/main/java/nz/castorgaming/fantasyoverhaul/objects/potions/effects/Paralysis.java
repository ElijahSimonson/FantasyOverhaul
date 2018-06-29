package nz.castorgaming.fantasyoverhaul.objects.potions.effects;

import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.ai.EntityAIMoveTowardsVampire;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingHurt;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingUpdate;

public class Paralysis extends PotionBase implements IHandleLivingUpdate, IHandleLivingHurt {

	public Paralysis(int color) {
		super(true, color);
		setIncurable();
	}

	@Override
	public void applyAttributeModifiersToEntity(EntityLivingBase entity, AbstractAttributeMap attributes,
			int amplifier) {
		if (canApplyToEntity(entity, amplifier)) {
			super.applyAttributeModifiersToEntity(entity, attributes, amplifier);
		} else if (isVillager(entity)) {
			EntityCreature creature = (EntityCreature) entity;
			creature.setAttackTarget(null);
			creature.setRevengeTarget(null);
			creature.tasks.addTask(0, new EntityAIMoveTowardsVampire(creature, 0.8, 1.0f, 16.0f));
		}
	}

	private boolean canApplyToEntity(EntityLivingBase entity, int amplifier) {
		return entity.isNonBoss() && amplifier < 5 || !isVillager(entity) && !(entity instanceof EntityPlayer)
				|| amplifier >= 2;
	}

	@Override
	public boolean handleAllHurtEvents() {
		return false;
	}

	public boolean isVillager(Entity entity) {
		return entity instanceof EntityVillager || entity instanceof EntityVillageGuard;
	}

	@Override
	public void onLivingHurt(World world, EntityLivingBase entity, LivingHurtEvent event, int amplifier) {
		if (!world.isRemote && amplifier >= 4 && event.getAmount() >= 1.0f) {
			entity.removePotionEffect(this);
		}
	}

	@Override
	public void onLivingUpdate(World world, EntityLivingBase entity, LivingUpdateEvent event, int amplifier,
			int duration) {
		if (canApplyToEntity(entity, amplifier)) {
			if (!world.isRemote) {
				if (entity instanceof EntityCreeper) {
					((EntityCreeper) entity).setCreeperState(-1);
				}
				if (amplifier >= 4 && duration <= 1 && entity instanceof EntityPlayer) {
					final EntityPlayer player = (EntityPlayer) entity;
					player.addPotionEffect(
							new PotionEffect(Potions.QUEASY, TimeUtilities.secsToTicks(90), 0, true, false));
				}
			}
			if (entity.ticksExisted % 20 != 2 || !isVillager(entity) || amplifier < 5) {
				entity.motionY = -0.2;
			}
		}
	}

	@Override
	public void postConstructInitialize() {
		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "E69059D5-CAE6-4695-9BE3-C6F0F22151E8",
				-40.0, 2);
	}

	@Override
	public void removeAttributeModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributes,
			int amplifier) {
		if (canApplyToEntity(entity, amplifier)) {
			super.removeAttributeModifiersFromEntity(entity, attributes, amplifier);
		} else if (isVillager(entity)) {
			EntityCreature creature = (EntityCreature) entity;
			Iterator<EntityAITaskEntry> itr = creature.tasks.taskEntries.iterator();
			EntityAIBase task = null;
			while (itr.hasNext()) {
				EntityAITasks.EntityAITaskEntry entityaitaskentry = (EntityAITaskEntry) itr.next();
				EntityAIBase entityaibase1 = entityaitaskentry.action;
				if (entityaibase1 instanceof EntityAIMoveTowardsVampire) {
					task = entityaibase1;
					break;
				}
			}
			if (task != null) {
				creature.tasks.removeTask(task);
			}
		}
	}

}
