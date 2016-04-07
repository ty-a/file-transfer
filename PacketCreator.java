import java.net.DatagramPacket;
import java.net.InetAddress;


public class PacketCreator {
	
	// basic method to reduce code duplication for packet creation
	public static DatagramPacket createPacket(int seqNum, byte[] payload, int read, InetAddress dest, int port, int sender) {
		byte[] seqBytes = ("" + seqNum).getBytes();
		byte[] hostByte = ("" + sender).getBytes();
		byte[] newPayload = new byte[seqBytes.length + hostByte.length + read];
		System.arraycopy(seqBytes, 0, newPayload, 0, seqBytes.length );
		System.arraycopy(hostByte, 0, newPayload, seqBytes.length, hostByte.length);
		System.arraycopy(payload, 0, newPayload, seqBytes.length + hostByte.length, read );
		return new DatagramPacket(newPayload, newPayload.length, dest, port);
	}
}