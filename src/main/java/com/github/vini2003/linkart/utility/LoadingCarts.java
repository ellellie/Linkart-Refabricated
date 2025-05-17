package com.github.vini2003.linkart.utility;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;

import java.util.HashSet;
import java.util.Set;
//? if >=1.21.5
/*import net.minecraft.world.PersistentStateType;*/

public class LoadingCarts extends PersistentState {

    /*? if <1.21.5*/
    private static final Type<LoadingCarts> TYPE = new Type<>(LoadingCarts::new, (compound, lookup) -> new LoadingCarts().readNbt(compound), null);//thanks, FAPI
    /*? if >=1.21.5 {*/
    /*private static final Codec<LoadingCarts> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BlockPos.CODEC.listOf().fieldOf("chunksToSave").forGetter(carts -> carts.chunksToReload.stream().toList())
        ).apply(instance, LoadingCarts::new)
    );

    private static final PersistentStateType<LoadingCarts> TYPE = new PersistentStateType<LoadingCarts>(
        "linkart_loading_carts", LoadingCarts::new, CODEC, null
    );
    *//*?}*/

    public static LoadingCarts getOrCreate(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(TYPE/*? if <1.21.5 {*/, "linkart_loading_carts"/*?}*/);
    }

    private final Set<BlockPos> chunksToReload = new HashSet<>();
    private final Set<AbstractMinecartEntity> cartsToBlockPos = new HashSet<>();

    public LoadingCarts() { this(List.of()); }

    public LoadingCarts(Collection<BlockPos> chunksToReload) {
        this.chunksToReload.addAll(chunksToReload);
    }

    /*? if <1.21.5 {*/
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();
        for (AbstractMinecartEntity minecart : cartsToBlockPos) {
            if (!minecart.isRemoved()) list.add(NbtLong.of(minecart.getBlockPos().asLong()));
        }
        nbt.put("chunksToSave", list);
        cartsToBlockPos.clear();
        return nbt;
    }

    public LoadingCarts readNbt(NbtCompound nbt) {
        NbtList list = nbt.getList("chunksToSave", NbtElement.LONG_TYPE);
        for (NbtElement element : list) {
            chunksToReload.add(BlockPos.fromLong(((NbtLong) element).longValue()));
        }
        return this;
    }
    /*?}*/

    public void tick(ServerWorld world) {
        if (!chunksToReload.isEmpty()) {
            for (BlockPos pos : chunksToReload) {
                ChunkPos chunkPos = new ChunkPos(pos);
                world.getChunkManager().addTicket(ChunkTicketType.PORTAL, chunkPos, 4/*? if <1.21.5 {*/, pos/*?}*/);
            }
            chunksToReload.clear();
            markDirty();
        }
    }


    public void addCart(AbstractMinecartEntity cart) {
        cartsToBlockPos.add(cart);
        markDirty();
    }

    public void removeCart(AbstractMinecartEntity cart) {
        cartsToBlockPos.remove(cart);
        markDirty();
    }
}
