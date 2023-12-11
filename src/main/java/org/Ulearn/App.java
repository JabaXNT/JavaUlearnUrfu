package org.Ulearn;

import com.opencsv.exceptions.CsvValidationException;
import org.Ulearn.Database.DatabaseController;
import org.Ulearn.Database.DatabaseController.DatasetParser;
import org.Ulearn.GraphExport.DatasetGraphs;
import org.Ulearn.Parser.ParserCSV;
import org.Ulearn.VkAPI.vkGroupParse;

import java.io.IOException;
import java.net.URISyntaxException;

public class App
{
    private static final String CSV_FILENAME = "basicprogramming.csv";
    private DatabaseController dbConnection;
    private DatasetParser dsParser;
    private DatasetGraphs graph;
    private ParserCSV parser;
    private vkGroupParse vkParser;

    public static void main( String[] args ) throws CsvValidationException, IOException, URISyntaxException {
        App app = new App();
        app.run();
    }

    public App() {
        dbConnection = new DatabaseController();
        dsParser = dbConnection.new DatasetParser();
        graph = new DatasetGraphs();
        parser = new ParserCSV(CSV_FILENAME);
        vkParser = new vkGroupParse();
    }

    public void run() throws CsvValidationException, IOException, URISyntaxException {
        dbConnection.connection();

        // updateDatabase();
        // parseVkData();
        createGraphs();

        dbConnection.closeConnection();
    }

    private void createGraphs() {
        graph.createBarChart(dsParser.getCountriesDataset(), "Number of students from each country", "Country", "Number of students", 15);
        graph.createBarChart(dsParser.getBirthDatesDataset(), "Number of students born in each year", "Year", "Number of students", 10);
        graph.createBarChart(dsParser.getSchoolsDataset(), "Number of students from different schools", "Schools", "Number of students", 1);
        graph.createBarChart(dsParser.getThemesDataset(), "Themes average score", "Theme", "Average score", 10);
        graph.createBarChart(dsParser.getAverageScoreBySexDataset(), "Sex average score", "Sex", "Average score", 50);
    }

    private void parseVkData() throws IOException, URISyntaxException {
        vkParser.parseUserData();
        dbConnection.updateStudentsWithVkAPI(vkParser.parseUserData());
    }

    private void updateDatabase() throws CsvValidationException, IOException {
        dbConnection.insertStudents(parser.parseStudents());
        dbConnection.insertThemeExercisesAndPractices(parser.parseExercisesByThemes(), parser.parsePracticesByThemes());
        dbConnection.insertAllStudentsPractiseProgress(parser.parsePracticesScoresByUid());
        dbConnection.insertAllStudentsExerciseProgress(parser.parseExercisesScoresByUid());
        dbConnection.createStudentExerciseThemeProgress();
        dbConnection.createStudentPracticeThemeProgress();
        dbConnection.insertSummaryProgress(parser.getSummaryProgressList());
    }
}
