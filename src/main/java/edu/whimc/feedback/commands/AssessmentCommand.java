package edu.whimc.feedback.commands;

import edu.whimc.feedback.StudentFeedback;
import edu.whimc.feedback.assessments.StructureAssessment;
import edu.whimc.feedback.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Class to define structure assessment command
 */
public class AssessmentCommand implements CommandExecutor {

    private final StudentFeedback plugin;

    /**
     * Constructor to set instance variable
     * @param plugin the StudentFeedback plugin instance
     */
    public AssessmentCommand(StudentFeedback plugin) {
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
        //Need to preprocess lowercase and remove non alpha characters
        String observation = String.join(" ", Arrays.copyOfRange(args,0,args.length-1));
        observation.replaceAll("[^A-Za-z0-9]", "");
        String observationType = args[args.length-1];

        StructureAssessment assessment = new StructureAssessment(observation, observationType);

        assessment.predict();
        plugin.getQueryer().updateSkills(player, observationType, assessment.getCorrect(), currentSkills -> {
            Utils.sendOpenLearnerModel(player, (List<Double>) currentSkills);
            player.sendMessage(assessment.getFeedback());
        });
        return true;
    }


}
