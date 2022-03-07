package edu.whimc.ObservationAssesser.assessments;

import edu.whimc.ObservationAssesser.bkt.Skills;
import org.pmml4s.model.Model;

import java.util.*;

/**
 * Class to assess observation structure with an ML pipeline
 */
public class StructureAssessment {
    private String observation;
    private String observationType;
    private String predictedType;
    private int correct;

    /**
     * Constructor to set instance variables
     * @param observation student's observation
     * @param observationType observation type as a string
     */
    public StructureAssessment(String observation, String observationType){
        this.observation = observation;
        this.observationType = observationType;
    }

    /**
     * Runs the ML model located in the plugins directory with the uberjar, sets correctness of observation
     */
    public void predict(){
        String[]input = {this.observation};
        Model model = Model.fromFile(System.getProperty("user.dir")+"/plugins/model.pmml");
        Object[] result = model.predict(input);
        int labelIndex = 0;
        String[] classes = {"analogy", "comparative", "descriptive", "inference", "factual", "off topic", "measurement mistake"};
        for(int k= 1; k < result.length; k++){
            if((double) result[k] > (double) result[labelIndex]){
                labelIndex = k;
            }
        }

        //Correct if predicted type = self-classified type, if null not counted as either
        predictedType = classes[labelIndex];
        if(observationType != null) {
            if (predictedType.equalsIgnoreCase(observationType)) {
                correct = 1;
            } else {
                correct = 0;
            }
        } else {
            correct = -1;
        }
    }

    /**
     * Gets assessment of observation
     * @return if observation classification was right wrong or neither
     */
    public int getCorrect(){
        return correct;
    }

    /**
     * Get observation type
     * @return observation type
     */
    public String getPredictedType(){
        return predictedType;
    }

    /**
     * Get error-flagging feedback dependent on assessment
     * @return feedback to display to the user based on assessment
     */
    public String getFeedback(){
        if(correct == 1){
            return "Great "+observationType.toLowerCase()+" observation! You really captured the structure.";
        } else if(correct == 0) {
            return "Nice attempt trying the "+observationType.toLowerCase()+" observation. I think it was more of a "+predictedType+" observation, you should try to fix it with another observation.";
        }
        return "You made a "+predictedType+" observation. Try labeling your next one!";
    }


}
