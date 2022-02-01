
import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.ArrayList;

public class Client {
	public static void main(String args[]) throws ClassNotFoundException {

		if (args.length != 2) {
			System.err.println("Usage: java Client <host name> <port number>");
			System.exit(1);
		}

		String hostName = args[0];

		int portNumber = Integer.parseInt(args[1]);

		try (Socket clientSocket = new Socket(hostName, portNumber);
				PrintWriter writeToServer = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader readFromServer = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));) {
			
			
			// get the amount of packets from the server and make an array with that length
			writeToServer.println("How many packets are there");
			int numOfPackets = Integer.parseInt(readFromServer.readLine());

			// array that will hold all the packets the client received
			Packets[] packets = new Packets[numOfPackets];

			// keep track of how many packets were received and filled
			int numFilled = 0;

			// continue looping until receives all of the packets
			while (numFilled < numOfPackets) {

				// variable that will hold the next word received 
				String packet;

				// loop until the server says it sent all packets
				while (!(packet = readFromServer.readLine()).equals("all packets sent")){

					// the index of the packet is before the symbol
					int index = Integer.parseInt(packet.split("~")[0]);
					
					//the word being sent is after the symbol
					String word = packet.split("~")[1];
					
					// make a new packet object for this packet
					Packets obj = new Packets(word, index);
					// add the packet to the array
					packets[obj.getIndex()] = obj;

					// print out what was received
					System.out.println("Received packet " + (index+1) + " from the server");
					// increment how many elements in the packet array are filled
					numFilled++;

				}

				// if we didn't receive all packets, tell the server that
				if (numFilled < numOfPackets) {

					// tell the server we didn't receive all elements
					writeToServer.println("didn't recive all packets");
					// tell server how many elements were not received
					writeToServer.println(numOfPackets-numFilled);

					// write to the server which elements were not received
					for (int i = 0; i < packets.length; i++) {
						// only send to server which elements are not filled
						if (packets[i] == null) {
							writeToServer.println(i);

							// Display what we're doing
							System.out.println(
									"Informing server that packet " + (i+1) + " was not received.");
						}
					}
					// inform server that all packets were received 
				} else {
					writeToServer.println("all packets recieved");
				}
			}

			// print the message
			System.out.println("\n\nThe Message Is:");
			for (int p = 0; p < packets.length; p++) {
				System.out.print(packets[p].getWord() + " ");
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + hostName);
			System.exit(1);
		}
	}
}
