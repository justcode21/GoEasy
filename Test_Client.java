import java.util.*;
import java.net.*;
import java.io.*;


class Client
{
	Socket socket;
	Scanner console_input, socket_input;
	PrintStream socket_output;

	void get_count_on_location()
	{
    	while(true)
		{	
			System.out.println("Enter the coordinates");
			socket_output.println(console_input.nextLine());
		}
	}

	void start_client()
	{
		try
		{
			// initilize sockets for client
			socket = new Socket("localhost", 8080);
			socket_input = new Scanner(socket.getInputStream());
			socket_output = new PrintStream(socket.getOutputStream());
			console_input = new Scanner(System.in);

			// print connection established message
			String recieved_message = socket_input.nextLine();
			System.out.println(recieved_message);

			// send your current location
			socket_output.println("0.0 33.33 66.66");
		}
		catch(Exception e){}

	}

} 

class Run_Client
{
	public static void main(String args[]) throws Exception
	{
		Client client = new Client();

		// start the client server and initialize it
		Thread start_client = new Thread(new Runnable()
		{
			public void run()
			{
				try{client.start_client();}
				catch(Exception e){}
			}
		});
		start_client.start();
		start_client.join();

		// independent threads for various function of the client
		Thread independent_location = new Thread(new Runnable()
		{
			public void run()
			{
				try{client.get_count_on_location();}
				catch(Exception e){}
			}

		});
		independent_location.start();
		independent_location.join();
	}
}