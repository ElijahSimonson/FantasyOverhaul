package nz.castorgaming.fantasyoverhaul.util.handlers.hooks;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.armor.specialArmors.HuntersClothes;
import nz.castorgaming.fantasyoverhaul.objects.entities.familiars.Familiar;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityNightmare;
import nz.castorgaming.fantasyoverhaul.objects.worlds.WorldProviderDreamWorld;
import nz.castorgaming.fantasyoverhaul.powers.brews.effects.InfusionBrewEffect;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.powers.playereffect.PlayerEffect;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.classes.Coord;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketPlayerStyle;

@EventBusSubscriber
public class InfusionEventHooks {

	private boolean isBannedSpiritObject(ItemStack stack) {
		if (stack != null) {
			Item item = stack.getItem();
			return item == Items.ENDER_PEARL || item == Items.BLAZE_POWDER;
		}

		return false;
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onEnderTeleport(EnderTeleportEvent e) {
		if (!e.isCanceled() && e.getEntityLiving() != null && !e.getEntityLiving().worldObj.isRemote
				&& e.getEntityLiving() instanceof EntityPlayer && HuntersClothes.isFullSetWorn(e.getEntityLiving(), false) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void FillBucket(FillBucketEvent e) {
		ItemStack result = attemptFill(e.getWorld(), e.getTarget());
		if (result != null) {
			e.setFilledBucket(result);
			e.setResult(Event.Result.ALLOW);
		}
	}

	private ItemStack attemptFill(World world, RayTraceResult p) {
		BlockPos pos = new BlockPos(p.getBlockPos().getX(), p.getBlockPos().getY(), p.getBlockPos().getZ());
		IBlockState blockstate = world.getBlockState(pos);
		Block block = blockstate.getBlock();
		if (block == Fluids.FLOWING_SPIRIT) {
			if (block.getMetaFromState(blockstate) == 0) {
				world.setBlockToAir(pos);
				return new ItemStack(ItemInit.bucket_flowingspirit);
			}
		} else if (block == Fluids.HOLLOW_TEARS) {
			if (block.getMetaFromState(blockstate) == 0) {
				world.setBlockToAir(pos);
				return new ItemStack(ItemInit.bucket_hollowtears);
			}
		}
		return null;
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onLivingDeath(LivingDeathEvent e) {
		if (!e.getEntityLiving().worldObj.isRemote && !e.isCanceled()) {
			if (e.getEntityLiving() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) e.getEntityLiving();
				World world = player.worldObj;
				NBTTagCompound nbtTag = Infusion.getNBT(player);
				if (nbtTag.hasKey(Reference.INFUSION_DEPTHS)) {
					nbtTag.removeTag(Reference.INFUSION_DEPTHS);
				}
				PlayerEffect.onDeath(player);
			}
			Familiar.handleLivingDeath(e);
		}
	}

	@SubscribeEvent
	public void onLivingFall(LivingFallEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntityLiving();
			InfusionRegistry.instance().get(player).onFalling(player.worldObj, player, e);
		}
	}

	@SubscribeEvent
	public void onLivingHurt(final LivingHurtEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			Item armorSlot2 = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem();

			if (event.getSource().isFireDamage() && event.isCancelable() && !event.isCanceled() && armorSlot2 != null
					&& armorSlot2 == ItemInit.DEATH_ROBE) {
				if (!player.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
					player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 100, 0));
				}
				event.setCanceled(true);
			}
			if (!event.isCanceled()) {
				InfusionRegistry.instance().get(player).onHurt(player.worldObj, player, event);
			}
		}
	}

	@SubscribeEvent
	public void onLivingSetAttackTarget(final LivingSetAttackTargetEvent event) {
		if (event.getTarget() != null && event.getEntityLiving() instanceof EntityLiving) {
			final EntityLiving aggressorEntity = (EntityLiving) event.getEntityLiving();
			if (event.getTarget() instanceof EntityPlayer) {
				final EntityPlayer player = (EntityPlayer) event.getTarget();
				if (player.isInvisible()) {
					if (aggressorEntity.worldObj.getNearestAttackablePlayer(
							new BlockPos(aggressorEntity.posX, aggressorEntity.posY, aggressorEntity.posZ), 16.0,
							16.0) != event.getTarget()) {
						aggressorEntity.setAttackTarget((EntityLivingBase) null);
					}
				} else if (aggressorEntity.isPotionActive(MobEffects.BLINDNESS)) {
					aggressorEntity.setAttackTarget((EntityLivingBase) null);
				} else if (aggressorEntity instanceof EntityCreeper) {
					final ItemStack stack = player.inventory.armorItemInSlot(2);
					if (stack != null && stack.getItem() == ItemInit.WITCH_ROBES) {
						aggressorEntity.setAttackTarget((EntityLivingBase) null);
					}
				} else if (aggressorEntity.isEntityUndead()) {
					if (aggressorEntity instanceof EntityZombie && IPlayerVampire.get(player).getVampireLevel() >= 10) {
						aggressorEntity.setAttackTarget((EntityLivingBase) null);
					} else {
						final ItemStack stack = player.inventory.armorItemInSlot(2);
						if (stack != null && stack.getItem() == ItemInit.NECROMANCERS_ROBES) {
							aggressorEntity.setAttackTarget((EntityLivingBase) null);
						}
					}
				}
			}
			if (event.getTarget() instanceof EntityVillageGuard && event.getEntityLiving() instanceof EntityGolem) {
				aggressorEntity.setAttackTarget((EntityLivingBase) null);
			} else if (Config.instance().isZombeIgnoreVillagerActive() && event.getTarget() instanceof EntityVillager
					&& event.getEntityLiving() instanceof EntityZombie) {
				aggressorEntity.setAttackTarget((EntityLivingBase) null);
			}
		}
	}

	@SubscribeEvent
	public void onLivingDamage(LivingHurtEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().worldObj != null
				&& !event.getEntityLiving().worldObj.isRemote && event.getEntityLiving() instanceof EntityPlayer
				&& !event.isCanceled()) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			PredictionManager.instance().checkIfFulfilled(player, event);
		}
	}

	// New Stuff

	@SubscribeEvent
	public void onServerChat(final ServerChatEvent event) {
		if (event.getPlayer() != null && !event.isCanceled() && !event.getPlayer().worldObj.isRemote
				&& event.getMessage() != null) {
			ItemInit.ruby_slippers.trySayTheresNoPlaceLikeHome(event.getPlayer(), event.getMessage());
		}
	}

	@SubscribeEvent
	public void onHarvestDrops(final BlockEvent.HarvestDropsEvent event) {
		if (event.getHarvester() != null && event.getHarvester().worldObj != null
				&& !event.getHarvester().worldObj.isRemote) {
			PredictionManager.instance().checkIfFulfilled(event.getHarvester(), event);
			PlayerEffect.onHarvestDrops(event.getHarvester(), event);
			EntityAIDigBlocks.onHarvestDrops(event.getHarvester(), event);
		}
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == Config.instance().dimensionDreamID
				&& !event.isCanceled()) {
			final Iterator<ItemStack> iterator = event.getDrops().iterator();
			while (iterator.hasNext()) {
				final ItemStack stack = iterator.next();
				if (stack != null && this.isBannedSpiritObject(stack)) {
					iterator.remove();
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().worldObj != null
				&& !event.getEntityLiving().worldObj.isRemote && event.getEntityLiving() instanceof EntityPlayer
				&& !event.isCanceled()) {
			final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			PredictionManager.instance().checkIfFulfilled(player, event);
			PlayerEffect.onInteract(player, event);
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
		final long counter = event.getEntityLiving().worldObj.getTotalWorldTime();
		if (event.getEntityLiving() instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if (!event.getEntityLiving().worldObj.isRemote) {
				final long time = TimeUtilities.getServerTimeInTicks();
				if (counter % 4L == 0L) {
					final NBTTagCompound nbtPlayer = Infusion.getNBT(player);
					this.handleBrewGrotesqueEffect(player, nbtPlayer);
					WorldProviderDreamWorld.updatePlayerEffects(player.worldObj, player, nbtPlayer, time, counter);
					WorldProviderTorment.updatePlayerEffects(player.worldObj, player, nbtPlayer, time, counter);
					if (counter % 20L == 0L) {
						this.handleSyncEffects(player, nbtPlayer);
						this.handleBrewDepthsEffect(player, nbtPlayer);
						this.handleCurseEffects(player, nbtPlayer);
						this.handleSeepingShoesEffect(player, nbtPlayer);
						InfusionBrewEffect.checkActiveEffects(player.worldObj, player, nbtPlayer, counter % 1200L == 0L,
								time);
					}
					if (counter % 100L == 0L && !event.isCanceled()) {
						PredictionManager.instance().checkIfFulfilled(player, event);
						if (Config.instance().allowCovenWitchVisits && nbtPlayer.hasKey(Reference.COVEN)
								&& player.worldObj.rand.nextInt(20) == 0) {
							final BlockPos coords = player.getBedLocation(player.dimension);
							if (coords != null && coords.getDistance((int) player.posX, (int) player.posY,
									(int) player.posZ) < 256.0f) {
								final NBTTagList nbtCovenList = nbtPlayer.getTagList("WITCCoven", 10);
								if (nbtCovenList.tagCount() > 0) {
									EntityCovenWitch.summonCovenMember(player.worldObj, player, 90);
								}
							}
						}
					}
				}
				PlayerEffect.onUpdate(player, time);
				if (counter % 100L == 1L) {
					EntityWitchHunter.handleWitchHunterEffects(player, time);
				}
			}
			this.handleIcySlippersEffect(player);
			this.handleFamiliarFollowerSync(player);
		} else if (!event.getEntityLiving().worldObj.isRemote && counter % 20L == 0L) {
			this.handleCurseEffects(event.getEntityLiving(), event.getEntityLiving().getEntityData());
		}
		if (counter % 100L == 0L) {
			final ItemStack belt = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.LEGS);
			if (belt != null && belt.getItem() == ItemInit.BARK_BELT) {
				final Block blockID = event.getEntityLiving().worldObj.getBlock(
						MathHelper.floor_double(event.getEntityLiving().posX),
						MathHelper.floor_double(event.getEntityLiving().posY) - 1,
						MathHelper.floor_double(event.getEntityLiving().posZ));
				if (blockID == Blocks.GRASS || blockID == Blocks.MYCELIUM) {
					final int maxChargeLevel = ItemInit.BARK_BELT.getMaxChargeLevel(event.getEntityLiving());
					final int currentChargeLevel = ItemInit.BARK_BELT.getChargeLevel(belt);
					if (currentChargeLevel < maxChargeLevel) {
						ItemInit.BARK_BELT.setChargeLevel(belt, Math.min(currentChargeLevel + 1, maxChargeLevel));
						event.getEntityLiving().worldObj.playSoundAtEntity(event.getEntityLiving(),
								"witchery:random.wood_creak", 0.5f,
								(float) (0.8 + 2.0 * event.getEntityLiving().worldObj.rand.nextGaussian()));
					}
				}
			}
		}
	}

	private void handleSeepingShoesEffect(final EntityPlayer player, final NBTTagCompound nbtTag) {
		if (!player.onGround) {
			return;
		}
		if (!player.isPotionActive(MobEffects.POISON) && !player.isPotionActive(MobEffects.WITHER)) {
			return;
		}
		final ItemStack shoes = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		if (shoes == null || shoes.getItem() != ItemInit.SEEPING_SHOES) {
			return;
		}
		boolean poisonRemoved = false;
		if (player.isPotionActive(MobEffects.POISON)) {
			player.removePotionEffect(MobEffects.POISON);
			poisonRemoved = true;
		}
		if (player.isPotionActive(MobEffects.WITHER)) {
			player.removePotionEffect(MobEffects.WITHER);
			poisonRemoved = true;
		}
		if (poisonRemoved) {
			final int x = MathHelper.floor_double(player.posX);
			final int z = MathHelper.floor_double(player.posZ);
			final int y = MathHelper.floor_double(player.posY);
			final int RADIUS = 3;
			final int RADIUS_SQ = 9;
			for (int dx = x - 3; dx <= x + 3; ++dx) {
				for (int dz = z - 3; dz <= z + 3; ++dz) {
					for (int dy = y - 1; dy <= y + 1; ++dy) {
						if (Coord.distanceSq(dx, 1.0, dy, x, 1.0, dy) <= 9.0 && player.worldObj.isAirBlock(new BlockPos(dx, dy + 1, dz)) && !player.worldObj.isAirBlock(new BlockPos(dx, dy, dz))) {
							ItemDye.applyBonemeal(new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("bonemeal"))), player.worldObj, new BlockPos(dx, dy, dz), player)
						}
					}
				}
			}
		}
	}

	private void handleSyncEffects(final EntityPlayer player, final NBTTagCompound nbtPlayer) {
		if (!player.worldObj.isRemote && nbtPlayer.hasKey("WITCResyncLook")) {
			final long nextSync = nbtPlayer.getLong("WITCResyncLook");
			if (nextSync <= MinecraftServer.getCurrentTimeMillis()) {
				nbtPlayer.removeTag("WITCResyncLook");
				Reference.PACKET_HANDLER.sendToDimension(new PacketPlayerStyle(player), player.dimension);
			}
		}
	}

	private void handleFamiliarFollowerSync(final EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			final NBTTagCompound compound = player.getEntityData();
			if (compound.hasKey(Reference.LAST_POS)) {
				final NBTTagCompound pos = compound.getCompoundTag("WITC_LASTPOS");
				final int lastDimension = pos.getInteger("D");
				if (lastDimension != player.dimension || Math.abs(pos.getDouble("X") - player.posX) > 32.0
						|| Math.abs(pos.getDouble("Z") - player.posZ) > 32.0) {
					if ((lastDimension != player.dimension && player.dimension == -1) || lastDimension == -1) {
						final NBTTagCompound nbtPlayer = Infusion.getNBT(player);
						nbtPlayer.setBoolean("WITCVisitedNether", true);
					}
					if (Familiar.hasActiveFamiliar(player)) {
						final EntityTameable familiar = Familiar.getFamiliarEntity(player);
						if (familiar != null && !familiar.isSitting()) {
							final int ipx = MathHelper.floor_double(player.posX) - 2;
							final int j = MathHelper.floor_double(player.posZ) - 2;
							final int k = MathHelper.floor_double(player.getEntityBoundingBox().minY) - 2;
							boolean done = false;
							for (int l = 0; l <= 4 && !done; ++l) {
								for (int i1 = 0; i1 <= 4 && !done; ++i1) {
									for (int dy = 0; dy <= 4 && !done; ++dy) {
										if (player.worldObj.getBlockState(new BlockPos(ipx + l, k + dy - 1, j + i1))
												.isSideSolid(player.worldObj, new BlockPos(ipx + l, k + dy - 1, j + i1),
														EnumFacing.UP)
												&& !player.worldObj.getBlockState(new BlockPos(ipx + l, k + dy, j + i1))
														.isNormalCube()
												&& !player.worldObj
														.getBlockState(new BlockPos(ipx + l, k + dy + 1, j + i1))
														.isNormalCube()) {
											EntityUtil.teleportToLocation(player.worldObj, 0.5 + ipx + l, k + dy,
													0.5 + j + i1, player.dimension, familiar, true);
											done = true;
										}
									}
								}
							}
						}
					}
				}
				pos.setDouble("X", player.posX);
				pos.setDouble("Z", player.posZ);
				pos.setInteger("D", player.dimension);
			} else {
				final NBTTagCompound pos = new NBTTagCompound();
				pos.setDouble("X", player.posX);
				pos.setDouble("Z", player.posZ);
				pos.setInteger("D", player.dimension);
				pos.setBoolean("visitedNether", player.dimension == -1);
			}
		}
	}

	private void handleIcySlippersEffect(final EntityPlayer player) {
		final ItemStack shoes = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		if (shoes != null && shoes.getItem() == ItemInit.icy_boots) {
			final int k = MathHelper.floor_double(player.posY - 1.0);
			for (int i = 0; i < 4; ++i) {
				final int j = MathHelper.floor_double(player.posX + (i % 2 * 2 - 1) * 0.5f);
				final int l = MathHelper.floor_double(player.posZ + (i / 2 % 2 * 2 - 1) * 0.5f);
				final Block blockID = player.worldObj.getBlockState(new BlockPos(j, k, l)).getBlock();
				if (blockID == Blocks.FLOWING_WATER || blockID == Blocks.WATER) {
					player.worldObj.setBlockState(new BlockPos(j, k, l), Blocks.ICE.getDefaultState());
				} else if (blockID == Blocks.FLOWING_LAVA || blockID == Blocks.LAVA) {
					player.worldObj.setBlockState(new BlockPos(j, k, l), Blocks.OBSIDIAN.getDefaultState());
					if (player.worldObj.rand.nextInt(10) == 0) {
						shoes.damageItem(1, player);
					}
				}
			}
		}
	}

	private void handleBrewDepthsEffect(final EntityPlayer player, final NBTTagCompound nbtTag) {
		if (nbtTag.hasKey(Reference.INFUSION_DEPTHS)) {
			int timeLeft = nbtTag.getInteger(Reference.INFUSION_DEPTHS);
			if (timeLeft > 0) {
				if (!player.isPotionActive(MobEffects.WATER_BREATHING)) {
					player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 6000));
				}
				if (!player.isInsideOfMaterial(Material.WATER)) {
					if (!player.isPotionActive(MobEffects.WITHER)) {
						player.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 1));
					}
				} else if (player.isPotionActive(MobEffects.WITHER)) {
					player.removePotionEffect(MobEffects.WITHER);
				}
			}
			if (--timeLeft <= 0) {
				nbtTag.removeTag(Reference.INFUSION_DEPTHS);
				if (player.isPotionActive(MobEffects.WATER_BREATHING)) {
					player.removePotionEffect(MobEffects.WATER_BREATHING);
				}
				if (player.isPotionActive(MobEffects.POISON)) {
					player.removePotionEffect(MobEffects.POISON);
				}
			} else {
				nbtTag.setInteger(Reference.INFUSION_DEPTHS, timeLeft);
			}
		}
	}

	private void handleBrewGrotesqueEffect(final EntityPlayer player, final NBTTagCompound nbtTag) {
		if (nbtTag.hasKey(Reference.INFUSION_GROTESQUE)) {
			int timeLeft = nbtTag.getInteger(Reference.INFUSION_GROTESQUE);
			if (timeLeft > 0) {
				final float radius = 4.0f;
				final AxisAlignedBB bounds = new AxisAlignedBB(
						new BlockPos(player.posX - 4.0, player.posY - 4.0, player.posZ - 4.0),
						new BlockPos(player.posX + 4.0, player.posY + 4.0, player.posZ + 4.0));
				final List<EntityLiving> list = player.worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);
				for (final EntityLiving entity : list) {
					final boolean victim = !(entity instanceof EntityDemon) && entity.isNonBoss()
							&& !(entity instanceof EntityGolem) && !(entity instanceof EntityWitch);
					if (victim && Coord.distance(entity.posX, entity.posY, entity.posZ, player.posX, player.posY,
							player.posZ) < 4.0) {
						RiteProtectionCircleRepulsive.push(player.worldObj, entity, player.posX, player.posY,
								player.posZ);
					}
				}
			}
			if (--timeLeft <= 0) {
				nbtTag.removeTag(Reference.INFUSION_GROTESQUE);
				Reference.PACKET_HANDLER.sendToDimension(new PacketPlayerStyle(player), player.dimension);
			} else {
				nbtTag.setInteger(Reference.INFUSION_GROTESQUE, timeLeft);
			}
		}
	}

	private void handleCurseEffects(final EntityLivingBase entity, final NBTTagCompound nbtTag) {
		if (entity != null && nbtTag != null) {
			if (!(entity instanceof EntityPlayer) && nbtTag.hasKey(Reference.INFUSION_SINKING)) {
				final int level = nbtTag.getInteger(Reference.INFUSION_SINKING);
				if (level > 0) {
					if (entity.isInWater() || (entity instanceof EntityPlayer && !entity.onGround)) {
						if (entity.motionY < 0.0) {
							entity.motionY *= 1.0 + Math.min(0.1 * level, 0.4);
						} else if (entity.motionY > 0.0) {
							entity.motionY *= 1.0 - Math.min(0.1 * level, 0.4);
						}
					}
				} else {
					nbtTag.removeTag(Reference.INFUSION_SINKING);
				}
			}
			if (nbtTag.hasKey(Reference.INFUSION_CURSED)) {
				final int level = nbtTag.getInteger(Reference.INFUSION_CURSED);
				if (level > 0) {
					if (!entity.isPotionActive(MobEffects.BLINDNESS) && !entity.isPotionActive(MobEffects.WEAKNESS)
							&& !entity.isPotionActive(MobEffects.MINING_FATIGUE)
							&& !entity.isPotionActive(MobEffects.SLOWNESS) && !entity.isPotionActive(MobEffects.POISON)
							&& entity.worldObj.rand.nextInt(20) == 0) {
						switch (entity.worldObj.rand.nextInt(
								(level >= 5) ? 6 : ((level >= 4) ? 5 : ((level >= 3) ? 4 : ((level >= 2) ? 3 : 2))))) {
						case 0: {
							entity.addPotionEffect(
									new PotionEffect(MobEffects.MINING_FATIGUE, 600, Math.min(level - 1, 4)));
							break;
						}
						case 1: {
							entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 600, Math.min(level - 1, 4)));
							break;
						}
						case 2: {
							entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (13 + 2 * level) * 20,
									Math.min(level - 2, 4)));
							break;
						}
						case 3: {
							entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 5 * level * 20));
							if (level > 5) {
								entity.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 5 * level * 20));
								break;
							}
							break;
						}
						case 5: {
							if (entity instanceof EntityPlayer) {
								final EntityPlayer player = (EntityPlayer) entity;
								final int heldItemIndex = player.inventory.currentItem;
								if (player.inventory.mainInventory[heldItemIndex] != null) {
									player.dropPlayerItemWithRandomChoice(player.inventory.mainInventory[heldItemIndex],
											true);
									player.inventory.mainInventory[heldItemIndex] = null;
								}
								break;
							}
							final ItemStack heldItem = entity.getHeldItemMainhand();
							if (heldItem != null) {
								Infusion.dropEntityItemWithRandomChoice(entity, heldItem, true);
								entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
								break;
							}
							break;
						}
						}
					}
				} else {
					nbtTag.removeTag(Reference.INFUSION_CURSED);
				}
			}
			if (nbtTag.hasKey(Reference.INFUSION_OVERHEAT)) {
				final int level = nbtTag.getInteger(Reference.INFUSION_OVERHEAT);
				if (level > 0) {
					final World world = entity.worldObj;
					if (!entity.isBurning() && world.rand.nextInt((level > 2) ? 20 : ((level > 1) ? 25 : 30)) == 0) {
						final int x = MathHelper.floor_double(entity.posX);
						final int z = MathHelper.floor_double(entity.posZ);
						final Biome biome = world.getBiomeForCoordsBody(new BlockPos(x, 64, z));
						if (biome.getTemperature() >= 1.5 && (!biome.canRain() || !world.isRaining())
								&& !entity.isInWater()) {
							entity.setFire(Math.min(world.rand.nextInt((level < 4) ? 2 : (level - 1)) + 1, 4));
						}
					}
				} else {
					nbtTag.removeTag(Reference.INFUSION_OVERHEAT);
				}
			}
			if (nbtTag.hasKey(Reference.INFUSION_NIGHTMARE) && entity instanceof EntityPlayer) {
				final EntityPlayer player2 = (EntityPlayer) entity;
				final int level2 = nbtTag.getInteger(Reference.INFUSION_NIGHTMARE);
				if (level2 > 0 && player2.dimension != Config.instance().dimensionDreamID) {
					final World world2 = player2.worldObj;
					if (world2.rand.nextInt((level2 > 4) ? 30 : ((level2 > 2) ? 60 : 180)) == 0) {
						final double R = 16.0;
						final double H = 8.0;
						final AxisAlignedBB bounds = new AxisAlignedBB(entity.posX - 16.0, entity.posY - 8.0,
								entity.posZ - 16.0, entity.posX + 16.0, entity.posY + 8.0, entity.posZ + 16.0);
						final List entities = world2.getEntitiesWithinAABB(EntityNightmare.class, bounds);
						boolean doNothing = false;
						for (final Object obj : entities) {
							final EntityNightmare nightmare = (EntityNightmare) obj;
							if (nightmare.getVictimUUID().equals(player2.getCommandSenderEntity().getUniqueID())) {
								doNothing = true;
								break;
							}
						}
						if (!doNothing) {
							Infusion.spawnCreature(world2, EntityNightmare.class, MathHelper.floor_double(player2.posX),
									MathHelper.floor_double(player2.posY), MathHelper.floor_double(player2.posZ),
									player2, 2, 6);
						}
					}
				} else {
					nbtTag.removeTag(Reference.INFUSION_NIGHTMARE);
				}
			}
			if (entity instanceof EntityPlayer && nbtTag.hasKey(Reference.INFUSION_INSANITY)) {
				final int level = nbtTag.getInteger(Reference.INFUSION_INSANITY);
				if (level > 0) {
					final World world = entity.worldObj;
					final int x = MathHelper.floor_double(entity.posX);
					final int y = MathHelper.floor_double(entity.posY);
					final int z2 = MathHelper.floor_double(entity.posZ);
					if (world.rand.nextInt((level > 2) ? 25 : ((level > 1) ? 30 : 35)) == 0) {
						Class<? extends EntityCreature> creatureType = null;
						switch (world.rand.nextInt(3)) {
						default: {
							creatureType = EntityIllusionCreeper.class;
							break;
						}
						case 1: {
							creatureType = EntityIllusionSpider.class;
							break;
						}
						case 2: {
							creatureType = EntityIllusionZombie.class;
							break;
						}
						}
						final int MAX_DISTANCE = 9;
						final int MIN_DISTANCE = 4;
						Infusion.spawnCreature(world, creatureType, x, y, z2, entity, 4, 9);
					} else if (level >= 4 && world.rand.nextInt(20) == 0) {
						SoundEffect sound = SoundEffect.NONE;
						switch (world.rand.nextInt(3)) {
						default: {
							sound = SoundEffect.RANDOM_EXPLODE;
							break;
						}
						case 1: {
							sound = SoundEffect.MOB_ENDERMAN_IDLE;
							break;
						}
						}
						sound.playOnlyTo((EntityPlayer) entity, 1.0f, 1.0f);
					}
				} else {
					nbtTag.removeTag(Reference.INFUSION_INSANITY);
				}
			}
		}
	}
}

}
