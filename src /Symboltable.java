import java.util.ArrayList;
import java.util.Iterator;

public class Symboltable {
	ArrayList<Nextusefield> fields;
	int nextfield;
	public Symboltable() {
		// TODO Auto-generated constructor stub
		fields = new ArrayList<Nextusefield>();
		//add a blank element to the front to start indexing on 1 like in the notes
		fields.add(new Nextusefield("*&#@#@", -1, ""));
		nextfield = 1;
	}
	
	
	public void genField(String o, int op1, String op2) {
		fields.add(new Nextusefield(o, op1, op2));
		nextfield++;
	}
	/// print the whole list

	public Nextusefield getfield(int index) {
		return fields.get(index);
	}
	public int Size() {
		return nextfield;
	}
	public Nextusefield getFieldByName(String name) {
		for(int i = 0;i<nextfield;i++) {
			if(fields.get(i).Name.equals(name)) {
				return fields.get(i);
			}
			}
		return null;
	}
	public boolean contain(String name) {
		for(int i = 0;i<fields.size();i++) {
			if(fields.get(i).Name.equals(name)) {
				return true;
			}
		}
		return false;
	}
}	
	
	

