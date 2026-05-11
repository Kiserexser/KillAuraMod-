package com.swill.killaura;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KillAuraMod implements ClientModInitializer {
    private static boolean enabled = false;

    @Override
    public void onInitializeClient() {
        KeyBinding key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "KillAura", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "KillAura"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (key.wasPressed()) enabled = !enabled;

            if (!enabled) return;

            for (Entity e : client.world.getEntities()) {
                if (e == client.player) continue;
                if (!(e instanceof LivingEntity)) continue;
                if (client.player.distanceTo(e) > 4.0) continue;
                client.interactionManager.attackEntity(client.player, e);
                break;
            }
        });
    }
}
