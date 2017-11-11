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
	double Longitude, Latitude;

	Location(String current_location)
	{
		String parts[] = current_location.split(" ");
		Type = (int)Double.parseDouble(parts[0]);
		Longitude = Double.parseDouble(parts[1]);
		Latitude = Double.parseDouble(parts[2]);
	}

	Location(int Type, String current_location)
	{
		this.Type = Type;
		String parts[] = current_location.split(" ");
		Longitude = Double.parseDouble(parts[0]);
		Latitude = Double.parseDouble(parts[1]);
	}

	// Returns the location of this object in string format 
	String get_location()
	{
		return Double.toString(Longitude) + " " + Double.toString(Latitude);
	}

	// Calculates the distance between two locations
	static double distance(Location first, Location second)
	{
    	return Math.sqrt(Math.pow((first.Latitude - second.Latitude), 2.0) + 
    					 Math.pow((first.Longitude - second.Longitude), 2.0));
	}

	// Returns all the nearest locations with count
    static String get_all_locations(Location current_location)
    {
        String all_locations = "";
        int all_locations_count = 0;
        Iterator it = Hash_Map.all_locations.entrySet().iterator();
        while (it.hasNext()) 
        {
            Map.Entry item = (Map.Entry)it.next();
            Location coordinates = new Location(1, item.getKey().toString());
            if(Location.distance(current_location, coordinates) <= 100.0)
                all_locations_count += (int)item.getValue();
        }
  		return Integer.toString(all_locations_count);
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
			socket_output.println(Location.get_all_locations(query));
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
		server = new ServerSocket(9999);
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