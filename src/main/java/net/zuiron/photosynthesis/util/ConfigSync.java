package net.zuiron.photosynthesis.util;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.zuiron.photosynthesis.Photosynthesis;
import net.zuiron.photosynthesis.config.ModConfig;
import net.zuiron.photosynthesis.networking.ModMessages;

public class ConfigSync {
    public static void syncConfig(ModConfig serverConfig, ServerPlayerEntity player) {
        Photosynthesis.LOGGER.info(player.getName().getString()+": Preparing server config 2 Client Packet...");
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(serverConfig.daysPerSeason);
        buffer.writeBoolean(serverConfig.thirst);
        buffer.writeBoolean(serverConfig.seasons);
        ServerPlayNetworking.send(player, ModMessages.CONFIG_SYNC_ID, buffer);
    }
}
