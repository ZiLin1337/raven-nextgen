package keystrokesmod.script.model;

import keystrokesmod.utility.Utils;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class Message {
    public MutableText component;
    
    public Message(String message) {
        this.component = Text.literal(message);
    }
    
    public void appendStyle(String style, String action, String styleMessage, String message) {
        Style chatStyle = Style.EMPTY;
        if (style.equals("HOVER")) {
            HoverEvent.Action<?> hoverAction = Utils.getEnum(HoverEvent.Action.class, action);
            if (hoverAction != null) {
                chatStyle = chatStyle.withHoverEvent(new HoverEvent(hoverAction, Text.literal(styleMessage)));
            }
        } else if (style.equals("CLICK")) {
            ClickEvent.Action clickAction = Utils.getEnum(ClickEvent.Action.class, action);
            if (clickAction != null) {
                chatStyle = chatStyle.withClickEvent(new ClickEvent(clickAction, styleMessage));
            }
        }
        MutableText sibling = Text.literal(message).styled(s -> chatStyle);
        this.component.append(sibling);
    }
    
    public void append(String append) {
        this.component.append(Text.literal(append));
    }
    
    public List<Message> getSiblings() {
        List<Message> siblings = new ArrayList<>();
        for (Text sibling : this.component.getSiblings()) {
            siblings.add(new Message(sibling.getString()));
        }
        return siblings;
    }
    
    public String getStyle() {
        return this.component.getStyle().toString();
    }
    
    public String getText() {
        return this.component.getString();
    }
    
    @Override
    public String toString() {
        return "TextComponent{text='" + this.component.getString() + '\'' +
               ", siblings=" + this.component.getSiblings() +
               ", style=" + this.component.getStyle() + '}';
    }
}