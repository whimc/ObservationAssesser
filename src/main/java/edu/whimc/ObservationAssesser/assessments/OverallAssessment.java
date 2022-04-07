package edu.whimc.ObservationAssesser.assessments;

import edu.whimc.ObservationAssesser.ObservationAssesser;
import org.bukkit.entity.Player;

public class OverallAssessment extends ProgressAssessment{

    private ObservationAssessment obs;
    private ScienceToolsAssessment tools;
    private ExplorationAssessment exp;
    private QuestAssessment quest;
    private static final int NUM_ASSESSMENTS = 4;
    public OverallAssessment(Player player, Long sessionStart, Object resultSet, ObservationAssessment obs, ScienceToolsAssessment tools,
    ExplorationAssessment exp, QuestAssessment quest) {
        super(player, sessionStart, resultSet);
        this.obs = obs;
        this.tools = tools;
        this.exp = exp;
        this.quest = quest;
    }

    @Override
    public double metric() {
        return (obs.metric() + tools.metric() + exp.metric() + quest.metric())/NUM_ASSESSMENTS;
    }

    @Override
    public String getName() {
        return "Overall Score";
    }

    public ObservationAssessment getObservationAssessment(){return obs;}
    public ScienceToolsAssessment getScienceToolAssessment(){return tools;}
    public ExplorationAssessment getExplorationAssessment(){return exp;}
    public QuestAssessment getQuestAssessment(){return quest;}


}
