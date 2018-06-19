package nz.castorgaming.fantasyoverhaul.objects.items.brew;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockProtect;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockUtil;
import nz.castorgaming.fantasyoverhaul.util.classes.Coord;
import nz.castorgaming.fantasyoverhaul.util.classes.EffectSpiral;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.interfaces.ISpiralBlockAction;

public class BrewSubstitution extends Brew{

	public BrewSubstitution(String name) {
		super(name);
	}
	
	@Override
	public BrewResult onImpact(World world, EntityLivingBase thrower, RayTraceResult rtr, boolean enhanced, double x,
			double y, double z, AxisAlignedBB bounds) {
		if (rtr == null || rtr.typeOfHit == RayTraceResult.Type.ENTITY) {
			return BrewResult.DROP_ITEM;
		}
		int R = enhanced ? 6 : 4;
		double RSQ = R * R;
		AxisAlignedBB aoe = bounds.expand(R, R, R);
		List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, aoe);
		if (entities != null && !entities.isEmpty()) {
			ArrayList<EntityItem> items = new ArrayList<EntityItem>();
			for (EntityItem item : entities){
				double distSq = item.getDistance(x, y, z);
				if (distSq <= RSQ) {
					ItemStack stack = item.getEntityItem();
					if (!(stack.getItem() instanceof ItemBlock)) {
						continue;
					}
					items.add(item);
				}
			}
			Block refBlock = BlockUtil.getBlock(world, rtr.getBlockPos());
			if (items.size() > 0 && refBlock != null && BlockProtect.canBreak(refBlock, world)) {
				new EffectSpiral(new ISpiralBlockAction() {
					int stackIndex = 0;
					int subCount = 0;

					@Override
					public void onSpiralActionStart(World world, int x, int y, int z) {
					}

					@SuppressWarnings("deprecation")
					@Override
					public boolean onSpiralBlockAction(World world, int x, int y, int z) {
						BlockPos pos = rtr.getBlockPos();
						if (Coord.distanceSq(pos.getX(), pos.getY(), pos.getZ(), x,y,z) < R) {
							boolean found = false;
							if (BlockUtil.getBlock(world, x,y,z) == refBlock && BlockUtil.isReplaceableBlock(world, x, y + 1, z)) {
								found = true;
							}else if (BlockUtil.getBlock(world, x, y + 1, z) == refBlock && BlockUtil.isReplaceableBlock(world, x, y + 2, z)) {
								y++;
								found = true;
							}else if (BlockUtil.getBlock(world, x, y - 1 , z) == refBlock && BlockUtil.isReplaceableBlock(world, x, y, z)) {
								y--;
								found = true;
							}else if (BlockUtil.getBlock(world, x, y + 2 , z) == refBlock && BlockUtil.isReplaceableBlock(world, x, y + 3, z)) {
								y += 2;
								found = true;
							}else if (BlockUtil.getBlock(world, x, y - 2 , z) == refBlock && BlockUtil.isReplaceableBlock(world, x, y - 1, z)) {
								y -= 2;
								found = true;
							}
							if (found) {
								subCount ++;
								ItemStack stack = items.get(stackIndex).getEntityItem();
								ItemBlock blockItem = (ItemBlock) stack.getItem();
								Block block = blockItem.block;
								BlockUtil.setBlock(world, new BlockPos(x, y, z), block.getStateFromMeta(stack.getItemDamage()), 3);
								ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, world, x, y, z, 1.0, 1.0, 16);
								ItemStack itemstack = stack;
								if (--itemstack.stackSize == 0) {
									items.get(stackIndex).setDead();
									stackIndex++;
								}
							}
						}
						return stackIndex < items.size();
					}

					@Override
					public void onSpiralActionStop(World world, int x, int y, int z) {
						while (subCount > 0) {
							int quantity = (subCount > 64) ? 64 : subCount;
							subCount -= quantity;
							world.spawnEntityInWorld(new EntityItem(world, x,y,z, new ItemStack(refBlock, quantity)));
						}
					}
				}).apply(world, rtr.getBlockPos(), (int) RSQ, (int)RSQ);
				return BrewResult.SHOW_EFFECT;
			}
		}
		return BrewResult.DROP_ITEM;
	}
	
}
