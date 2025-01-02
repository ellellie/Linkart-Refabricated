package com.github.vini2003.linkart.mixin;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
//? if >=1.21.4 {
/*import net.minecraft.server.world.ServerWorld;
*///?}

@Mixin(AbstractMinecartEntity.class)
public interface MinecartAccessor {

    @Invoker("getMaxSpeed")
    //? if =1.21.1
    double linkart$getMaxSpeed();
    //? if >=1.21.4
    /*double linkart$getMaxSpeed(ServerWorld world);*/
}
