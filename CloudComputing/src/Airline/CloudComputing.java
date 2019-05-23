package Airline;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class CloudComputing {

	/***************************************************************************
	 * TASK 1:  Determine the number of flights from each airport, include a
	 * list of any airports not used.
	 ***************************************************************************/
	
	public static void GetNumberOfFlights() {
		
		TreeMap<String, Integer> simpleMap = new TreeMap<String, Integer>();
		
		try {
			// Read input file provided
			BufferedReader buffer = new BufferedReader(new FileReader(inputFile));
			String line;
			ArrayList<String> unMatchedAirports = new ArrayList<String>();
			while ((line = buffer.readLine()) != null) {
				String[] split = line.split(",");
				if (regexThis(split, 1)) {
					if (simpleMap.containsKey(split[2])) {
						// calculate number of flights from each airport
						simpleMap.put(split[2], simpleMap.get(split[2]) + 1);
					} else {
						simpleMap.put(split[2], 1);
					}
				} else {
					unMatchedAirports.add(split[2]);
				}
			}
			buffer.close();

			// Write output to file
			//FileWriter fw = new FileWriter(outputFile);
			for (Entry<String, Integer> entry : simpleMap.entrySet()) {
				//System.out.println(entry.getKey() + " : " + entry.getValue());
				log(entry.getKey() + " : " + entry.getValue(), fw);
				log(System.getProperty("line.separator"), fw);
			}

			System.out.println("");
			log("Unused airports (" + unMatchedAirports.size() + "): " + System.getProperty("line.separator"), fw);
			for (String airport : unMatchedAirports) {
				if (!simpleMap.containsKey(airport)) {
					log(airport + ", ", fw);
				}
			}

			log(System.getProperty("line.separator"), fw);

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * TASK 2 : Create a list of flights based on the Flight id, this output
	 * should include the passenger Id, relevant IATA/FAA codes, the departure
	 * time, the arrival time (times to be converted to HH:MM:SS format), and
	 * the flight times.
	 ***************************************************************************/
	// Map needs to be shared with Task 3
	static TreeMap<String, List<Flight>> flights = new TreeMap<String, List<Flight>>();

	public static void CreateListOfFlights() {
		
		// Map
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(inputFile));
			String line;
			while ((line = buffer.readLine()) != null) {
				String[] split = line.split(",");
				if (regexThis(split, 2)) {
					try {
						if (flights.get(split[1]) == null) {
							flights.put(split[1], new ArrayList<Flight>());
						}
						flights.get(split[1])
								.add(new Flight(split[0], split[1], split[2], split[3], split[4], split[5]));
					} catch (Exception e) {
						System.out.println("Failed to add <" + split[2].toString() + "> to map.");
					}
				}
			}
			buffer.close();
			log(ls, fw);
			
			// Reduce 
			for (Map.Entry<String, List<Flight>> entry : flights.entrySet()) {
				log(entry.getKey(), fw);
				log(ls, fw);
				for (Flight detail : entry.getValue()) {
					log(detail.toString(), fw);
					log(ls, fw);
				}
				log(ls, fw);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * TASK 3 -- Calculate the number of passengers on each flight.
	 ***************************************************************************/
	public static void CalculateTotalPassengers() {
		
		log("Passenger Count"+ls, fw);
		log("---------------------------------"+ls, fw);
		
		// MapReduce
		try {
			TreeMap<String, Integer> passengerCount = new TreeMap<String, Integer>();
			BufferedReader buffer = new BufferedReader(new FileReader(inputFile));
			String line;
			while ((line = buffer.readLine()) != null) {
				String[] split = line.split(",");
				if (regexThis(split, 3)) {
					if (passengerCount.get(split[1]) == null) {
						passengerCount.put(split[1], 1);
					} else {
						passengerCount.put(split[1], passengerCount.get(split[1]) + 1);
					}
				}
			}
			buffer.close();
			
			for (Map.Entry<String, Integer> entry : passengerCount.entrySet()) {
				log(entry.getKey() + ": " + entry.getValue(), fw);
				log(ls, fw);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * TASK 4 : Calculate the line-of-sight(nautical) miles for each flight and
	 * the total travelled by each passenger.
	 ***************************************************************************/
	public static void CalculateDistanceTravelled() {
		
		// Read top 30 airports
		// Calculate distance travelled by each passenger for each flight
		// Reduce each passenger to be combined 
		
		log(ls, fw);
		log("Distance Travelled Per Flight"+ls, fw);
		log("---------------------------------", fw);
		
		// Map
		try {
			TreeMap<String, Airport> airports = new TreeMap<String, Airport>();
			BufferedReader br = new BufferedReader(new FileReader(secondInput));
			String line;

			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");
				if (regexThis(split, 4)) {
					try {
						if (airports.get(split[1]) == null) {
							airports.put(split[1], new Airport(split[1], split[2], split[3]));
						}
						// airports.get(split[1]).add(new Airport(split[1],
						// split[2], split[3]));
					} catch (Exception e) {
						System.out.println("Failed to add <" + split[0] + "> to map.");
					}
				}
			}
			br.close();
			log(ls, fw);
			// Reduce
			TreeMap<String, Double> distances = new TreeMap<String, Double>();
			// calculate the distance between the airports per flight.
			for (Map.Entry<String, List<Flight>> entry : flights.entrySet()) {
				for (Flight aFlight : entry.getValue()) {
					try {
						double x1 = ((Airport) airports.get(aFlight.getFrom())).getLatitude();
						double y1 = ((Airport) airports.get(aFlight.getFrom())).getLongitude();
						double x2 = ((Airport) airports.get(aFlight.getTo())).getLatitude();
						double y2 = ((Airport) airports.get(aFlight.getTo())).getLongitude();

						double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
						 // Save each distance in a map
						distances.put(entry.getKey(), distance);
						
					} catch (Exception e) {
						log("Failed to calculate distance for a flight " + aFlight.getFrom() + " to "
								+ aFlight.getTo() + ".", fw);
						log(ls, fw);
					}
				}
			}
			// Reduce Flight ID distances to per record.
			Map<String, Double> reducer = new TreeMap<String, Double>();
			for (Map.Entry<String, Double> entry : distances.entrySet()) {
				reducer.put(entry.getKey(), entry.getValue());
			}
			for (Map.Entry<String, Double> entry : reducer.entrySet()) {
				log(entry.getKey() + " > " + entry.getValue() + " nautical miles.", fw);
				log(ls, fw);
			
			}
			
			log(ls, fw);
			log("Distance Travelled Per Passenger"+ls, fw);
			log("---------------------------------"+ls, fw);
			
			// Store Key-Value pairs of passengers and distances travelled.
			TreeMap<String, Double> passengerDistance = new TreeMap<String, Double>();
			for (Map.Entry<String, List<Flight>> aFlight : flights.entrySet()) {
					for(Flight passenger : aFlight.getValue()){
						if (passengerDistance.get(passenger.getPassengerID()) == null) {
							passengerDistance.put(passenger.getPassengerID(), distances.get(passenger.getFlightID()));
						} else {
							double distance = passengerDistance.get(passenger.getPassengerID());
							passengerDistance.put(passenger.getPassengerID(), distance + distances.get(passenger.getFlightID()));
						}
					}
			}
			
			for (Map.Entry<String, Double> entry : passengerDistance.entrySet()) {
				log(entry.getKey() + " travelled " + entry.getValue() + " nautical miles.", fw);
				log(ls, fw);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 *                             Main Program 
	 * 
	 ****************************************************************************/	
	static String ls = System.getProperty("line.separator");
	static String inputFile = "";
	static String secondInput = "";
	static String outputFile = "";
	static FileWriter fw;
	
	public static void main(String[] args) throws FileNotFoundException {

		try {
			inputFile = args[0];
			secondInput = args[1];
			outputFile = args[2];
			fw = new FileWriter(outputFile);
		} catch (Exception e) {
			System.out.println("Input or output file missing.");
			System.exit(0);
		}

		try {
			
			// Execute each task
			GetNumberOfFlights();

			CreateListOfFlights();

			CalculateTotalPassengers();

			CalculateDistanceTravelled();
			
			fw.flush();
			fw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/***************************************************************************
	 * Method use to verify input string is valid, all elements must be valid.
	 * Input: String to verify, number of task to verify.
	 **************************************************************************/
	private static boolean regexThis(String[] split, int task) {
		switch (task) {
		case 1:
			if (split.length < 3)
				return false;
			return split[2].matches("[A-Z]{3}");
		case 2:
			if (split.length < 6)
				return false;
			return split[0].matches("[A-Z]{3}[0-9]{4}[A-Z]{2}[0-9]{1}") & split[1].matches("[A-Z]{3}[0-9]{4}[A-Z]{1}")
					& split[2].matches("[A-Z]{3}") & split[3].matches("[A-Z]{3}") & split[4].matches("\\d{10}")
					& split[5].matches("\\d{1,4}");
		case 3:
			if (split.length < 2)
				return false;
			return split[1].matches("[A-Z]{3}[0-9]{4}[A-Z]{1}");
		case 4:
			if (split.length < 4)
				return false;
			/************************************************************
			 * Validating with only between 3 and 13 digits results if very few
			 * results.
			 * 
			 * return split[1].matches("[A-Z]{3}") &
			 * split[2].matches("[+-]?\\d{3,13}\\.?\\d{3,13}") &
			 * split[3].matches("[+-]?\\d{3,13}\\.?\\d{3,13}");
			 ************************************************************/
			// Using this less specific implementation instead.
			return split[1].matches("[A-Z]{3}") & split[2].matches("[+-]?\\d*\\.?\\d*")
					& split[3].matches("[+-]?\\d*\\.?\\d*");
		default:
			return false;
		}
	}
	
	
	/***
	 * Helper function to log and print output to screen at the same time.
	 * @param toLog
	 * @param fw
	 */
	public static void log(String toLog, FileWriter fw){
		try {
			fw.write(toLog);
			System.out.print(toLog);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
