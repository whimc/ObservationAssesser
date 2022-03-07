package edu.whimc.ObservationAssesser.commands;

import edu.whimc.ObservationAssesser.ObservationAssesser;
import edu.whimc.ObservationAssesser.assessments.StructureAssessment;
import edu.whimc.ObservationAssesser.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to define assessment command
 */
public class AssessmentCommand implements CommandExecutor, TabCompleter {

    private Player player;
    private final ObservationAssesser plugin;

    /**
     * Constructor to set instance variable
     * @param plugin the ObservationAssesser plugin instance
     */
    public AssessmentCommand(ObservationAssesser plugin) {
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
        player = (Player) commandSender;
        //Need to preprocess lowercase and remove non alpha characters
        String observation = String.join(" ", Arrays.copyOfRange(args,0,args.length-1));
        String observationType = args[args.length-1];

        StructureAssessment assessment = new StructureAssessment(observation, observationType);
        player.sendMessage("Let me check your surroundings, it might take a bit. Feel free to explore! ");
        player.sendMessage(observation+ " "+ observationType);
        assessment.predict();
        plugin.getQueryer().updateSkills(player, observationType, assessment.getCorrect(), currentSkills -> {
            Utils.sendOpenLearnerModel(player, (List<Double>) currentSkills);
            player.sendMessage(assessment.getFeedback());
        });
        return true;
    }

    /**
     * Not necessary since command should not be invoked manually
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
