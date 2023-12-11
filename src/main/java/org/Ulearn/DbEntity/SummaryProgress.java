package org.Ulearn.DbEntity;

public class SummaryProgress {
    public final String student_id;
    public final String activity;
    public final String exercise;
    public final String practise;
    public final String seminars;

    public SummaryProgress(String student_id, String activity, String exercise, String practise, String seminars) {
        this.student_id = student_id;
        this.activity = activity;
        this.exercise = exercise;
        this.practise = practise;
        this.seminars = seminars;
    }

    @Override
    public String toString() {
        return "SummaryProgress [student_id=" + student_id + ", activity=" + activity + ", exercise=" + exercise
                + ", practise=" + practise + ", seminars=" + seminars + "]";
    }

    public String getActivity() {
        return activity;
    }

    public String getExercise() {
        return exercise;
    }

    public String getPractise() {
        return practise;
    }

    public String getSeminars() {
        return seminars;
    }

    public String getStudent_id() {
        return student_id;
    }
}
