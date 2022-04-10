package edu.whimc.feedback.dialoguetemplate;



import edu.whimc.feedback.StudentFeedback;
import edu.whimc.feedback.dialoguetemplate.gui.TemplateGui;
import edu.whimc.feedback.dialoguetemplate.gui.TemplateSelection;
import edu.whimc.feedback.dialoguetemplate.models.DialoguePrompt;
import edu.whimc.feedback.dialoguetemplate.models.DialogueTemplate;
import edu.whimc.feedback.dialoguetemplate.models.DialogueType;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to instantiate agent dialogue
 */
public class TemplateManager {

    private final TemplateGui gui;

    private final SpigotCallback spigotCallback;

    private final Map<DialogueType, DialogueTemplate> templates = new HashMap<>();

    public TemplateManager(StudentFeedback plugin) {
        this.spigotCallback = new SpigotCallback(plugin);

        for (DialogueType type : DialogueType.values()) {
            DialogueTemplate template = new DialogueTemplate(plugin, type);
            this.templates.put(type, template);
        }

        // The templates must be loaded before creating the GUI
        this.gui = new TemplateGui(plugin, this);

        for (DialogueTemplate template : this.templates.values()) {
            if (template.isGuiEnabled()) {
                this.gui.setAction(template.getGuiPosition(), player -> {
                    new TemplateSelection(plugin, this.spigotCallback, player, template);
                });
            }
        }
    }

    public TemplateGui getGui() {
        return this.gui;
    }

    public DialogueTemplate getTemplate(DialogueType type) {
        return this.templates.get(type);
    }

}
