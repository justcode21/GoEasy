import java.util.*;
import java.net.*;
import java.io.*;

// Global hash_map with increase and decrease functions
class Hash_Map
{
	public static HashMap<String, Integer> all_locations = new HashMap<String, Integer>();

	public static void increase_count(String key)
	{
		if (Hash_Map.all_locations.containsKey(key)) 
          	Hash_Map.all_locations.put(key, Hash_Map.all_locations.get(key) + 1);
        else
        	Hash_Map.all_locations.put(key, 1);
	}

	public static void decrease_count(String key)
	{
		if (Hash_Map.all_locations.containsKey(key) && Hash_Map.all_locations.get(key) > 0) 
          	Hash_Map.all_locations.put(key, Hash_Map.all_locations.get(key) - 1);
	}

}

class Location
{
	int Type;
	double Lattitude, Longitude;

	Location(String current_location)
	{
		String parts[] = current_location.split(" ");
		Type = (int)Double.parseDouble(parts[0]);
		Lattitude = Double.parseDouble(parts[1]);
		Longitude = Double.parseDouble(parts[2]);
	}

	Location(int Type, String current_location)
	{
		this.Type = Type;
		String parts[] = current_location.split(" ");
		Lattitude = Double.parseDouble(parts[0]);
		Longitude = Double.parseDouble(parts[1]);
	}

	// Returns the location of this object in string format 
	String get_location()
	{
		return Double.toString(Lattitude) + " " + Double.toString(Longitude);
	}

	static double degree_to_radian(double degree)
	{
		return degree * (Math.PI/180);
	}

	// Calculates the distance between two locations
	static double distance(Location first, Location second)
	{
		double Lattitude = degree_to_radian(first.Lattitude);
		double Longitude = degree_to_radian(first.Longitude);
		double newLattitude = degree_to_radian(second.Lattitude);
		double newLongitude = degree_to_radian(second.Longitude);

		double differenceLattitude = Math.abs(Lattitude - newLattitude);
		double differenceLongitude = Math.abs(Longitude - newLongitude);
		double numerator = Math.sqrt(Math.pow((Math.cos(newLattitude) * Math.sin(differenceLongitude)), 2) + Math.pow(((Math.cos(Lattitude) * Math.sin(newLattitude)) - 
						   (Math.sin(Lattitude) * Math.cos(newLattitude) * Math.cos(differenceLongitude))), 2)); 
		
		double denominator = (Math.sin(Lattitude) * Math.sin(newLattitude)) + 
							 (Math.cos(Lattitude) * Math.cos(newLattitude) * Math.cos(differenceLongitude));

		return 6371.0088 * Math.atan2(numerator, denominator);
	}

	// Returns all the nearest locations with count
    static int get_all_locations(Location current_location)
    {
        int all_locations_count = 0;
        Iterator it = Hash_Map.all_locations.entrySet().iterator();
        while (it.hasNext()) 
        {
            Map.Entry item = (Map.Entry)it.next();
            Location coordinates = new Location(1, item.getKey().toString());
            if(Location.distance(current_location, coordinates) <= 100.0)
                all_locations_count += (int)item.getValue();
        }
  		return all_locations_count;
    }

    // Simulate a random hash to demostrate the working
    static void fill_hash_map(Location current_location)
    {
    	Random r = new Random();
    	double number_of_element = r.nextInt(50) + 1;
    	for(int i = 0; i < number_of_element; i++)
    	{
    		double extra_longitude = 1 + 34 * r.nextDouble();
    		double extra_lattitude = 1 + 34 * r.nextDouble();
    		String new_key = Double.toString(current_location.Lattitude + extra_lattitude/100) + " " +
    						 Double.toString(current_location.Longitude + extra_longitude/100);
    		Hash_Map.increase_count(new_key); 
    	}
    }
}

class Create_User implements Runnable
{
	Socket socket;
	Scanner socket_input;
	PrintStream socket_output;
	Location current_location;

	Create_User(Socket socket, Location my_location)
	{
		this.socket = socket;
		this.current_location = my_location;
	}

	void handle_query(Location query)
	{
		if (query.Type == 0)
			Hash_Map.increase_count(query.get_location());
		else
		{
			// Comment out next line to get the real app working
			Location.fill_hash_map(query);
			socket_output.println(Location.get_all_locations(query));
		}
	}

	public void run()
	{
		try
		{
			// Create socket streams for current user thread
			socket_input = new Scanner(socket.getInputStream());
			socket_output = new PrintStream(socket.getOutputStream());

			while(true)
			{
				// Start accepting and handleing queries
				Location query = new Location(socket_input.nextLine());
				handle_query(query);
			}
		}
		catch(Exception e){}
	}
}

class Server
{
	ServerSocket server;
	Socket socket;
	Scanner socket_input;
	PrintStream socket_output;
	
	void start_server() throws Exception
	{
		// Create server and a location hashmap to keep count
		server = new ServerSocket(8080);
		System.out.println("Server Started..");
		while(true)
		{
			// Start accepting requests
			socket = server.accept();

			socket_input = new Scanner(socket.getInputStream());
			socket_output = new PrintStream(socket.getOutputStream());

			// Send a Connection Established message 
			socket_output.println("Connection Established");

			// Get current location of the user
			Location current_location = new Location(socket_input.nextLine());
			Hash_Map.increase_count(current_location.get_location());
			
			// Create a new thread for a new user
			Thread new_user = new Thread(new Create_User(socket, current_location));
			new_user.start();
		}
	}
}

class Run_Server
{
	public static void main(String args[]) throws Exception
	{
		Server server = new Server();
		server.start_server();
	}
}