package nz.castorgaming.fantasyoverhaul.objects.items.item;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItemEnchanted;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.TargetPointUtil;

public class BroomEnchanted extends GeneralItemEnchanted{

	public BroomEnchanted(String name) {
		super(name);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		float pitchCalc = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * 1.0f;
		float yawCalc = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * 1.0f;
		
		double vx = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX);
		double vy = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) + 1.62 - playerIn.getYOffset();
		double vz = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ);
		
		Vec3d posVec = new Vec3d(vx, vy, vz);
		
		float vx2 = MathHelper.sin((float) (-yawCalc * 0.017453292f - Math.PI)) * -MathHelper.cos(-pitchCalc - 0.017453292f);
		float vz2 = MathHelper.cos((float) (-yawCalc * 0.017453292f - Math.PI)) * -MathHelper.cos(-pitchCalc - 0.017453292f);
		float vy2 = MathHelper.sin(-pitchCalc * 0.0117453292f);
		
		Vec3d rotVec = posVec.addVector(vx2 * 5.0, vy2 * 5.0, vz2 * 5.0);
		
		RayTraceResult rtr = worldIn.rayTraceBlocks(posVec, rotVec, true);
		
		if (rtr == null) {
			return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		}
		
		Vec3d playerLook = playerIn.getLookVec();
		
		boolean flag = false;
		
		List<Entity> list = worldIn.getEntitiesInAABBexcluding(playerIn, playerIn.getCollisionBoundingBox().addCoord(playerLook.xCoord * 5.0, playerLook.yCoord * 5.0, playerLook.zCoord * 5.0).expand(1.0, 1.0, 1.0), null);
		
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity.canBeCollidedWith()) {
				float collisionSide = entity.getCollisionBorderSize();
				AxisAlignedBB bb = entity.getEntityBoundingBox().expand(collisionSide, collisionSide, collisionSide);
				if (bb.isVecInside(posVec)) {
					flag = true;
				}
			}
		}
		if (flag) {
			return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		}
		if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos hitPos = rtr.getBlockPos();
			if (worldIn.getBlockState(hitPos).getBlock() == Blocks.SNOW) {
				hitPos.down();
			}
			EntityBroom broom = new EntityBroom(worldIn, hitPos);
			if (stack.hasDisplayName()) {
				broom.setCustomNameTag(stack.getDisplayName());
			}
			setBroomEntityColor(broom, stack);
			broom.rotationYaw = playerIn.rotationYaw;
			if (!worldIn.collidesWithAnyBlock(broom.getBoundingBox().expand(-0.1, -0.1, -0.1))){
				super.onItemUse(stack, playerIn, worldIn, hitPos, hand, facing, hitX, hitY, hitZ);
			}
			broom.rotationYaw = ((MathHelper.floor_double(playerIn.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3) - 1) * 90;
			if (!worldIn.isRemote) {
				worldIn.spawnEntityInWorld(broom);
				Reference.PACKET_HANDLER.sendToAllAround(new SPacketEntity.S17PacketEntityLookMove(broom), TargetPointUtil.from(broom, 128.0));
			}
			if (!playerIn.capabilities.isCreativeMode) {
				--stack.stackSize;
			}
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	private void setBroomEntityColor(EntityBroom broom, ItemStack stack) {
		broom.setBrushColor(getBroomItemColor(stack));
	}
	
	public void setBroomItemColor(ItemStack stack, EnumDyeColor color) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tag = stack.getTagCompound();
		tag.setInteger("BrushColor", color.getDyeDamage());
	}

	public EnumDyeColor getBroomItemColor(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("BrushColor")) {
			return EnumDyeColor.byDyeDamage(tag.getInteger("BrushColor"));
		}
		return EnumDyeColor.BROWN;
	}
}
