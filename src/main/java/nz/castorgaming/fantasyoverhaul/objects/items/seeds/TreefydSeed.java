package nz.castorgaming.fantasyoverhaul.objects.items.seeds;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class TreefydSeed extends GeneralItem{

	public TreefydSeed(String name) {
		super(name);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing != EnumFacing.UP) {
			return EnumActionResult.PASS;
		}
		BlockPos workPos = pos.up();
		Material mat = worldIn.getBlockState(workPos).getMaterial();
		if (Blocks.TALLGRASS.canBlockStay(worldIn, workPos, worldIn.getBlockState(workPos)) && (mat == null || !mat.isSolid())) {
			if (!worldIn.isRemote) {
				worldIn.setBlockState(workPos, Blocks.TALLGRASS);
				EntityTreefyd treefyd = new EntityTreefyd(worldIn);
				treefyd.setLocationAndAngles(workPos, 0.0f, 0.0f);
				treefyd.enablePersistance();
				treefyd.setOwner(playerIn.getUniqueID());
				worldIn.spawnEntityInWorld(treefyd);
				ParticleEffect.SLIME.send(SoundEffect.MOB_SILVERFISH_KILL, treefyd, 1.0, 2.0, 16);
				ParticleEffect.EXPLODE.send(SoundEffect.NONE, treefyd, 1.0, 2.0, 16);
			}
			--stack.stackSize;
		}
		return EnumActionResult.SUCCESS;
	}

}
