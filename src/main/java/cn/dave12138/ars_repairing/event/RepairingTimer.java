package cn.dave12138.ars_repairing.event;

import cn.dave12138.ars_repairing.Config;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

@AutoRegisterCapability
public class RepairingTimer {
    public static final Capability<RepairingTimer> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final String NAME = "repairing_timer";
    private int tickCount = 0;
    private boolean shouldUpdate = false;

    public void tick() {
        if (shouldUpdate) {
            if (tickCount > 0) {
                tickCount--;
            }
            shouldUpdate = false;
        }
    }

    public void startRepair() {
        shouldUpdate = Config.getRepairingDelay() != -1;
    }

    public boolean canRepair() {
        if (tickCount == 0) {
            tickCount = /*Config.repairingDelay*/ Config.getRepairingDelay();
            shouldUpdate = false;
            return true;
        }
        return false;
    }

    public static class Provider implements ICapabilityProvider {
        private LazyOptional<RepairingTimer> cachedCapability;

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == CAPABILITY) {
                if (cachedCapability == null || !cachedCapability.isPresent()) {

                    cachedCapability = LazyOptional.of(RepairingTimer::new);
                }
                return cachedCapability.cast();
            }
            return LazyOptional.empty();
        }

    }
}
