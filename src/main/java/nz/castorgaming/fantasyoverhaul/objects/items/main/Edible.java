package nz.castorgaming.fantasyoverhaul.objects.items.main;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class Edible extends GeneralItem {

	private int healAmount;
	private float saturationModifier;
	private boolean wolfFavorite;
	private boolean eatAnyTime;

	public Edible(String name, int heal, float sat, boolean wolfFav) {
		this(name, heal, sat, wolfFav, false);
	}
	
	public Edible(String name, int heal, float sat, boolean wolfFav, boolean eatAny) {
		super(name);
		healAmount = heal;
		saturationModifier = sat;
		wolfFavorite = wolfFav;
		eatAnyTime = eatAny;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.EAT;
	}
	
	public int getHealAmount() {
		return healAmount;
	}
	
	public float getSaturationModifier() {
		return saturationModifier;
	}
	
	public boolean isWolfsFavoriteMeat() {
		return wolfFavorite;
	}
	
	public ItemStack onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
			if (stack.stackSize <= 0) {
				stack.stackSize = 0;
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}
		}
		
		player.getFoodStats().addStats(healAmount, saturationModifier);
		
		world.playSound(player, player.getPosition(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5f, world.rand.nextFloat() * 0.1f + 0.9f);
		
		return stack;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if (playerIn.canEat(false) || eatAnyTime) {
			playerIn.setActiveHand(hand);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
}
