package net.anoltongi;

import net.anoltongi.data.OreXPData;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;



public class CustomOreXP {
    //We use this function to calculate the amount of xp the block will drop
    public static void registerXP() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, be) -> {
            if (world.isClient) return;
            if (player.isCreative()) return;

            Block block = state.getBlock();
            OreXPData.IntRange range = OreXPData.get(block);
            if (range != null) {
                //int fortune = EnchantmentHelper.getLevel(Enchantments.FORTUNE, player.getMainHandStack());
                //I was originally thinking of getting more xp per fortune level but, it just might be too OP xD Instead I might try making it as a config later on.
                int baseXp  = range.random(world.random);
                int totalXp = baseXp;
                spawnXp(world, pos, totalXp);
            }
            else if (block instanceof ExperienceDroppingBlock) {
                return;
            }
        });
    }
    //Just simple Xp spawn logic
    private static void spawnXp(World world, BlockPos pos, int amount) {
        if (amount > 0) {
            double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;
            world.spawnEntity(new ExperienceOrbEntity(world, x, y, z, amount));
        }
    }

}
