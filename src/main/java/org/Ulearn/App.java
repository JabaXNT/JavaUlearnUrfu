package org.Ulearn;

import com.opencsv.exceptions.CsvValidationException;

import org.Ulearn.Database.DatabaseConnection;
import org.Ulearn.Parser.ParserCSV;
import org.Ulearn.VkAPI.vkGroupParse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class App
{
    public static void main( String[] args ) throws CsvValidationException, IOException, URISyntaxException {
        String csvFilename = "basicprogramming.csv";
        DatabaseConnection dbConnection = new DatabaseConnection();
        vkGroupParse vkParser = new vkGroupParse();
        dbConnection.connection();
        ParserCSV parser = new ParserCSV(csvFilename);
         dbConnection.insertStudents(parser.parseStudents());
        // dbConnection.insertSummaryProgress(parser.getSummaryProgressList());
        dbConnection.printStudentSummaryProgress("Аль-Рифаие Хуссейн");
        dbConnection.updateStudentsWithBdate(vkParser.parseBdate());
        // System.out.println(parser.getSummaryProgressList());
    }
}
