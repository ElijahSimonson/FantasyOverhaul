package nz.castorgaming.fantasyoverhaul.objects.items.contract;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Contract;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.ChatUtilities;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class ContractTorment extends Contract {

	public ContractTorment(String name) {
		super(name);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entityIn, int count) {
		EntityPlayer player = (EntityPlayer) entityIn;
		World world = player.worldObj;
		int elapsedTicks = getMaxItemUseDuration(stack) - count;
		if (!world.isRemote && player instanceof EntityPlayerMP) {
			if (elapsedTicks == 0 || elapsedTicks == 40) {
				if (Infusion.aquireEnergy(world, (EntityPlayer) player, 10, true)) {
					if (elapsedTicks > 0 || circleNear(world, player)) {
						SoundEffect.MOB_BLAZE_DEATH.playAtPlayer(world, (EntityPlayer) player);
					}else {
						SoundEffect.NOTE_SNARE.playAtPlayer(world, (EntityPlayer) player);
						ChatUtilities.sendTranslated(TextFormatting.RED, player, Reference.TORMENT_NOSTONE);
						player.resetActiveHand();
					}
				}
			}
			else if ((elapsedTicks == 80 || elapsedTicks == 120) && Infusion.aquireEnergy(world, player, 10, true)) {
					ParticleEffect.MOB_SPELL.send(SoundEffect.MOB_BLAZE_DEATH, player, 1.0, 2.0, 16);
				
			}else if ((elapsedTicks == 160 || elapsedTicks == 200 || elapsedTicks == 240) && Infusion.aquireEnergy(world, player, 10, true)) {
					ParticleEffect.MOB_SPELL.send(SoundEffect.MOB_BLAZE_DEATH, player, 1.0, 2.0, 16);
					ParticleEffect.FLAME.send(SoundEffect.NONE, player, 1.0, 2.0, 16);
				
			}else if (elapsedTicks == 280 && Infusion.aquireEnergy(world, player, 10, true)) {
				if (circleNear(world, player)) {
					ParticleEffect.MOB_SPELL.send(SoundEffect.NONE, player, 1.0, 2.0, 16);
					ParticleEffect.FLAME.send(SoundEffect.NONE, player, 1.0, 2.0, 16);
					ParticleEffect.FLAME.send(SoundEffect.NONE, player, 1.0,  2.0, 16);
					player.resetActiveHand();
					EntityLiving living = Infusion.spawnCreature(world, EntityLordOfTorment.class, player.posX, player.posY, player.posZ, null, 2, 4, ParticleEffect.FLAME, SoundEffect.MOB_ENDERDRAGON_GROWL);
					if (living != null ) {
						if (player.capabilities.isCreativeMode) {
							--stack.stackSize;
							if (stack.stackSize <= 0) {
								player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
							}
						}
						living.enablePersistence();
						world.newExplosion(living, living.posX, living.posY, living.posZ, 7.0f, false, world.getGameRules().getBoolean("mobGriefing"));
					}else {
						SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
					}
				}
				else {
					SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
					player.resetActiveHand();
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		playerIn.setActiveHand(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
}
