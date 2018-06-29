package nz.castorgaming.fantasyoverhaul.objects.items.main;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class Drinkable extends GeneralItem {

	protected PotionEffect[] effects;
	protected EnumAction userAction;

	public Drinkable(String name, PotionEffect... effectsIn) {
		this(name, EnumAction.DRINK, effectsIn);
	}

	public Drinkable(String name, EnumAction useAction, PotionEffect... effectsIn) {
		super(name);
		effects = effectsIn;
		userAction = useAction;
	}

	public ItemStack onDrunk(ItemStack stack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
			if (stack.stackSize <= 0) {
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}
		}
		for (PotionEffect effect : effects) {
			player.addPotionEffect(new PotionEffect(effect));
		}
		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		playerIn.setActiveHand(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
}
