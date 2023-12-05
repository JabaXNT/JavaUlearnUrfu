package org.Ulearn;

import com.opencsv.exceptions.CsvValidationException;

import org.Ulearn.Database.DatabaseController;
import org.Ulearn.Database.DatabaseController.DatasetParser;
import org.Ulearn.GraphExport.DatasetGraphs;
import org.Ulearn.Parser.ParserCSV;
import org.Ulearn.VkAPI.vkGroupParse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class App
{
    public static void main( String[] args ) throws CsvValidationException, IOException, URISyntaxException {
        String csvFilename = "basicprogramming.csv";
        DatabaseController dbConnection = new DatabaseController();
        vkGroupParse vkParser = new vkGroupParse();
        DatasetGraphs graph = new DatasetGraphs();
        dbConnection.connection();
        ParserCSV parser = new ParserCSV(csvFilename);
        // System.out.println(parser.parseExercisesScoresByUid());
        // System.out.println(parser.parsePracticesByThemes());
        // graphs.createBarChart(dbConnection.getCountriesDataset(), "Number of students from each country", "Country", "Number of students");
        // graphs.createBarChart(dbConnection.getBirthDatesDataset(), "Number of students born in each year", "Year", "Number of students");
        // dbConnection.insertStudents(parser.parseStudents());
        // dbConnection.insertThemeExercisesAndPractices(parser.parseExercisesByThemes(), parser.parsePracticesByThemes());
        // dbConnection.insertAllStudentsPractiseProgress(parser.parsePracticesScoresByUid());
        // dbConnection.insertAllStudentsExerciseProgress(parser.parseExercisesScoresByUid());
        // dbConnection.createStudentExerciseThemeProgress();
        // dbConnection.createStudentPracticeThemeProgress();
        // dbConnection.insertSummaryProgress(parser.getSummaryProgressList());
//        dbConnection.printStudentSummaryProgress("Абдалов Сергей");
        // vkParser.parseUserData();
        // dbConnection.updateStudentsWithVkAPI(vkParser.parseUserData());
        // System.out.println(parser.getSummaryProgressList());
    }
}
