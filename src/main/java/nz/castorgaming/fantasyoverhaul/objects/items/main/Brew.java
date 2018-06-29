package nz.castorgaming.fantasyoverhaul.objects.items.main;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class Brew extends Drinkable {

	public Brew(String name) {
		super("brew_ " + name);
	}

	public BrewResult onImpact(World world, EntityLivingBase thrower, RayTraceResult rtr, boolean enhanced, double x,
			double y, double z, AxisAlignedBB bounds) {
		return BrewResult.SHOW_EFFECT;
	}

	protected static boolean setBlockIfNotSolid(World world, BlockPos pos, Block block) {
		return setBlockIfNotSolid(world, pos, block, block.getDefaultState());
	}

	@SuppressWarnings("deprecation")
	protected static boolean setBlockIfNotSolid(World world, BlockPos pos, Block block, int metadata) {
		return setBlockIfNotSolid(world, pos, block, block.getStateFromMeta(metadata));
	}

	protected static boolean setBlockIfNotSolid(World world, BlockPos pos, Block block, IBlockState state) {
		if (!world.getBlockState(pos).getMaterial().isSolid()
				|| (block == Blocks.WEB && BlockUtil.getBlock(world, pos) == Blocks.SNOW)) {
			BlockUtil.setBlock(world, pos, state);
			ParticleEffect.EXPLODE.send(SoundEffect.NONE, world, 0.5 + pos.getX(), 0.5 + pos.getY(), 0.5 + pos.getZ(),
					1.0, 1.0, 16);
			return true;
		}
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		throwBrew(itemStackIn, worldIn, playerIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

	public void throwBrew(ItemStack stack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
		}
		world.playSound(player, player.getPosition(), SoundEffect.RANDOM_BOW.event(), SoundCategory.PLAYERS, 0.5f,
				0.4f / (GeneralItem.itemRand.nextFloat() * 0.4f + 0.8f));
		if (!world.isRemote) {
			world.spawnEntityInWorld(new EntityWitchProjectile(world, player, stack.getItem()));
		}
	}

	public enum BrewResult {
		DROP_ITEM, SHOW_EFFECT, HIDE_EFFECT;
	}
}
