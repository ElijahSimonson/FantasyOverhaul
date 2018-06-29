package nz.castorgaming.fantasyoverhaul.objects.blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.items.dreamweave.DreamWeave;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;

public class BlockDreamCatcher extends BlockBaseContainer {

	public static final AxisAlignedBB bounds = new AxisAlignedBB(0.25f, 0.0f, 0.25f, 0.75f, 1.0f, 0.75f);
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockDreamCatcher(String name) {
		super(name, Material.VINE);
		disableStats();
		setHardness(1.0f);
		setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
		this.setSoundType(SoundType.WOOD);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return bounds;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return bounds;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing side) {
		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity != null && tileEntity instanceof TileEntityDreamCatcher) {
				TileEntityDreamCatcher tileDreamCatcher = (TileEntityDreamCatcher) tileEntity;
				DreamWeave weave = tileDreamCatcher.getWeave();
				if (weave != null) {
					worldIn.spawnEntityInWorld(
							new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), weave.createStack()));
				}
			}
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<ItemStack>();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		boolean flag = true;
		IBlockState state = world.getBlockState(pos);
		ImmutableMap<IProperty<?>, Comparable<?>> stateProps = state.getProperties();
		EnumFacing facing = (EnumFacing) stateProps.get(FACING);
		if (comparePos(pos, neighbor, facing)) {
			if (world.getBlockState(neighbor).getMaterial().isSolid()) {
				flag = false;
			}
		}
		if (flag) {
			dropBlockAsItem((World) world, pos, world.getBlockState(pos), 0);
			((World) world).setBlockToAir(pos);
		}
		super.onNeighborChange(world, pos, neighbor);
	}

	private boolean comparePos(BlockPos origin, BlockPos neighbor, EnumFacing direction) {
		origin.offset(direction);
		return ((origin.getX() == neighbor.getX()) && (origin.getY() == neighbor.getY())
				&& (origin.getZ() == neighbor.getZ()));
	}

	public static boolean causesNightmares(World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity != null && tileEntity instanceof TileEntityDreamCatcher) {
			TileEntityDreamCatcher tileCatcher = (TileEntityDreamCatcher) tileEntity;
			return tileCatcher.dreamWeave == ItemInit.WEAVE_NIGHTMARE;
		}
		return false;
	}

	public static boolean enchancesDreams(World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity != null && tileEntity instanceof TileEntityDreamCatcher) {
			TileEntityDreamCatcher tileCatcher = (TileEntityDreamCatcher) tileEntity;
			return tileCatcher.dreamWeave == ItemInit.WEAVE_INTENSITY;
		}
		return false;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		return BlockInit.DREAM_CATCHER.createStackedBlock(getDefaultState());
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = getDefaultState();
		switch (meta) {
		case 0:
			return state.withProperty(FACING, EnumFacing.NORTH);
		case 1:
			return state.withProperty(FACING, EnumFacing.SOUTH);
		case 2:
			return state.withProperty(FACING, EnumFacing.WEST);
		case 3:
			return state.withProperty(FACING, EnumFacing.EAST);
		default:
			return state.withProperty(FACING, EnumFacing.NORTH);
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumFacing facing = (EnumFacing) state.getProperties().get(FACING);
		switch (facing) {
		case EAST:
			return 3;
		case NORTH:
			return 0;
		case SOUTH:
			return 1;
		case WEST:
			return 2;
		default:
			return 0;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityDreamCatcher();
	}

	public static class TileEntityDreamCatcher extends TileEntity implements ITickable {
		private boolean buffIfDay;
		private boolean buffIfNight;
		private DreamWeave dreamWeave;
		private static final String DWEAVE_KEY = "FOWeaveID";

		public void setEffect(DreamWeave dreamWeave) {
			this.dreamWeave = dreamWeave;
			if (!worldObj.isRemote) {
				worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
			}
		}

		public DreamWeave getWeave() {
			return dreamWeave;
		}

		private boolean areAllPlayersAsleep(World world) {
			Iterator<EntityPlayer> itr = world.playerEntities.iterator();
			int sleepThreshold = MathHelper.floor_float(
					0.01f * Config.instance().percentageOfPlayersSleepingForBuff * world.playerEntities.size());
			while (itr.hasNext()) {
				EntityPlayer player = itr.next();
				if (player.isPlayerSleeping() && --sleepThreshold <= 0) {
					return true;
				}
			}
			return false;
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			super.writeToNBT(compound);
			if (dreamWeave != null) {
				compound.setString(DWEAVE_KEY, dreamWeave.getRegistryName().toString());
			}
			return compound;
		}

		@Override
		public void readFromNBT(NBTTagCompound compound) {
			super.readFromNBT(compound);
			if (compound.hasKey(DWEAVE_KEY)) {
				dreamWeave = ItemInit.WEAVES.get(compound.getString(DWEAVE_KEY));
			}
		}

		@Override
		public SPacketUpdateTileEntity getUpdatePacket() {
			NBTTagCompound tag = new NBTTagCompound();
			writeToNBT(tag);
			return new SPacketUpdateTileEntity(pos, 1, tag);
		}

		@Override
		public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
			super.onDataPacket(net, pkt);
			readFromNBT(pkt.getNbtCompound());
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
		}

		@Override
		public void update() {
			if (!worldObj.isRemote && dreamWeave != null) {
				if (buffIfDay || buffIfNight) {
					boolean day = worldObj.isDaytime();
					if ((buffIfDay && day) || (buffIfNight && !day)) {
						boolean isDream = true;
						boolean isEnhanced = false;
						int r = 5;
						boolean done = false;
						for (int y = pos.getY() - r; y <= pos.getY() + r && !done; ++y) {
							for (int x = pos.getX() - r; x <= pos.getX() + r && !done; ++x) {
								for (int z = pos.getZ() - r; z <= pos.getZ() + r && !done; ++z) {
									if ((y != pos.getY() || x != pos.getX() || z != pos.getZ())
											&& worldObj.getBlockState(new BlockPos(x, y, z))
													.getBlock() == BlockInit.DREAM_CATCHER) {
										isDream = false;
										done = isEnhanced;
									} else if (BlockDreamCatcher.enchancesDreams(worldObj, new BlockPos(x, y, z))) {
										isEnhanced = true;
										done = !isDream;
									}
								}
							}
						}
						AxisAlignedBB bound = new AxisAlignedBB(pos.getX() - r, pos.getY() - r, pos.getZ() - r,
								pos.getX() + r, pos.getY() + r, pos.getZ() + r);
						List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, bound);
						for (EntityPlayer player : list) {
							PlayerVampire playerVamp = IPlayerVampire.get(player);
							if ((day && !playerVamp.isVampire()) || (!day && playerVamp.isVampire())) {
								dreamWeave.applyEffect(player, isDream, isEnhanced);
							}
						}
					}
					buffIfNight = false;
					buffIfDay = false;
				}
				if (!buffIfDay && !buffIfNight && areAllPlayersAsleep(worldObj)) {
					buffIfDay = !worldObj.provider.isDaytime();
					buffIfNight = !buffIfDay;
				}
			}
		}

	}
}
