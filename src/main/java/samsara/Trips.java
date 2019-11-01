package samsara;

import java.math.BigDecimal;

/**
 * This is a POJO - Plain Old Java Object to serve as a data structure to hold all the fields from the CSV file
 */
public class Trips {

    public int durationSec;
    public String startTime;
    public String endTime;
    public int startStationID;
    public String startStationName;
    public BigDecimal startStationLatitude;
    public BigDecimal startStationLongitude;
    public int endStationID;
    public String endStationName;
    public BigDecimal endStationLatitude;
    public BigDecimal endStationLongitude;
    public int bikeID;
    public String userType;
    public int memberBirthYear;
    public String memberGender;
    public String bikeShareForAllTrip;

}
