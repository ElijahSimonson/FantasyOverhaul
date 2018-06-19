package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import java.util.UUID;

import com.google.common.base.Optional;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class EntitySummonedUndead extends EntityMob{
	
	private int timeToLive;
	
	private DataParameter<Boolean> OBSCURED = EntityDataManager.createKey(EntitySummonedUndead.class, DataSerializers.BOOLEAN);
	private DataParameter<Optional<UUID>> SUMMONER = EntityDataManager.createKey(EntitySummonedUndead.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private DataParameter<Boolean> SCREAMING = EntityDataManager.createKey(EntitySummonedUndead.class, DataSerializers.BOOLEAN);

	public EntitySummonedUndead(World worldIn) {
		super(worldIn);
		setTimeToLive(-1);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(OBSCURED, false);
		dataManager.register(SUMMONER, Optional.absent());
		dataManager.register(SCREAMING, false);
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}
	
	public boolean isTemp() {
		return timeToLive != -1;
	}

	@Override
	protected int decreaseAirSupply(int air) {
		return air;
	}
	
	public UUID getSummonerUUID() {
		return dataManager.get(SUMMONER).isPresent() ? dataManager.get(SUMMONER).get() : Reference.BLANK_UUID;
	}
	
	public EntityPlayer getSummoner() {
		return worldObj.getPlayerEntityByUUID(getSummonerUUID());
	}
	
	public void setSummoner(UUID uuid) {
		enablePersistence();
		dataManager.set(SUMMONER, Optional.fromNullable(uuid));
	}
	
	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}
	
	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		if (!isTemp()) {
			int chance = rand.nextInt(Math.max(4 - lootingModifier, 2));
			int quantity = (chance == 0) ? 1 : 0;
			if (quantity > 0) {
				entityDropItem(ItemInit.DUST_SPECTRAL.createStack(quantity), 0.0f);
			}
		}
	}
	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		if (worldObj != null && !isDead && !worldObj.isRemote && timeToLive != -1) {
			timeToLive -= 1;
			if (timeToLive == 0 || getAttackTarget() == null || getAttackTarget().isDead) {
				ParticleEffect.EXPLODE.send(SoundEffect.NONE, this, 1.0, 1.0, 16);
				setDead();
			}
		}
	}
	
	@Override
	public int getTalkInterval() {
		return super.getTalkInterval() * 3;
	}
	
	public boolean isScreaming() {
		return dataManager.get(SCREAMING);
	}
	
	public void setScreaming(boolean scream) {
		dataManager.set(SCREAMING, scream);
	}
	
	public boolean isObscured() {
		return dataManager.get(OBSCURED);
	}
	
	public void setObscured(boolean obscured) {
		dataManager.set(OBSCURED, obscured);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return super.attackEntityFrom(source, Math.min(amount, 15.0f));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (getSummonerUUID() != Reference.BLANK_UUID) {
			compound.setUniqueId("Summoner", getSummonerUUID());
		}
		compound.setBoolean("Obscured", isObscured());
		if (timeToLive != -1) {
			compound.setInteger("SuicideIn", timeToLive);	
		}
		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("Summoner")) {
			setSummoner(compound.getUniqueId("Summoner"));
		}
		setObscured(compound.getBoolean("Obscured"));
		if (compound.hasKey("SuicideIn")) {
			timeToLive = compound.getInteger("SuicideIn");
		}else {
			timeToLive = -1;
		}
	}
}
