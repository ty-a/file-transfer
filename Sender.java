import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Sender {
	
	public static void main(String[] args) {
		int port = Integer.parseInt(args[1]);
		System.out.println("1".getBytes().length);
		System.out.println("10".getBytes().length);
		System.out.println("101".getBytes().length);
		System.out.println("181".getBytes().length);
		System.exit(0);
		
		

		try (RandomAccessFile file = new RandomAccessFile("pic.png", "r");
				DatagramSocket socket = new DatagramSocket(9000)
		) {
			InetAddress dest = InetAddress.getByName(args[0]);
			// http://stackoverflow.com/a/1099359/1998761
			byte[] buffer = new byte[512];

			try {
				while(file.read(buffer) != -1) {
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, dest, port);
					socket.send(packet);
					
					socket.receive(packet);
					if(new String(packet.getData(), 0, packet.getLength()).equalsIgnoreCase("ACK")) {
						System.out.println("ACK");
						continue;
					} else {
						System.out.println(new String(packet.getData()));
					}
				}
				
				socket.send(new DatagramPacket("DONE".getBytes(), "DONE".getBytes().length, dest, port));
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
}
