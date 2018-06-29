package nz.castorgaming.fantasyoverhaul.capabilities.extendedVillager;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Resizing;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;

public class ExtendVillager implements IExtendVillager {
	private EntityVillager villager;
	private int blood = 500;
	private boolean sleeping;
	private int sleepingTicks;
	public boolean synced;
	private boolean trySync;

	public ExtendVillager() {
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		blood = nbt.getInteger("blood");
		sleeping = nbt.getBoolean("sleeping");
		sleepingTicks = nbt.getInteger("sleepingTicks");
		synced = nbt.getBoolean("synced");
		trySync = nbt.getBoolean("trySync");
	}

	@Override
	public int getBlood() {
		return blood;
	}

	@Override
	public void giveBlood(int quantity) {
		if (blood < 500) {
			setBlood(getBlood() + quantity);
		}
	}

	@Override
	public void incrementSleepingTicks() {
		++sleepingTicks;
	}

	@Override
	public boolean isClientSynced() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSleeping() {
		return sleeping;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("blood", blood);
		tag.setBoolean("sleeping", sleeping);
		tag.setInteger("sleepingTicks", sleepingTicks);
		tag.setBoolean("synced", synced);
		tag.setBoolean("trySync", trySync);

		return tag;
	}

	@Override
	public void setBlood(int blood) {
		if (this.blood != blood) {
			this.blood = Math.max(Math.min(blood, 500), 0);
			sync();
		}
	}

	@Override
	public void setSleeping(boolean sleeping) {
		if (this.sleeping != sleeping) {
			this.sleeping = sleeping;
			if (this.sleeping) {
				Resizing.setEntitySize(villager, 0.8f, 1.1f);
			} else {
				Resizing.setEntitySize(villager, 0.6f, 1.8f);
				if (sleepingTicks > TimeUtilities.minsToTicks(1)) {
					int blops = sleepingTicks / TimeUtilities.minsToTicks(1);
					giveBlood(50 * blops);
				}
			}
			sleepingTicks = 0;
			sync();
		}
	}

	@Override
	public void setVillager(EntityVillager entity) {
		villager = entity;
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub

	}

	@Override
	public int takeBlood(int quantity, EntityLivingBase player) {
		PotionEffect potionEffect = villager.getActivePotionEffect(Potions.PARALYSED);
		boolean isKnockedOut = isSleeping() || potionEffect != null && potionEffect.getAmplifier() >= 4;
		if (!isKnockedOut) {
			quantity = (int) Math.ceil(0.66f * quantity);
		}
		int remainder = Math.max(blood - quantity, 0);
		int taken = blood - remainder;
		setBlood(remainder);
		if (player instanceof EntityPlayer) {
			if (blood < Math.ceil(250.0)) {
				villager.attackEntityFrom(new EntityDamageSource(DamageSource.magic.getDamageType(), player), 1.3f);
			} else if (!isKnockedOut) {
				villager.attackEntityFrom(new EntityDamageSource(DamageSource.magic.getDamageType(), player), 0.1f);
			}
		}

		return taken;
	}

}
