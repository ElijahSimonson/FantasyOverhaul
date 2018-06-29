package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityDeathHorse;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.util.classes.GeneralUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class BinkySkull extends GeneralItem {

	public BinkySkull(String name) {
		super(name);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing != EnumFacing.UP) {
			return EnumActionResult.FAIL;
		}
		pos.up();
		Material mat = worldIn.getBlockState(pos).getMaterial();
		if (mat == null || !mat.isSolid()) {
			if (!worldIn.isRemote) {
				EntityDeathHorse horse = new EntityDeathHorse(worldIn);
				horse.setHorseTamed(true);
				horse.setHorseVariant(4);
				horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
				horse.enablePersistence();
				horse.setCustomNameTag(GeneralUtil.resource("item.death_horse.customname"));
				horse.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0f, 0.0f);
				NBTTagCompound tag = horse.getEntityData();
				if (tag != null) {
					tag.setBoolean("FOIsBinky", true);
				}
				ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, worldIn, pos, 1.0, 1.0, 16);
				worldIn.spawnEntityInWorld(horse);
			}
			--stack.stackSize;
		}
		return EnumActionResult.SUCCESS;
	}
}
