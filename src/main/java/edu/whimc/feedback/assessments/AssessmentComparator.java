package edu.whimc.feedback.assessments;

import java.util.Comparator;

/**
 * Class to define how to compare assessments
 */
public class AssessmentComparator implements Comparator<OverallAssessment> {

    /**
     * Implementation of assessment comparison
     * @param o1 assessment 1 to compare
     * @param o2 assesssment 2 to compare
     * @return 1 if assessment 1 is greater, -1 if lesser, and 0 if equal
     */
    public int compare(OverallAssessment o1, OverallAssessment o2){
        if(o1.metric() > o2.metric()){
            return 1;
        }
        else if(o1.metric() == o2.metric()){
            return 0;
        }
        else{
            return -1;
        }
    }
}
