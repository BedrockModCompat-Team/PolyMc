/*
 * PolyMc
 * Copyright (C) 2020-2021 TheEpicBlock_TEB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package io.github.theepicblock.polymc.mixins.context.block;

import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.mixin.PlayerContextContainer;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This packet writes the raw id of the updated {@link BlockState} to itself.
 * Normally the remapping of that would be caught by {@link io.github.theepicblock.polymc.mixins.block.BlockPolyImplementation} but that method doesn't respect individuals their PolyMaps.
 * {@link io.github.theepicblock.polymc.mixins.context.NetworkHandlerContextProvider} will provide the player context to this packet via {@link #setPolyMcProvidedPlayer(ServerPlayerEntity)}
 */
@Mixin(BlockUpdateS2CPacket.class)
public class BlockUpdatePacketMixin implements PlayerContextContainer {
	@Shadow private BlockPos pos;
	@Unique
	private ServerPlayerEntity player;

	@Override
	public ServerPlayerEntity getPolyMcProvidedPlayer() {
		return player;
	}

	@Override
	public void setPolyMcProvidedPlayer(ServerPlayerEntity v) {
		player = v;
	}

	@Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRawIdFromState(Lnet/minecraft/block/BlockState;)I"))
	public int getRawIdFromStateRedirect(BlockState state) {
		return Util.getPolydRawIdFromState(state, this.player);
	}
}
