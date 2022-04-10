package edu.whimc.feedback.dialoguetemplate.models;

import org.apache.commons.lang.StringUtils;

public enum DialogueType {

    SESSION,

    OVERALL,

    SCIENCE
    ;

    @Override
    public String toString() {
        return StringUtils.capitalize(super.toString().toLowerCase());
    }

}
