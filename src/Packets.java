
public class Packets {
	private int index;
	private String word;
	
	public Packets(String word, int index) {
		this.index= index;
		this.word=word; 
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	
}
