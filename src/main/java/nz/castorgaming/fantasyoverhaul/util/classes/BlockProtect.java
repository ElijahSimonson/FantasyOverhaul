package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class BlockProtect {

	public static boolean canBreak(Block block, World world) {
		return canBreak(block, world, true);
	}

	public static boolean canBreak(Block block, World world, boolean denyContainers) {
		return (block == null || !block.hasTileEntity(block.getDefaultState()) && block != Blocks.DRAGON_EGG
				&& block != Blocks.BEDROCK);
	}

	public static boolean canBreak(int x, int y, int z, World world) {
		return canBreak(new BlockPos(x, y, z), world, true);
	}

	public static boolean canBreak(BlockPos blockPos, World world) {
		return canBreak(blockPos, world, true);
	}

	public static boolean canBreak(BlockPos blockPos, World world, boolean denyContainers) {
		IBlockState state = world.getBlockState(blockPos);
		Block block = state.getBlock();

		return canBreak(block, world, denyContainers);
	}

	public static boolean checkModsForBreakOK(World world, int x, int y, int z, EntityLivingBase entity) {
		IBlockState state = world.getBlockState(new BlockPos(x, y, z));
		return checkModsForBreakOK(world, x, y, z, state, entity);
	}

	public static boolean checkModsForBreakOK(World world, int x, int y, int z, IBlockState state,
			EntityLivingBase entity) {
		return checkModsForBreakOK(world, new BlockPos(x, y, z), state, entity);
	}

	public static boolean checkModsForBreakOK(World world, BlockPos blockPos, IBlockState state,
			EntityLivingBase entity) {
		Block block = state.getBlock();
		boolean allowBreak = block.getBlockHardness(state, world, blockPos) != -1.0f;

		if (allowBreak && entity != null && entity instanceof EntityPlayer && Config.instance().allowBlockBreakEvents) {
			BreakEvent event = new BreakEvent(world, blockPos, state, (EntityPlayer) entity);
			event.setCanceled(false);
			MinecraftForge.EVENT_BUS.post(event);
			allowBreak = !event.isCanceled();
		}
		return allowBreak;
	}
}
