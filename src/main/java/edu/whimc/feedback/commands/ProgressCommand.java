package edu.whimc.feedback.commands;

import edu.whimc.feedback.StudentFeedback;
import edu.whimc.feedback.assessments.*;
import edu.whimc.feedback.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Class to define assessment command
 */
public class ProgressCommand implements CommandExecutor, TabCompleter {

    private final StudentFeedback plugin;
    private final String COMMAND = "progress";

    /**
     * Constructor to set instance variable
     * @param plugin the StudentFeedback plugin instance
     */
    public ProgressCommand(StudentFeedback plugin) {
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
        if (!(commandSender instanceof Player)) {
            Utils.msg(commandSender, ChatColor.RED + "You must be a player!");
            return true;
        }
        Player player = (Player) commandSender;

        HashMap<Player,Long> sessions = plugin.getPlayerSessions();
        Long sessionStart = sessions.get(player);
        if(sessionStart == null){
            return false;
        }
        this.plugin.getQueryer().storeNewProgressCommand(player, COMMAND, id -> {
            plugin.getQueryer().getSessionObservations(player, sessionStart, observations -> {
                ObservationAssessment obs = new ObservationAssessment(player, sessionStart, observations);
                plugin.getQueryer().getSessionScienceTools(player, sessionStart, scienceTools -> {
                    ScienceToolsAssessment sci = new ScienceToolsAssessment(player, sessionStart, scienceTools);
                    plugin.getQueryer().getSessionPositions(player, sessionStart, positions -> {
                        ExplorationAssessment exp = new ExplorationAssessment(player, sessionStart, positions, plugin);
                        plugin.getQueryer().getQuestsCompleted(player, completedQuests -> {
                            QuestAssessment quest = new QuestAssessment(player, sessionStart, completedQuests);
                            OverallAssessment assessment = new OverallAssessment(player, sessionStart, null, obs, sci, exp, quest);
                            Utils.sendProgressFeedback(assessment);
                        });
                    });
                });
            });
        });
        return true;
    }

    /**
     * Allows tab completion
     * @param commandSender player sending the command
     * @param command the command being sent
     * @param s the command alias
     * @param strings the args of the command
     * @return list of string for tab completion for command
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
