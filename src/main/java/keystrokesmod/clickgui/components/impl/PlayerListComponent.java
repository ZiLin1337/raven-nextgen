package keystrokesmod.clickgui.components.impl;

import keystrokesmod.clickgui.animation.ScrollOffsetAnimation;
import keystrokesmod.utility.Theme;
import keystrokesmod.module.setting.impl.PlayerListSetting;
import keystrokesmod.utility.PlayerRelationsManager;
import keystrokesmod.utility.PlayerSkinCache;
import keystrokesmod.utility.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListComponent extends AbstractTextInputComponent {
    private static final String CLOSE_ICON_PATH = "/assets/keystrokesmod/textures/gui/close.png";
    private static final float SELECTED_LIST_GAP = 4f;
    private static final int MAX_VISIBLE_SELECTED = 7;
    private static final int CLOSE_SIZE = 6;
    private static final float CLOSE_PAD = 3f;
    private static final float HEAD_SIZE = 8f;
    private static final float LIST_ROW_TEXT_SCALE = 0.56f;
    private static final float LIST_ROW_TEXT_Y_OFFSET = LIST_ROW_TEXT_SCALE;
    private static final float LIST_ROW_VISUAL_HEIGHT = ROW_HEIGHT - 1f;
    private final PlayerListSetting setting;
    private final ScrollOffsetAnimation selectedScrollAnim = new ScrollOffsetAnimation(200);

    private float lastMouseX;
    private float lastMouseY;

    public PlayerListComponent(PlayerListSetting setting, ModuleComponent moduleComponent, float o) {
        super(moduleComponent, o, setting.getName(), 128);
        this.setting = setting;
    }

    @Override
    public void render() {
        Layout layout = layout(false);
        renderLabel(layout, setting.getName());
        renderTextField(layout);
        renderSelectedEntries(layout);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        clampSelectedScroll();
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button) {
        if (!moduleComponent.isOpened || !moduleComponent.isVisible(this)) {
            return false;
        }
        Layout layout = layout(true);
        if (button == 0 && isTextFieldClicked(mouseX, mouseY, layout)) {
            setTextFieldFocused(true);
            return true;
        }
        if (button == 0 && handleSelectedEntryClick(mouseX, mouseY, layout)) {
            return true;
        }
        if (isTextFieldFocused()) {
            getTextField().setText("");
            setTextFieldFocused(false);
        }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!moduleComponent.isOpened || !isTextFieldFocused()) {
            return;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            getTextField().setText("");
            setTextFieldFocused(false);
            return;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            submitText();
            setTextFieldFocused(false);
            return;
        }
        getTextField().textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void onScroll(int scroll) {
        if (!moduleComponent.isOpened || !moduleComponent.isVisible(this)) {
            return;
        }
        if (!capturesCategoryScroll(lastMouseX, lastMouseY)) {
            return;
        }
        float delta = (float) MinecraftClient.getInstance().mouse.getDWheel() * (scroll / 120f);
        if (delta != 0f) {
            selectedScrollAnim.extend(-delta);
        }
        clampSelectedScroll();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        getTextField().setText("");
        selectedScrollAnim.reset(0f);
    }

    @Override
    public float getHeightF() {
        int count = setting.getPlayers().size();
        float selectedHeight = count == 0 ? 0f : SELECTED_LIST_GAP + Math.min(MAX_VISIBLE_SELECTED, count) * ROW_HEIGHT;
        return (2f * ROW_HEIGHT) + selectedHeight;
    }

    @Override
    public boolean isBaseVisible() {
        return setting.visible;
    }

    @Override
    public String getGroupName() {
        return setting.getGroup() != null ? setting.getGroup().getName() : "";
    }

    public boolean capturesCategoryScroll(float mouseX, float mouseY) {
        return setting.getPlayers().size() > MAX_VISIBLE_SELECTED && isMouseOverSelectedList(mouseX, mouseY);
    }

    public boolean containsClick(int mouseX, int mouseY) {
        Layout layout = layout(true);
        return isTextFieldClicked(mouseX, mouseY, layout) || isMouseOverSelectedList(mouseX, mouseY);
    }

    public void onExternalDataChanged() {
        clampSelectedScroll();
    }

    private void submitText() {
        String typedName = getTextField().getText();
        if (typedName == null || typedName.trim().isEmpty()) {
            return;
        }
        if (setting.addPlayer(typedName) != null) {
            getTextField().setText("");
        }
        moduleComponent.updateSettingPositions();
        clampSelectedScroll();
    }

    private void renderSelectedEntries(Layout layout) {
        List<String> entries = setting.getPlayers();
        if (entries.isEmpty()) {
            return;
        }
        float selectedTop = getSelectedTop(layout);
        float offsetPx = selectedScrollAnim.getValue();
        int firstRow = (int) (offsetPx / ROW_HEIGHT);
        int end = Math.min(firstRow + MAX_VISIBLE_SELECTED + 1, entries.size());
        Map<String, PlayerListEntry> playerInfoMap = getPlayerInfoMap();
        for (int i = firstRow; i < end; i++) {
            String entry = entries.get(i);
            float rowTop = selectedTop - offsetPx + i * ROW_HEIGHT;
            int bg = (i % 2 == 0) ? 0xFF1A1A2A : 0xFF1E1E2E;
            renderEntryRow(entry, playerInfoMap.get(entry), layout.left, layout.right, rowTop, bg);
        }
    }

    private void renderEntryRow(PlayerRelationsManager.PlayerEntry entry, PlayerListEntry playerInfo, float left, float right, float rowTop, int bgColor) {
        RenderUtils.drawRect(left, rowTop, right, rowTop + ROW_HEIGHT - 1f, bgColor);
        renderPlayerHead(entry, playerInfo, left + 2f, rowTop + (LIST_ROW_VISUAL_HEIGHT - HEAD_SIZE) / 2f);
        drawScaledTextNoShadow(entry, left + 13f, centeredScaledTextY(rowTop, LIST_ROW_VISUAL_HEIGHT, LIST_ROW_TEXT_SCALE) + LIST_ROW_TEXT_Y_OFFSET, 0xFFCCCCCC);
        renderCloseIcon(right, rowTop);
    }

    private boolean handleSelectedEntryClick(int mouseX, int mouseY, Layout layout) {
        List<String> entries = setting.getPlayers();
        float offsetPx = selectedScrollAnim.getValue();
        for (int i = 0; i < entries.size(); i++) {
            float rowTop = getSelectedTop(layout) - offsetPx + i * ROW_HEIGHT;
            if (isOverClose(mouseX, mouseY, rowTop, layout.right)) {
                setting.removePlayer(entries.get(i).getKey());
                moduleComponent.updateSettingPositions();
                clampSelectedScroll();
                return true;
            }
        }
        return false;
    }

    private void renderPlayerHead(PlayerRelationsManager.PlayerEntry entry, PlayerListEntry playerInfo, float x, float y) {
        Identifier skin = getSkin(entry, playerInfo);
        if (skin == null) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean depthEnabled = false;
        boolean blendEnabled = false;
        try {
            if (!depthEnabled) RenderSystem.disableDepthTest();
            if (!blendEnabled) RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, skin);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            // Simplified head rendering
            RenderUtils.drawRect((int) x, (int) y, (int) x + (int) HEAD_SIZE, (int) y + (int) HEAD_SIZE, 0xFFFFFFFF);
        } finally {
            if (!depthEnabled) RenderSystem.enableDepthTest();
            if (!blendEnabled) RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }

    private Identifier getSkin(PlayerRelationsManager.PlayerEntry entry, PlayerListEntry playerInfo) {
        return PlayerSkinCache.getSkin(entry, playerInfo);
    }

    private Map<String, PlayerListEntry> getPlayerInfoMap() {
        Map<String, PlayerListEntry> playerInfoMap = new HashMap<>();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getNetworkHandler() == null) {
            return playerInfoMap;
        }
        for (PlayerListEntry playerInfo : mc.getNetworkHandler().getPlayerList()) {
            if (playerInfo == null) continue;
            com.mojang.authlib.GameProfile profile = playerInfo.getProfile();
            if (profile == null || profile.getName() == null) continue;
            playerInfoMap.put(profile.getName().toLowerCase(), playerInfo);
        }
        return playerInfoMap;
    }

    private void clampSelectedScroll() {
        int count = setting.getPlayers().size();
        float maxScrollPx = Math.max(0f, (count - MAX_VISIBLE_SELECTED) * ROW_HEIGHT);
        selectedScrollAnim.clampTarget(0f, maxScrollPx);
        if (selectedScrollAnim.getValue() > maxScrollPx) {
            selectedScrollAnim.reset(maxScrollPx);
        }
    }

    private boolean isMouseOverSelectedList(float mouseX, float mouseY) {
        List<String> entries = setting.getPlayers();
        if (entries.isEmpty()) return false;
        Layout layout = layout(true);
        float selectedTop = getSelectedTop(layout);
        float selectedHeight = getSelectedVisibleHeight(entries.size());
        return mouseX >= layout.left && mouseX <= layout.right && mouseY >= selectedTop && mouseY < selectedTop + selectedHeight;
    }

    private float getSelectedTop(Layout layout) {
        return layout.contentTop + SELECTED_LIST_GAP;
    }

    private float getSelectedVisibleHeight(int count) {
        return Math.min(MAX_VISIBLE_SELECTED, count) * ROW_HEIGHT;
    }

    private void renderCloseIcon(float right, float rowTop) {
        Identifier close = RenderUtils.getIcon(CLOSE_ICON_PATH);
        if (close == null) return;
        float closeX = right - CLOSE_SIZE - CLOSE_PAD;
        float closeY = rowTop + (LIST_ROW_VISUAL_HEIGHT - CLOSE_SIZE) / 2f;
        int closeColor = Theme.getGradient(Theme.hiddenBind[0], Theme.hiddenBind[1], 0);
        RenderUtils.drawIcon(close, closeX, closeY, CLOSE_SIZE, closeColor);
    }

    private boolean isOverClose(float mouseX, float mouseY, float rowTop, float right) {
        float closeX = right - CLOSE_SIZE - CLOSE_PAD;
        float closeY = rowTop + (LIST_ROW_VISUAL_HEIGHT - CLOSE_SIZE) / 2f;
        return mouseX >= closeX && mouseX <= closeX + CLOSE_SIZE && mouseY >= closeY && mouseY <= closeY + CLOSE_SIZE;
    }

    private static void drawScaledTextNoShadow(String text, float x, float y, int color) {
        drawScaledText(text, x, y, color, LIST_ROW_TEXT_SCALE);
    }
}
