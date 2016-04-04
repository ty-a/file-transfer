import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Reciever {
	public static void main(String[] args) {
		int port = Integer.parseInt(args[1]);

		try (
				DatagramSocket socket = new DatagramSocket(9002)
		) {
			InetAddress dest = InetAddress.getByName(args[0]);
			// http://stackoverflow.com/a/1099359/1998761
			byte[] buffer = new byte[512];
			
			try (FileOutputStream writer = new FileOutputStream("pic2.png")) {
				DatagramPacket packet;
				while(true) {
					packet = new DatagramPacket(buffer, buffer.length);
					System.out.println("RECV");
					socket.receive(packet);
					//packet = new DatagramPacket(buffer, buffer.length, dest, port);
					//socket.send(packet);
					if(new String(packet.getData(), 0, packet.getLength()).equalsIgnoreCase("DONE")) {
						break;
					} else {
						writer.write(packet.getData());
					}

					socket.send(new DatagramPacket("ACK".getBytes(), "ACK".getBytes().length, dest, port));
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
