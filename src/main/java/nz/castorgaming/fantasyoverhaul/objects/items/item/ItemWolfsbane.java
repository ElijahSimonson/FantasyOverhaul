package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.powers.infusions.player.InfusionOtherwhere;
import nz.castorgaming.fantasyoverhaul.util.classes.CreatureUtilities;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class ItemWolfsbane extends GeneralItem{

	public ItemWolfsbane(String name) {
		super(name);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if (!worldIn.isRemote) {
			RayTraceResult rtr = InfusionOtherwhere.raytraceEntities(worldIn, playerIn, true, 2.0);
			if (rtr != null && rtr.entityHit != null) {
				if (CreatureUtilities.isWerewolf(rtr.entityHit, true)) {
					ParticleEffect.FLAME.send(SoundEffect.MOB_WOLFMAN_HOWL, rtr.entityHit, 0.5, 1.5, 16);
				}
				else {
					SoundEffect.NOTE_SNARE.playAtPlayer(worldIn, playerIn);
				}
				--itemStackIn.stackSize;
				if (itemStackIn.stackSize <= 0) {
					itemStackIn.stackSize = 0;
					playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
				}
			}
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
	}

}
