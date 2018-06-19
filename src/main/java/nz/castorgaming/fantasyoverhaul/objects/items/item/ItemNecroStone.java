package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Enslaved;
import nz.castorgaming.fantasyoverhaul.powers.brews.effects.InfusionBrewEffect;
import nz.castorgaming.fantasyoverhaul.powers.infusions.player.InfusionOtherwhere;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;
import nz.castorgaming.fantasyoverhaul.util.classes.TargetPointUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketParticles;

public class ItemNecroStone extends GeneralItem{

	public ItemNecroStone(String name) {
		super(name);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if (worldIn.isRemote) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
		}
		double MAX_TARGET_RANGE = 15.0;
		RayTraceResult rtr = InfusionOtherwhere.doCustomRayTrace(worldIn, playerIn, true, MAX_TARGET_RANGE);
		if (rtr != null) {
			switch (rtr.typeOfHit) {
			case BLOCK:
				{
					if (worldIn.getBlockState(rtr.getBlockPos()).getBlock() == BlockInit.ALLURING_SKULL) {
						return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
					}
					if (rtr.sideHit != EnumFacing.UP) {
						break;
					}
					int minionCount = 0;
					int r = 50;
					AxisAlignedBB bounds = new AxisAlignedBB(playerIn.posX - r, playerIn.posY - 15.0, playerIn.posZ - r, playerIn.posX + r, playerIn.posY + 15.0, playerIn.posZ + r);
					for (EntityLiving living : worldIn.getEntitiesWithinAABB(EntityLiving.class, bounds)) {
						EntityCreature creature = (creature instanceof EntityCreature) ? creature : null;
						if (living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD && Enslaved.isMobEnslavedBy(living, playerIn)) {
							++minionCount;
							living.setAttackTarget(null);
							living.setRevengeTarget(null);
							if ((!(creature instanceof EntitySpider) && creature.getNavigator().tryMoveToXYZ(rtr.getBlockPos().getX(), rtr.getBlockPos().getY(), rtr.getBlockPos().getZ(), 1.0) || creature == null)) {
								continue;
							}
							creature.getNavigator().setPath(creature.getNavigator().getPathToPos(new BlockPos(rtr.getBlockPos()).up()), 1.0);
						}
					}
					if (minonCount > 0) {
						ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_POP, worldIn, rtr.getBlockPos(), 1.0, 1.0, 16);
						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
					}
					break;
				}
			case ENTITY:
			{
				if (!(rtr.entityHit instanceof EntityLivingBase)) {
					break;
				}
				if (!playerIn.isSneaking()) {
					EntityLivingBase targetEntity = (EntityLivingBase) rtr.entityHit;
					int r = 50;
					int minionCount = 0;
					AxisAlignedBB bounds = new AxisAlignedBB(playerIn.posX - r, playerIn.posY - r, playerIn.posZ - r, playerIn.posX + r, playerIn.posY + r, playerIn.posZ + r);
					for (EntityLiving entity : worldIn.getEntitiesWithinAABB(EntityLiving.class, bounds)) {
						if (entity.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD && Enslaved.isMobEnslavedBy(entity, playerIn)) {
							minionCount ++;
							EntityUtil.setTarget(entity, targetEntity);
						}
					}
					if (minionCount > 0) {
						Reference.PACKET_HANDLER.sendToAllAround(new PacketParticles(ParticleEffect.CRIT, SoundEffect.MOB_ZOMBIE_DEATH, rtr.entityHit, 0.5, 2.0), TargetPointUtil.from(targetEntity, 16.0));
						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
					}
					break;
				}else {
					if (InfusionBrewEffect.GRAVE.isActive(playerIn) && InfusionBrewEffect.Grave.tryUseEffect(playerIn, rtr)) {
						 Reference.PACKET_HANDLER.sendToAllAround(new PacketParticles(ParticleEffect.MOB_SPELL, SoundEffect.MOB_ZOMBIE_INFECT, rtr.entityHit, 1.0, 1.0), TargetPointUtil.from(rtr.entityHit, 16.0));
						 return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
					}
					Reference.PACKET_HANDLER.sendToAllAround(new PacketParticles(ParticleEffect.SMOKE, SoundEffect.NOTE_SNARE, rtr.entityHit, 1.0, 1.0), TargetPointUtil.from(rtr.entityHit, 16.0));
					break;
				}
				
			}
			default:
				break;
			}
			SoundEffect.NOTE_SNARE.playAtPlayer(worldIn, playerIn);
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
		}
	}
}
