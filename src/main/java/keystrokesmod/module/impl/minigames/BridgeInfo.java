package keystrokesmod.module.impl.minigames;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.io.IOException;
import java.util.Iterator;

public class BridgeInfo extends Module {
    private final int textRGB = new Color(0, 200, 200).getRGB();
    // message types
    private final String bridge = "the brid";
    private final String start = "Defend!";
    private final String start2 = "Jump in to score!";
    private final String qt = "First player to score 5 goals wins";
    private final String enemyText = "Enemy: ";
    private final String distance = "Distance to goal: ";
    private final String enemyDistance = "Enemy distance to goal: ";
    private final String blocks = "Blocks: ";

    private static int hudX = 5;
    private static int hudY = 70;

    private String enemyName = "";
    private BlockPos g1p = null;
    private BlockPos g2p = null;

    private boolean q = false;
    private double d1 = 0.0D;
    private double d2 = 0.0D;
    private int blc = 0;

    public BridgeInfo() {
        super("Bridge Info", category.minigames, 0);
        this.registerSetting(new DescriptionSetting(new String("Only for solos.")));
        this.registerSetting(new ButtonSetting("Edit position", () -> {
            mc.setScreen(new EditScreen());
        }));
    }

    @Override
    public void onDisable() {
        this.reset();
    }

    @Override
    public void onUpdate() {
        if (!this.enemyName.isEmpty() && this.isBridge()) {
            PlayerEntity enem = null;
            Iterator var2 = mc.world.loadedEntityList.iterator();

            while (var2.hasNext()) {
                Entity e = (Entity) var2.next();
                if (e instanceof PlayerEntity) {
                    if (e.getName().equals(this.enemyName)) {
                        enem = (PlayerEntity) e;
                    }
                } else if (e instanceof ArmorStandEntity) {
                    if (e.getName().contains(this.start)) {
                        this.g1p = e.getPosition();
                    }
                    else if (e.getName().contains(this.start2)) {
                        this.g2p = e.getPosition();
                    }
                }
            }

            if (this.g1p != null && this.g2p != null) {
                this.d1 = Utils.round(mc.player.getDistance((double) this.g2p.getX(), (double) this.g2p.getY(), (double) this.g2p.getZ()) - 1.4D, 1);
                if (this.d1 < 0.0D) {
                    this.d1 = 0.0D;
                }

                this.d2 = enem == null ? 0.0D : Utils.round(enem.getDistance((double) this.g1p.getX(), (double) this.g1p.getY(), (double) this.g1p.getZ()) - 1.4D, 1);
                if (this.d2 < 0.0D) {
                    this.d2 = 0.0D;
                }
            }

            int blc2 = 0;

            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock().equals(Blocks.TERRACOTTA)) {
                    blc2 += stack.stackSize;
                }
            }

            this.blc = blc2;
        }
    }

    
    public void onRenderTick(Object ev) {
        if (ev.phase == Phase.END && Utils.nullCheck() && this.isBridge()) {
            if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
                return;
            }

            mc.textRenderer.drawString(this.enemyText + this.enemyName, (float) hudX, (float) hudY, textRGB, true);
            mc.textRenderer.drawString(this.distance + this.d1, (float) hudX, (float) (hudY + 11), textRGB, true);
            mc.textRenderer.drawString(this.enemyDistance + this.d2, (float) hudX, (float) (hudY + 22), textRGB, true);
            mc.textRenderer.drawString(this.blocks + this.blc, (float) hudX, (float) (hudY + 33), textRGB, true);
        }

    }

    
    public void onChat(Object c) {
        if (Utils.nullCheck()) {
            String s = Utils.stripColor(c.message.getString());
            if (s.startsWith(" ")) {
                if (s.contains(this.qt)) {
                    this.q = true;
                }
                else if (this.q && s.contains("Opponent:")) {
                    String n = s.split(":")[1].trim();
                    if (n.contains("[")) {
                        n = n.split("] ")[1];
                    }
                    this.enemyName = n;
                    this.q = false;
                }
            }
        }

    }

    
    public void onWorldJoin(Object e) {
        if (e.entity == mc.player) {
            this.reset();
        }
    }

    private boolean isBridge() {
        if (Utils.isHypixel()) {
            Iterator var1 = Utils.getScoreBoardOld().iterator();

            while (var1.hasNext()) {
                String s = (String) var1.next();
                String s2 = s.toLowerCase();
                if (s2.contains("mode") && s2.contains(bridge)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void reset() {
        this.enemyName = "";
        this.q = false;
        this.g1p = null;
        this.g2p = null;
        this.d1 = 0.0D;
        this.d2 = 0.0D;
        this.blc = 0;
    }

    private class EditScreen extends Screen {
        String example = "Enemy: Player123-Distance to goal: 17.2-Enemy distance to goal: 16.3-Blocks: 98";
        ButtonWidget resetPosition;
        boolean dragging = false;
        int minX = 0;
        int minY = 0;
        int maxX = 0;
        int maxY = 0;
        int anchorX = 5;
        int anchorY = 70;
        int lastAnchorX = 0;
        int lastAnchorY = 0;
        int lastMouseX = 0;
        int lastMouseY = 0;

        public EditScreen() {
            super(Text.literal("Bridge Info Editor"));
        }

        @Override
        protected void init() {
            super.init();
            this.resetPosition = ButtonWidget.builder(Text.literal("Reset position"), button -> {
                this.anchorX = BridgeInfo.hudX = 5;
                this.anchorY = BridgeInfo.hudY = 70;
            }).dimensions(this.width - 90, 5, 85, 20).build();
            this.addDrawableChild(this.resetPosition);
            this.anchorX = BridgeInfo.hudX;
            this.anchorY = BridgeInfo.hudY;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            int minX = this.anchorX;
            int minY = this.anchorY;
            int maxX = minX + 140;
            int maxY = minY + 41;
            this.renderExample(this.textRenderer, this.example);
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
            BridgeInfo.hudX = minX;
            BridgeInfo.hudY = minY;
            
            int x = this.width / 2 - 84;
            int y = this.height / 2 - 20;
            drawCenteredTextWithShadow(matrices, this.textRenderer, "Edit the HUD position by dragging.", x, y, 0xFFFFFF);
            
            super.render(matrices, mouseX, mouseY, delta);
        }

        private void renderExample(TextRenderer textRenderer, String text) {
            int x = this.minX;
            int y = this.minY;
            String[] lines = text.split("-");
            for (String line : lines) {
                textRenderer.drawStringWithShadow(line, (float) x, (float) y, textRGB);
                y += textRenderer.fontHeight + 2;
            }
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (button == 0) {
                if (this.dragging) {
                    this.anchorX = this.lastAnchorX + (int)(mouseX - this.lastMouseX);
                    this.anchorY = this.lastAnchorY + (int)(mouseY - this.lastMouseY);
                } else if (mouseX > this.minX && mouseX < this.maxX && mouseY > this.minY && mouseY < this.maxY) {
                    this.dragging = true;
                    this.lastMouseX = (int) mouseX;
                    this.lastMouseY = (int) mouseY;
                    this.lastAnchorX = this.anchorX;
                    this.lastAnchorY = this.anchorY;
                }
            }
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (button == 0) {
                this.dragging = false;
            }
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }
    }
}
