package nz.castorgaming.fantasyoverhaul.objects.tileEntity;

import java.util.ArrayList;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.classes.CreatureUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class TileEntityBearTrap extends TileEntityBase {

	private final boolean silvered;
	private GameProfile owner;
	private boolean sprung;
	private long setTime;
	private long startTime;
	private UUID spawnedWolfID;
	private static final int MIN_LURE_TIME;
	private static final int LURE_EXTRA;

	public TileEntityBearTrap() {
		this(false);
	}

	public TileEntityBearTrap(boolean silvered) {
		setSprung(true);
		setSetTime(0);
		startTime = 0;
		spawnedWolfID = null;
		this.silvered = silvered;
	}

	public boolean tryTrapWolf(EntityLivingBase living) {
		if (silvered && living instanceof EntityWolfman) {
			EntityWolfman wolf = (EntityWolfman) living;
			if (spawnedWolfID != null && wolf != null && wolf.getPersistanceID().equals(spawnedWolfID)) {
				SoundEffect.MOB_WOLFMAN_HOWL.playAt(this, 1.0f);
				wolf.setInfectious();
				return true;
			}
		}
		return false;
	}

	public boolean isSprung() {
		return sprung;
	}

	public boolean canUpdate() {
		return silvered;
	}

	@Override
	public void update() {
		super.update();
		if (!worldObj.isRemote && silvered && !isSprung() && spawnedWolfID == null && TimeUtilities.secondsElapsed(10, ticks)) {
			if (baitFound() && CreatureUtilities.isFullMoon(worldObj)) {
				long time = worldObj.getTotalWorldTime();
				if (startTime > 0) {
					long activateTime = startTime;
					if (time > activateTime && CreatureUtilities.isFullMoon(worldObj)) {
						EntityCreature creature = Infusion.spawnCreature(worldObj, EntityWolfman.class, pos.getX(), pos.getY(), pos.getZ(), null, 10, 32, ParticleEffect.SMOKE,
								SoundEffect.MOB_WOLFMAN_TALK);
						if (creature != null) {
							creature.enablePersistence();
							spawnedWolfID = creature.getPersistentID();
						}
					}
				}
				else {
					startTime = time;
				}
			}
			else {
				startTime = 0;
			}
		}
	}

	private boolean baitFound() {
		boolean foundSheep = false;
		AxisAlignedBB bounds = new AxisAlignedBB(0.5 + pos.getX() - 8.0, 0.5 + pos.getY() - 8.0, 0.5 + pos.getZ() - 8.0, 0.5 + pos.getX() + 8.0, 0.5 + pos.getY() + 8.0, 0.5 + pos.getZ() + 8.0);
		ArrayList<EntitySheep> sheep = (ArrayList) worldObj.getEntitiesWithinAABB(EntitySheep.class, bounds);
		for (EntitySheep aSheep : sheep) {
			if (aSheep.getDistanceSq(getPos()) <= 64.0 && aSheep.getLeashed()) {
				foundSheep = true;
				break;
			}
		}
		boolean wolfAltar = findWolfAltar();
		return wolfAltar && foundSheep;
	}

	private boolean findWolfAltar() {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			if (worldObj.getBlockState(getPos().offset(facing)) == BlockInit.wolf_altar) {
				return true;
			}
		}

		return false;
	}

	public boolean isVisibleTo(EntityPlayer player) {
		return isSprung() || getOwner() == null || silvered || (player != null && player.getGameProfile().equals(getOwner()));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("Sprung", isSprung());
		nbt.setLong("WolfTrapStart", startTime);
		if (spawnedWolfID != null) {
			nbt.setLong("WolfLeast", spawnedWolfID.getLeastSignificantBits());
			nbt.setLong("WolfMost", spawnedWolfID.getMostSignificantBits());
		}
		if (getOwner() != null) {
			NBTTagCompound player = new NBTTagCompound();
			NBTUtil.writeGameProfile(player, getOwner());
			nbt.setTag("Owner", player);
		}

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		setSprung(compound.getBoolean("Sprung"));
		startTime = compound.getLong("WolfTrapStart");
		if (compound.hasKey("Owner", 10)) {
			setOwner(NBTUtil.readGameProfileFromNBT(compound.getCompoundTag("Owner")));
		}
		else {
			setOwner(null);
		}

		if (compound.hasKey("WolfMost") && compound.hasKey("WolfLeast")) {
			spawnedWolfID = new UUID(compound.getLong("WolfMost"), compound.getLong("WolfLeast"));
		}
		else {
			spawnedWolfID = null;
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.getNbtCompound());
		worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
	}

	public GameProfile getOwner() {
		return owner;
	}

	public void setOwner(GameProfile owner) {
		this.owner = owner;
	}

	public void setSprung(boolean sprung) {
		this.sprung = sprung;
	}

	public long getSetTime() {
		return setTime;
	}

	public void setSetTime(long setTime) {
		this.setTime = setTime;
	}

	static {
		MIN_LURE_TIME = TimeUtilities.minsToTicks(1);
		LURE_EXTRA = TimeUtilities.minsToTicks(1);
	}

}
