import java.util.*;
import java.net.*;
import java.io.*;

class Hash_Map
{
	public static HashMap<String, Integer> all_locations = new HashMap<String, Integer>();
}

class Location
{
	double Longitue;
	double Latitude;
	int Flag; 
	Location(String current_location)
	{
		String parts[] = current_location.split(" ");
		Flag = Integer.parseInt(parts[0]);
		Longitue = Double.parseDouble(parts[1]);
		Latitude = Double.parseDouble(parts[2]);
	}

	String get_location()
	{
		return Integer.toString(Longitue) + " " + Integer.toString(Latitude);
	}
}

class Create_User implements Runnable
{
	Socket socket;
	Scanner socket_input;
	PrintStream socket_output;
	Location current_location;

	Create_User(Socket socket, String my_location)
	{
		this.socket = socket;
		this.current_location = new Location(my_location);
	}

	void handle_query(Location query)
	{
		String location = query.get_location();
		if (query.Flag == 0)
		{
			if (Hash_Map.all_locations.containsKey(location)) 
          		Hash_Map.all_locations.put(location,Hash_Map.all_locations.get(location) + 1);
        	else
        		Hash_Map.all_locations.put(location, 1);

       		System.out.println("People at " + s + " : " + Hash_Map.all_locations.get(location));
					
			System.out.println(query_location.Longitue + "  " + query_location.Latitude);
		}
		else
			socket_output.println(find_Loc(query));
	}

	public void run()
	{
		try
		{
			// create socket streams for current user thread
			socket_input = new Scanner(socket.getInputStream());
			socket_output = new PrintStream(socket.getOutputStream());

			while(true)
			{

				Location query = new Location(socket_input.nextLine());
				handle_query(query);
			}
		}
		catch(Exception e){}
	}

    String find_Loc(Location location)
    {
            double lat = loc.Latitude;
            double lon = loc.Longitue;
            String s1="";
            String s2="";
            Iterator it = H.hm.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                System.out.println(pair.getKey() + " - " + pair.getValue());
                s1=pair.getKey().toString();
                String parts[] = s1.split(" ");
                double lo = Double.parseDouble(parts[0]);
		double la = Double.parseDouble(parts[1]);
                double distance = Math.sqrt(Math.pow((la-lat),2.0)+Math.pow((lo-lon),2.0));
                if(distance<10.0){
                    s2+=pair.getKey()+" ";
                }
                it.remove(); // avoids a ConcurrentModificationException
            }
            System.out.println(s2);
            return s2;
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
		// create server and a location hashmap to keep count
		server = new ServerSocket(9999);
		System.out.println("Server Started..");
		while(true)
		{
			// start accepting requests
			socket = server.accept();

			socket_input = new Scanner(socket.getInputStream());
			socket_output = new PrintStream(socket.getOutputStream());

			// Send a Connection Established message 
			socket_output.println("Connection Established");

			// get current location of the user
			String current_location = socket_input.nextLine();

			String parts[] = current_location.split(" ");
			String s = (parts[1] + " " + parts[2]);
			H.hm.put(s, 1);

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