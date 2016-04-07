import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Sender {
	
	public static void main(String[] args) {
		
		if(args.length < 2) {
			System.out.println("Not enough params, please use java Sender dest port");
			System.exit(1);
		}
		
		int port = Integer.parseInt(args[1]);
		
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter source file name (with extension!): ");
		String filename = in.nextLine();
		in.close();

		try (RandomAccessFile file = new RandomAccessFile(filename, "r");
				DatagramSocket socket = new DatagramSocket(9000)
		) {
			InetAddress dest = InetAddress.getByName(args[0]);
			// http://stackoverflow.com/a/1099359/1998761
			byte[] buffer = new byte[510]; // Note: Has to be 2 bytes smaller than the Reciever and Proxy classes buffers
										   //     because of the 2 added bytes for seqNum and host

			try {
				int read;
				int seqNum = 0;
				int lastAckedPacket = 1;
				DatagramPacket[] onthewire = new DatagramPacket[2];
				
				// contains all of the file contents
				ArrayList<FileContents> fileContents = new ArrayList<FileContents>();
				while((read = file.read(buffer)) != -1) {
					// Have to copy the buffer so that we do not overwrite it
					fileContents.add(new FileContents(Arrays.copyOf(buffer, read), read));
				}

				boolean[] acked = new boolean[fileContents.size()];
				int currPacket = 0;
				
				while(haveUnAckedPackets(acked)) {
					
					while(true) { // loop until we get an ACK on the packet we are sending
						// No response from previous packet
						if(seqNum == lastAckedPacket) {
							System.out.println("RESENDING PACKET " + ((seqNum == 0)? 1 : 0));
							if(seqNum == 0) 
								socket.send(onthewire[1]);
							else 
								socket.send(onthewire[0]);
						} else {
							onthewire[seqNum] = PacketCreator.createPacket(seqNum, fileContents.get(currPacket).getData(), fileContents.get(currPacket).getSize(), dest, port, 1);
							
							System.out.println("SENDING " + seqNum);
							socket.send(onthewire[seqNum]);
							
							// change seqNum for next iteration
							if(seqNum == 0)
								seqNum = 1;
							else
								seqNum = 0;
						}
						try {
							DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
							socket.setSoTimeout(1000); // time out of 1 second
							socket.receive(packet);
							PacketProcessor pp = new PacketProcessor(packet.getData(), packet.getLength());
							if(new String(pp.getPayload(), 0, pp.getLength()).startsWith("ACK")) {
								System.out.println("ACK " + pp.getSeqNum());
								lastAckedPacket = pp.getSeqNum();
								acked[currPacket] = true;
								currPacket++;
								break;
							} else {
								// should not happen, so see what we get
								System.out.println(new String(packet.getData()));
							}
						} catch (SocketTimeoutException e) {
							continue;
						}
					} 
				}
				socket.send(new DatagramPacket((1 + "1DONE").getBytes(), (1 + "1DONE").getBytes().length, dest, port));
			} catch (IOException e) {
				System.out.println("Unable to write to destination file. Check permissions");
				System.exit(1);
			} 
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			System.exit(1);
		} catch (UnknownHostException err) {
			System.out.println("Invalid hostname provided; Please provide an IP address or a hostname.");
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Unable to read source file. Check permissions");
			System.exit(1);
		}
	} 
	
	private static boolean haveUnAckedPackets(boolean[] acked) {
		for(int i = 0; i < acked.length; i++) {
			if(!(acked[i]))
				return true;
		}
		return false;
	}
}
