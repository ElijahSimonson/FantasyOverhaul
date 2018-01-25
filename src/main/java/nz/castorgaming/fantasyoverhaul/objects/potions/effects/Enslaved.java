package nz.castorgaming.fantasyoverhaul.objects.potions.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;
import nz.castorgaming.fantasyoverhaul.objects.potions.tasks.EntityAIEnslaverHurtByTarget;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingSetAttackTarget;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingUpdate;

public class Enslaved extends PotionBase implements IHandleLivingSetAttackTarget, IHandleLivingUpdate {

	public Enslaved(int liquidColorIn) {
		super(true, liquidColorIn);
	}

	private static final String ENSLAVER_KEY = "WITCEnslaverName";

	@Override
	public void onLivingUpdate(World world, EntityLivingBase entity, LivingUpdateEvent event, int p1, int p2) {
		if (!world.isRemote && world.getTotalWorldTime() % 20L == 3L && entity instanceof EntityCreature) {
			EntityCreature creature = (EntityCreature) entity;
			for (Object obj : creature.targetTasks.taskEntries) {
				EntityAITasks.EntityAITaskEntry task = (EntityAITaskEntry) obj;
				if (task.action instanceof EntityAIEnslaverHurtByTarget) {
					return;
				}
			}
			creature.targetTasks.addTask(1, new EntityAIEnslaverHurtByTarget(creature));
		}
	}

	@Override
	public void onLivingSetAttackTarget(World world, EntityLiving entity, LivingSetAttackTargetEvent event, int p1) {
		Entity target = event.getTarget();
		if (target != null && target instanceof EntityPlayer && entity instanceof EntityLivingBase) {
			String enslaverName = getMobEnslaverName(entity);
			if (enslaverName.equals(target.getUniqueID().toString())) {
				entity.setAttackTarget(null);
			}
		}
	}

	public static boolean setEnslaverForMob(EntityLiving entity, EntityPlayer player) {
		if (entity == null || player == null) {
			return false;
		}
		String enslaverName = entity.getEntityData().getString(ENSLAVER_KEY);
		boolean isEnslaved = enslaverName != null && !enslaverName.isEmpty();
		if (!isEnslaved || !player.getCommandSenderEntity().getName().equals(enslaverName)) {
			entity.getEntityData().setString(ENSLAVER_KEY, player.getUniqueID().toString());
			entity.addPotionEffect(new PotionEffect(Potions.ENSLAVED, Integer.MAX_VALUE));
			EntityUtil.dropAttackTarget(entity);
			return true;
		}
		return false;
	}

	public static boolean isMobEnslavedBy(EntityLiving entity, EntityPlayer player) {
		return player != null && entity != null && entity.getEntityData() != null && player.getCommandSenderEntity()
				.getUniqueID().toString().equals(entity.getEntityData().getString(ENSLAVER_KEY));
	}

	public static boolean canCreatureBeEnslaved(EntityLivingBase entity) {
		return entity instanceof EntityLiving && entity.isNonBoss() && !(entity instanceof EntityGolem)
				&& !(entity instanceof EntityDemon) && !(entity instanceof EntityWitch)
				&& !(entity instanceof EntityImp) && !(entity instanceof EntityEnt);
	}

	public static boolean isMobEnslaved(EntityLiving entity) {
		if (entity == null) {
			return false;
		}
		String enslaverName = entity.getEntityData().getString(ENSLAVER_KEY);
		return enslaverName != null && !enslaverName.isEmpty();
	}

	public static String getMobEnslaverName(EntityLiving entity) {
		if (entity == null) {
			return "";
		}
		String enslaverName = entity.getEntityData().getString(ENSLAVER_KEY);
		return enslaverName;
	}
}
