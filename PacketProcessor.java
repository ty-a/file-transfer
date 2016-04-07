import java.util.Arrays;


public class PacketProcessor {
	private int seqNum;
	private byte[] payload;
	private int length;
	private boolean fromSender;
	// first byte is seqNum, 2nd byte is from which host it is sent
	PacketProcessor(byte[] data, int length) {
		String num = new String(Arrays.copyOfRange(data, 0, 1), 0, 1);
		this.seqNum = Integer.parseInt(num);
		String temp = new String(Arrays.copyOfRange(data, 1, 2), 0, 1);
		this.fromSender = temp.equals("1");
		this.payload = Arrays.copyOfRange(data, 2, length + 1 );

		this.length = length;
	}
	
	public int getSeqNum() {
		return seqNum;
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	public int getLength() {
		return length - 2;
	}
	
	public boolean fromSender() {
		return fromSender;
	}

}
