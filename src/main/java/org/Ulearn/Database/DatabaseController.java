package org.Ulearn.Database;

import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.MongoCollection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;

import org.Ulearn.DbEntity.Student;
import org.Ulearn.DbEntity.SummaryProgress;
import org.Ulearn.DbEntity.Theme;
import org.Ulearn.VkAPI.vkGroupParse.StudentInfoVkApi;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class DatabaseController {
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

        try {
            this.mongoClient = MongoClients.create(settings);
            this.database = mongoClient.getDatabase("ulearn");
            this.studentCollection = database.getCollection("students");
            this.themesCollection = database.getCollection("themes");
            this.summaryProgressCollection = database.getCollection("summary_progress");
            System.out.println("Connection successful");
        } catch (MongoException me) {
            System.err.println("Connection failed: " + me.getMessage());
        }
    }

    public void closeConnection() {
        mongoClient.close();
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
                    .append("practise", summaryProgress.getPractise())
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

    public void updateStudentsWithVkAPI(List<StudentInfoVkApi> students) {
        try {
            int count = 0;
            int count2 = 0;
            for (StudentInfoVkApi student : students) {
                String fullName = student.getFullName();
                String bdate = student.getBdate();
                String sex = student.getSex();
                String country = student.getCountry();
                List<String> schools = student.getSchools();
                Document studentDoc = studentCollection.find(new Document("name", fullName)).first();
                if (studentDoc != null) { 
                    count++;
                    Bson filter = new Document("name", fullName);

                    Bson update = new Document("$set", new Document("bdate", bdate));
                    studentCollection.updateOne(filter, update);
                    update = ((Document) update).append("$set", new Document("sex", sex));
                    studentCollection.updateOne(filter, update);
                    update = ((Document) update).append("$set", new Document("country", country));
                    studentCollection.updateOne(filter, update);
                    if (schools != null && !schools.isEmpty()) {
                        update = ((Document) update).append("$set", new Document("schools", schools));
                        studentCollection.updateOne(filter, update);
                    }
                }
            }
            System.out.println(count);
            System.out.println(count2);
            System.out.println("Student data updated successfully");
        } catch (MongoException me) {
            System.err.println("Update failed: " + me.getMessage());
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

    public void insertThemeExercisesAndPractices(Map<String, List<String>> themeExercises, Map<String, List<String>> themePractices) {
        for (Map.Entry<String, List<String>> entry : themeExercises.entrySet()) {
            ObjectId themeId = insertTheme(entry.getKey());
    
            for (String exercise : entry.getValue()) {
                insertExercise(exercise, themeId);
            }
    
            List<String> practices = themePractices.get(entry.getKey());
            if (practices != null) {
                for (String practice : practices) {
                    insertPractice(practice, themeId);
                }
            }
        }
        System.out.println("Theme exercises and practices inserted successfully");
    }

    public ObjectId insertTheme(String themeName) {
        MongoCollection<Document> themesCollection = database.getCollection("themes");
        Document existingTheme = themesCollection.find(new Document("name", themeName)).first();
        if (existingTheme == null) {
            Document themeDoc = new Document("name", themeName);
            themesCollection.insertOne(themeDoc);
            return themeDoc.getObjectId("_id");
        }
        return existingTheme.getObjectId("_id");
    }
    
    public void insertExercise(String exercise, ObjectId themeId) {
        MongoCollection<Document> exercisesCollection = database.getCollection("exercises");
        Document existingExercise = exercisesCollection.find(new Document("exercise", exercise).append("theme_id", themeId)).first();
        if (existingExercise == null) {
            Document exerciseDoc = new Document("exercise", exercise)
                .append("theme_id", themeId);
            exercisesCollection.insertOne(exerciseDoc);
        }
    }
    
    public void insertPractice(String practice, ObjectId themeId) {
        MongoCollection<Document> practicesCollection = database.getCollection("practices");
        Document existingPractice = practicesCollection.find(new Document("practice", practice).append("theme_id", themeId)).first();
        if (existingPractice == null) {
            Document practiceDoc = new Document("practice", practice)
                .append("theme_id", themeId);
            practicesCollection.insertOne(practiceDoc);
        }
    }

    public void insertAllStudentsPractiseProgress(HashMap<String, HashMap<String, Integer>> practicesScoresByUid) {
        MongoCollection<Document> taskProgressCollection = database.getCollection("practise_progress");
    
        List<Document> newTaskProgressDocs = new ArrayList<>();
    
        for (Map.Entry<String, HashMap<String, Integer>> entry : practicesScoresByUid.entrySet()) {
            String practiceName = entry.getKey();
            String practiceId = getPracticeIdByName(practiceName);
            HashMap<String, Integer> scoresByPractice = entry.getValue();
    
            for (Map.Entry<String, Integer> scoreEntry : scoresByPractice.entrySet()) {
                String studentId = scoreEntry.getKey();
                int score = scoreEntry.getValue();
    
                Document existingTaskProgress = taskProgressCollection.find(
                    new Document("student_id", studentId)
                    .append("practice_id", practiceId)
                ).first();
    
                Document taskProgressDoc = new Document("student_id", studentId)
                    .append("practice_id", practiceId)
                    .append("score", score);
    
                if (existingTaskProgress == null) {
                    newTaskProgressDocs.add(taskProgressDoc);
                } else {
                    Bson filter = new Document("student_id", studentId)
                        .append("practice_id", practiceId);
                    Bson update = new Document("$set", new Document("score", score));
                    taskProgressCollection.updateOne(filter, update);
                }
            }
        }
    
        if (!newTaskProgressDocs.isEmpty()) {
            taskProgressCollection.insertMany(newTaskProgressDocs);
        }
    }

    public String getPracticeIdByName(String practiceName) {
        MongoCollection<Document> practicesCollection = database.getCollection("practices");
    
        Document practice = practicesCollection.find(new Document("practice", practiceName)).first();
    
        if (practice != null) {
            return practice.get("_id").toString();
        } else {
            return null;
        }
    }

    
    public void insertAllStudentsExerciseProgress(HashMap<String, HashMap<String, Integer>> exercisesScoresByUid) {
        MongoCollection<Document> exerciseProgressCollection = database.getCollection("exercises_progress");
    
        List<Document> newExerciseProgressDocs = new ArrayList<>();
    
        for (Map.Entry<String, HashMap<String, Integer>> entry : exercisesScoresByUid.entrySet()) {
            String exerciseName = entry.getKey();
            String exerciseId = getExerciseIdByName(exerciseName);
            HashMap<String, Integer> scoresByExercise = entry.getValue();
    
            for (Map.Entry<String, Integer> scoreEntry : scoresByExercise.entrySet()) {
                String studentId = scoreEntry.getKey();
                int score = scoreEntry.getValue();
    
                Document existingExerciseProgress = exerciseProgressCollection.find(
                    new Document("student_id", studentId)
                    .append("exercise_id", exerciseId)
                ).first();
    
                Document exerciseProgressDoc = new Document("student_id", studentId)
                    .append("exercise_id", exerciseId)
                    .append("score", score);
    
                if (existingExerciseProgress == null) {
                    newExerciseProgressDocs.add(exerciseProgressDoc);
                } else {
                    Bson filter = new Document("student_id", studentId)
                        .append("exercise_id", exerciseId);
                    Bson update = new Document("$set", new Document("score", score));
                    exerciseProgressCollection.updateOne(filter, update);
                }
            }
        }
    
        if (!newExerciseProgressDocs.isEmpty()) {
            exerciseProgressCollection.insertMany(newExerciseProgressDocs);
        }
    }
    
    public String getExerciseIdByName(String exerciseName) {
        MongoCollection<Document> exercisesCollection = database.getCollection("exercises");
    
        Document exercise = exercisesCollection.find(new Document("exercise", exerciseName)).first();
    
        if (exercise != null) {
            return exercise.get("_id").toString();
        } else {
            return null;
        }
    }

    public void createStudentExerciseThemeProgress() {
        MongoCollection<Document> studentsCollection = database.getCollection("students");
        MongoCollection<Document> exercisesProgressCollection = database.getCollection("exercises_progress");
        MongoCollection<Document> exercisesCollection = database.getCollection("exercises");
        MongoCollection<Document> themesCollection = database.getCollection("themes");
        MongoCollection<Document> studentThemeProgressCollection = database.getCollection("student_exercise_themes_progress");
    
        FindIterable<Document> students = studentsCollection.find();
    
        List<Document> newStudentThemeProgressDocs = new ArrayList<>();
    
        for (Document student : students) {
            String studentId = student.get("_id").toString();
    
            FindIterable<Document> exerciseProgresses = exercisesProgressCollection.find(new Document("student_id", studentId));
    
            Document studentThemeProgressDoc = new Document("student_id", studentId);
    
            for (Document exerciseProgress : exerciseProgresses) {
                if (exerciseProgress.get("exercise_id") == null) {
                    continue;
                }
                String exerciseId = exerciseProgress.get("exercise_id").toString();
                int score = exerciseProgress.getInteger("score");
    
                Document exercise = exercisesCollection.find(new Document("_id", new ObjectId(exerciseId))).first();
    
                String themeId = exercise.get("theme_id").toString();
    
                Document theme = themesCollection.find(new Document("_id", new ObjectId(themeId))).first();
                String themeName = theme.get("name").toString();
    
                Integer currentScore = studentThemeProgressDoc.getInteger(themeName);
                if (currentScore == null) {
                    studentThemeProgressDoc.append(themeName, score);
                } else {
                    studentThemeProgressDoc.put(themeName, currentScore + score);
                }
            }
    
            newStudentThemeProgressDocs.add(studentThemeProgressDoc);
        }
    
        studentThemeProgressCollection.insertMany(newStudentThemeProgressDocs);
    }

    public void createStudentPracticeThemeProgress() {
        MongoCollection<Document> studentsCollection = database.getCollection("students");
        MongoCollection<Document> practicesProgressCollection = database.getCollection("practise_progress");
        MongoCollection<Document> practicesCollection = database.getCollection("practices");
        MongoCollection<Document> themesCollection = database.getCollection("themes");
        MongoCollection<Document> studentPracticeProgressCollection = database.getCollection("student_practice_themes_progress");
    
        FindIterable<Document> students = studentsCollection.find();
    
        List<Document> newStudentPracticeProgressDocs = new ArrayList<>();
    
        for (Document student : students) {
            String studentId = student.get("_id").toString();
    
            FindIterable<Document> practiceProgresses = practicesProgressCollection.find(new Document("student_id", studentId));
    
            Document studentPracticeProgressDoc = new Document("student_id", studentId);
    
            for (Document practiceProgress : practiceProgresses) {
                if (practiceProgress.get("practice_id") == null) {
                    continue;
                }
                String practiceId = practiceProgress.get("practice_id").toString();
                int score = practiceProgress.getInteger("score");
    
                Document practice = practicesCollection.find(new Document("_id", new ObjectId(practiceId))).first();
    
                String themeId = practice.get("theme_id").toString();
    
                Document theme = themesCollection.find(new Document("_id", new ObjectId(themeId))).first();
                String themeName = theme.get("name").toString();
    
                Integer currentScore = studentPracticeProgressDoc.getInteger(themeName);
                if (currentScore == null) {
                    studentPracticeProgressDoc.append(themeName, score);
                } else {
                    studentPracticeProgressDoc.put(themeName, currentScore + score);
                }
            }
    
            newStudentPracticeProgressDocs.add(studentPracticeProgressDoc);
        }
    
        studentPracticeProgressCollection.insertMany(newStudentPracticeProgressDocs);
    }

    public class DatasetParser {

        public DefaultCategoryDataset getBirthDatesDataset() {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
            try {
                FindIterable<Document> students = studentCollection.find();
                Map<Integer, Integer> ageCounts = new HashMap<>();
                for (Document student : students) {
                    if (student.containsKey("bdate")) {
                        String bdate = student.getString("bdate");
                        if (bdate != null) {
                            String[] bdateParts = bdate.split("\\.");
                            if (bdateParts.length == 3) {
                                int birthYear = Integer.parseInt(bdateParts[2]);
                                int age = LocalDate.now().getYear() - birthYear;
                                ageCounts.put(age, ageCounts.getOrDefault(age, 0) + 1);
                            }
                        }
                    }
                }
                List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>(ageCounts.entrySet());
                sortedEntries.sort(Map.Entry.comparingByValue());
        
                for (Map.Entry<Integer, Integer> ageCount : sortedEntries) {
                    dataset.addValue(ageCount.getValue(), "Age " + ageCount.getKey(), "");
                }
            } catch (MongoException me) {
                System.err.println("Operation failed: " + me.getMessage());
            }
            return dataset;
        }

        public DefaultCategoryDataset getCountriesDataset() {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
            try {
                FindIterable<Document> students = studentCollection.find();
                Map<String, Integer> countryCounts = new HashMap<>();
                for (Document student : students) {
                    if (student.containsKey("country")) {
                        String country = student.getString("country");
                        if (country != null) {
                            countryCounts.put(country, countryCounts.getOrDefault(country, 0) + 1);
                        }
                    }
                }
                List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(countryCounts.entrySet());
                sortedEntries.sort(Map.Entry.comparingByValue());

                for (Map.Entry<String, Integer> countryCount : sortedEntries) {
                    if (countryCount.getKey() != null) {
                        dataset.addValue(countryCount.getValue(), countryCount.getKey(), "");
                    }
                }
            } catch (MongoException me) {
                System.err.println("Operation failed: " + me.getMessage());
            }
        
            return dataset;
        }

        public DefaultCategoryDataset getSexDistributionDataset() {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
            try {
                FindIterable<Document> students = studentCollection.find();
                Map<String, Integer> sexCounts = new HashMap<>();
                for (Document student : students) {
                    if (student.containsKey("sex")) {
                        String sex = student.getString("sex");
                        if (sex != null) {
                            sexCounts.put(sex, sexCounts.getOrDefault(sex, 0) + 1);
                        }
                    }
                }
        
                List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(sexCounts.entrySet());
                sortedEntries.sort(Map.Entry.comparingByValue());
        
                for (Map.Entry<String, Integer> sexCount : sortedEntries) {
                    dataset.addValue(sexCount.getValue(), "Sex " + sexCount.getKey(), "");
                }
            } catch (MongoException me) {
                System.err.println("Operation failed: " + me.getMessage());
            }
        
            return dataset;
        }

        public DefaultCategoryDataset getSchoolsDataset() {
            MongoCollection<Document> studentsCollection = database.getCollection("students");
        
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            HashMap<String, Integer> schoolCounts = new HashMap<>();
        
            FindIterable<Document> students = studentsCollection.find();
            for (Document student : students) {
                List<String> schools = (List<String>) student.get("schools");
                if (schools != null) {
                    for (String school : schools) {
                        schoolCounts.put(school, schoolCounts.getOrDefault(school, 0) + 1);
                    }
                }
            }
        
            for (Map.Entry<String, Integer> entry : schoolCounts.entrySet()) {
                if (entry.getValue() > 1) {
                    dataset.addValue(entry.getValue(), "Number of Students", entry.getKey());
                }
            }
        
            return dataset;
        }

        public DefaultCategoryDataset getThemesDataset() {
            MongoCollection<Document> themesCollection = database.getCollection("themes");
            MongoCollection<Document> practiceProgressCollection = database.getCollection("student_practice_themes_progress");
        
            HashMap<String, List<Integer>> themeScores = new HashMap<>();
        
            FindIterable<Document> themes = themesCollection.find();
            for (Document theme : themes) {
                String themeName = theme.getString("name");
                themeScores.put(themeName, new ArrayList<>());
            }
        
            FindIterable<Document> practiceProgress = practiceProgressCollection.find();
            for (Document progress : practiceProgress) {
                for (String key : progress.keySet()) {
                    if (!key.equals("_id") && !key.equals("student_id") && themeScores.containsKey(key)) {
                        int score = progress.getInteger(key);
                        themeScores.get(key).add(score);
                    }
                }
            }
        
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
            List<Map.Entry<String, List<Integer>>> entries = new ArrayList<>(themeScores.entrySet());
            entries.sort((entry1, entry2) -> {
                double averageScore1 = entry1.getValue().stream().mapToInt(Integer::intValue).average().orElse(0);
                double averageScore2 = entry2.getValue().stream().mapToInt(Integer::intValue).average().orElse(0);
                return Double.compare(averageScore2, averageScore1);
            });
        
            for (Map.Entry<String, List<Integer>> entry : entries) {
                double averageScore = entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(0);
                dataset.addValue(averageScore, "Average Score", entry.getKey());
            }
        
            return dataset;
        }

        public DefaultCategoryDataset getAverageScoreBySexDataset() {
            MongoCollection<Document> summaryProgressCollection = database.getCollection("summary_progress");
            MongoCollection<Document> studentsCollection = database.getCollection("students");
        
            Map<String, String> studentSexes = new HashMap<>();
            FindIterable<Document> students = studentsCollection.find();
            for (Document student : students) {
                studentSexes.put(student.getString("_id"), student.getString("sex"));
            }
        
            List<Integer> maleScores = new ArrayList<>();
            List<Integer> femaleScores = new ArrayList<>();
        
            FindIterable<Document> summaryProgress = summaryProgressCollection.find();
            for (Document progress : summaryProgress) {
                String studentId = progress.getString("student_id");
                int totalScore = Integer.parseInt(progress.getString("practise"));
        
                String sex = studentSexes.get(studentId);
                if (sex != null) {
                    if ("Male".equals(sex)) {
                        maleScores.add(totalScore);
                    } else if ("Female".equals(sex)) {
                        femaleScores.add(totalScore);
                    }
                }
            }
        
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            double averageMaleScore = maleScores.stream().mapToInt(Integer::intValue).average().orElse(0);
            double averageFemaleScore = femaleScores.stream().mapToInt(Integer::intValue).average().orElse(0);
            dataset.addValue(averageMaleScore, "Average Score", "Male");
            dataset.addValue(averageFemaleScore, "Average Score", "Female");
        
            return dataset;
        }
    }
}