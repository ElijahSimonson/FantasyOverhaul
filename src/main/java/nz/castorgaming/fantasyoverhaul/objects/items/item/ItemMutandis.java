package nz.castorgaming.fantasyoverhaul.objects.items.item;

import java.util.ArrayList;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.util.MutableBlock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import scala.actors.threadpool.Arrays;

public class ItemMutandis extends GeneralItem {

	private boolean isExtreme;

	public ItemMutandis(String name, boolean extreme) {
		super(name);
		isExtreme = extreme;
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			Block block = worldIn.getBlockState(pos).getBlock();
			Block blockAbove = worldIn.getBlockState(new BlockPos(pos).up()).getBlock();
			if (isExtreme && (block == Blocks.GRASS || block == Blocks.MYCELIUM)) {
				if (worldIn.rand.nextInt(2) == 0) {
					worldIn.setBlockState(pos, (block == Blocks.GRASS) ? Blocks.MYCELIUM.getDefaultState()
							: Blocks.GRASS.getDefaultState());
					ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_FIZZ, worldIn, new BlockPos(pos).up(), 1.0,
							1.0, 16);
					--stack.stackSize;
				}
			} else if (isExtreme && block == Blocks.DIRT
					&& (blockAbove == Blocks.WATER || blockAbove == Blocks.FLOWING_WATER)) {
				if (worldIn.rand.nextInt(2) == 0) {
					setBlockToClay(worldIn, pos);
					setBlockToClay(worldIn, new BlockPos(pos).north());
					setBlockToClay(worldIn, new BlockPos(pos).south());
					setBlockToClay(worldIn, new BlockPos(pos).east());
					setBlockToClay(worldIn, new BlockPos(pos).west());
				} else {
					ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_FIZZ, worldIn, pos, 1.0, 1.0, 16);
				}
				--stack.stackSize;
			} else {
				ArrayList<MutableBlock> list;
				MutableBlocks[] blocks = { new MutableBlock(Blocks.SAPLING, 0), new MutableBlock(Blocks.SAPLING, 1),
						new MutableBlock(Blocks.SAPLING, 2), new MutableBlock(Blocks.SAPLING, 3),
						new MutableBlock(Blocks.SAPLING, 4), new MutableBlock(Blocks.SAPLING, 5),
						new MutableBlock(BlockInit.SAPLING, 0), new MutableBlock(BlockInit.SAPLING, 1),
						new MutableBlock(BlockInit.SAPLING, 2), new MutableBlock(BlockInit.EMBER_MOSS, 0),
						new MutableBlock((Block) Blocks.TALLGRASS, 1), new MutableBlock(Blocks.WATERLILY),
						new MutableBlock((Block) Blocks.BROWN_MUSHROOM_BLOCK),
						new MutableBlock((Block) Blocks.RED_MUSHROOM_BLOCK),
						new MutableBlock((Block) Blocks.RED_FLOWER, 0), new MutableBlock((Block) Blocks.YELLOW_FLOWER),
						new MutableBlock(BlockInit.SPANISH_MOSS, 1) };
				list = new ArrayList<MutableBlock>(Arrays.asList(blocks));
				for (String extra : Config.instance().mutandisExtras) {
					try {
						list.add(new MutableBlock(extra));
					} catch (Throwable t) {
					}
				}
				if (isExtreme) {
					MutableBlock[] extremisBlocks = { new MutableBlock(Blocks.CARROTS, -1, Math.min(metadata, 7)),
							new MutableBlock(Blocks.POTATOES, -1, Math.min(metadata, 7)),
							new MutableBlock(Blocks.WHEAT, -1, Math.min(metadata, 7)),
							new MutableBlock(Blocks.REEDS, -1, Math.min(metadata, 7)),
							new MutableBlock(BlockInit.CROP_BELLADONNA, -1,
									Math.min(metadata, BlockInit.CROP_BELLADONNA.getNumGrowthStages())),
							new MutableBlock(BlockInit.CROP_MANDRAKE, -1,
									Math.min(metadata, BlockInit.CROP_MANDRAKE.getNumGrowthStages())),
							new MutableBlock(BlockInit.CROP_ARTICHOKE, -1,
									Math.min(metadata, BlockInit.CROP_ARTICHOKE.getNumGrowthStages())),
							new MutableBlock(Blocks.PUMPKIN_STEM, -1, Math.min(metadata, 7)),
							new MutableBlock(Blocks.CACTUS),
							new MutableBlock(Blocks.MELON_STEM, -1, Math.min(metadata, 7)),
							new MutableBlock(Blocks.NETHER_WART, -1, Math.min(metadata, 3)) };
					list.addAll(Arrays.asList(extremisBlocks));
				} else if (playerIn.dimension == Config.instance().dimensionDreamID) {
					MutableBlock[] spiritBlocks = { new MutableBlock(Blocks.NETHER_WART, -1, 3) };
					list.addAll(Arrays.asList(spiritBlocks));
				}
				MutableBlock mutableBlock = new MutableBlock(block, block.getBlockState(), 0);
				int index = list.indexOf(mutableBlock);
				if (index != -1) {
					list.remove(index);
					list.get(worldIn.rand.nextInt(list.size())).mutate(worldIn, pos);
					ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_FIZZ, worldIn, pos, 1.0, 1.0, 16);
					--stack.stackSize;
				}

			}
		}
		return EnumActionResult.SUCCESS;
	}

	public static void setBlockToClay(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		Block blockAbove = world.getBlockState(new BlockPos(pos).up()).getBlock();
		if (block == Blocks.DIRT && (blockAbove == Blocks.WATER || blockAbove == Blocks.FLOWING_WATER)) {
			world.setBlockState(pos, Blocks.CLAY.getDefaultState());
			if (!world.isRemote) {
				ParticleEffect.INSTANT_SPELL.send(SoundEffect.MOB_SLIME_BIG, world, pos.add(0.5, 1.5, 0.5), 1.0, 1.0,
						16);
			}
		}
	}
}
