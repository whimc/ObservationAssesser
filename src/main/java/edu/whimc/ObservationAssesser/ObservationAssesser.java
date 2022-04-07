package edu.whimc.ObservationAssesser;

import edu.whimc.ObservationAssesser.assessments.*;
import edu.whimc.ObservationAssesser.commands.AssessmentCommand;
import edu.whimc.ObservationAssesser.commands.LeaderboardCommand;
import edu.whimc.ObservationAssesser.commands.ProgressCommand;
import edu.whimc.ObservationAssesser.utils.Utils;
import edu.whimc.ObservationAssesser.utils.sql.Queryer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Dictionary;
import java.util.HashMap;


/**
 * Class to create plugin and enable it in MC
 * @author sam
 */
public class ObservationAssesser extends JavaPlugin implements Listener {
    private static ObservationAssesser instance;
    private Queryer queryer;
    private HashMap<Player,Long> sessions;
    /**
     * Method to return instance of plugin
     * @return instance of ObservationAssesser plugin
     */
    public static ObservationAssesser getInstance() {
        return instance;
    }

    /**
     * Method to enable plugin
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        sessions = new HashMap<>();
        System.currentTimeMillis();

        ObservationAssesser.instance = this;
        this.queryer = new Queryer(this, q -> {
            // If we couldn't connect to the database disable the plugin
            if (q == null) {
                this.getLogger().severe("Could not establish MySQL connection! Disabling plugin...");
                getCommand("observationassesser").setExecutor(this);
                getCommand("progress").setExecutor(this);
                getCommand("leaderboard").setExecutor(this);
                return;
            }
            });
        getCommand("observationassesser").setExecutor(new AssessmentCommand(this));


        getCommand("progress").setExecutor(new ProgressCommand(this));


        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    public Queryer getQueryer() {
        return this.queryer;
    }

    public HashMap getPlayerSessions(){return this.sessions;}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        sessions.putIfAbsent(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Long sessionStart = sessions.get(player);
        queryer.getSessionObservations(player, sessionStart, observations -> {
            ObservationAssessment obs = new ObservationAssessment(player, sessionStart,observations);
            queryer.getSessionScienceTools(player, sessionStart, scienceTools -> {
                ScienceToolsAssessment sci = new ScienceToolsAssessment(player, sessionStart, scienceTools);
                queryer.getSessionPositions(player,sessionStart, positions -> {
                    ExplorationAssessment exp = new ExplorationAssessment(player, sessionStart, positions, this);
                    QuestAssessment quest = new QuestAssessment(player, sessionStart, null);
                    OverallAssessment assessment = new OverallAssessment(player, sessionStart, null, obs, sci, exp, quest);
                    queryer.storeNewProgress(assessment, rowID -> {
                        sessions.remove(event.getPlayer());
                    });
                });
            });
        });

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Utils.msg(sender, "&cThis plugin is disabled because it was unable to connect to the configured database. " +
                "Please modify the config to ensure the credentials are correct then restart the server.");
        return true;
    }
}
