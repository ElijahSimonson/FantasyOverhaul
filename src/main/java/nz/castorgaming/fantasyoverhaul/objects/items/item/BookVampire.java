package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.objects.items.main.ItemMarkupBook;

public class BookVampire extends ItemMarkupBook {

	public BookVampire() {
		super("book_vampire", 7, new int[] { 0, 9 });
	}

	@Override
	public void onBookRead(ItemStack stack, World world, EntityPlayer player) {
		IPlayerVampire.get(player).increaseVampireLevelCap(stack.getItemDamage() + 1);
	}

}
