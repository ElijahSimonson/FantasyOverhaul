package nz.castorgaming.fantasyoverhaul.powers.playereffect;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItemEnchanted;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class BoundWaystone extends GeneralItemEnchanted {

	public BoundWaystone(String name) {
		super(name);
	}

	@Override
	public ItemStack onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote && player instanceof EntityPlayerMP) {
			Reference.PACKET_HANDLER.sendTo((IMessage) new PacketCamPos(false, false, null), player);
		}
		return stack;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		World world = player.worldObj;
		int elapsedTicks = getMaxItemUseDuration(stack) - countdown;
		if (!world.isRemote && player instanceof EntityPlayerMP) {
			if (elapsedTicks % 20 == 0) {
				if (elapsedTicks == 0) {
					NBTTagCompound tag = stack.getTagCompound();
					if (tag != null && tag.hasKey("PosX") && tag.hasKey("PosY") && tag.hasKey("PosZ")
							&& tag.hasKey("PosD")) {
						int newX, newY, newZ, newD;
						newX = tag.getInteger("PosX");
						newY = tag.getInteger("PosY");
						newZ = tag.getInteger("PosZ");
						newD = tag.getInteger("PosD");
						EntityEye eye = new EntityEye("world");
						eye.setLocationAndAngles(newX, newY, newZ, player.rotationYaw, 90.0f);
						world.spawnEntityInWorld(eye);
						Reference.PACKET_HANDLER.sendTo(new PacketCamPos(true, elapsedTicks == 0, eye), player);
					}
				} else {
					Reference.PACKET_HANDLER.sendTo(new PacketCamPos(true, false, null), player);
				}
			}
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (!worldIn.isRemote && entityLiving instanceof EntityPlayerMP) {
			Reference.PACKET_HANDLER.sendTo(new PacketCamPos(false, false, null), entityLiving);
		}
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		Block block = BlockUtil.getBlock(world, pos);
		if (block == BlockInit.CRYSTAL_BALL) {
			if (!world.isRemote && BlockCrystalBall.consumePower(world, player, pos)) {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag != null && tag.hasKey("PosX") && tag.hasKey("PosY") && tag.hasKey("PosZ")
						&& tag.hasKey("PosD")) {
					final int newX = tag.getInteger("PosX");
					final int newY = tag.getInteger("PosY");
					final int newZ = tag.getInteger("PosZ");
					final int newD = tag.getInteger("PosD");
					if (newD == player.dimension && player.getDistanceSq(new BlockPos(newX, newY, newZ)) <= 22500.0) {
						player.setActiveHand(hand);
					} else {
						SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
					}
				} else {
					SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
				}
			} else if (world.isRemote) {
				player.setActiveHand(hand);
			}
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUseFirst(stack, player, world, pos, side, hitX, hitY, hitZ, hand);
	}

}
