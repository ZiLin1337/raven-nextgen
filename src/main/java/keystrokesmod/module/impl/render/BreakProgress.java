package keystrokesmod.module.impl.render;

// Removed accessor
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.Utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.HitResult;



import org.lwjgl.opengl.GL11;

public class BreakProgress extends Module {
    private SliderSetting mode;
    private ButtonSetting manual;
    private ButtonSetting bedAura;
    private ButtonSetting fadeIn;

    private String[] MODES = new String[] { "Percentage", "Second", "Decimal" };

    private float progress;
    private BlockPos block;
    private String progressStr;

    public BreakProgress() {
        super("BreakProgress", category.render);
        this.registerSetting(mode = new SliderSetting("Mode", 0, MODES));
        this.registerSetting(manual = new ButtonSetting("Show manual", true));
        this.registerSetting(bedAura = new ButtonSetting("Show BedAura", true));
        this.registerSetting(fadeIn = new ButtonSetting("Fade in", false));
    }

    
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (this.progress == 0.0f || this.block == null || !Utils.nullCheck()) {
            return;
        }
        final double x = this.block.getX() + 0.5 - mc.getEntityRenderDispatcher().viewerPosX;
        final double y = this.block.getY() + 0.5 - mc.getEntityRenderDispatcher().viewerPosY;
        final double z = this.block.getZ() + 0.5 - mc.getEntityRenderDispatcher().viewerPosZ;
        RenderSystem.pushMatrix();
        RenderSystem.translate((float) x, (float) y, (float) z);
        RenderSystem.rotate(-mc.getEntityRenderDispatcher().playerViewY, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotate((mc.options.thirdPersonView == 2 ? -1 : 1) * mc.getEntityRenderDispatcher().playerViewX, 1.0f, 0.0f, 0.0f);
        RenderSystem.scale(-0.02266667f, -0.02266667f, -0.02266667f);
        RenderSystem.depthMask(false);
        RenderSystem.disableDepth();
        RenderSystem.enableBlend(GL11.GL_BLEND);
        int colorAlpha = Utils.mergeAlpha(-1, Math.max(10, (int) (255 * progress)));
        mc.textRenderer.drawString(this.progressStr, (float) (-mc.textRenderer.getStringWidth(this.progressStr) / 2), -3.0f, fadeIn.isToggled() ? colorAlpha : -1, true);
        RenderSystem.disableBlend(GL11.GL_BLEND);
        RenderSystem.enableDepth();
        RenderSystem.depthMask(true);
        RenderSystem.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.popMatrix();
    }

    private void setProgress() {
        switch ((int) mode.getInput()) {
            case 0: {
                this.progressStr = (int) (100.0 * (this.progress / 1.0)) + "%";
                break;
            }
            case 1: {
                double timeLeft = Utils.round((double) ((1.0f - this.progress) / BlockUtils.getBlockHardness(BlockUtils.getBlockState(this).block)), mc.player.getHeldItem(), false, false) / 20.0, 1);
                this.progressStr = timeLeft == 0 ? "0" : timeLeft + "s";
                break;
            }
            case 2: {
                this.progressStr = String.valueOf(Utils.round(this.progress, 2));
                break;
            }
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player.capabilities.isCreativeMode || !mc.player.capabilities.allowEdit) {
            this.resetVariables();
            return;
        }
        if (bedAura.isToggled() && ModuleManager.bedAura != null && ModuleManager.bedAura.isEnabled()) {
            BlockPos ap = ModuleManager.bedAura.getAuraTargetPos();
            float bp = ModuleManager.bedAura.getAuraBreakProgress();
            if (ap != null && bp > 0.0f) {
                this.progress = Math.min(1.0f, bp);
                this.block = ap;
                this.setProgress();
                return;
            }
        }
        if (!manual.isToggled() || mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != HitResult.MovingObjectType.BLOCK) {
            this.resetVariables();
            return;
        }
        this.progress = ((IAccessorPlayerControllerMP) mc.interactionManager).getCurBlockDamageMP();
        if (this.progress == 0.0f) {
            this.resetVariables();
            return;
        }
        this.block = mc.objectMouseOver.getBlockPos();
        this.setProgress();
    }

    @Override
    public void onDisable() {
        this.resetVariables();
    }

    private void resetVariables() {
        this.progress = 0.0f;
        this.block = null;
        this.progressStr = "";
    }
}
