#!/usr/bin/env python3
import os

root = 'src/main/java/keystrokesmod'
fixed = []

def do_fix(fp, old, new):
    p = os.path.join(root, fp)
    with open(p) as f: c = f.read()
    if old in c:
        c = c.replace(old, new)
        with open(p, 'w') as f: f.write(c)
        fixed.append(fp)
        print(f'FIXED: {fp}')
    else:
        print(f'  (not found in {fp})')

# === BridgeInfo.java ===
do_fix('module/impl/minigames/BridgeInfo.java',
    '() ->)) {',
    '() -> {')

# === SpeedBuilders.java ===
do_fix('module/impl/minigames/SpeedBuilders.java',
    'stripped.endsWith("%)") {',
    'stripped.endsWith("%)")) {')

# === Indicators.java ===
do_fix('module/impl/render/Indicators.java',
    'block.canCollideCheck(blockState, false) {',
    'block.canCollideCheck(blockState, false)) {')

# === BreakProgress.java ===
do_fix('module/impl/render/BreakProgress.java',
    'Utils.round((double) ((1.0f - this.progress) / BlockUtils.getBlockHardness(BlockUtils.getBlockState(this).block)), mc.player.getHeldItem(), false, false) / 20.0, 1)',
    'Utils.round((double) ((1.0f - this.progress) / BlockUtils.getBlockHardness(BlockUtils.getBlockState(this).block)), mc.player.getHeldItem(), false, false) / 20.0, 1);')

# === ProfileUtils.java ===
do_fix('utility/ProfileUtils.java',
    '} else if c.equals(") {\\"success\\":true,\\"player\\":\\"}") {',
    '} else if (c.equals(") {\\"success\\":true,\\"player\\":\\"}")) {')

# === Request.java ===
fp = os.path.join(root, 'script/model/Request.java')
with open(fp) as f: c = f.read()
c = c.replace('try (OutputStream os = con.getOutputStream() {', 'try (OutputStream os = con.getOutputStream()) {')
with open(fp, 'w') as f: f.write(c)
print(f'FIXED: script/model/Request.java')

# === Utils.java ===
fp = os.path.join(root, 'utility/Utils.java')
with open(fp) as f: c = f.read()
c = c.replace('switch (Character.toLowerCase(formatCode) {', 'switch (Character.toLowerCase(formatCode)) {')
c = c.replace("if ch == ') {') {", "if (ch == ') {') {")
c = c.replace('if braceLevel == 0 && !processedLine.contains(") {") && !processedLine.contains("}") && !processedLine.startsWith("@") {', 'if (braceLevel == 0 && !processedLine.contains(") {") && !processedLine.contains("}") && !processedLine.startsWith("@")) {')
c = c.replace('HitResult entityHit = RotationUtils.rayTracereach, 1.0f, new float[] ) { yaw, pitch }, null);', 'HitResult entityHit = RotationUtils.rayTrace(reach, 1.0f, new float[] { yaw, pitch }, null);')
with open(fp, 'w') as f: f.write(c)
print('FIXED: utility/Utils.java')

print(f'\nDone. Fixed {len(fixed) + 2} files.')
