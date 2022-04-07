package edu.whimc.ObservationAssesser.assessments;

import edu.whimc.ObservationAssesser.ObservationAssesser;
import edu.whimc.ObservationAssesser.utils.sql.Queryer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ObservationAssessment extends ProgressAssessment{

    public ObservationAssessment(Player player, Long sessionStart,Object resultSet) {
        super(player, sessionStart, resultSet);
    }

    @Override
    public double metric() {
        HashMap<String, ArrayList<String>> observations = (HashMap<String, ArrayList<String>>) this.getResultSet();
        return observations.size();
    }



    @Override
    public String getName() {
        return "ObservationAssessment";
    }
}
