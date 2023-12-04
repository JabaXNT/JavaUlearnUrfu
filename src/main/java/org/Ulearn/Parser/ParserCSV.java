package org.Ulearn.Parser;

import com.opencsv.CSVReader;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import org.Ulearn.DbEntity.Student;
import org.Ulearn.DbEntity.SummaryProgress;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.Charset;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParserCSV {
    private String fileName;

    public ParserCSV(String fileName) {
        setFileName(fileName);
    }

    public void setFileName(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            this.fileName = fileName;
            return;
        }
        throw new IllegalArgumentException();
    }

    public ArrayList<Student> parseStudents() throws IOException, CsvValidationException {
        try (CSVReader reader = createCsvReaderWithSkipLines(3)) {
            String[] headers = readCsvHeaders();
            int fullNameIndex = ArrayUtils.indexOf(headers, "Фамилия Имя");
            int ulearnIdIndex = ArrayUtils.indexOf(headers, "Ulearn id");
            int emailIndex = ArrayUtils.indexOf(headers, "Эл. почта");
            int groupIndex = ArrayUtils.indexOf(headers, "Группа");
    
            ArrayList<Student> students = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                String name = row[fullNameIndex];
                String id = row[ulearnIdIndex];
                String email = row[emailIndex];
                String group = row[groupIndex];
                students.add(new Student(id, name, email, group));
            }
            return students;
        }
    }

    public ArrayList<SummaryProgress> getSummaryProgressList() throws IOException, CsvValidationException {
        ArrayList<SummaryProgress> summaryProgressList = new ArrayList<>();
        try (CSVReader reader = createCsvReaderWithSkipLines(3)) {
            String[] headers = readCsvHeaders();
            int ulearnIdIndex = ArrayUtils.indexOf(headers, "Ulearn id");

            String[] row;
            while ((row = reader.readNext()) != null) {
                String ulearn_id = row[ulearnIdIndex];
                String activity = row[4];
                String exercise = row[5];
                String homework = row[6];
                String seminars = row[7];
                summaryProgressList.add(new SummaryProgress(ulearn_id, activity, exercise, homework, seminars));
            }
        }
        return summaryProgressList;
    }

    public ArrayList<String> parseThemes() throws IOException, CsvValidationException {
        try (CSVReader reader = createCsvReader()) {
            String[] row = reader.readNext();
            return IntStream.range(1, row.length)
                .mapToObj(i -> row[i])
                .filter(s -> !s.isEmpty() && !s.equals("За весь курс") && !s.equals("Преподавателю о курсе"))
                .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public ArrayList<String> parseExercises() throws IOException, CsvValidationException {
        try (CSVReader reader = createCsvReaderWithSkipLines(1)) {
            String[] row = reader.readNext();
            return Arrays.stream(row)
                .filter(s -> s.startsWith("Упр: "))
                .map(s -> s.substring(5))
                .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public ArrayList<String> parsePractices() throws IOException, CsvValidationException {
        try (CSVReader reader = createCsvReaderWithSkipLines(1)) {
            String[] row = reader.readNext();
            return Arrays.stream(row)
                .filter(s -> s.startsWith("ДЗ: "))
                .map(s -> s.substring(4))
                .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public HashMap<String, HashMap<String, Integer>> parseExercisesScores() throws CsvValidationException, IOException {
        try (CSVReader reader = createCsvReaderWithSkipLines(1)) {
            String[] row = reader.readNext();
            if (row == null) {
                return new HashMap<>();
            }
            HashMap<String, Integer> colIndexes = mapColumnNamesToIndexes(parseExercises(), row, "Упр: ");
            return getUidScores(colIndexes);
        }
    }

    public HashMap<String, HashMap<String, Integer>> parsePracticesScores() throws CsvValidationException, IOException {
        try (CSVReader reader = createCsvReaderWithSkipLines(1)) {
            String[] row = reader.readNext();
            if (row == null) {
                return new HashMap<>();
            }
            HashMap<String, Integer> colIndexes = mapColumnNamesToIndexes(parsePractices(), row, "ДЗ: ");
            return getUidScores(colIndexes);
        }
    }

    private String[] readCsvHeaders() throws IOException, CsvValidationException {
        CSVReader reader = createCsvReaderWithSkipLines(1);
        return reader.readNext();
    }

    private CSVReader createCsvReader() throws IOException {
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        FileReader fileReader = new FileReader(fileName, Charset.forName("UTF-8"));
        return new CSVReaderBuilder(fileReader).withCSVParser(parser).build();
    }

    private CSVReader createCsvReaderWithSkipLines(int skip) throws IOException {
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        FileReader fileReader = new FileReader(fileName, Charset.forName("UTF-8"));
        return new CSVReaderBuilder(fileReader).withCSVParser(parser).withSkipLines(skip).build();
    }

    private HashMap<String, Integer> mapColumnNamesToIndexes(ArrayList<String> values, String[] array, String headerPrefix) {
        HashMap<String, Integer> indexes = new HashMap<String, Integer>();
        for (String e: values) {
            indexes.put(e, ArrayUtils.indexOf(array, headerPrefix + e));
        }
        return indexes;
    }

    private HashMap<String, HashMap<String, Integer>> getUidScores(HashMap<String, Integer> colIndexes) throws IOException, CsvValidationException {
        HashMap<String, HashMap<String, Integer>> result = new HashMap<String, HashMap<String, Integer>>();
        CSVReader reader = createCsvReaderWithSkipLines(1);
        String[] headerRow = reader.readNext();
        reader.readNext();
        for (String key: colIndexes.keySet()) {
            result.put(key, new HashMap<String, Integer>());
        }
        int uidIndex = ArrayUtils.indexOf(headerRow, "Ulearn id");
        String[] row;
        while ((row = reader.readNext()) != null) {
            for (String header: colIndexes.keySet()) {
                Integer index = colIndexes.get(header);
                result.get(header).put(row[uidIndex], Integer.valueOf(row[index]));
            }
        }
        return result;
    }
}