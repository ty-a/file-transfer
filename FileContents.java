
public class FileContents {
	
	private byte[] content;
	private int length;
	
	// A simple container class to hold data related to the file contents
	FileContents(byte[] data, int size) {
		content = data;
		length = size;
	}
	
	public byte[] getData() {
		return content;
	}
	
	public int getSize() {
		return length;
	}
}
