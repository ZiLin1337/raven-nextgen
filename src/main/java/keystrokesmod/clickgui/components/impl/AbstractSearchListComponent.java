package keystrokesmod.clickgui.components.impl;

import keystrokesmod.clickgui.components.Component;

public abstract class AbstractSearchListComponent extends Component {
    public boolean capturesCategoryScroll(float x, float y) {
        return false;
    }
}
