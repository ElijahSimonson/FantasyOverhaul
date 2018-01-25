package nz.castorgaming.fantasyoverhaul.powers.playereffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class ImpFireTouch extends PlayerEffect {
	public ImpFireTouch() {
		super("impfiretouch");
	}

	@Override
	protected void doUpdate(final EntityPlayer player, final int worldTicks) {
	}

	@Override
	protected void doHarvest(final EntityPlayer player, final BlockEvent.HarvestDropsEvent event) {
	}

	@Override
	protected void doInteract(final EntityPlayer player, final PlayerInteractEvent event) {
		final World world = player.worldObj;
		if (world.rand.nextDouble() < 0.2) {
			final Block block = BlockUtil.getBlock(world, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
			if (block != null && block != Blocks.AIR) {
				int par4 = event.getPos().getX();
				int par5 = event.getPos().getY();
				int par6 = event.getPos().getZ();
				final EnumFacing par7 = event.getFace();
				if (par7 == EnumFacing.DOWN) {
					--par5;
				}
				if (par7 == EnumFacing.UP) {
					++par5;
				}
				if (par7 == EnumFacing.NORTH) {
					--par6;
				}
				if (par7 == EnumFacing.SOUTH) {
					++par6;
				}
				if (par7 == EnumFacing.WEST) {
					--par4;
				}
				if (par7 == EnumFacing.EAST) {
					++par4;
				}
				if (event instanceof PlayerInteractEvent.LeftClickBlock) {
					par4 = par4 - 1 + world.rand.nextInt(3);
					par6 = par6 - 1 + world.rand.nextInt(3);
				}
				if (world.isAirBlock(new BlockPos(par4, par5, par6)) && !world.isAirBlock(new BlockPos(par4, par5 - 1, par6))) {
					world.playSound(null, new BlockPos(par4 + 0.5, par5 + 0.5, par6 + 0.5), SoundEffect.FIRE_FIRE.event(), SoundCategory.PLAYERS, 1.0f, world.rand.nextFloat() * 0.4f + 0.8f);
					world.setBlockState(new BlockPos(par4, par5, par6), Blocks.FIRE.getDefaultState());
				}
			}
		}
	}
}
