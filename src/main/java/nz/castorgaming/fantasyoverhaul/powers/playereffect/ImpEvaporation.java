package nz.castorgaming.fantasyoverhaul.powers.playereffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class ImpEvaporation extends PlayerEffect {

	public ImpEvaporation() {
		super("impevaporation");
	}

	@Override
	protected void doUpdate(final EntityPlayer player, final int worldTicks) {
		if (player.worldObj.rand.nextInt(5) == 0) {
			final int midX = MathHelper.floor_double(player.posX);
			final int midY = MathHelper.floor_double(player.posY);
			final int midZ = MathHelper.floor_double(player.posZ);
			final int R = 3;
			final int RSq = 9;
			boolean found = false;
			for (int x = midX - 3; x <= midX + 3; ++x) {
				for (int z = midZ - 3; z <= midZ + 3; ++z) {
					for (int y = midY + 2; y >= midY - 1; --y) {
						if (player.getDistanceSq(x, y, z) <= 9.0) {
							final Block block = BlockUtil.getBlock(player.worldObj, x, y, z);
							if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && player.worldObj.isAirBlock(new BlockPos(x, y + 1, z))) {
								player.worldObj.setBlockToAir(new BlockPos(x, y, z));
								ParticleEffect.EXPLODE.send(SoundEffect.NONE, player.worldObj, x, y + 1, z, 1.0, 1.0, 16);
								found = true;
							}
						}
					}
				}
			}
			if (found) {
				SoundEffect.RANDOM_FIZZ.playAt(player.worldObj, player.posX, player.posY, player.posZ, 1.0f, 2.6f + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.8f);
			}
		}
	}

	@Override
	protected void doHarvest(final EntityPlayer player, final BlockEvent.HarvestDropsEvent event) {
	}

	@Override
	protected void doInteract(final EntityPlayer player, final PlayerInteractEvent event) {
	}
}
