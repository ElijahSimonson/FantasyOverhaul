package nz.castorgaming.fantasyoverhaul.objects.items.main;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockUtil;
import nz.castorgaming.fantasyoverhaul.util.classes.Coord;
import nz.castorgaming.fantasyoverhaul.util.classes.GeneralUtil;

public class GeneralItem extends ItemBase {

	public GeneralItem(String name) {
		super(name);
	}
	
	public static boolean isBrew(ItemStack stack) {
		return stack.getItem() instanceof Brew;
	}


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
	
	public boolean copyLocationBinding(World world, ItemStack from, ActivatedRitual to) {
		if (!hasLocationBinding(from)) {
			return false;
		}
		NBTTagCompound tagFrom = from.getTagCompound();
		if (tagFrom.getInteger("PosD") != world.provider.getDimension()) {
			return false;
		}
		Coord coord = new Coord(tagFrom.getInteger("PosX"), tagFrom.getInteger("PosY"), tagFrom.getInteger("PosZ"));
		to.setLocation(coord);
		return true;
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
		return (pass == 0) && stack.isItemEnchanted() && ItemInit.BROOM_ENCHANTED.isMatch(stack) || ItemInit.SPIRIT_SUBDUED.isMatch(stack);
	}

	public ItemStack onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		return stack;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		String location = getBoundDisplayName(stack);
		if (location != null && !location.isEmpty()) {
			tooltip.add(location);
		}
		String taglock = ItemInit.TAGLOCK_KIT.getBoundEntityDisplayName(stack, 1);
		if (!taglock.isEmpty()) {
			tooltip.add(String.format(GeneralUtil.resource(Reference.BOUND_TO), taglock));
		}
		if (ItemInit.DOOR_KEY.isMatch(stack)) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null && tag.hasKey("doorX") && tag.hasKey("doorY") && tag.hasKey("doorZ")) {
				int x,y,z;
				x = tag.getInteger("doorX");
				y = tag.getInteger("doorY");
				z = tag.getInteger("doorZ");
				if (tag.hasKey("doorD") && tag.hasKey("doorDN")) {
					tooltip.add(String.format("%s: %d, %d, %d", tag.getString("doorDN"), x, y, z));
				}else {
					tooltip.add(String.format("%d, %d,%d", x, y, z));
				}
			}
		}
		else if (ItemInit.DOOR_KEYRING.isMatch(stack)) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null && tag.hasKey("doorKeys")) {
				NBTTagList keys = tag.getTagList("doorKeys", 11);
				if (keys != null) {
					for (int i = 0; i < keys.tagCount(); i++) {
						NBTTagCompound key = keys.getCompoundTagAt(i);
						if (key != null && key.hasKey("doorX") && key.hasKey("doorY") && key.hasKey("doorZ")) {
							int x,y,z;
							x = key.getInteger("doorX");
							y = key.getInteger("doorY");
							z = key.getInteger("doorZ");
							if (key.hasKey("doorD") && key.hasKey("doorDN")) {
								tooltip.add(String.format("%s: %d, %d, %d", key.getString("doorDN"), x,y,z));
							}else {
								tooltip.add(String.format("%d, %d, %d", x,y,z));
							}
						}
					}
				}
			}
		}
		super.addInformation(stack, playerIn, tooltip, advanced);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof Edible || item instanceof Drinkable) {
			return 32;
		} else if (ItemInit.WAYSTONE_BOUND.isMatch(stack) || ItemInit.CONTRACT_TORMENT.isMatch(stack) || ItemInit.SEER_STONE.isMatch(stack)) {
			return 1200;
		}
		return super.getMaxItemUseDuration(stack);
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof Drinkable) {
			return ((Drinkable)item).userAction;
		} else if (ItemInit.CONTRACT_TORMENT.isMatch(stack) || ItemInit.SEER_STONE.isMatch(stack)) {
			return EnumAction.BOW;
		}
		return super.getItemUseAction(stack);
	}
	
	public boolean isBook(ItemStack stack) {
		boolean found = false;
		for (GeneralItem item : ItemInit.BOOKS) {
			if (item.isMatch(stack)) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		Item item = stack.getItem();
		if (item instanceof Drinkable) {
			return ((Drinkable) item).onDrunk(stack, worldIn, (EntityPlayer)entityLiving);
		}
		else if (item instanceof Edible) {
			return ((Edible) item).onFoodEaten(stack, worldIn, (EntityPlayer) entityLiving);
		}else {
			return onFoodEaten(stack, worldIn, (EntityPlayer) entityLiving);
		}
	}

	
	protected boolean circleNear(World world, EntityPlayer player) {
		int midX, midY, midZ;
		midX = MathHelper.floor_double(player.posX);
		midY = MathHelper.floor_double(player.posY);
		midZ = MathHelper.floor_double(player.posZ);
		int[][] PATTERN = { { 0, 0, 0, 0, 4, 3, 4, 0, 0, 0, 0 }, { 0, 0, 4, 3, 1, 1, 1, 3, 4, 0, 0 }, { 0, 4, 1, 1, 1, 1, 1, 1, 1, 4, 0 }, { 0, 3, 1, 1, 1, 1, 1, 1, 1, 3, 0 }, { 4, 1, 1, 1, 2, 2, 2, 1, 1, 1, 4 }, { 3, 1, 1, 1, 2, 1, 2, 1, 1, 1, 3 }, { 4, 1, 1, 1, 2, 2, 2, 1, 1, 1, 4 }, { 0, 3, 1, 1, 1, 1, 1, 1, 1, 3, 0 }, { 0, 4, 1, 1, 1, 1, 1, 1, 1, 4, 0 }, { 0, 0, 4, 3, 1, 1, 1, 3, 4, 0, 0 }, { 0, 0, 0, 0, 4, 3, 4, 0, 0, 0, 0 } };
		int offsetZ = (PATTERN.length -1) / 2;
		for (int z = 0; z < PATTERN.length - 1; z++) {
			int worldZ = midZ - offsetZ + z;
			int offsetX = (PATTERN[z].length) /2;
			for (int x = 0; x < PATTERN[z].length; x++) {
				int worldX = midX - offsetX + x;
				int value = PATTERN[PATTERN.length - 1 - z][x];
				if (value != 0 && isPost(world, worldX, midY, worldZ, value == 2 || value == 4, value == 4, value == 3 || value ==4)) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if (isBook(itemStackIn)) {
			openWitchcraftBook(worldIn, playerIn, itemStackIn);
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
		
	private void openWitchcraftBook(World world, EntityPlayer player, ItemStack stack) {
		int posX, posY, posZ;
		posX = MathHelper.floor_double(player.posX);
		posY = MathHelper.floor_double(player.posY);
		posZ = MathHelper.floor_double(player.posZ);
		player.openGui(FantasyOverhaul.instance, 1, world, posX, posY, posZ);
	}
	
	@SuppressWarnings("deprecation")
	private boolean placeBlock(Block spawnBlock, ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = world.getBlockState(pos).getBlock();
		if (stack.stackSize == 0) {
			return false;
		}
		if (block != Blocks.VINE && block != Blocks.TALLGRASS && block != Blocks.DEADBUSH) {
			switch (facing) {
			case DOWN: {
				pos.down();
				break;
			}
			case EAST:{
				pos.east();
				break;
			}
			case NORTH:				{
					pos.north();
					break;
				}
			case SOUTH:{
				pos.south();
				break;
			}
			case UP:{
				pos.up();
				break;
			}
			case WEST:			{
				pos.west();
				break;
			}
			}
		}
		if (!player.canPlayerEdit(pos, facing, stack)) {
			return false;
		}
		AxisAlignedBB bounds = spawnBlock.getCollisionBoundingBox(spawnBlock.getDefaultState(), world, pos);
		if (world.checkBlockCollision(bounds)){
			IBlockState state = spawnBlock.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, spawnBlock.getMetaFromState(spawnBlock.getDefaultState()), player);
			placeCheckedBlock(spawnBlock, stack, player, world, pos, facing, hitX, hitY, hitZ, state);
		}
		return true;
	}
	
	private void placeCheckedBlock(Block spawnBlock, ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, IBlockState state){
		if (world.setBlockState(pos, state)) {
			if (world.getBlockState(pos) == state) {
				spawnBlock.onBlockPlacedBy(world, pos, state, player, stack);
			}
			SoundType type = spawnBlock.getSoundType(state, world, pos, player);
			world.playSound(player, pos, type.getPlaceSound(), SoundCategory.BLOCKS, type.getVolume(), type.getPitch());
			--stack.stackSize;
		}
	}
	
    public void setHeadingFromThrower(EntityItem item, Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        setThrowableHeading(item, (double)f, (double)f1, (double)f2, velocity, inaccuracy);
        item.motionX += entityThrower.motionX;
        item.motionZ += entityThrower.motionZ;

        if (!entityThrower.onGround)
        {
            item.motionY += entityThrower.motionY;
        }
    }
    
    public void setThrowableHeading(EntityItem item, double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = MathHelper.sqrt_double(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x + item.worldObj.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        y = y + item.worldObj.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + item.worldObj.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        item.motionX = x;
        item.motionY = y;
        item.motionZ = z;
        float f1 = MathHelper.sqrt_double(x * x + z * z);
        item.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        item.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
        item.prevRotationYaw = item.rotationYaw;
        item.prevRotationPitch = item.rotationPitch;
    }
    
}
