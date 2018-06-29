package nz.castorgaming.fantasyoverhaul.objects.items.brew.base;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;

public class BrewFluid extends Brew {

	protected final Fluid fluid;

	public BrewFluid(String name, Fluid fluidIn) {
		super(name);
		fluid = fluidIn;
	}

	@Override
	public BrewResult onImpact(World world, EntityLivingBase thrower, RayTraceResult rtr, boolean enhanced, double x,
			double y, double z, AxisAlignedBB bounds) {
		switch (rtr.typeOfHit) {
		case BLOCK: {
			depositLiquid(world, rtr.getBlockPos(), enhanced, rtr.sideHit);
			break;
		}
		case ENTITY: {
			int entX = MathHelper.floor_double(rtr.entityHit.posX);
			int entY = MathHelper.floor_double(rtr.entityHit.posY);
			int entZ = MathHelper.floor_double(rtr.entityHit.posZ);
			depositLiquid(world, new BlockPos(entX, entY, entZ), enhanced, null);
			break;
		}
		default:
			break;
		}
		return BrewResult.SHOW_EFFECT;
	}

	public void depositLiquid(World world, BlockPos pos, boolean enhanced, @Nullable EnumFacing side) {
		int x = pos.getX() + ((side == EnumFacing.WEST) ? -1 : (side == EnumFacing.EAST) ? 1 : 0);
		int z = pos.getZ() + ((side == EnumFacing.NORTH) ? -1 : (side == EnumFacing.SOUTH) ? 1 : 0);
		int y = pos.getY() + ((side == EnumFacing.UP) ? 1 : (side == EnumFacing.DOWN) ? -1 : 0);
		if (side == EnumFacing.UP && !world.getBlockState(new BlockPos(x, pos.getY(), z)).getMaterial().isSolid()) {
			y--;
		}
		setBlockIfNotSolid(world, new BlockPos(x, y, z), fluid.getBlock());
	}

}
