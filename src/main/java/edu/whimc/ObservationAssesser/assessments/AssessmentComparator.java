package edu.whimc.ObservationAssesser.assessments;

import java.util.Comparator;

public class AssessmentComparator implements Comparator<OverallAssessment> {
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
