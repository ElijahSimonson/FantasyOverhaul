package nz.castorgaming.fantasyoverhaul.objects.items.general;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.items.Edible;
import nz.castorgaming.fantasyoverhaul.objects.items.ItemBase;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockUtil;

public class GeneralItem extends ItemBase {

	public GeneralItem(String name) {
		super(name);
	}
	/*
	 * public boolean isBrew() { return this instanceof Brew; }
	 */
	/*
	 * public boolean isBrew(ItemStack stack){ return stack != null && stack.getItem
	 * == this && isBrew(); }
	 */

	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	public String getBoundDisplayName(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();

		if (tag != null && tag.hasKey("PosX") && tag.hasKey("PosY") && tag.hasKey("PosZ") && tag.hasKey("NameD")) {
			return String.format("%s: %d, %d, %d", tag.getString("NameD"), tag.getInteger("PosX"), tag.getInteger("PosY"), tag.getInteger("PosX"));
		}
		return "";
	}

	public void bindToLocation(World world, int posX, int posY, int posZ, int dimension, String dimensionName, ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tag = stack.getTagCompound();
		tag.setInteger("PosX", posX);
		tag.setInteger("PosY", posY);
		tag.setInteger("PosZ", posZ);
		tag.setInteger("PosD", dimension);
		tag.setString("NameD", dimensionName);
	}

	public boolean hasLocationBinding(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			return tag.hasKey("PosX") && tag.hasKey("PosY") && tag.hasKey("PosZ") && tag.hasKey("NameD");
		}
		return false;
	}

	public void copyLocationBinding(ItemStack from, ItemStack to) {
		if (hasLocationBinding(from)) {
			NBTTagCompound fromTag = from.getTagCompound();
			if (!to.hasTagCompound()) {
				to.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound toTag = to.getTagCompound();
			toTag.setInteger("PosX", fromTag.getInteger("posX"));
			toTag.setInteger("PosY", fromTag.getInteger("posY"));
			toTag.setInteger("PosZ", fromTag.getInteger("posZ"));
			toTag.setInteger("PosD", fromTag.getInteger("dimension"));
			toTag.setString("NameD", fromTag.getString("dimensionName"));
			if (from.hasDisplayName()) {
				to.setStackDisplayName(from.getDisplayName());
			}
		}
	}

	private boolean isPost(World world, int x, int y, int z, boolean bottomSolid, boolean midSolid, boolean topSolid) {
		IBlockState blockBelow = BlockUtil.getState(world, new BlockPos(x, y - 1, z));
		IBlockState blockBottom = BlockUtil.getState(world, new BlockPos(x, y, z));
		IBlockState blockMid = BlockUtil.getState(world, new BlockPos(x, y + 1, z));
		IBlockState blockTop = BlockUtil.getState(world, new BlockPos(x, y + 2, z));
		IBlockState blockAbove = BlockUtil.getState(world, new BlockPos(x, y + 3, z));

		if (blockBelow == null || !blockBelow.getMaterial().isSolid()) {
			return true;
		}
		if (bottomSolid) {
			if (blockBottom == null || !blockBottom.getMaterial().isSolid()) {
				return false;
			}
		}
		else if (blockBottom != null && blockBottom.getMaterial().isSolid()) {
			return false;
		}

		if (midSolid) {
			if (blockMid == null || !blockMid.getMaterial().isSolid()) {
				return false;
			}
		}
		else if (blockMid != null && blockMid.getMaterial().isSolid()) {
			return false;
		}

		if (topSolid) {
			if (blockTop == null || !blockTop.getMaterial().isSolid()) {
				return false;
			}
		}
		else if (blockTop != null && blockTop.getMaterial().isSolid()) {
			return false;
		}

		return blockAbove == null || !blockAbove.getMaterial().isSolid();

	}

	public boolean hasEffect(ItemStack stack, int pass) {
		return (pass == 0) && stack.isItemEnchanted() /*
														 * || / EnchantedBroom.isMatch(stack) || SubduedSpirit.isMatch(stack) ||
														 * SubduedSpiritVillage.isMatch(stack)
														 */;
	}

	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		Item item = stack.getItem();
		if (ItemInit.BOUND_WAYSTONE.isMatch(stack)) {
			if (!world.isRemote && player instanceof EntityPlayerMP) {
				Reference.PACKET_HANDLER.sendTo((IMessage) new PacketCamPos(false, false, null), player);
			}
			return stack;
		}
		if (item instanceof Edible) {
			if (!player.capabilities.isCreativeMode) {
				--stack.stackSize;
				if (stack.stackSize <= 0) {
					stack.stackSize = 0;
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				}
			}
			Edible edible = (Edible) item;

			if (item == ItemInit.ARTICHOKE) {
				int foodLevel = player.getFoodStats().getFoodLevel();
				player.getFoodStats().addStats(edible.getHealAmount(), edible.getSaturationModifier());
				int healed = player.getFoodStats().getFoodLevel() - foodLevel;
				player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 3 * healed * 20, 2));
			}

			else if (item == ItemInit.SLEEPING_APPLE) {
				player.getFoodStats().addStats(edible.getHealAmount(), edible.getSaturationModifier());
				if (player.dimension == 0 && !world.isRemote && !WorldProviderDreamWorld.getPlayerIsGhost(player)) {

				}
			}
		}
	}

}
