package samsara;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CsvReader {

    private final static String birthYear = "1993";
    private final static String userType = "Customer";
    private final static String genderMale = "Male";
    private final static String genderFemale = "Female";
    private final static String stationNamePartial = "BART";
    private final static int shortestRidesValue = 100;
    private final static File csvFile =
            new File("/home/matt/IdeaProjects/trips/src/main/resources/trips.csv");
    private final static CsvMapper csvMapper = new CsvMapper();
    // Create a schema for our class that recognizes the header, so it will not be read into MappingIterator
    private final static CsvSchema schema = csvMapper.schemaFor(Trip.class).withHeader();

    // Standard main() method, executes the methods that process the csv data. Runs via Gradle 'application' plugin
    public static void main(String args[]) throws IOException, ParseException {

        // Note that I designed these functions to take alternate parameters. So say you want to check another gender,
        // stationNamePartial, etc - you can do so
        findMostPopularStartStationName(createMappingIterator());
        countFemaleUsersByBirthYear(createMappingIterator(), birthYear, genderFemale);
        countUserType(createMappingIterator(), userType);
        countEndStationNameStringOccurrences(createMappingIterator(), stationNamePartial);
        calculateAverageRideTime(createMappingIterator());
        findShortestRidesByGender(createMappingIterator(), genderMale, shortestRidesValue);
        checkMatchingStartAndEndStations(createMappingIterator());
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

    public static void countFemaleUsersByBirthYear(MappingIterator<Trip> mi, String birthYear, String gender) {
        // Compare all female users and their birth year and count only the applicable ones
        int count = 0;
        while (mi.hasNext()) {
            if(mi.next().memberGender.contains(gender) && mi.next().memberBirthYear.contains(birthYear)) count++;
        }
        System.out.println("Number of female users born in " + birthYear + ": " + count);
    }

    public static void countUserType(MappingIterator<Trip> mi, String userType) {
        // Count the number of times the user type appears
        int count = 0;
        while (mi.hasNext()) {
            if(mi.next().userType.contains(userType)) count++;
        }
        System.out.println("Number of users of type 'Customer': " + count);
    }

    public static void countEndStationNameStringOccurrences(MappingIterator<Trip> mi, String value) {
        // Count the number of times a given string is contained in the 'endStationName' variable
        int count = 0;
        while (mi.hasNext()) {
            if(mi.next().endStationName.contains(value)) count++;
        }
        System.out.println("Number of occurrences of String " + value + " in End Station Names: " + count);
    }

    // Since the library is giving us back String data, we need to convert the rides to dates to do the calculation
    // See helper method below
    public static void calculateAverageRideTime(MappingIterator<Trip> mi) throws IOException, ParseException {
        // How many trips total, since readAll() consumes the iterator, we need another one
        int count = createMappingIterator().readAll().size();
        long totalTripSeconds = 0;
        // Add all ride times (duration_sec) together, and divide by how many trips total
        while (mi.hasNext()) {
            totalTripSeconds += Long.parseLong(mi.next().durationSec);
        }
        long averageTripSeconds = totalTripSeconds / count;
        System.out.println("Average trip time: " + averageTripSeconds + " seconds or "
                + averageTripSeconds / 60 + " minutes");
    }

    public static void findShortestRidesByGender(MappingIterator<Trip> mi, String gender, int loops) throws IOException {
        // We will use a list, so that we can sort it easily
        List<Trip> tripList = new ArrayList<>(mi.readAll());

        // We have to use  Collections.sort with a comparator as integers. Sorting by the provided String value
        // Results in the numbers not being in order (ex: 100 takes precedence over 66)
        Collections.sort(tripList, new Comparator<Trip>() {
            @Override
            public int compare(Trip trip, Trip t1) {
                return Integer.valueOf(trip.durationSec).compareTo(Integer.valueOf(t1.durationSec));
            }
        });

        // Loop over the first 100 items, which are the smallest rides. Check if the riders are male, increase count
        int count = 0;
        for(int i = 0; i < loops; i++) {
            if(tripList.get(i).memberGender.contains(genderMale)) count++;
        }
        System.out.println("Of the shortest " + loops + " rides, there were " + count + " male riders");
    }

    public static void checkMatchingStartAndEndStations(MappingIterator<Trip> mi) throws IOException {
        int count = 0;
        // For each Trip, check the start and end name of the station. If they match, increment the count
        while (mi.hasNext()){
            // Some values are null (or a string 'NULL'), requiring a check for a valid match
            if(!mi.next().startStationName.contains("NULL") && !mi.next().endStationName.contains("NULL")
            && mi.next().startStationName != null && mi.next().endStationName != null) {
                // If they match, increase the count
                if (mi.next().startStationName == mi.next().endStationName) count++;
            }
        }
        System.out.println("The number of trips that start and end at the same station is: " + count);
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
