package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import java.util.UUID;

import com.google.common.base.Optional;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import nz.castorgaming.fantasyoverhaul.objects.worlds.WorldProviderDreamWorld;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;

public class EntityCorpse extends EntityLiving {

	private ThreadDownloadImageData downloadImageSkin;
	private ResourceLocation locationSkin;
	private DataParameter<Optional<UUID>> OWNER = EntityDataManager.createKey(EntityCorpse.class,
			DataSerializers.OPTIONAL_UNIQUE_ID);

	public EntityCorpse(World world) {
		super(world);
		setSize(1.2f, 0.5f);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
	}

	@Override
	public void moveEntity(double x, double y, double z) {
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(OWNER, Optional.fromNullable(Reference.BLANK_UUID));
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (worldObj.isRemote) {
			return super.attackEntityFrom(source, amount);
		}
		if (source.getSourceOfDamage() != null && source.getSourceOfDamage() instanceof EntityPlayer
				&& ((EntityPlayer) source.getSourceOfDamage()).capabilities.isCreativeMode) {
			return super.attackEntityFrom(source, amount);
		}
		UUID user = getOwner();
		for (WorldServer world : worldObj.getMinecraftServer().worldServers) {
			EntityPlayer player = world.getPlayerEntityByUUID(user);
			if (player != null) {
				return super.attackEntityFrom(source, amount);
			}
		}
		return false;
	}

	public UUID getOwner() {
		return dataManager.get(OWNER).isPresent() ? dataManager.get(OWNER).get() : Reference.BLANK_UUID;
	}

	public void setOwner(UUID user) {
		enablePersistence();
		dataManager.set(OWNER, Optional.fromNullable(user));
	}

	public void setOwner(String user) {
		setOwner(UUID.fromString(user));
	}

	public String getOwnerName() {
		MinecraftServer server;
		if (!worldObj.isRemote) {
			server = worldObj.getMinecraftServer();
		} else {
			server = FMLCommonHandler.instance().getMinecraftServerInstance();
		}
		return server.getPlayerList().getPlayerByUUID(getOwner()).getName();
	}

	protected void setupCustomSkin() {
		String user = getOwnerName();
		locationSkin = AbstractClientPlayer.getLocationSkin(user);
		downloadImageSkin = AbstractClientPlayer.getDownloadImageSkin(locationSkin, user);
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		if (!worldObj.isRemote) {
			UUID uuid = getOwner();
			WorldServer[] servers = worldObj.getMinecraftServer().worldServers;
			int len = servers.length;
			int i = 0;
			while (i < len) {
				WorldServer world = servers[i];
				EntityPlayer player = world.getPlayerEntityByUUID(uuid);
				if (player != null) {
					if (player.dimension == Config.instance().dimensionDreamID) {
						WorldProviderDreamWorld.returnPlayerToOverworld(player);
						break;
					}
					if (WorldProviderDreamWorld.isPlayerGhost(player)) {
						WorldProviderDreamWorld.returnGhostPlayerToSpiritWorld(player);
						WorldProviderDreamWorld.returnPlayerToOverworld(player);
						break;
					}
					break;
				} else {
					i++;
				}
			}
		}
	}

	public ResourceLocation getLocationSkin() {
		if (locationSkin == null) {
			setupCustomSkin();
		}
		if (locationSkin != null) {
			return locationSkin;
		}
		return DefaultPlayerSkin.getDefaultSkinLegacy();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (getOwner() != Reference.BLANK_UUID) {
			compound.setUniqueId("Owner", getOwner());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("Owner")) {
			setOwner(compound.getUniqueId("Owner"));
		}
	}
}
