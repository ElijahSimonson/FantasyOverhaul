package nz.castorgaming.fantasyoverhaul.objects.items.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;

public class SubduedSpirit extends GeneralItem{

	public SubduedSpirit(String name) {
		super(name);
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			EntityCreature creature = Infusion.spawnCreature(worldIn, EntitySpirit.class, pos, null, 0, 0, ParticleEffect.INSTANT_SPELL, null);
			if (creature != null) {
				EntitySpirit spirit = (EntitySpirit) creature;
				creature.enablePersistence();
				if (stack.getMetadata() == 1) {
					spirit.setTarget("Village", 1);
				}
				if (!playerIn.capabilities.isCreativeMode && stack.stackSize == 0) {
					playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
					if (playerIn instanceof EntityPlayerMP) {
						((EntityPlayerMP)playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
					}
				}
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = stack.getMetadata();
		return super.getUnlocalizedName(stack) + ((i == 1) ? ".villager" : ""); 
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
	}
	
	@Override
	public void registerModels() {
		FantasyOverhaul.proxy.registerItemRenderer(this, 0, "");
		FantasyOverhaul.proxy.registerItemRenderer(this, 1, "villager");
	}

}
