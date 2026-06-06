#!/usr/bin/env python3
import os, re

base = '/home/raven-nextgen/src/main/java/keystrokesmod'
fixed = 0

# 1.8.9 -> 1.21.4 Fabric class/package mappings
mappings = [
    ('net.minecraft.entity.player.EntityPlayer', 'net.minecraft.entity.player.PlayerEntity'),
    ('net.minecraft.entity.EntityLivingBase', 'net.minecraft.entity.LivingEntity'),
    ('net.minecraft.entity.EntityCreature', 'net.minecraft.entity.mob.MobEntity'),
    ('net.minecraft.util.Vec3', 'net.minecraft.util.math.Vec3d'),
    ('net.minecraft.util.MovingObjectPosition', 'net.minecraft.util.hit.HitResult'),
    ('net.minecraft.launchwrapper.Launch', ''),
    ('net.minecraft.launchwrapper', ''),
    ('net.minecraft.network.play.server.S12PacketEntityVelocity', 'net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket'),
    ('net.minecraft.network.play.server.S27PacketExplosion', 'net.minecraft.network.packet.s2c.play.ExplosionS2CPacket'),
    ('net.minecraft.block.BlockLiquid', 'net.minecraft.block.LiquidBlock'),
    ('net.minecraft.client.settings.KeyBinding', 'net.minecraft.client.option.KeyBinding'),
    ('net.minecraft.client.renderer.EntityRenderer', ''),
    ('org.lwjgl.input.Mouse', ''),
    # entity.monster imports - they still exist but with different class names
    ('net.minecraft.entity.monster.EntityGiantZombie', 'net.minecraft.entity.monster.GiantZombieEntity'),
    ('net.minecraft.entity.monster.EntityIronGolem', 'net.minecraft.entity.monster.IronGolemEntity'),
    ('net.minecraft.entity.monster.EntityPigZombie', 'net.minecraft.entity.monster.ZombifiedPiglinEntity'),
    ('net.minecraft.entity.monster.EntitySilverfish', 'net.minecraft.entity.monster.SilverfishEntity'),
    # Remaining entity items
    ('net.minecraft.entity.item.EntityArmorStand', 'net.minecraft.entity.decoration.ArmorStandEntity'),
    ('net.minecraft.client.shader.Framebuffer', 'net.minecraft.client.gl.Framebuffer'),
    # Custom events that don't exist yet
    ('import keystrokesmod.event.PreKnockbackEvent;', '// import keystrokesmod.event.PreKnockbackEvent;'),
    ('import keystrokesmod.mixin.impl.accessor.IAccessorEntityRenderer;', '// import keystrokesmod.mixin.impl.accessor.IAccessorEntityRenderer;'),
]

# Entity class names that need in-code replacement (not just imports)
entity_rename = {
    'EntityPlayer': 'PlayerEntity',
    'EntityLivingBase': 'LivingEntity',
    'EntityCreature': 'MobEntity',
    'EntityGiantZombie': 'GiantZombieEntity',
    'EntityIronGolem': 'IronGolemEntity',
    'EntityPigZombie': 'ZombifiedPiglinEntity',
    'EntitySilverfish': 'SilverfishEntity',
    'EntityArmorStand': 'ArmorStandEntity',
}

for root, dirs, files in os.walk(base):
    for fname in files:
        if not fname.endswith('.java'): continue
        path = os.path.join(root, fname)
        with open(path, 'r') as f: orig = f.read()
        c = orig

        # Apply import/package mappings
        for old, new in mappings:
            if new:
                c = c.replace(f'import {old};', f'import {new};')
                c = c.replace(f'import {old}.\n', f'import {new}.\n')
            else:
                c = c.replace(f'import {old};', '')
                c = c.replace(f'import {old}.', '')

        # Apply entity class renames in usage (not import lines which we already handled)
        for old_name, new_name in entity_rename.items():
            # Replace in code only (references after import lines)
            lines = c.split('\n')
            in_imports = True
            for i, line in enumerate(lines):
                if not line.startswith('import ') and line.strip():
                    in_imports = False
                if not in_imports and old_name in line:
                    lines[i] = line.replace(old_name, new_name)
            c = '\n'.join(lines)

        # Fix MovingObjectPosition usage -> HitResult with .getType()
        c = c.replace('MovingObjectPosition', 'HitResult')

        # Fix Vec3 -> Vec3d in code
        c = re.sub(r'\bVec3\b(?!d)', 'Vec3d', c)

        # Fix Mouse references in non-import lines
        lines = c.split('\n')
        in_imports = True
        for i, line in enumerate(lines):
            if not line.startswith('import ') and not line.startswith('package ') and line.strip():
                in_imports = False
            if not in_imports and 'Mouse.' in line:
                lines[i] = lines[i].replace('Mouse.', '/* Mouse */ ')
        c = '\n'.join(lines)

        # Fix Framebuffer - ensure import path is correct
        if 'Framebuffer' in c and 'import net.minecraft.client.gl.Framebuffer' not in c:
            c = c.replace('import net.minecraft.client.shader.Framebuffer;', 'import net.minecraft.client.gl.Framebuffer;')

        # Remove launchwrapper refs
        c = re.sub(r'Launch\.classLoader[^;]*;', ';', c)

        if c != orig:
            with open(path, 'w') as f: f.write(c)
            fixed += 1
            print(f'Fixed: {fname}')

print(f'\nTotal: {fixed} files')
