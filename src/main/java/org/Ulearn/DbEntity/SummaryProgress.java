package org.Ulearn.DbEntity;

public class SummaryProgress {
    public final String student_id;
    public final String activity;
    public final String exercise;
    public final String homework;
    public final String seminars;

    public SummaryProgress(String student_id, String activity, String exercise, String homework, String seminars) {
        this.student_id = student_id;
        this.activity = activity;
        this.exercise = exercise;
        this.homework = homework;
        this.seminars = seminars;
    }

    @Override
    public String toString() {
        return "SummaryProgress [student_id=" + student_id + ", activity=" + activity + ", exercise=" + exercise
                + ", homework=" + homework + ", seminars=" + seminars + "]";
    }

    public String getActivity() {
        return activity;
    }

    public String getExercise() {
        return exercise;
    }

    public String getHomework() {
        return homework;
    }

    public String getSeminars() {
        return seminars;
    }

    public String getStudent_id() {
        return student_id;
    }
}
