package edu.whimc.feedback.assessments;

import org.bukkit.entity.Player;

/**
 * Abstract class to define assessments
 */
public abstract class ProgressAssessment {
    private Player player;
    private Long sessionStart;
    private Object resultSet;

    /**
     * Constructor for abstract class
     * @param player player invoking command
     * @param sessionStart time when player joined
     * @param resultSet result of querying the db
     */
    public ProgressAssessment(Player player, Long sessionStart, Object resultSet){
        this.player = player;
        this.sessionStart = sessionStart;
        this.resultSet = resultSet;
    }

    /**
     * Returns player
     * @return player
     */
    public Player getPlayer(){
        return player;
    }

    /**
     * Returns session start
     * @return session start
     */
    public long getSessionStart(){
        return sessionStart;
    }

    /**
     * Returns result set from query
     * @return result set
     */
    public Object getResultSet(){return resultSet;}

    /**
     * Method to be defined by subclasses for how to assess interest for individual gameplay behaviors
     * @return assessment of interest
     */
    public abstract double metric();

    /**
     * Returns display name of assessment
     * @return display name of assessment
     */
    public abstract String getName();
}
