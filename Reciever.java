import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Reciever {
	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("Not enough params, please use java Reciever src port");
			System.exit(1);
		}
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter destination file name (with extension!): ");
		String filename = in.nextLine();
		in.close();
		
		int port = Integer.parseInt(args[1]);

		try (
				DatagramSocket socket = new DatagramSocket(9002)
		) {
			InetAddress dest = InetAddress.getByName(args[0]);
			// http://stackoverflow.com/a/1099359/1998761
			byte[] buffer = new byte[512];
			
			try (FileOutputStream writer = new FileOutputStream(filename)) {
				DatagramPacket packet;
				int lastSeqNum = -1;
				
				while(true) {
					packet = new DatagramPacket(buffer, buffer.length);
					
					socket.receive(packet);
					
					PacketProcessor pp = new PacketProcessor(packet.getData(), packet.getLength());
					if(lastSeqNum == pp.getSeqNum()) {
						if(new String(pp.getPayload(), 0, pp.getLength()).startsWith("DONE")) 
							break;
						
						System.out.println("DUPLICATE PACKET");
						socket.send(new DatagramPacket((pp.getSeqNum() + "0ACK").getBytes(), (pp.getSeqNum() + "0ACK").getBytes().length, dest, port));
						continue; // resend ACK and drop this packet
					} else {
						lastSeqNum = pp.getSeqNum();
					}
					System.out.println("RECV " + pp.getSeqNum());
					
					if(new String(pp.getPayload(), 0, pp.getLength()).startsWith("DONE")) {
						break;
					} else {
						writer.write(pp.getPayload(), 0, pp.getLength());
					}

					socket.send(new DatagramPacket((pp.getSeqNum() + "0ACK").getBytes(), (pp.getSeqNum() + "0ACK").getBytes().length, dest, port));
				}
			} catch (IOException e) {
				System.out.println("Unable to write to destination file. Check permissions");
				System.exit(1);
			} 
		} catch (UnknownHostException err) {
			System.out.println("Invalid hostname provided; Please provide an IP address or a hostname.");
			System.exit(1);
		} catch (IOException e1) {
			System.out.println("Unable to read source file. Check permissions");
			System.exit(1);
		}
	} 
}
