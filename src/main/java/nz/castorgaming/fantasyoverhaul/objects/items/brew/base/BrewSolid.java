package nz.castorgaming.fantasyoverhaul.objects.items.brew.base;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockProtect;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class BrewSolid extends Brew {
	
	protected Block replacementBlock;

	public BrewSolid(String name, Block stone) {
		super(name);
		replacementBlock = stone;
	}
	
	@Override
	public BrewResult onImpact(World world, EntityLivingBase thrower, RayTraceResult rtr, boolean enhanced, double x,
			double y, double z, AxisAlignedBB bounds) {
		if (rtr.typeOfHit == RayTraceResult.Type.ENTITY) {
			return BrewResult.DROP_ITEM;
		}
		
		Block blockHit = BlockUtil.getBlock(world, rtr);
		BlockPos pos = rtr.getBlockPos();
		if (blockHit != BlockInit.HOLLOW_TEARS) {
			switch (rtr.sideHit) {
			case DOWN:
				pos.down();
				break;
			case EAST:
				pos.east();
				break;
			case NORTH:
				pos.north();
				break;
			case SOUTH:
				pos.south();
				break;
			case UP:
				pos.up();
				break;
			case WEST:
				pos.west();
				break;
			}
			blockHit = BlockUtil.getBlock(world, pos);
			if (blockHit != BlockInit.HOLLOW_TEARS) {
				return BrewResult.DROP_ITEM;
			}
		}
		SpreadEffect.spread(world, pos, 64, new SpreadEffect(new Block[] {BlockInit.HOLLOW_TEARS}) {

			@Override
			public boolean doEffect(World world, BlockPos pos, Block block) {
				ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, world, pos, 2.0, 2.0, 16);
				if (replacementBlock == null) {
					world.setBlockToAir(pos);
					Block blockBelow = BlockUtil.getBlock(world, pos.down());
					if (blockBelow != null && BlockProtect.canBreak(blockBelow, world)) {
						world.setBlockToAir(pos.down());
					}
				}else {
					BlockUtil.setBlock(world, pos, replacementBlock.getDefaultState());
				}
				return true;
			}
		});
		return BrewResult.SHOW_EFFECT;
	}

	public abstract static class SpreadEffect{
		protected Block[] blocks;
		
		public SpreadEffect(Block... blocksToSpread) {
			blocks = blocksToSpread;
		}
		
		public abstract boolean doEffect(World world, BlockPos pos, Block block);
		
		public static void spread(World world, BlockPos pos, int range, SpreadEffect effect) {
			spread(world, pos, pos, range, effect);
		}

		private static void spread(World world, BlockPos pos, BlockPos pos2, int range, SpreadEffect effect) {
			if (Math.abs(pos.getX() - pos2.getX()) >= range || Math.abs(pos.getY() - pos2.getY()) >= range || Math.abs(pos.getZ() - pos2.getZ()) >= range) {
				return;
			}
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos work = pos.offset(facing);
				if (checkEffect(world, work, effect)) {
					spread(world, work, pos, range, effect);
				}
			}
		}
		
		private static boolean checkEffect(World world, BlockPos pos, SpreadEffect effect) {
			boolean continueCheck = false;
			
			Block foundBlock = BlockUtil.getBlock(world, pos);
			if (foundBlock != null) {
				for (Block block : effect.blocks) {
					if (foundBlock == block) {
						continueCheck = effect.doEffect(world, pos, block);
						break;
					}
				}
			}
			
			return continueCheck;
		}
	}
	
}
