package edu.whimc.ObservationAssesser.commands;

import edu.whimc.ObservationAssesser.ObservationAssesser;
import edu.whimc.ObservationAssesser.assessments.*;
import edu.whimc.ObservationAssesser.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class LeaderboardCommand  implements CommandExecutor, TabCompleter {
    private ObservationAssesser plugin;
    public LeaderboardCommand(ObservationAssesser plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player sender = (Player) commandSender;
        HashMap<Player,Long> sessions = plugin.getPlayerSessions();
        this.getSortedLeaderboard(sessions, sorted -> {
            Utils.sendLeaderboardFeedback(sender, (ArrayList<OverallAssessment>) sorted);
        });

        return true;
    }
    public void getSortedLeaderboard(HashMap<Player, Long> sessions, Consumer callback){
        async(() -> {
            ArrayList<OverallAssessment> scores = new ArrayList<>();
            AtomicInteger ctr = new AtomicInteger();
            for(Map.Entry<Player, Long> entry : sessions.entrySet()) {
                Player player = entry.getKey();
                Long sessionStart = entry.getValue();

            plugin.getQueryer().getSessionObservations(player, sessionStart, observations -> {
                ObservationAssessment obs = new ObservationAssessment(player, sessionStart,observations);
                plugin.getQueryer().getSessionScienceTools(player, sessionStart, scienceTools -> {
                    ScienceToolsAssessment sci = new ScienceToolsAssessment(player, sessionStart, scienceTools);
                    plugin.getQueryer().getSessionPositions(player,sessionStart, positions -> {
                        ExplorationAssessment exp = new ExplorationAssessment(player, sessionStart, positions, plugin);
                        QuestAssessment quest = new QuestAssessment(player, sessionStart, null);
                        OverallAssessment assessment = new OverallAssessment(player, sessionStart, null, obs, sci, exp, quest);
                        scores.add(assessment);
                        ctr.getAndIncrement();
                        if(ctr.get() == sessions.keySet().size()){
                            scores.sort(new AssessmentComparator());
                            sync(callback,scores);
                        }

                    });
                });
            });
            }

        });
    }
    private <T> void sync(Consumer<T> cons, T val) {
        Bukkit.getScheduler().runTask(this.plugin, () -> cons.accept(val));
    }

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }
    private void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
