package edu.whimc.feedback.dialoguetemplate.models;

import edu.whimc.feedback.StudentFeedback;

import edu.whimc.feedback.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to handle config entries for inventory UI
 */
public class DialogueTemplate {

    private final List<DialoguePrompt> prompts = new ArrayList<>();

    private final DialogueType type;

    private final boolean guiEnabled;

    private final Material guiItem;

    private final String guiItemName;

    private final int guiPosition;

    private final List<String> guiLore;

    public DialogueTemplate(StudentFeedback plugin, DialogueType type) {
        this.type = type;
        String path = "templates." + type.name() + ".prompts";

        List<Map<?, ?>> entries = plugin.getConfig().getMapList(path);
        for (Map<?, ?> entry : entries) {
            this.prompts.add(new DialoguePrompt(entry));
        }

        FileConfiguration config = plugin.getConfig();
        String pathPrefix = "templates." + type.name() + ".gui.";

        this.guiEnabled = config.getBoolean(pathPrefix + "enabled", true);
        this.guiItem = Utils.matchMaterial(plugin, config.getString(pathPrefix + "item"), Material.STONE);
        this.guiItemName = config.getString(pathPrefix + "name");
        this.guiPosition = config.getInt(pathPrefix + "position");
        this.guiLore = config.getStringList(pathPrefix + "lore");
    }

    public List<DialoguePrompt> getPrompts() {
        return this.prompts;
    }

    public DialogueType getType() {
        return this.type;
    }

    public boolean isGuiEnabled() {
        return this.guiEnabled;
    }

    public Material getGuiItem() {
        return this.guiItem;
    }

    public String getGuiItemName() {
        return this.guiItemName;
    }

    public int getGuiPosition() {
        return this.guiPosition;
    }

    public List<String> getGuiLore() {
        return this.guiLore;
    }

    public String getColor() {
        return ChatColor.getLastColors(Utils.color(this.guiItemName));
    }

}
