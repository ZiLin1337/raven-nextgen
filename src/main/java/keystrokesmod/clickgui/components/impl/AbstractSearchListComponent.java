package keystrokesmod.clickgui.components.impl;

public abstract class AbstractSearchListComponent extends AbstractTextInputComponent {
    protected AbstractSearchListComponent(ModuleComponent moduleComponent, float o, String placeholder, int maxLength) {
        super(moduleComponent, o, placeholder, maxLength);
    }
    protected static class SelectedRowData {
        public final String storageId;
        public final String displayName;
        public final net.minecraft.item.ItemStack previewStack;
        public final java.util.List<net.minecraft.item.ItemStack> cyclingStacks;
        public SelectedRowData(String storageId, String displayName, net.minecraft.item.ItemStack previewStack, java.util.List<net.minecraft.item.ItemStack> cyclingStacks) {
            this.storageId = storageId;
            this.displayName = displayName;
            this.previewStack = previewStack;
            this.cyclingStacks = cyclingStacks;
        }
    }
}

