package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.classes.TeleportUtil;

public class Waystone extends GeneralItem {

	private final boolean enchanted;

	public Waystone(String name, boolean enchantedIn) {
		super(name);
		enchanted = enchantedIn;
	}

	public static boolean isWaystoneBound(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return tag != null && tag.hasKey("PosX") && tag.hasKey("PosY") && tag.hasKey("PosZ") && tag.hasKey("PosD");
	}

	public static int getWaystoneDimension(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("PosX") && tag.hasKey("PosY") && tag.hasKey("PosZ") && tag.hasKey("PosD")) {
			return tag.getInteger("PosD");
		}
		return 0;
	}

	private boolean isRestrictedTeleportTarget(int source, int target) {
		return source != target && (source == Config.instance().dimensionDreamID
				|| source == Config.instance().dimensionMirrorID || target == Config.instance().dimensionDreamID
				|| target == Config.instance().dimensionMirrorID);
	}

	public boolean teleportToLocation(World world, ItemStack stack, Entity entity, int radius, boolean presetPosition) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("PosX") && tag.hasKey("PosY") && tag.hasKey("PosZ") && tag.hasKey("PosD")) {
			int newX = tag.getInteger("PosX");
			int newY = tag.getInteger("PosY");
			int newZ = tag.getInteger("PosZ");
			int newD = tag.getInteger("PosD");
			if (!isRestrictedTeleportTarget(entity.dimension, newD)) {
				TeleportUtil.teleportToLocation(world, newX, newY, newZ, newD, entity, presetPosition);
				return true;
			}
		} else if (tag != null) {
			EntityLivingBase target = ItemInit.TAGLOCK_KIT.getBoundEntity(world, null, stack, 1);
			if (entity != null && target != null && !isRestrictedTeleportTarget(entity.dimension, target.dimension)) {
				TeleportUtil.teleportToLocation(world, MathHelper.floor_double(target.posX),
						MathHelper.floor_double(target.posY), MathHelper.floor_double(target.posZ), target.dimension,
						entity, presetPosition);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return enchanted;
	}

	@Override
	public boolean hasEffect(ItemStack stack, int pass) {
		return enchanted;
	}
}
