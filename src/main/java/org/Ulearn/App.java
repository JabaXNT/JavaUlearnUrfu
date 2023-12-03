package org.Ulearn;

import com.opencsv.exceptions.CsvValidationException;

import org.Ulearn.Database.DatabaseConnection;
import org.Ulearn.Parser.ParserCSV;

import java.io.IOException;
import java.util.Arrays;

public class App
{
    public static void main( String[] args ) throws CsvValidationException, IOException {
        String csvFilename = "basicprogramming.csv";
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.connection();
        ParserCSV parser = new ParserCSV(csvFilename);
        // dbConnection.insertStudents(parser.parseStudents());
        // dbConnection.insertSummaryProgress(parser.getSummaryProgressList());
        dbConnection.printStudentSummaryProgress("Аль-Рифаие Хуссейн");

        // System.out.println(parser.getSummaryProgressList());
    }
}
