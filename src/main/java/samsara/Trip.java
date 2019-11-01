package samsara;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This is a POJO - Plain Old Java Object to serve as a data structure to hold all the fields from the CSV file
 * We will map each line from the CSV into an instance of this Object in a type of Collection
 */

@JsonPropertyOrder({"durationSec", "startTime", "endTime", "startStationID", "startStationName", "startStationLatitude",
        "startStationLongitude", "endStationID", "endStationName", "endStationLatitude", "endStationLongitude",
        "bikeID", "userType", "memberBirthYear", "memberGender", "bikeShareForAllTrip"})
public class Trip {

    public String durationSec;
    public String startTime;
    public String endTime;
    public String startStationID;
    public String startStationName;
    public String startStationLatitude;
    public String startStationLongitude;
    public String endStationID;
    public String endStationName;
    public String endStationLatitude;
    public String endStationLongitude;
    public String bikeID;
    public String userType;
    public String memberBirthYear;
    public String memberGender;
    public String bikeShareForAllTrip;

    public Trip() {
        // Our Jackson library requires a public no argument constructor
    }

}
