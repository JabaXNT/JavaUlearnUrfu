package org.Ulearn.DbEntity;

public class Theme {
    public final String name;
    public final String max_score_activities;
    public final String max_score_exercises;
    public final String max_score_homeworks;

    public Theme(String name, String max_score_activities, String max_score_exercises, String max_score_homeworks) {
        this.name = name;
        this.max_score_activities = max_score_activities;
        this.max_score_exercises = max_score_exercises;
        this.max_score_homeworks = max_score_homeworks;
    }

    public String getName() {
        return name;
    }

    public String getMax_score_activities() {
        return max_score_activities;
    }

    public String getMax_score_exercises() {
        return max_score_exercises;
    }

    public String getMax_score_homeworks() {
        return max_score_homeworks;
    }
}
