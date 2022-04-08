package edu.whimc.feedback.assessments;

import org.bukkit.entity.Player;

/**
 * Class to define overall assessment
 */
public class OverallAssessment extends ProgressAssessment{

    private ObservationAssessment obs;
    private ScienceToolsAssessment tools;
    private ExplorationAssessment exp;
    private QuestAssessment quest;
    private static final int NUM_ASSESSMENTS = 4;

    /**
     * Constructor for OverallAssessment
     * @param player player invoke the command
     * @param sessionStart when the player joined the server
     * @param resultSet null
     * @param obs ObservationAssessment of the player
     * @param tools ScienceToolsAssessment of the player
     * @param exp ExplorationAssessment of the player
     * @param quest QuestAssessment of the player
     */
    public OverallAssessment(Player player, Long sessionStart, Object resultSet, ObservationAssessment obs, ScienceToolsAssessment tools,
    ExplorationAssessment exp, QuestAssessment quest) {
        super(player, sessionStart, resultSet);
        this.obs = obs;
        this.tools = tools;
        this.exp = exp;
        this.quest = quest;
    }

    /**
     * Returns average of the assessments
     * @return overall metric
     */
    @Override
    public double metric() {
        return (obs.metric() + tools.metric() + exp.metric() + quest.metric())/NUM_ASSESSMENTS;
    }

    /**
     * Returns display name of assessment
     * @return display name of assessment
     */
    @Override
    public String getName() {
        return "Overall Score";
    }

    /**
     * Returns ObservationAssessment for the player
     * @return ObservationAssessment
     */
    public ObservationAssessment getObservationAssessment(){return obs;}

    /**
     * Returns ScienceToolAssessment for the player
     * @return ScienceToolAssessment
     */
    public ScienceToolsAssessment getScienceToolAssessment(){return tools;}

    /**
     * Returns ExplorationAssessment for the player
     * @return ExplorationAssessment
     */
    public ExplorationAssessment getExplorationAssessment(){return exp;}

    /**
     * Returns QuestAssessment for the player
     * @return QuestAssessment
     */
    public QuestAssessment getQuestAssessment(){return quest;}


}
