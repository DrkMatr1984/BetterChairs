package betterchairs.nms.v1_12_R1;

import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.CustomChairEntity;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.World;

class CustomArmorStand extends EntityArmorStand implements CustomChairEntity {
    private boolean remove = false;
    private final int regenerationAmplifier;

    /**
     * @param regenerationAmplifier provide a negative value to disable regeneration
     */
    public CustomArmorStand(World world, double d0, double d1, double d2, int regenerationAmplifier) {
        super(world, d0, d1, d2);

        this.regenerationAmplifier = regenerationAmplifier;
    }

    @Override
    public void markAsRemoved() {
        this.remove = true;
    }

    @Override
    public void a(float f1, float f2, float f3) {
        if (remove) return; // If the ArmorStand is being removed, no need to bother
        if (this.ticksLived % 10 == 0) return;  // Only run every 10 ticks

        Entity passenger = this.passengers.isEmpty() ? null : this.passengers.get(0);

        if (!(passenger instanceof EntityHuman)) {
            remove = true;
            this.getBukkitEntity().remove();
            return;
        }

        // Rotate the ArmorStand together with its passenger
        this.setYawPitch(passenger.yaw, passenger.pitch * .5F);
        this.aM = this.yaw;

        ChairUtils.applyRegeneration(((EntityHuman) passenger).getBukkitEntity(), this.regenerationAmplifier);
    }

    @Override
    public void killEntity() {
        // Prevents the ArmorStand from getting killed unexpectedly
        if (shouldDie()) super.killEntity();
    }

    @Override
    public void die() {
        // Prevents the ArmorStand from getting killed unexpectedly
        if (shouldDie()) super.die();
    }

    private boolean shouldDie() {
        return remove || this.passengers.isEmpty() || !(this.passengers.get(0) instanceof EntityHuman);
    }
}