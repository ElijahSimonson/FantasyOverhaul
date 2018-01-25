package nz.castorgaming.fantasyoverhaul.powers.playereffect;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import nz.castorgaming.fantasyoverhaul.util.Log;

public class ImpMeltingTouch extends PlayerEffect {
	public ImpMeltingTouch() {
		super("impmeltingtouch");
	}

	@Override
	protected void doUpdate(final EntityPlayer player, final int worldTicks) {
	}

	@Override
	protected void doHarvest(final EntityPlayer player, final BlockEvent.HarvestDropsEvent event) {
		final ArrayList<ItemStack> newDrops = new ArrayList<ItemStack>();
		for (final ItemStack drop : event.getDrops()) {
			final ItemStack smeltedDrop = FurnaceRecipes.instance().getSmeltingResult(drop);
			if (smeltedDrop != null) {
				Log.instance().debug("Smelting Touch: " + drop.toString() + " -> " + smeltedDrop.toString());
				final ItemStack smelted = smeltedDrop.copy();
				if (player.worldObj.rand.nextDouble() < 0.25) {
					final ItemStack itemStack = smelted;
					++itemStack.stackSize;
				}
				newDrops.add(smelted);
			}
			else {
				Log.instance().debug("Smelting Touch: " + drop.toString() + " -> none");
				newDrops.add(drop);
			}
		}
		event.getDrops().clear();
		for (final ItemStack newDrop : newDrops) {
			event.getDrops().add(newDrop);
		}
	}

	@Override
	protected void doInteract(final EntityPlayer player, final PlayerInteractEvent event) {
	}
}
