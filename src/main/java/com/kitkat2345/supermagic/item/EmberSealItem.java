package com.kitkat2345.supermagic.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EmberSealItem extends Item {
    public EmberSealItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20;
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseTicks) {
        if (level.isClientSide) {
            Vec3 start = living.getEyePosition();
            Vec3 look = living.getLookAngle();
            for (int i = 0; i < 6; i++) {
                double offset = i * 0.22D;
                double x = start.x + look.x * offset;
                double y = start.y + look.y * offset;
                double z = start.z + look.z * offset;
                level.addParticle(
                        ParticleTypes.FLAME,
                        x,
                        y,
                        z,
                        look.x * 0.08D + (Math.random() - 0.5D) * 0.04D,
                        look.y * 0.08D + (Math.random() - 0.5D) * 0.04D,
                        look.z * 0.08D + (Math.random() - 0.5D) * 0.04D
                );
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (living instanceof Player player) {
            Vec3 start = player.getEyePosition();
            Vec3 dir = player.getLookAngle().normalize();
            Vec3 end = start.add(dir.scale(12.0D));

            if (!level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) level;

                for (int i = 0; i < 25; i++) {
                    double t = i / 25.0D;
                    double x = start.x + dir.x * 12.0D * t;
                    double y = start.y + dir.y * 12.0D * t + 0.3D;
                    double z = start.z + dir.z * 12.0D * t;
                    serverLevel.sendParticles(ParticleTypes.FLAME, x, y, z, 5, 0.1D, 0.1D, 0.1D, 0.03D);
                    serverLevel.sendParticles(ParticleTypes.LAVA, x, y, z, 2, 0.0D, 0.0D, 0.0D, 0.0D);
                }

                AABB hitbox = new AABB(
                        end.x - 3.0D, end.y - 3.0D, end.z - 3.0D,
                        end.x + 3.0D, end.y + 3.0D, end.z + 3.0D
                );

                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, hitbox, entity -> entity != player)) {
                    double distance = target.position().distanceTo(end);
                    if (distance <= 3.5D) {
                        target.hurt(level.damageSources().playerAttack(player), 8.0F);
                        target.setSecondsOnFire(4);
                        target.setDeltaMovement(target.getDeltaMovement().add(
                                dir.x * 0.75D,
                                0.45D,
                                dir.z * 0.75D
                        ));
                    }
                }

                level.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.0F, 1.2F);
            } else {
                for (int i = 0; i < 30; i++) {
                    level.addParticle(
                            ParticleTypes.FLAME,
                            end.x + (Math.random() - 0.5D) * 0.5D,
                            end.y + (Math.random() - 0.5D) * 0.5D,
                            end.z + (Math.random() - 0.5D) * 0.5D,
                            0.0D,
                            0.0D,
                            0.0D
                    );
                }
            }
        }

        return stack;
    }
}
