package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;

public class ItemBatBall extends GeneralItem{

	public ItemBatBall(String name) {
		super(name);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		--itemStackIn.stackSize;
		if (itemStackIn.stackSize <= 0) {
			playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
		}
		if (!worldIn.isRemote) {
			EntityItem item = new EntityItem(worldIn, playerIn.posX,playerIn.posY + 1.3, playerIn.posZ, ItemInit.BAT_BALL.createStack());
			item.setPickupDelay(5);
			item.setLocationAndAngles(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
			item.posX -= MathHelper.cos((float) (item.rotationYaw / 180.0f * Math.PI)) * 0.16f;
			item.posY -= 0.10000000149011612;
			item.posZ -= MathHelper.sin((float) (item.rotationYaw / 180.0f * Math.PI)) * 0.16f;
			item.setPosition(item.posX, item.posY, item.posZ);
			float f = 0.4f;
			item.motionX = -MathHelper.sin((float) (item.rotationYaw / 180.0f * Math.PI)) * MathHelper.cos((float) (item.rotationPitch / 180.0f * Math.PI)) * f;
			item.motionZ = MathHelper.cos((float) (item.rotationYaw / 180.0f * Math.PI)) * MathHelper.cos((float) (item.rotationPitch / 180.0f * Math.PI)) * f;
			item.motionY = -MathHelper.sin((float) (item.rotationPitch / 180.0f * Math.PI)) * f;
			setThrowableHeading(item, item.motionX, item.motionY, item.motionZ, 1.0f, 1.0f);
			worldIn.spawnEntityInWorld(item);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

}
