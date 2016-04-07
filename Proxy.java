import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Proxy {
	public static void main(String[] args) throws UnknownHostException {
		int port = 9001;
		double dropRate;
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter drop rate (as a double!): ");
		dropRate = in.nextDouble();
		in.close();
		
		System.out.println("Current drop rate is " + dropRate);

		try (DatagramSocket socket = new DatagramSocket(port)
		) {
			// http://stackoverflow.com/a/1099359/1998761
			byte[] buffer = new byte[512];

			try {
				while(true) {
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					
					socket.receive(packet);
					PacketProcessor pp = new PacketProcessor(packet.getData(), packet.getLength());
					// from sender so send to reciever
					if(Math.random() < dropRate) {
						System.out.println("DROPPED PACKET");
						continue; // drop packet
					} else {
						if(pp.fromSender()) {
							System.out.println("RECV packet from SENDER");
							socket.send(new DatagramPacket(packet.getData(), packet.getLength(), packet.getAddress(), 9002));
						} else {
							System.out.println("RECV packet from Reciever");
							socket.send(new DatagramPacket(packet.getData(), packet.getLength(), packet.getAddress(), 9000));
						}
					}
					

				}
				
			} catch (IOException e) {
				System.out.println("Unable to write to destination file. Check permissions");
				System.exit(1);
			} 
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Unable to read source file. Check permissions");
			System.exit(1);
		}
		
	}

}
