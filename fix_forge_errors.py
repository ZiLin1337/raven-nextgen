#!/usr/bin/env python3
import os, re

base = '/home/raven-nextgen/src/main/java/keystrokesmod'
fixed = 0

for root, dirs, files in os.walk(base):
    for fname in files:
        if not fname.endswith('.java'):
            continue
        path = os.path.join(root, fname)
        with open(path, 'r') as f:
            orig = f.read()
        c = orig

        # 1. Remove Forge/LWJGL2 imports that don't exist in Fabric 1.21.4
        forge_imports = [
            'import net.minecraft.client.renderer.EntityRenderer;',
            'import net.minecraft.client.settings.KeyBinding;',
            'import org.lwjgl.input.Mouse;',
            'import net.minecraft.client.shader.Framebuffer;',
            'import net.minecraft.entity.item.EntityArmorStand;',
            'import net.minecraftforge.client.event.ClientChatReceivedEvent;',
            'import net.minecraftforge.fml.client.config.GuiButtonExt;',
            'import net.minecraftforge.fml.common.gameevent.TickEvent;',
            'import net.minecraftforge.fml.common.gameevent.TickEvent.*;',
            'import net.minecraftforge.client.event.RenderWorldLastEvent;',
            'import net.minecraftforge.event.entity.EntityJoinWorldEvent;',
        ]
        for imp in forge_imports:
            c = c.replace(imp + '\n', '')
            c = c.replace(imp, '')

        # 2. Remove all remaining forge imports
        c = re.sub(r'import\s+net\.minecraftforge[^;]*;\n?', '', c)

        # 3. Fix Mouse references -> use Fabric's GLFW
        c = c.replace('Mouse.isButtonDown(0)', 'GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS')
        c = c.replace('Mouse.isButtonDown(1)', 'GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS')

        # 4. Fix Framebuffer references
        c = c.replace('Framebuffer', 'net.minecraft.client.gl.Framebuffer')

        # 5. Fix KeyBinding references
        c = c.replace('KeyBinding.setKeyBindState', 'InputUtil.setKeyPressed')

        # 6. Fix duplicate HUD class - if in player directory, rename or remove
        if 'player/HUD.java' in path:
            # Check if there's already a HUD in render
            render_hud = os.path.join(base, 'module/impl/render/HUD.java')
            if os.path.exists(render_hud):
                # This is a duplicate, rename to PlayerHUD
                c = c.replace('public class HUD extends Module', 'public class PlayerHUD extends Module')
                # Update filename
                new_path = path.replace('HUD.java', 'PlayerHUD.java')
                os.rename(path, new_path)
                path = new_path

        # 7. Fix ItemStackModel file naming issue
        if 'ItemStack.java' in fname:
            if 'public class ItemStackModel' in c:
                # Rename file to ItemStackModel.java
                new_path = path.replace('ItemStack.java', 'ItemStackModel.java')
                os.rename(path, new_path)
                path = new_path

        # 8. Fix duplicate MinecraftFontAdapter - if it's in a separate file, remove it
        if 'MinecraftFontAdapter' in c and 'class MinecraftFontAdapter' in c:
            # Check if this is a standalone file for the adapter
            if fname == 'MinecraftFontAdapter.java':
                # This is the standalone file, it conflicts with the inner class
                # Remove this file
                os.remove(path)
                print(f'Removed duplicate: {fname}')
                fixed += 1
                continue

        if c != orig:
            with open(path, 'w') as f:
                f.write(c)
            fixed += 1
            print(f'Fixed: {fname}')

print(f'\nTotal: {fixed} files')