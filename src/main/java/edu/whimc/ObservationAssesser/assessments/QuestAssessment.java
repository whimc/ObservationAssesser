package edu.whimc.ObservationAssesser.assessments;

import edu.whimc.ObservationAssesser.ObservationAssesser;
import org.bukkit.entity.Player;

public class QuestAssessment extends ProgressAssessment{
    public QuestAssessment(Player player, Long sessionStart,Object resultSet) {
        super(player, sessionStart, resultSet);
    }

    @Override
    public double metric() {
        return 0;
    }


    @Override
    public String getName() {
        return "Quest Assessment";
    }
}
