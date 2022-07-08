package edu.whimc.feedback.assessments;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * Class to define observation assessment
 */
public class ObservationAssessment extends ProgressAssessment{

    /**
     * Constructor to set instance variables in super class
     * @param player player invoking command
     * @param sessionStart time when the player joined the server
     * @param resultSet the worlds and categories of observations during the session
     */
    public ObservationAssessment(Player player, Long sessionStart,Object resultSet) {
        super(player, sessionStart, resultSet);
    }

    /**
     * Returns number of observations during the session
     * @return observation metric
     */
    @Override
    public double metric() {
        HashMap<String, ArrayList<String>> observations = (HashMap<String, ArrayList<String>>) this.getResultSet();
        int score = 0;
        for(Map.Entry<String, ArrayList<String>> entry : observations.entrySet()) {
            for(int k = 0 ; k < entry.getValue().size(); k++){
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
        return "Observation Assessment";
    }
}
