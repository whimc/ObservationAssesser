package edu.whimc.feedback.assessments;

import org.bukkit.entity.Player;


import java.util.*;

/**
 * Class to define science tool assessment
 */
public class ScienceToolsAssessment extends ProgressAssessment{
    public ScienceToolsAssessment(Player player, Long sessionStart,Object resultSet) {
        super(player, sessionStart, resultSet);
    }

    /**
     * Returns number of unique science tools used during the session
     * @return science tools metric
     */
    @Override
    public double metric() {
        HashMap<String, HashSet<String>> tools = (HashMap<String, HashSet<String>>) this.getResultSet();
        int score = 0;
        for(Map.Entry<String, HashSet<String>> entry : tools.entrySet()) {
            for (String ele : entry.getValue()) {
                score++;
            }
        }
        return score;
    }

    /**
     * Returns display name of assessment
     * @return display name of assessment
     */
    @Override
    public String getName() {
        return "Science Tool Assessment";
    }
}
