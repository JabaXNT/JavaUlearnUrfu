package org.Ulearn.Database;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.MongoCollection;

import java.util.ArrayList;

import org.Ulearn.DbEntity.Student;
import org.Ulearn.DbEntity.SummaryProgress;
import org.Ulearn.DbEntity.Theme;
import org.bson.Document;
import org.bson.conversions.Bson;

public class DatabaseConnection {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> studentCollection;
    private MongoCollection<Document> themesCollection;
    private MongoCollection<Document> summaryProgressCollection;

    public void connection() {
        String uri = "mongodb://localhost:27017";
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build();

        // Create a new client and connect to the server
        try {
            this.mongoClient = MongoClients.create(settings);
            this.database = mongoClient.getDatabase("ulearn");
            this.studentCollection = database.getCollection("students");
            this.themesCollection = database.getCollection("themes");
            this.summaryProgressCollection = database.getCollection("summaryProgress");
            System.out.println("Connection successful");
        } catch (MongoException me) {
            System.err.println("Connection failed: " + me.getMessage());
        }
    }

    public void insertStudents(ArrayList<Student> students) {
        try {
            for (Student student : students) {
                Document existingStudent = studentCollection.find(new Document("_id", student.getUlearn_id())).first();
                if (existingStudent == null) {
                    Document doc = new Document("name", student.getFullName())
                        .append("email", student.getEmail())
                        .append("_id", student.getUlearn_id())
                        .append("group", student.getGroup());
                    studentCollection.insertOne(doc);
                }
            }

            System.out.println("Students inserted successfully");
        } catch (MongoException me) {
            System.err.println("Insertion failed: " + me.getMessage());
        }
    }

    public void insertThemes(ArrayList<Theme> themes) {
        try {
            for (Theme theme : themes) {
                Document doc = new Document("name", theme.getName())
                    .append("max_activity", theme.getMax_score_activities())
                    .append("max_exercise", theme.getMax_score_exercises())
                    .append("max_homework", theme.getMax_score_homeworks());
                themesCollection.insertOne(doc);
            }
            System.out.println("Themes inserted successfully");
        } catch (MongoException me) {
            System.err.println("Insertion failed: " + me.getMessage());
        }
    }

    public void insertSummaryProgress(ArrayList<SummaryProgress> summaryProgressList) {
        try {
            for (SummaryProgress summaryProgress : summaryProgressList) {
                Document doc = new Document("student_id", summaryProgress.getStudent_id())
                    .append("activity", summaryProgress.getActivity())
                    .append("exercise", summaryProgress.getExercise())
                    .append("homework", summaryProgress.getHomework())
                    .append("seminar", summaryProgress.getSeminars());

                Bson filter = new Document("student_id", summaryProgress.getStudent_id());
                UpdateOptions options = new UpdateOptions().upsert(true);
                summaryProgressCollection.updateOne(filter, new Document("$set", doc), options);
            }
            System.out.println("Summary progress inserted/updated successfully");
        } catch (MongoException me) {
            System.err.println("Insertion/Update failed: " + me.getMessage());
        }
    }

    public void printStudentSummaryProgress(String fullName) {
        try {
            Document student = studentCollection.find(new Document("name", fullName)).first();
            if (student == null) {
                System.out.println("No student found with name: " + fullName);
                return;
            }
    
            String studentId = student.getString("_id");
    
            Document summaryProgress = summaryProgressCollection.find(new Document("student_id", studentId)).first();
            if (summaryProgress == null) {
                System.out.println("No summary progress found for student: " + fullName);
                return;
            }
    
            System.out.println("Activity: " + summaryProgress.getString("activity"));
            System.out.println("Exercise: " + summaryProgress.getString("exercise"));
            System.out.println("Homework: " + summaryProgress.getString("homework"));
            System.out.println("Seminar: " + summaryProgress.getString("seminar"));
        } catch (MongoException me) {
            System.err.println("Operation failed: " + me.getMessage());
        }
    }
}