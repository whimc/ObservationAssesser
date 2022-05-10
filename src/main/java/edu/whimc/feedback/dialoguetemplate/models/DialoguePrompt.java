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

    public static final String GLOBAL_FEEDBACK = "ALL";
    private final String prompt;

    private final Map<String, Map<String, String>> responses;

    @SuppressWarnings("unchecked")
    public DialoguePrompt(Map<?, ?> entry) {
        // This is pretty hacky and will blow up if the config does not match the intended format
        this.responses = new HashMap<>();
        this.prompt = (String) entry.get("prompt");

        Map<String, Object> worlds = (Map<String, Object>) entry.get("worlds");

        for (String worldName : worlds.keySet()) {
            this.responses.put(worldName, (Map<String, String>) worlds.get(worldName));
        }
    }

    public String getPrompt() {
        return this.prompt;
    }

    public String getResponses(World world, String key) {
        if (this.responses.containsKey(GLOBAL_FEEDBACK)){
            return this.responses.get(GLOBAL_FEEDBACK).get(key);
        }else if (!this.responses.containsKey(world.getName())) {
            return null;
        }
        return this.responses.get(world.getName()).get(key);
    }

    public int getNumberOfFillIns(World world) {
        String temp = getResponses(world, "feedback").replace(FILLIN, "");
        return (this.prompt.length() - temp.length()) / FILLIN.length();
    }

}
