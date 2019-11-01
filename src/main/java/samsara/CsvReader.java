package samsara;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class CsvReader {

    private final static String birthYear = "1993";
    private final static String userType = "Customer";
    private final static String stationNamePartial = "BART";
    private final static File csvFile =
            new File("/home/matt/IdeaProjects/trips/src/main/resources/trips.csv");
    private final static CsvMapper csvMapper = new CsvMapper();
    // Create a schema for our class that recognizes the header, so it will not be read into MappingIterator
    private final static CsvSchema schema = csvMapper.schemaFor(Trip.class).withHeader();

    public static void main(String args[]) throws IOException {
        findMostPopularStartStationName(createMappingIterator());
        countFemaleUsersByBirthYear(createMappingIterator(), birthYear);
        countUserType(createMappingIterator(), userType);
        countEndStationNameStringOccurrences(createMappingIterator(), stationNamePartial);
    }

    public static void findMostPopularStartStationName(MappingIterator<Trip> mi) {
        // Count occurrences and populate the HashMap
        Map<String, Integer> startStationNamesMap = new HashMap<>();
        while (mi.hasNext()) {
            String s = mi.next().startStationName;
            Integer count = startStationNamesMap.get(s);
            if(count == null) startStationNamesMap.put(s, 1); // If we haven't seen this String, add 1
            else startStationNamesMap.put(s, count + 1); // If we have seen this String, add count + 1
        }

        // Sort the HasMap, use the Iterator to get the first item (highest count value)
        startStationNamesMap = sortByValue(startStationNamesMap);
        Iterator it = startStationNamesMap.keySet().iterator();
        System.out.println("Most popular starting station name:" + " " + it.next());
    }

    public static void countFemaleUsersByBirthYear(MappingIterator<Trip> mi, String birthYear) {
        // Compare all female users and their birth year and count only the applicable ones
        int count = 0;
        while (mi.hasNext()) {
            if(mi.next().memberGender.contains("Female") && mi.next().memberBirthYear.contains(birthYear)) count++;
        }
        System.out.println("Number of female users born in " + birthYear + ": " + count);
    }

    public static void countUserType(MappingIterator<Trip> mi, String userType) {
        int count = 0;
        while (mi.hasNext()) {
            if(mi.next().userType.contains(userType)) count++;
        }
        System.out.println("Number of users of type 'Customer': " + count);
    }

    public static void countEndStationNameStringOccurrences(MappingIterator<Trip> mi, String value) {
        int count = 0;
        while (mi.hasNext()) {
            if(mi.next().endStationName.contains(value)) count++;
        }
        System.out.println("Number of occurrences of String " + value + " in End Station Names: " + count);
    }

    // The MappingIterator can only iterate once, so we need a new one each time we want to iterate
    public static MappingIterator<Trip> createMappingIterator() throws IOException {
        // Read the values from the csv file and input into mappingIterator
        MappingIterator mappingIterator = csvMapper.readerFor(Trip.class).with(schema).readValues(csvFile);
        return mappingIterator;
    }
    // Method to sort a Map from highest to lowest
    public static Map<String, Integer> sortByValue(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
