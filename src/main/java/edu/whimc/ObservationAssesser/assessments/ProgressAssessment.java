package edu.whimc.ObservationAssesser.assessments;

import edu.whimc.ObservationAssesser.ObservationAssesser;
import org.bukkit.entity.Player;

public abstract class ProgressAssessment {
    private Player player;
    private Long sessionStart;
    private Object resultSet;
    public ProgressAssessment(Player player, Long sessionStart, Object resultSet){
        this.player = player;
        this.sessionStart = sessionStart;
        this.resultSet = resultSet;
    }
    public Player getPlayer(){
        return player;
    }
    public long getSessionStart(){
        return sessionStart;
    }
    public Object getResultSet(){return resultSet;}
    public abstract double metric();
    public abstract String getName();
}
