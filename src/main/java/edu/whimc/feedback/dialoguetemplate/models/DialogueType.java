package edu.whimc.feedback.dialoguetemplate.models;

import org.apache.commons.lang.StringUtils;

/**
 * Types of topics that the agent can have
 */
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
