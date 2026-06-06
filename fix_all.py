#!/usr/bin/python3
import os, re, sys

base = '/home/raven-nextgen/src/main/java/keystrokesmod'
fixed = 0
files_to_check = []

for root, dirs, files in os.walk(base):
    for fname in files:
        if not fname.endswith('.java'): continue
        path = os.path.join(root, fname)
        files_to_check.append(path)

for path in files_to_check:
    with open(path, 'r') as f:
        orig = f.read()
    c = orig

    # 1. Remove (priority = X) lines
    c = re.sub(r'^\s*\(priority\s*=\s*[A-Z_]+\).*$', '', c, flags=re.MULTILINE)

    # 2. Remove all forms of mangled ScaledResolution
    c = re.sub(r'import[^;]*(?:ScaledResolution|Object\s*/\*)[^;]*;', '', c)
    c = re.sub(r'Object\s*/\*[^*]*\*/\s*[A-Za-z]*\s*/\*[^*]*\*/\s*removed\s*\*+/', 'int', c)
    c = re.sub(r'Object\s*/\*.*?ScaledResolution.*?\*/', 'int', c)
    c = re.sub(r'/\*.*?ScaledResolution.*?\*/', '', c)

    # 3. Fix new int(mc) patterns - most common error
    # standalone: final int(mc); or int(mc);
    c = re.sub(r'final\s+int\s*\(\s*mc\s*\)\s*;', '', c)
    c = re.sub(r'\n\s*int\s*\(\s*mc\s*\)\s*;', '\n', c)
    c = re.sub(r'\n\s*int\s*\(\s*this\.mc\s*\)\s*;', '\n', c)
    c = re.sub(r'\n\s*int\s*\(\s*MinecraftClient\.getInstance\(\)\s*\)\s*;', '\n', c)

    # as argument: , new int(mc))
    c = re.sub(r',\s*new\s+int\s*\(\s*mc\s*\)\s*\)', ')', c)

    # syncPosition(new int(mc))
    c = re.sub(r'syncPosition\s*\(\s*new\s+int\s*\(\s*mc\s*\)\s*\)', 'syncPosition()', c)

    # new int(mc).getScaleFactor()
    c = re.sub(r'new\s+int\s*\(\s*mc\s*\)\s*\.\s*getScaleFactor\s*\(\s*\)', 'MinecraftClient.getInstance().getWindow().getScaleFactor()', c)
    c = re.sub(r'new\s+int\s*\(\s*MinecraftClient\.getInstance\(\)\s*\)\s*\.\s*getScaleFactor\s*\(\s*\)', 'MinecraftClient.getInstance().getWindow().getScaleFactor()', c)
    c = re.sub(r'final\s+int\s+scaleFactor\s*=\s*new\s+int\s*\([^)]*\)\s*\.\s*getScaleFactor\s*\(\)', 'final int scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor()', c)

    # 4. Fix int(this.mc); int x = res.getScaledWidth() -> window API
    c = re.sub(r'int\(\s*this\.mc\s*\);\s*\n\s*int\s+x\s*=\s*res\.getScaledWidth\(\)\s*/\s*2\s*-\s*\d+;', 'int x = mc.getWindow().getScaledWidth() / 2 - 84;', c)
    c = re.sub(r'int\(\s*mc\s*\);\s*\n\s*int\s+x\s*=\s*res\.getScaledWidth\(\)\s*/\s*2\s*-\s*\d+;', 'int x = mc.getWindow().getScaledWidth() / 2 - 84;', c)
    c = re.sub(r'res\.getScaledWidth\(\)', 'mc.getWindow().getScaledWidth()', c)
    c = re.sub(r'res\.getScaledHeight\(\)', 'mc.getWindow().getScaledHeight()', c)

    # 5. final int(mc); or final int scaledResolution = null;
    c = re.sub(r'final\s+int\s+scaledResolution\s*=\s*;', '', c)
    c = re.sub(r'final\s+scaledResolution\s*=\s*;', '', c)
    c = re.sub(r'final\s+int\s*\(\s*mc\s*\)\s*;', '', c)

    # 6. Remove " /* int" (unclosed comment)
    c = re.sub(r',\s*/\*\s*int\s*\)', ')', c)
    c = re.sub(r',\s*\s*/\*\s*int\s*\)', ')', c)
    c = re.sub(r',\s*\s*/\*\s*int\s*', '', c)

    # 7. Fix method params with bare int (missing type)
    c = re.sub(r'void\s+\w+\s*\(\s*int\s+mc\s*\)', 'void method0(int mc)', c)
    c = re.sub(r'void\s+\w+\s*\(\s*,\s*', 'void method0(int param1, ', c)
    c = re.sub(r',\s*\)\s*\{', ') {', c)

    # 8. Fix PotionHUD unclosed comment
    c = re.sub(r'/\*\s*int\s*\)', ')', c)
    # Fix any remaining /* comment that's unclosed
    if 'PotionHUD' in path:
        c = re.sub(r'/\*[^*]*$', '', c)

    # 9. Minecraft.getMinecraft()
    c = c.replace('Minecraft.getMinecraft()', 'MinecraftClient.getInstance()')

    # 10. AutoRequeue fix
    c = c.replace('Pattern.compile("/([\\\\S]+)\\\\s*")', 'Pattern.compile("/([^\\\\s]+)\\\\s*")')

    # 11. Remove Forge imports
    c = re.sub(r'import net\.minecraftforge[^;]*;\n?', '', c)

    if c != orig:
        with open(path, 'w') as f:
            f.write(c)
        fixed += 1
        print(f'Fixed: {fname}')

print(f'\nTotal: {fixed} files')
