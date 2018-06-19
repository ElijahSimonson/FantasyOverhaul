package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayer;
import nz.castorgaming.fantasyoverhaul.objects.entities.familiars.Familiar;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItemEnchanted;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.powers.infusions.player.InfusionOtherwhere;
import nz.castorgaming.fantasyoverhaul.powers.symbols.SymbolEffect;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.ChatUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.Coord;
import nz.castorgaming.fantasyoverhaul.util.classes.GeneralUtil;

public class SeerStone extends GeneralItemEnchanted{

	public SeerStone(String name) {
		super(name);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		World world = player.worldObj;
		if ( !(!world.isRemote && player instanceof EntityPlayerMP)) {
			return;
		}
		int elapsedTicks = getMaxItemUseDuration(stack) - count;
		EntityCovenWitch.summonCoven(world, player, new Coord(player), elapsedTicks);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		useSeerStone(worldIn, playerIn, itemStackIn, hand); 
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
	
	private boolean useSeerStone(World world, EntityPlayer player, ItemStack stack, EnumHand hand) {
		if (player.isSneaking()) {
			if (!world.isRemote) {
				RayTraceResult rtr = InfusionOtherwhere.doCustomRayTrace(world, player, true, 3.0);
				if (rtr != null) {
					switch(rtr.typeOfHit) {
						case ENTITY : {
							if (rtr.entityHit instanceof EntityPlayer) {
								readPlayer(player, (EntityPlayer) rtr.entityHit);
								return true;
							}
							break;
						}
						default : {
							readPlayer(player, player);
							break;
						}
					}
				} else {
					readPlayer(player, player);
				}
			}
		}
		else {
			player.setActiveHand(hand);
		}
		return true;
	}
	
	private void readPlayer(EntityPlayer player, EntityPlayer target) {
		String read = "";
		NBTTagCompound nbtTarget = Infusion.getNBT(target);
		
		if (nbtTarget != null && nbtTarget.hasKey(Reference.MANIFESTATION_COUNTDOWN)) {
			int timeRemaining = nbtTarget.getInteger(Reference.MANIFESTATION_COUNTDOWN);
			if (timeRemaining > 0) {
				read += String.format(GeneralUtil.resource("item.seerstone.manifestationtime"), Integer.valueOf(timeRemaining).toString()) + ", ";
			}
			else {
				read += GeneralUtil.resource("item.seerstone.nomanifestationtime") + ", ";
			}
		}else {
			read += GeneralUtil.resource("item.seerstone.nomanifestationtime") + ", ";
		}
		String familiarName = Familiar.getFamiliarName(target);
		if (familiarName != null && !familiarName.isEmpty()) { 
			read += String.format(GeneralUtil.resource("item.seerstone.familiar"),familiarName) + ", ";
		}else {
			read += GeneralUtil.resource("item.seerstone.familiar") + ", ";
		}
		int covenSize = EntityCovenWitch.getCovenSize(target);
		if (covenSize > 0) {
			read += String.format(GeneralUtil.resource("item.seerstone.covensize"), Integer.valueOf(covenSize).toString()) + ", ";
		}else {
			read += GeneralUtil.resource("item.seerstone.nocoven") + ", ";
		}
		String spellKnowledge = SymbolEffect.getKnowledge(target);
		if (!spellKnowledge.isEmpty()) {
			read += String.format(GeneralUtil.resource("item.seerstone.knownspells"), spellKnowledge) + ", ";
		}
		else {
			read += GeneralUtil.resource("item.seerstone.nospells") + ", ";
		}
		ExtendedPlayer extPlayer = ExtendedPlayer.get(target);
		if (extPlayer != null) {
			int bottlingSkill = extPlayer.getSkillPotionBottling();
			read += String.format(GeneralUtil.resource("item.seerstone.bottlingskill"), Integer.valueOf(bottlingSkill).toString()) + ", ";
		}
		if (nbtTarget != null && (nbtTarget.hasKey(Reference.INFUSION_CURSED) || nbtTarget.hasKey(Reference.INFUSION_INSANITY) || nbtTarget.hasKey(Reference.INFUSION_SINKING) || nbtTarget.hasKey(Reference.INFUSION_OVERHEAT) || nbtTarget.hasKey(Reference.WAKING_NIGHTMARE))) {
			if (nbtTarget.hasKey(Reference.INFUSION_CURSED)) {
				int level = nbtTarget.getInteger(Reference.INFUSION_CURSED);
				read += String.format(GeneralUtil.resource("item.seerstone.misfortune"), level) + ", ";
			}
			if (nbtTarget.hasKey(Reference.INFUSION_INSANITY)) {
				int level = nbtTarget.getInteger(Reference.INFUSION_INSANITY);
				read += String.format(GeneralUtil.resource("item.seerstone.insanity"), level) + ", ";
			}
			if (nbtTarget.hasKey(Reference.INFUSION_SINKING)) {
				int level = nbtTarget.getInteger(Reference.INFUSION_SINKING);
				read += String.format(GeneralUtil.resource("item.seerstone.sinking"), level) + ", ";
			}
			if (nbtTarget.hasKey(Reference.INFUSION_OVERHEAT)) {
				int level = nbtTarget.getInteger(Reference.INFUSION_OVERHEAT);
				read += String.format(GeneralUtil.resource("item.seerstone.overheating"), level) + ", ";
			}
			if (nbtTarget.hasKey(Reference.WAKING_NIGHTMARE)){
				int level = nbtTarget.getInteger(Reference.INFUSION_NIGHTMARE);
				read += String.format(GeneralUtil.resource("item.seerstone.nightmare"), level) + ", ";
			}
		}else {
			read += GeneralUtil.resource("item.seerstone.notcursed");
		}
		ChatUtilities.sendPlain(TextFormatting.BLUE, player, read);
	}

}
