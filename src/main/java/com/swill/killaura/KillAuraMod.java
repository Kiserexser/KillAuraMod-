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
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class KillAuraMod implements ClientModInitializer {

    private static boolean enabled = false;
    private static double radius = 4.0;
    private static int cooldown = 0;
    private static final Random random = new Random();

    @Override
    public void onInitializeClient() {
        KeyBinding toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "KillAura (R)", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "KillAura"));
        KeyBinding radiusUp = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Radius +", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_ADD, "KillAura"));
        KeyBinding radiusDown = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Radius -", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_SUBTRACT, "KillAura"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            if (toggleKey.wasPressed()) {
                enabled = !enabled;
                client.player.sendMessage(Text.literal("§7[§cKA§7] " + (enabled ? "§aON" : "§cOFF") + " §7Radius: " + radius), true);
            }
            if (radiusUp.wasPressed()) {
                radius = Math.min(10.0, radius + 0.5);
                client.player.sendMessage(Text.literal("§7[§cKA§7] Radius: §e" + radius), true);
            }
            if (radiusDown.wasPressed()) {
                radius = Math.max(3.0, radius - 0.5);
                client.player.sendMessage(Text.literal("§7[§cKA§7] Radius: §e" + radius), true);
            }

            if (!enabled) return;

            if (cooldown > 0) {
                cooldown--;
                return;
            }

            Entity target = null;
            double closestDist = radius;

            for (Entity entity : client.world.getEntities()) {
                if (entity == client.player) continue;
                if (!(entity instanceof LivingEntity)) continue;
                if (entity instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) continue;

                double dist = client.player.distanceTo(entity);
                if (dist <= closestDist) {
                    closestDist = dist;
                    target = entity;
                }
            }

            if (target != null) {
                // Атака без поворота прицела
                client.interactionManager.attackEntity(client.player, target);
                client.player.swingHand(Hand.MAIN_HAND);
                cooldown = 4 + random.nextInt(3);
            }
        });
    }
}
