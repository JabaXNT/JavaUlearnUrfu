package org.Ulearn.DbEntity;


public class Student {
    public final String fullName;
    public final String ulearn_id;
    public final String email;
    public final String group;

    public Student(String ulearn_id, String fullName, String email, String group) {
        this.ulearn_id = ulearn_id;
        this.fullName = fullName;
        this.email = email;
        this.group = group;
    }

    @Override
    public String toString() {
        return "Student [fullName=" + fullName + ", ulearn_id=" + ulearn_id + ", email=" + email + ", group=" + group
                + "]";
    }

    public String getFullName() {
        return fullName;
    }

    public String getUlearn_id() {
        return ulearn_id;
    }

    public String getEmail() {
        return email;
    }

    public String getGroup() {
        return group;
    }
}
