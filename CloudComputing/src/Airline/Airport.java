package Airline;

/***************************************************************************
 * Airport: returns Code, Latitude and Longitude
 ***************************************************************************/

public class Airport {
	String code;
	double lat, lon;
	
	// constructor to return Airport Details
	Airport(String _code, String _lat, String _lon){
		code = _code;
		lat = Double.parseDouble(_lat);
		lon = Double.parseDouble(_lon);
	}
	
	//Get Latitude
	public double getLatitude(){
		return lat;
	}
	
	//Get Longitude
	public double getLongitude(){
		return lon;
	}
	public String toString(){
		return code + " " + lat + " " + lon;
	}
	
	//Get Airport Code
	public String getCode(){
		return code;
	}
	
}
