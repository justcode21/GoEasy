import java.util.*;
import java.net.*;
import java.io.*;

class Location
{
	double Longitue;
	double Latitude;

	Location(String current_location)
	{
		String parts[] = current_location.split(" ");
		Longitue = Double.parseDouble(parts[0]);
		Longitue = Double.parseDouble(parts[1]);
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

	public void run()
	{
		try
		{
			socket_input = new Scanner(socket.getInputStream());
			socket_output = new PrintStream(socket.getOutputStream());
			while(true)
			{
				Location query_location = new Location(socket_input.nextLine());
				socket_output.println("Got you query");
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
	HashMap<Location, Integer> current_location;

	void start_server() throws Exception
	{
		current_location = new HashMap<Location, Integer>();
		while(true)
		{
			// Create Server Socket and start accepting requests
			server = new ServerSocket(9999);
			socket = server.accept();

			socket_input = new Scanner(socket.getInputStream());
			socket_output = new PrintStream(socket.getOutputStream());

			// Send a Connection Established message 
			socket_output.println("Connection Established");
			String current_location = socket_input.nextLine();
			Thread new_user = new Thread(new Create_User(socket, current_location));
			new_user.start();
		}
	}
}

class Run_Server
{
	public static void main(String args) throws Exception
	{
		Server server = new Server();
		server.start_server();
	}
}