package edu.whimc.feedback.commands;

import edu.whimc.feedback.StudentFeedback;
import edu.whimc.feedback.assessments.StructureAssessment;
import edu.whimc.feedback.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Class to define structure agent dialogue command
 */
public class AgentDialogueCommand implements CommandExecutor {

    private final StudentFeedback plugin;

    /**
     * Constructor to set instance variable
     * @param plugin the StudentFeedback plugin instance
     */
    public AgentDialogueCommand(StudentFeedback plugin) {
        this.plugin = plugin;
    }

    /**
     * Defines behavior of command when invoked
     * @param commandSender the player sending the command
     * @param command the command being sent
     * @param s the command alias
     * @param args the arguments sent with the command (separate elements are the words separated by spaces in the command)
     * @return boolean for command execution
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        this.plugin.getTemplateManager().getGui().openTemplateInventory(player);
        return true;
    }


}
