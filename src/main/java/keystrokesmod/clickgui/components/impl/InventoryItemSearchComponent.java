package keystrokesmod.clickgui.components.impl;

import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.module.setting.impl.InventoryItemListSetting;
import keystrokesmod.utility.ItemSearchIndex;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.font.RavenTextRenderer;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class InventoryItemSearchComponent extends AbstractItemSearchComponent<InventoryItemListSetting> {
    private static final float SLOT_PILL_MIN_WIDTH = 9f;
    private static final float SLOT_PILL_HORIZONTAL_PAD = 3f;
    private static final float SLOT_PILL_TEXT_Y_OFFSET = 0.5f;
    private static final float SLOT_BOX_GAP = 3f;
    private static final float DRAG_SCROLL_EDGE = 10f;
    private static final float DRAG_SCROLL_SPEED = 3f;

    private static final class InventorySelectedRowData extends SelectedRowData {
        final Integer assignedSlot;
        private InventorySelectedRowData(String sid, String name, net.minecraft.item.ItemStack stack, List<net.minecraft.item.ItemStack> cycling, Integer slot) {
            super(sid, name, stack, cycling);
            this.assignedSlot = slot;
        }
    }

    private List<InventorySelectedRowData> selectedRowsCache;
    private String listeningStorageId;
    private String draggingStorageId;
    private float dragGrabOffsetY;

    public InventoryItemSearchComponent(InventoryItemListSetting setting, ModuleComponent moduleComponent, float o) {
        super(setting, moduleComponent, o);
    }

    @Override protected int getSelectedEntryCount() { return setting.getItems().size(); }

    @Override public void drawScreen(int mx, int my) { super.drawScreen(mx, my); updateDragState(); }
    @Override public void mouseReleased(int mx, int my, int btn) { if (btn == 0) draggingStorageId = null; }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!moduleComponent.isOpened) return;
        if (listeningStorageId != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) { listeningStorageId = null; return; }
            int slot = getHotbarSlotForKey(keyCode);
            if (slot != -1) {
                setting.setAssignedSlot(listeningStorageId, slot);
                listeningStorageId = null;
                invalidateSelectedRows();
                markUnsaved();
            }
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override protected boolean hasAdditionalTextInputFocus() { return listeningStorageId != null; }
    @Override protected void clearAdditionalTextInputFocus() { listeningStorageId = null; }
    @Override protected void onDropdownClickHandled(int mx, int my) { listeningStorageId = null; }
    @Override protected void onSearchFocusHandled(int mx, int my) { listeningStorageId = null; }
    @Override protected void onOutsideClick(int mx, int my, int btn) { listeningStorageId = null; }

    @Override
    protected void renderSelectedRows(Layout layout, float offsetPx, int firstRow, int end) {
        List<String> items = setting.getItems();
        if (selectedRowsCache == null || selectedRowsCache.size() != items.size()) {
            selectedRowsCache = new ArrayList<InventorySelectedRowData>();
            for (String sid : items) {
                List<ItemSearchIndex.ItemEntry> variants = ItemSearchIndex.isGroupedSelection(sid) ? ItemSearchIndex.getSelectionVariants(sid) : null;
                List<net.minecraft.item.ItemStack> cycling = null;
                if (variants != null && !variants.isEmpty()) { cycling = new ArrayList<>(); for (ItemSearchIndex.ItemEntry v : variants) cycling.add(v.toItemStack()); }
                selectedRowsCache.add(new InventorySelectedRowData(sid, ItemSearchIndex.getDisplayName(sid), ItemSearchIndex.getItemStack(sid), cycling, setting.getAssignedSlot(sid)));
            }
        }
        for (int i = firstRow; i < end; i++) {
            InventorySelectedRowData row = selectedRowsCache.get(i);
            float rowTop = getSelectedTop(layout) - offsetPx + i * ROW_HEIGHT;
            int bg = row.storageId.equals(draggingStorageId) ? 0xFF2A2A3C : ((i % 2 == 0) ? 0xFF1A1A2A : 0xFF1E1E2E);
            RenderUtils.DrawContextHelper.drawRect(layout.left, rowTop, layout.right, rowTop + ROW_HEIGHT - 1f, bg);
            renderItemInRow(getPreviewStack(row), layout.left + 2f, rowTop);
            drawListRowText(row.displayName != null ? row.displayName : "", layout.left + 13f, rowTop, 0xFFCCCCCC);
            float closeX = layout.right - CLOSE_SIZE - CLOSE_PAD;
            float slotRight = closeX - SLOT_BOX_GAP;
            float slotLeft = slotRight - getSlotPillWidth(row.storageId);
            boolean listening = row.storageId.equals(listeningStorageId);
            int fill = listening ? 0xFF35557A : 0xFF244966;
            float pillTop = rowTop + 2f;
            float pillBottom = rowTop + ROW_HEIGHT - 3f;
            RenderUtils.DrawContextHelper.drawRect(slotLeft, pillTop, slotRight, pillBottom, 0xFF11141C);
            RenderUtils.DrawContextHelper.drawRect(slotLeft + 1f, pillTop + 1f, slotRight - 1f, pillBottom - 1f, fill);
            String label = listening ? "..." : Integer.toString(row.assignedSlot != null ? row.assignedSlot : 1);
            RavenTextRenderer rend = Gui.getClickGuiSettingTextRenderer();
            float tw = rend.getStringWidth(label) * TEXT_SCALE;
            float tx = slotLeft + ((slotRight - slotLeft) - tw) / 2f;
            float ty = centeredScaledTextY(pillTop, pillBottom - pillTop) + SLOT_PILL_TEXT_Y_OFFSET;
            drawScaledText(label, tx, ty, 0xFFE8EEF5);
            renderCloseIcon(layout.right, rowTop);
        }
    }

    @Override
    protected boolean handleSelectedEntryClick(int mouseX, int mouseY, Layout layout) {
        int rowIndex = getSelectedRowIndex(mouseX, mouseY, layout);
        if (rowIndex < 0) { draggingStorageId = null; return false; }
        String sid = setting.getItems().get(rowIndex);
        float rowTop = getSelectedTop(layout) - selectedScrollAnim.getValue() + rowIndex * ROW_HEIGHT;
        if (isOverClose(mouseX, mouseY, rowTop, layout.right)) {
            setting.removeItem(sid); invalidateSelectedRows(); listeningStorageId = null; draggingStorageId = null;
            markUnsaved(); clampSelectedScroll(); updateDropdownAnimation(); moduleComponent.updateSettingPositions(); return true;
        }
        float sr = layout.right - CLOSE_SIZE - CLOSE_PAD - SLOT_BOX_GAP;
        float sl = sr - getSlotPillWidth(sid);
        if (mouseX >= sl && mouseX <= sr && mouseY >= rowTop + 1f && mouseY <= rowTop + ROW_HEIGHT - 1f) {
            listeningStorageId = sid; draggingStorageId = null; return true;
        }
        draggingStorageId = sid; dragGrabOffsetY = mouseY - rowTop;
        if (!isPointInSlotPill(mouseX, mouseY, layout)) listeningStorageId = null;
        return true;
    }

    @Override protected void invalidateSelectedRows() { selectedRowsCache = null; }
    @Override protected void onSearchStateReset() { listeningStorageId = null; draggingStorageId = null; }

    private void updateDragState() {
        if (draggingStorageId == null || setting.getItems().isEmpty()) return;
        Layout layout = layout(true);
        float selectedTop = getSelectedTop(layout);
        float selectedHeight = getSelectedVisibleHeight();
        if (selectedHeight <= 0f) { draggingStorageId = null; return; }
        if (setting.getItems().size() > MAX_VISIBLE_SELECTED && lastMouseX >= layout.left && lastMouseX <= layout.right) {
            if (lastMouseY < selectedTop + DRAG_SCROLL_EDGE) { selectedScrollAnim.extend(DRAG_SCROLL_SPEED); clampSelectedScroll(); }
            else if (lastMouseY > selectedTop + selectedHeight - DRAG_SCROLL_EDGE) { selectedScrollAnim.extend(-DRAG_SCROLL_SPEED); clampSelectedScroll(); }
        }
        List<String> ordered = setting.getItems();
        int curIdx = ordered.indexOf(draggingStorageId);
        if (curIdx < 0) { draggingStorageId = null; return; }
        float draggedRowCenter = (lastMouseY - dragGrabOffsetY) + ROW_HEIGHT / 2f;
        int desired = (int) Math.floor((draggedRowCenter - selectedTop + selectedScrollAnim.getValue()) / ROW_HEIGHT);
        desired = Math.max(0, Math.min(desired, ordered.size() - 1));
        if (desired != curIdx) { setting.moveItem(draggingStorageId, desired); invalidateSelectedRows(); markUnsaved(); }
    }

    private int getSelectedRowIndex(int mx, int my, Layout layout) {
        if (!isMouseOverSelectedList(mx, my)) return -1;
        int idx = (int) ((my - getSelectedTop(layout) + selectedScrollAnim.getValue()) / ROW_HEIGHT);
        if (idx < 0 || idx >= setting.getItems().size()) return -1;
        float rowTop = getSelectedTop(layout) - selectedScrollAnim.getValue() + idx * ROW_HEIGHT;
        return (my >= rowTop && my < rowTop + ROW_HEIGHT) ? idx : -1;
    }

    private boolean isPointInSlotPill(int mx, int my, Layout l) {
        int idx = getSelectedRowIndex(mx, my, l);
        if (idx < 0) return false;
        float rowTop = getSelectedTop(l) - selectedScrollAnim.getValue() + idx * ROW_HEIGHT;
        float sr = l.right - CLOSE_SIZE - CLOSE_PAD - SLOT_BOX_GAP;
        float sl = sr - getSlotPillWidth(setting.getItems().get(idx));
        return mx >= sl && mx <= sr && my >= rowTop + 1f && my <= rowTop + ROW_HEIGHT - 1f;
    }

    private float getSlotPillWidth(String sid) {
        RavenTextRenderer r = Gui.getClickGuiSettingTextRenderer();
        return Math.max(SLOT_PILL_MIN_WIDTH, r.getStringWidth(getSlotPillLabel(sid)) * TEXT_SCALE + SLOT_PILL_HORIZONTAL_PAD * 2f);
    }

    private String getSlotPillLabel(String sid) {
        if (sid != null && sid.equals(listeningStorageId)) return "...";
        Integer slot = setting.getAssignedSlot(sid);
        return Integer.toString(slot != null ? slot : 1);
    }

    private int getHotbarSlotForKey(int keyCode) {
        Minecraft mc = MinecraftClient.getInstance();
        for (int i = 0; i < mc.options.keyBindsHotbar.length; i++) {
            if (keyCode == mc.options.keyBindsHotbar[i].getKeyCode()) return i + 1;
        }
        return -1;
    }
}