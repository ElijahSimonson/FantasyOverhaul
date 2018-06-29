package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.ChunkProviderOverworld;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.entities.EntityFlyingTameable;
import nz.castorgaming.fantasyoverhaul.objects.entities.ai.EntityAIFlyerFlyToWaypoint;
import nz.castorgaming.fantasyoverhaul.objects.entities.ai.EntityAIFlyerFollowOwner;
import nz.castorgaming.fantasyoverhaul.objects.entities.ai.EntityAIFlyerLand;
import nz.castorgaming.fantasyoverhaul.objects.entities.ai.EntityAIFlyerWander;
import nz.castorgaming.fantasyoverhaul.objects.entities.ai.EntityAIFlyingTempt;
import nz.castorgaming.fantasyoverhaul.objects.entities.ai.EntityAISitAndStay;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class EntitySpirit extends EntityFlyingTameable {
	public EntityAIFlyingTempt aiTempt;
	private int timeToLive;
	private Type type;
	private static final Set<Item> TEMPTATION;
	private static Field fieldStructureGenerators;
	private static Field fieldVillageGenerator;
	private DataParameter<Integer> COLOR = EntityDataManager.createKey(EntitySpirit.class, DataSerializers.VARINT);

	public EntitySpirit(World world) {
		super(world);
		type = Type.WILD;
		timeToLive = -1;
		setSize(0.25f, 0.25f);
		((PathNavigateGround) getNavigator()).setCanSwim(true);
		tasks.addTask(1, new EntityAISitAndStay(this));
		tasks.addTask(3, aiTempt = new EntityAIFlyingTempt(this, true, EntitySpirit.TEMPTATION));
		tasks.addTask(5, new EntityAIFlyerFollowOwner(this, 1.0, 14.0f, 5.0f));
		tasks.addTask(8, new EntityAIFlyerFlyToWaypoint(this, EntityAIFlyerFlyToWaypoint.CarryRequirement.NONE));
		tasks.addTask(9, new EntityAIFlyerLand(this, 0.8, true));
		tasks.addTask(10, new EntityAIFlyerWander(this, 0.8, 10.0));
		tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0f, 0.2f));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.isRemote) {
			int color = getFeatherColor();
			float red = 1.0f;
			float green = 1.0f;
			float blue = 1.0f;
			if (color > 0) {
				red = (color >> 16 & 0xFF) / 255.0f;
				green = (color >> 8 & 0xFF) / 255.0f;
				blue = (color & 0xFF) / 255.0f;
			}
			FantasyOverhaul.proxy.generateParticle(this.worldObj,
					this.posX - this.width * 0.5 + this.worldObj.rand.nextDouble() * this.width,
					0.1 + this.posY + this.worldObj.rand.nextDouble() * 0.2,
					this.posZ - this.width * 0.5 + this.worldObj.rand.nextDouble() * this.width, red, green, blue, 10,
					-0.1f);
		}
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		ItemStack stack = null;
		switch (type) {
		case SUBDUED:
			return;
		case VILLAGE:
			stack = new ItemStack(ItemInit.SPIRIT_SUBDUED, 1, 1);
			break;
		case WILD:
			stack = new ItemStack(ItemInit.SPIRIT_SUBDUED, 1, 0);
			break;
		}
		entityDropItem(stack, 0.0f);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("SuicideIn")) {
			timeToLive = compound.getInteger("SuicideIn");
		} else {
			timeToLive = -1;
		}
		if (compound.hasKey("SpiritType")) {
			type = Type.fromMeta(compound.getInteger("SpiritType"));
		} else {
			type = Type.WILD;
		}
	}

	public void setTarget(String target, Type typeIn) {
		timeToLive = TimeUtilities.secsToTicks(10);
		type = typeIn;
		try {
			if (target.equals("Village")) {
				IChunkProvider cp;
				for (cp = worldObj.getChunkProvider(); cp != null && cp instanceof ChunkProviderServer;) {
					if (cp instanceof ChunkProviderFlat) {
						if (EntitySpirit.fieldStructureGenerators == null) {
							EntitySpirit.fieldStructureGenerators = ReflectionHelper.findField(ChunkProviderFlat.class,
									"structureGenerators");
						}
						Iterator itr = ((List) EntitySpirit.fieldStructureGenerators.get(cp)).iterator();
						while (itr.hasNext()) {
							if (setWaypointTo(itr.next(), MapGenVillage.class)) {
								return;
							}
						}
					} else if (cp instanceof ChunkProviderOverworld) {
						if (EntitySpirit.fieldVillageGenerator == null) {
							EntitySpirit.fieldVillageGenerator = ReflectionHelper
									.findField(ChunkProviderOverworld.class, "villageGenerator");
						}
						if (EntitySpirit.fieldVillageGenerator != null) {
							setWaypointTo(EntitySpirit.fieldVillageGenerator.get(cp), MapGenVillage.class);
						}
					} else if (cp instanceof ChunkProviderHell) {
						Field fieldGenNetherBridge = ReflectionHelper.findField(ChunkProviderHell.class,
								"genNetherBridge");
						MapGenStructure genNetherBridge = (MapGenStructure) fieldGenNetherBridge.get(cp);
						setWaypointTo(genNetherBridge);
					}
				}
			}
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
	}

	private void setWaypointTo(MapGenStructure mapStructure) {
		if (mapStructure != null) {
			BlockPos pos = mapStructure.getClosestStrongholdPos(worldObj, getPosition());
			if (pos != null) {
				homeX = pos.getX();
				homeY = pos.getY();
				homeZ = pos.getZ();
				waypoint = ItemInit.WAYSTONE.createStack();
			}
		}
	}

	private boolean setWaypointTo(Object object, Class<MapGenVillage> class1) {
		if (object != null && class1.isAssignableFrom(object.getClass())) {
			setWaypointTo((MapGenStructure) object);
			return true;
		}
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("SuicideIn", timeToLive);
		compound.setInteger("SpiritType", type.toMeta());
	}

	@Override
	protected int decreaseAirSupply(int air) {
		return air;
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}

	@Override
	public boolean canMateWith(EntityAnimal otherAnimal) {
		return false;
	}

	@Override
	protected void updateAITasks() {
		getNavigator().clearPathEntity();
		super.updateAITasks();
		if (worldObj != null && !isDead && !worldObj.isRemote && timeToLive != -1 && --timeToLive == 0) {
			ParticleEffect.EXPLODE.send(SoundEffect.NONE, this, 1.0, 1.0, 16);
			setDead();
			if (!worldObj.isRemote) {
				dropFewItems(false, 0);
			}
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		if (worldObj.provider.getDimension() == Config.instance().dimensionDreamID) {
			boolean canSpawnHere = worldObj.checkNoEntityCollision(getEntityBoundingBox())
					&& worldObj.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty()
					&& !worldObj.containsAnyLiquid(getEntityBoundingBox());
			int x = MathHelper.floor_double(posX);
			int y = MathHelper.floor_double(getEntityBoundingBox().minY);
			int z = MathHelper.floor_double(posZ);
			canSpawnHere = (canSpawnHere && getBlockPathWeight(new BlockPos(x, y, z)) >= 0.0f && y >= 60);
			Block block = worldObj.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
			return canSpawnHere && worldObj.rand.nextInt(10) == 0 && (block == Blocks.GRASS || block == Blocks.SAND)
					&& worldObj.getLight(new BlockPos(x, y, z)) > 8;
		}
		return false;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(COLOR, 0);
	}

	public int getFeatherColor() {
		return dataManager.get(COLOR);
	}

	public void setFeatherColor(int color) {
		dataManager.set(COLOR, color);
	}

	@Override
	public int getTalkInterval() {
		return super.getTalkInterval() * 2;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack == null ? false : stack.getItem() == Items.BONE;
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	static {
		TEMPTATION = new HashSet<Item>();
		TEMPTATION.add(ItemInit.FOCUSED_WILL);
	}

	enum Type implements IStringSerializable {
		SUBDUED("subdued", 0), VILLAGE("village", 1), WILD("wild", 2);

		private String name;
		private static Map<String, Type> relations;
		private int meta;

		private Type(String nameIn, int metaIn) {
			name = nameIn;
			meta = metaIn;
		}

		@Override
		public String getName() {
			return name;
		}

		public static Type fromName(String name) {
			return relations.get(name);
		}

		public int toMeta() {
			return meta;
		}

		public static Type fromMeta(int meta) {
			for (Type type : Type.values()) {
				if (type.meta == meta) {
					return type;
				}
			}
			return Type.SUBDUED;
		}

		static {
			relations = new HashMap<String, Type>();
			for (Type type : Type.values()) {
				relations.put(type.getName(), type);
			}
		}
	}

}
