package nz.castorgaming.fantasyoverhaul.objects.blocks.tileBlocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.tileEntity.TileEntityBearTrap;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockUtil;
import nz.castorgaming.fantasyoverhaul.util.classes.CreatureUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class BlockBearTrap extends TileBlockBase {

	private AxisAlignedBB boundingBox = new AxisAlignedBB(0.19999999f, 0.01f, 0.19999999f, 0.8f, 0.1f, 0.8f);
	private boolean silvered;
	private PropertyDirection facing = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	private PropertyBool sprung = PropertyBool.create("sprung");

	public BlockBearTrap(boolean silvered) {
		super(Material.IRON, TileEntityBearTrap.class, "bear_trap");
		this.silvered = silvered;
		setHardness(5.0f);
		setResistance(10.0f);
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState().withProperty(facing, EnumFacing.NORTH).withProperty(sprung, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, facing, sprung);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return boundingBox;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return null;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return boundingBox;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBearTrap(silvered);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing side) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		switch (placer.getHorizontalFacing()) {
		case NORTH: {
			worldIn.setBlockState(pos, state.withProperty(facing, EnumFacing.NORTH).withProperty(sprung, false), 2);
			break;
		}
		case SOUTH: {
			worldIn.setBlockState(pos, state.withProperty(facing, EnumFacing.SOUTH).withProperty(sprung, false), 2);
			break;
		}
		case EAST: {
			worldIn.setBlockState(pos, state.withProperty(facing, EnumFacing.EAST).withProperty(sprung, false), 2);
			break;
		}
		case WEST: {
			worldIn.setBlockState(pos, state.withProperty(facing, EnumFacing.WEST).withProperty(sprung, false), 2);
			break;
		}
		default:
			break;
		}
		if (worldIn.isRemote && placer instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) placer;
			TileEntityBearTrap tile = BlockUtil.getTileEntity(worldIn, pos, TileEntityBearTrap.class);
			if (tile != null) {
				tile.setOwner(player.getGameProfile());
				tile.setSprung(true);
				tile.notifyBlockUpdate(false);
			}
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if (silvered) {
			return new ArrayList<ItemStack>();
		}
		return super.getDrops(world, pos, state, fortune);
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!worldIn.isRemote && entityIn instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entityIn;
			TileEntityBearTrap tile = BlockUtil.getTileEntity(worldIn, pos, TileEntityBearTrap.class);
			if (tile != null && !tile.isSprung() && worldIn.getTotalWorldTime() > tile.getSetTime() + 20L
					&& (!silvered || CreatureUtilities.isWerewolf(living, false))) {
				AxisAlignedBB trapBounds = FULL_BLOCK_AABB;
				if (trapBounds.intersectsWith(living.getCollisionBoundingBox())
						&& (silvered || tile.tryTrapWolf(living))) {
					boolean isCreative = entityIn instanceof EntityPlayer
							&& ((EntityPlayer) living).capabilities.isCreativeMode;
					if (!isCreative) {
						living.addPotionEffect(
								new PotionEffect(Potions.PARALYSED, TimeUtilities.secsToTicks(30), 2, true, false));
					}
					living.attackEntityFrom(DamageSource.anvil, 4.0f);
					ParticleEffect.REDDUST.send(SoundEffect.RANDOM_MANTRAP, worldIn, pos, 0.25, 0.5, 16);
					tile.setSprung(true);
					tile.notifyBlockUpdate(true);

				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileEntityBearTrap tile = BlockUtil.getTileEntity(worldIn, pos, TileEntityBearTrap.class);
			if (tile != null) {
				SoundEffect.RANDOM_CLICK.playAtPlayer(worldIn, playerIn);
				tile.setSprung(!tile.isSprung());
				if (!tile.isSprung()) {
					tile.setSetTime(worldIn.getTotalWorldTime());
				}
				tile.notifyBlockUpdate(false);
			}
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static boolean checkForHiddenTrap(EntityPlayer player, RayTraceResult ray) {
		if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK
				&& player.worldObj.getBlockState(ray.getBlockPos()).getBlock() == BlockInit.bear_trap) {
			TileEntityBearTrap tile = BlockUtil.getTileEntity(player.worldObj, ray.getBlockPos(),
					TileEntityBearTrap.class);
			if (tile != null) {
				return !tile.isVisibleTo(player);
			}
		}
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = getDefaultState();

		switch (meta) {
		case 0: {
			return state.withProperty(facing, EnumFacing.NORTH).withProperty(sprung, false);
		}
		case 1: {
			return state.withProperty(facing, EnumFacing.SOUTH).withProperty(sprung, false);
		}
		case 2: {
			return state.withProperty(facing, EnumFacing.EAST).withProperty(sprung, false);
		}
		case 3: {
			return state.withProperty(facing, EnumFacing.WEST).withProperty(sprung, false);
		}
		case 4: {
			return state.withProperty(facing, EnumFacing.NORTH).withProperty(sprung, true);
		}
		case 5: {
			return state.withProperty(facing, EnumFacing.SOUTH).withProperty(sprung, true);
		}
		case 6: {
			return state.withProperty(facing, EnumFacing.EAST).withProperty(sprung, true);
		}
		case 7: {
			return state.withProperty(facing, EnumFacing.WEST).withProperty(sprung, true);
		}
		default:
			break;
		}

		return state;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		boolean isSprung = state.getValue(sprung);
		EnumFacing isFacing = state.getValue(facing);

		int meta;
		switch (isFacing) {
		case NORTH:
			meta = 0;
			break;
		case SOUTH:
			meta = 1;
			break;
		case EAST:
			meta = 2;
			break;
		case WEST:
			meta = 3;
			break;
		default:
			meta = 0;
			break;
		}
		if (isSprung) {
			meta += 4;
		}

		return meta;
	};

}
