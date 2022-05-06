package edu.whimc.feedback.assessments;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class to define quest assessment
 */
public class QuestAssessment extends ProgressAssessment{
    public QuestAssessment(Player player, Long sessionStart,Object resultSet) {
        super(player, sessionStart, resultSet);
    }

    /**
     * Returns quest metric (currently undefined)
     * @return quest metric
     */
    @Override
    public double metric() {
        ArrayList<String> completedQuests = (ArrayList<String>) this.getResultSet();
        return completedQuests.size();
    }

    /**
     * Returns display name of assessment
     * @return display name of assessment
     */
    @Override
    public String getName() {
        return "Quest Assessment";
    }
}
