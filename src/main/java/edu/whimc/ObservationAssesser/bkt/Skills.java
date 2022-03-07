package edu.whimc.ObservationAssesser.bkt;


import java.util.ArrayList;
import java.util.List;

/**
 * Class to update skills using BKT parameters
 */
public class Skills {
    private static final int ANALOGYLOCATION = 0;
    private static final int COMPARISONLOCATION = 1;
    private static final int DESCRIPTIVELOCATION = 2;
    private static final int INFERENCELOCATION = 3;

    private static final int GUESSLOCATION = 0;
    private static final int SLIPLOCATION = 1;
    private static final int TRANSFERLOCATION = 2;

    /**
     * Updates BKT skill
     * @param previousSkills the students learning estimate before attempt
     * @param type the observation type attempted
     * @param correct the assessment of the observation
     * @return the updated skill of the student for the type
     */
    public static List<Double> updateSkills(List<Double> previousSkills, String type, int correct){
        double[][] params = new double[4][3];
        //Analogy params
        params[0][0] = .01;  //Guess
        params[0][1] = .1;  //Slip
        params[0][2] = .01;  //Transfer

        //Comparison params
        params[1][0] = .29;
        params[1][1] = .1;
        params[1][2] = .13;

        //Descriptive params
        params[2][0] = .29;
        params[2][1] = .01;
        params[2][2] = .29;

        //Inference params
        params[3][0] = .01;
        params[3][1] = .1;
        params[3][2] = .01;

        List<Double> currentSkills = previousSkills;
        int skillType = -1;
        if (type.equalsIgnoreCase("analogy")) {
            skillType = ANALOGYLOCATION;
        } else if (type.equalsIgnoreCase("comparative"))  {
            skillType = COMPARISONLOCATION;
        } else if (type.equalsIgnoreCase("descriptive"))  {
            skillType = DESCRIPTIVELOCATION;
        } else if (type.equalsIgnoreCase("inference"))  {
            skillType = INFERENCELOCATION;
        }

        //BKT update rule application
        double speedCalc = (previousSkills.get(skillType) * (1.0 - params[skillType][SLIPLOCATION])) + ((1.0 - previousSkills.get(skillType)) * params[skillType][GUESSLOCATION]);

        double update;
        if(correct == 1) {
            update = ((previousSkills.get(skillType) * (1.0 - params[skillType][SLIPLOCATION]))) / speedCalc;
        } else {
            update = ((previousSkills.get(skillType) * params[skillType][SLIPLOCATION])) / ((previousSkills.get(skillType) * params[skillType][SLIPLOCATION]) + ((1.0 - previousSkills.get(skillType))*(1.0 - params[skillType][GUESSLOCATION])));
        }
        currentSkills.set(skillType, update + ((1.0 - update) * params[skillType][TRANSFERLOCATION]));
        return currentSkills;
    }
}