package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.objects.worlds.WorldProviderDreamWorld;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;

public class ItemIcyNeedle extends GeneralItem {

	public ItemIcyNeedle(String name) {
		super(name);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		useIcyNeedle(worldIn, playerIn, itemStackIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,
				playerIn.inventory.getCurrentItem() != null ? playerIn.inventory.getCurrentItem() : itemStackIn);
	}

	private void useIcyNeedle(World world, EntityPlayer player, ItemStack stack) {
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
			if (stack.stackSize <= 0) {
				stack.stackSize = 0;
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}
		}
		if (world.provider.getDimension() == Config.instance().dimensionDreamID) {
			WorldProviderDreamWorld.returnPlayerToOverworld(player);
			stack.stackSize = 0;
		} else if (WorldProviderDreamWorld.isPlayerGhost(player)) {
			WorldProviderDreamWorld.returnGhostPlayerToSpiritWorld(player);
			stack.stackSize = 0;
		} else {
			player.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0f);
		}
	}

}
