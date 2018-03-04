import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class QuadList {
	ArrayList<Quad> quads;
	int nextQuad;
	public QuadList() {
		quads = new ArrayList<Quad>();
		//add a blank element to the front to start indexing on 1 like in the notes
		quads.add(new Quad(" ","","",""));    
		nextQuad = 1;
	}
	
	
	public void genQuad(String o, String op1, String op2, String r) {
		quads.add(new Quad(o, op1, op2, r));
		nextQuad++;
	}
	/// print the whole list
	public String print() {
		int i = 0;
		String s = "";
		for (Quad quad : quads) {
			if(i != 0) {
				System.out.println(i +":"+quad.toString());
				s += i +":"+quad.toString() + "\n";
			}
			i++;
		}
		return s;
	}
	public void printfull() {
		int i = 0;
		for (Quad quad : quads) {
			if(i != 0)
				System.out.println(i +":"+quad.tofullString());
			i++;
		}
	}
	public Quad getQuad(int index) {
		return quads.get(index);
	}
	public int Size() {
		return nextQuad;
	}
	public void setReturn(String A, int val) {
		quads.get(Integer.parseInt(A)).result = val+"";
	}
	public void backpatch(String A, int n) {
		int n2 = Integer.parseInt(quads.get(Integer.parseInt(A)).result);
		setReturn(A,n);
		if(n2!=0)
			backpatch(n2+"",n);
	}
	
	public void readInQuads(File f) throws FileNotFoundException {
		Scanner file = new Scanner(f);
		while(file.hasNextLine()) {
			String s = file.nextLine();
			String[] list = s.split(",");
			this.genQuad(list[0].substring(list[0].indexOf(':')+1), list[1], list[2], list[3]);
		}
	}
	public void readInQuads(String ss) throws FileNotFoundException {
		Scanner file = new Scanner(ss);
		while(file.hasNextLine()) {
			String s = file.nextLine();
			String[] list = s.split(",");
			this.genQuad(list[0].substring(list[0].indexOf(':')+1), list[1], list[2], list[3]);
		}
	}
	

}
