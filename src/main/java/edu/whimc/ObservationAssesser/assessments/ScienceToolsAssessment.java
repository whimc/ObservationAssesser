package edu.whimc.ObservationAssesser.assessments;

import edu.whimc.ObservationAssesser.ObservationAssesser;
import edu.whimc.ObservationAssesser.utils.Utils;
import edu.whimc.ObservationAssesser.utils.sql.Queryer;
import org.bukkit.entity.Player;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class ScienceToolsAssessment extends ProgressAssessment{
    public ScienceToolsAssessment(Player player, Long sessionStart,Object resultSet) {
        super(player, sessionStart, resultSet);
    }

    @Override
    public double metric() {
        HashMap<String, HashSet<String>> tools = (HashMap<String, HashSet<String>>) this.getResultSet();
        return tools.size();
    }

    @Override
    public String getName() {
        return "Science Tool Assessment";
    }
}
