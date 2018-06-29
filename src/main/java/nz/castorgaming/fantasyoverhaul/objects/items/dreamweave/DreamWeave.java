package nz.castorgaming.fantasyoverhaul.objects.items.dreamweave;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.blocks.BlockDreamCatcher;
import nz.castorgaming.fantasyoverhaul.objects.blocks.BlockDreamCatcher.TileEntityDreamCatcher;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;

public class DreamWeave extends GeneralItem {

	private final Potion potionDream;
	private final Potion potionNightmare;
	private final int duration;
	private final int amp;

	public DreamWeave(String name, Potion potionDream, Potion potionNightmare, int duration, int amplifier) {
		super(name);
		ItemInit.WEAVES.put(name, this);
		this.potionDream = potionDream;
		this.potionNightmare = potionNightmare;
		this.amp = amplifier;
		this.duration = duration;
	}

	public void setEffect(BlockDreamCatcher.TileEntityDreamCatcher dreamCatcherEntity) {
		dreamCatcherEntity.setEffect(this);
	}

	public void applyEffect(EntityPlayer player, boolean isDream, boolean isEnhanced) {
		if (isDream) {
			player.addPotionEffect(new PotionEffect(potionDream,
					(isEnhanced && potionDream == MobEffects.SATURATION) ? (duration + 2400)
							: (isEnhanced ? (duration - 2400) : duration),
					(isEnhanced && potionDream != MobEffects.SATURATION) ? (amp + 1) : amp));
		} else {
			player.addPotionEffect(new PotionEffect(potionNightmare, duration, isEnhanced ? amp + 1 : amp));
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
			return EnumActionResult.FAIL;
		}
		switch (facing) {
		case EAST:
			pos.east();
			break;
		case NORTH:
			pos.north();
			break;
		case SOUTH:
			pos.south();
			break;
		case WEST:
			pos.west();
			break;
		case DOWN:
		case UP:
			return EnumActionResult.FAIL;
		}

		if (!playerIn.canPlayerEdit(pos, facing, stack)) {
			return EnumActionResult.FAIL;
		}
		if (worldIn.isRemote) {
			return EnumActionResult.SUCCESS;
		}
		worldIn.setBlockState(pos, BlockInit.DREAM_CATCHER.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ,
				stack.getMetadata(), playerIn, stack));
		--stack.stackSize;
		BlockDreamCatcher.TileEntityDreamCatcher tileEnt = (TileEntityDreamCatcher) worldIn.getTileEntity(pos);
		if (tileEnt != null) {
			DreamWeave weave = (DreamWeave) stack.getItem();
			weave.setEffect(tileEnt);
		}
		return EnumActionResult.SUCCESS;
	}

}
