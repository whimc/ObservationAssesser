package edu.whimc.feedback.dialoguetemplate.models;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold entries from config for dialogues
 */
public class DialoguePrompt {

    public static final String FILLIN = "{}";

    private final String prompt;

    private final Map<World, Map<String, String>> responses;

    @SuppressWarnings("unchecked")
    public DialoguePrompt(Map<?, ?> entry) {
        // This is pretty hacky and will blow up if the config does not match the intended format
        this.responses = new HashMap<>();
        this.prompt = (String) entry.get("prompt");

        Map<String, Object> worlds = (Map<String, Object>) entry.get("worlds");
        for (String worldName : worlds.keySet()) {
            World world = Bukkit.getWorld(worldName);
            this.responses.put(world, (Map<String, String>) worlds.get(worldName));
        }
    }

    public String getPrompt() {
        return this.prompt;
    }

    public String getResponses(World world, String key) {
        if (!this.responses.containsKey(world)) {
            return "";
        }

        return this.responses.get(world).get(key);
    }

    public int getNumberOfFillIns(World world) {
        String temp = getResponses(world, "feedback").replace(FILLIN, "");
        return (this.prompt.length() - temp.length()) / FILLIN.length();
    }

}
