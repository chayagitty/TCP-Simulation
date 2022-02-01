import java.net.*;
import java.io.*;
import java.util.*;// need for random and arrayList 

public class Server {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: java Server <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);
		Random rand = new Random();

		try (ServerSocket serverSocket = new ServerSocket(portNumber);
				Socket clientSocket = serverSocket.accept();
				PrintWriter writeToClient = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader readFromClient = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));) {

			String message = "Before you judge a man, walk a mile in his shoes. After that who cares? He's a mile away and you've got his shoes! ";
			// split the message into an array of words
			String words[] = message.split(" ");

			// convert the words arr3
			//an to an arrayList that keeps track of which packets
			// the client didn't receive yet
			ArrayList<Packets> packetsThatStillNeedToBeSent = new ArrayList<Packets>();
			for (int j = 0; j < words.length; j++) {
				packetsThatStillNeedToBeSent.add(new Packets(words[j], j));
			}

			// how many packets there are
			int numOfPackets = packetsThatStillNeedToBeSent.size();
			// client requesting the number of packets
			readFromClient.readLine();
			// tell the client how many packets to expect
			writeToClient.println(numOfPackets);

			boolean allPacketsRecievedByClient = false;

			// continue sending packets while the user didn't receive all of them
			while (!allPacketsRecievedByClient) {

				// temporary array list to keep track of the packets that are attempting to send
				// we initialize it to be identical to the packetsThatStillNeedToBeSent arrayList
				ArrayList<Packets> packetsStillNeedToAttemptToSend = new ArrayList<Packets>(packetsThatStillNeedToBeSent);

				// continue until there are no more packets we need to try to send
				while (packetsStillNeedToAttemptToSend.size() > 0) {
					// probability that we're sending it
					int probability = rand.nextInt(100);
					// what packet from the packet array list should be sent
					int randElement = rand.nextInt(packetsStillNeedToAttemptToSend.size());

					//print out that we're sending this packet
					System.out.println("Sending packet "
							+ (packetsStillNeedToAttemptToSend.get(randElement).getIndex()+1) + " to the client");
				
				//20% of the time the sending fails and the packet doesn't actually get sent
					if (probability >= 20) {
						//string and index being sent to the  client
						String word = packetsStillNeedToAttemptToSend.get(randElement).getWord();
						int index =packetsStillNeedToAttemptToSend.get(randElement).getIndex();
						
						//send the string and index in one long string divided by a symbol
						writeToClient.println(index+"~"+word);
					}
					// a send attempt took place so we can remove this element from the
					// packetsStillNeedToAttemptToSend array list
					packetsStillNeedToAttemptToSend.remove(randElement);
				}
				//
				System.out.println("All packets sent to client");
				
				// tell the user that all packets were sent
				writeToClient.println("all packets sent");

				// get the client's response
				String clientResponse = readFromClient.readLine();

				// if the client says that they received all packets
				if (clientResponse.equals("all packets recieved")) {
					System.out.println("all packets recieved by the client");
					allPacketsRecievedByClient = true;

				} else if (clientResponse.equals("didn't recive all packets")) {
					System.out.println("all packets sent but client didn't receive all");

					// find out how many packets were not received by the client
					int packetsNotRecieved = Integer.parseInt(readFromClient.readLine());

					//reinitialize the packetsThatStillNeedToBeSent ArrayList
					packetsThatStillNeedToBeSent = new ArrayList<Packets>();
					//add to it whatever still needs to be sent
					for (int h = 0; h < packetsNotRecieved; h++) {
						//read from client what packets need to be sent again
						int num = Integer.parseInt(readFromClient.readLine());
						//print out this info
						System.out.println("Packet "+(num+1)+" wasn't recieved by the client");
						//add this packet to the packetsThatStillNeedToBeSent array list
						packetsThatStillNeedToBeSent.add(new Packets(words[num],num));

					}
				}

			}
		}

		catch (Exception e) {
			 System.out.println(
			 "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}

	}


}
