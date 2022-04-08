package edu.whimc.feedback;

import edu.whimc.feedback.assessments.*;
import edu.whimc.feedback.commands.AssessmentCommand;
import edu.whimc.feedback.commands.LeaderboardCommand;
import edu.whimc.feedback.commands.ProgressCommand;
import edu.whimc.feedback.utils.Utils;
import edu.whimc.feedback.utils.sql.Queryer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;


/**
 * Class to create plugin and enable it in MC
 * @author sam
 */
public class StudentFeedback extends JavaPlugin implements Listener {
    private static StudentFeedback instance;
    private Queryer queryer;
    private HashMap<Player,Long> sessions;

    /**
     * Method to return instance of plugin
     * @return instance of StudentFeedback plugin
     */
    public static StudentFeedback getInstance() {
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

        StudentFeedback.instance = this;
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

    /**
     * Returns queryer
     * @return queryer
     */
    public Queryer getQueryer() {
        return this.queryer;
    }

    /**
     * Returns current sessions on server
     * @return sessions on server
     */
    public HashMap getPlayerSessions(){return this.sessions;}

    /**
     * When players join they are added to sessions
     * @param event PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        sessions.putIfAbsent(event.getPlayer(), System.currentTimeMillis());
    }

    /**
     * When players leave their progress is saved to db and they are removed from sessions
     * @param event PlayerQuitEvent
     */
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

    /**
     * Display when plugin cannot be enabled
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Utils.msg(sender, "&cThis plugin is disabled because it was unable to connect to the configured database. " +
                "Please modify the config to ensure the credentials are correct then restart the server.");
        return true;
    }
}
