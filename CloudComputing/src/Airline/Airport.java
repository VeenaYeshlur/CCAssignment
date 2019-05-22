package Airline;

/***************************************************************************
 * Airport: returns latitude and longitude
 ***************************************************************************/

public class Airport {
	String code;
	double lat, lon;
	
	Airport(String _code, String _lat, String _lon){
		code = _code;
		lat = Double.parseDouble(_lat);
		lon = Double.parseDouble(_lon);
	}
		
	public double getLatitude(){
		return lat;
	}
	
	public double getLongitude(){
		return lon;
	}
	public String toString(){
		return code + " " + lat + " " + lon;
	}
	
	public String getCode(){
		return code;
	}
	
}
