import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

public class Codegen {
	
	public ArrayList<Integer> leadingblocks;
	public QuadList ql;
	public Symboltable st;
	public ArrayList<String>[ ]	rt;
	public String code = "";
	private int regCount = 8;
	private int memAddr = Integer.parseInt("2000",16);
	public Codegen(QuadList d) {
		ql = d;
		rt = new ArrayList[regCount];
		for(int i = 0; i < rt.length; i++) {
			rt[i] = new ArrayList<String>();
		}
	}
	public void codeGeneration() {

		reservememory();
		for(int i = 0; i < this.leadingblocks.size()-1; i++) {
			//perform live varible analysis
			scanblock();
//			System.out.printf("On basic block %d to %d", i, i+1);
			for(int j = leadingblocks.get(i); j < leadingblocks.get(i+1)
					||j==leadingblocks.get(leadingblocks.size()-1); j++) {
				ql.getQuad(j).address = String.format("%04X", memAddr);
				generateOppCode(ql.getQuad(j),j);
			}
			//generate store instruction for vars that are ive & not in memory
			
			generateStoreIns();
		}
		fixAddressses();
		System.out.println("code \n" + code);
	}
	private void fixAddressses() {
		String code2 = "";
		Scanner codeScan = new Scanner(code);
//		System.out.println("CODE before fix\n" +code);
		while(codeScan.hasNext()) {
			String line = codeScan.nextLine();
//			System.out.println(line);
			if(!isJmp(line)) {
				code2+=line;
			} else {
				String ss = "",s = "";
				Scanner chop = new Scanner(line);
				while(chop.hasNext()) {
					ss = chop.next();
					s+=ss;
				}
				int i = Integer.parseInt(ss);
				line = line.substring(0, line.lastIndexOf(' ')) + " " + ql.getQuad(i).address; 
				code2+=line;
			}
			code2 += "\n";
		}
		code = code2;
//		System.out.println("CODE after fix\n" +code);
	}
	private void generate(String s) {
		// TODO Auto-generated method stub
//		System.out.println(String.format("%04X : ", memAddr) +s);
		code = code + String.format("%04X : ", memAddr) + s +"\n";
		memAddr += 2;
	}
	private boolean isJmp(String s) {
		return (s.contains("GOTO") || s.contains("BLE") || 
				s.contains("BGE") || s.contains("BLT") || 
				s.contains("BGT") || s.contains("BEQ")) && !s.contains("*");
	}
	private void generateOppCode(Quad q, int i) {
		// TODO Auto-generated method stub
		switch(q.opperation) {
		//Assignment opp
			case "=":
				generateAss(q);
			//Arithmetic opp
				break;
			case "!":
				generateUnary("NOT",q);
				break;
			case "*":
				generateAriLog("MUL",q);
				break;
			case "+":
				generateAriLog("ADD",q);
				break;
			case "-":
				generateNeg("-",q);
				break;
			case "/":
				generateAriLog("DIVM",q);
				break;
			case "%":
				generateAriLog("DIV",q);
				break;
				//Logical opp
			case "&":
				generateAriLog("AND",q);
				break;
			case "|":
				generateAriLog("OR",q);
			//relational opp
				break;
			case "==":
				generateRel("BEQ", q);
				break;
			case "<":
				generateRel("BLT", q);
				break;
			case ">":
				generateRel("BGT", q);
				break;
			case "<=":
				generateRel("BGE", q);
				break;
			case ">=":
				generateRel("BLE", q);
				break;
			//unconditional jump
			case "jmp":
				generateJmp(q);
				break;
			//conditional jump
			case "jeqz":
				generateCondJmp("BEQ ",q);
				break;
			case "rtr":
				generatereturn(q);
				break;
		}
//		System.out.printf("\tGenerating code for %d: %s\n",i,q);
	}
	private void generatereturn(Quad q) {
		generate(String.format("MOVE.L %s,  D%d" , q.result, 1));
		generate("RTR");
	}
	private void generateUnary(String op, Quad q) {
		// TODO Auto-generated method stub
//		System.out.println("evaulating a unary " + q.toString());
		
		int REG = GetReg(q);
		String B = q.opperand1;
		String A = q.result;
		Nextusefield symbol = st.getFieldByName(B);
		if(symbol == null || !symbol.location.equals(REG +"")) {
			if(symbol != null && !symbol.location.equals("Memory")) {
				generate(String.format("MOVE.L D%s,  D%d" , symbol.location, REG));
			} else {
				generate(String.format("MOVE.L %s, D%d", B, REG));
			}
		}

		generate(op + " D" +REG);

		if(!q.isliveB() &&(!symbol.location.equals("Memory"))) {
	    		String R = symbol.location;
	    		rt[Integer.parseInt(R)].remove(B);
	    		symbol.location = "Memory";
		}

	    symbol = st.getFieldByName(A);
	    symbol.location = REG +"";
	    rt[REG].add(A);
	}
	private void generateNeg(String string, Quad q) {
		// TODO Auto-generated method stub
		if(q.opperand2.trim().equals("")) {
			generateUnary("NEG" , q);
		} else {
			generateAriLog("SUB" , q);
		}
	}
	/***
	 * Generates store instructions for vars that are live and not in memory
	 */
	private void generateStoreIns() {
//		System.out.println("----------------------");
		for (int i = 0; i < rt.length; i++) {
			ArrayList<String> reg = rt[i];
			while(!reg.isEmpty()) {
				generate("MOVE.L, D" + i+ ", " + reg.get(0));
				reg.remove(0);
			}
		}
	}
	private void generateAss(Quad quad) {
		// TODO Auto-generated method stub
//		System.out.printf("generateAss quad: %s\n",quad.toString());
	    int REG;
	    String B = quad.opperand1;
	    String A = quad.result;
	    String R;
	    //if B is not in a reg
	    Nextusefield symbol;
	    symbol = st.getFieldByName(B);
	    if(symbol == null || symbol.location.equals("Memory")) {
	    		REG = GetReg(quad);
	    		generate("MOVE.L " + B + ", D" + REG);
	    		
	    }
	    else {//B is in a REG we set REG to that Register
    			REG = Integer.parseInt(symbol.location); 
	    }
	    if(!quad.isliveB() &&(!symbol.location.equals("Memory"))) {
	    		R = symbol.location;
	    		rt[Integer.parseInt(R)].remove(B);
	    		symbol.location = "Memory";
	    }
	    symbol = st.getFieldByName(A);
	    st.getFieldByName(A).location = REG +"";
	    rt[REG].add(A);
	}
	private void generateCondJmp(String op, Quad quad) {
		// TODO Auto-generated method stub
		String B = quad.opperand1;
		Nextusefield symbol = st.getFieldByName(B);
		int REG;
		//if B is not in a reg, get one and put b in it
	    if(symbol == null || symbol.location.equals("Memory")) {
	    		REG = GetReg(quad);
	    		generate("MOVE.L " + B + ", D" + REG);
	    } else {
	    		REG = Integer.parseInt(symbol.location);
	    }
	    generateStoreIns();
	    generate("TST D" +REG);
	    generate(op + quad.result);
	    
		
	}
	private void generateJmp(Quad quad) {
		// TODO Auto-generated method stub
		generate("GOTO " + quad.result);
	}
	private void generateRel(String op, Quad quad) {
		// TODO Auto-generated method stub
		int REG = GetReg(quad);
		String A = quad.result;
		String B = quad.opperand1;
		String C = quad.opperand2;
		String R;
		Nextusefield symbolB = st.getFieldByName(B),
				symbolC = st.getFieldByName(C);
		if (symbolB == null || !symbolB.location.equals(REG +"")) {
			if(symbolB != null && !symbolB.location.equals("Memory")) {
				generate(String.format("MOVE.L D%s,D%d", symbolB.location,REG));
			} else {
				generate(String.format("MOVE.L %s, D%d", B,REG));
			}
		}
		if(symbolC != null && !symbolC.location.equals("Memory")) {
			generate(String.format("CMP.L D%s, D%d",symbolC.location,REG));
		} else {
			generate(String.format("CMP.L %s, D%d",C,REG));
		}
		generate(op + "*+4");
		generate("CLR.L D" + REG);
		generate("BRA *+2");
		generate("MOVEQ.L #1, D" +REG);
		
		if(!quad.isliveB() &&(!symbolB.location.equals("Memory"))) {
	    		R = symbolB.location;
	    		rt[Integer.parseInt(R)].remove(B);
	    		symbolB.location = "Memory";
		}
		if(!quad.isliveC() &&(!symbolC.location.equals("Memory"))) {
	    		R = symbolC.location;
	    		rt[Integer.parseInt(R)].remove(C);
	    		symbolC.location = "Memory";
		}
	    symbolB = st.getFieldByName(A);
	    symbolB.location = REG +"";
	    rt[REG].add(A);
	}
	private void generateAriLog(String op, Quad quad) {
		// TODO Auto-generated method stub
//		System.out.println("arith op with " + op + " "  + quad.toString());
		int REG = GetReg(quad);
		String A = quad.result;
		String B = quad.opperand1;
		String C = quad.opperand2;
		String lefthand;
		String R;
		REG = GetReg(quad);
		Nextusefield symbolB = st.getFieldByName(B);
		Nextusefield symbolC = st.getFieldByName(C);
		if (symbolB == null || !symbolB.location.equals(REG +"")) {
			if(symbolB != null && !symbolB.location.equals("Memory")) {
				generate(String.format("MOVE.L D%s,D%d", symbolB.location,REG));
			} else {
				generate(String.format("MOVE.L %s, D%d", B,REG));
			}
		}
		if(symbolC != null && quad.BequalC(st)) {
//			generate(String.format("%s R%d, R%d", op, REG, REG));
			lefthand = String.format("D%d, D%d", REG, REG);
		} else if(symbolC != null && !symbolC.location.equals("Memory")) {
//			generate(String.format("%s R%s, R%d", op, symbolC.location, REG));
			lefthand = String.format("D%s, D%d", symbolC.location, REG);
		} else {
//			generate(String.format("%s %s, R%d", op, C , REG));
			lefthand = String.format("%s, D%d", C , REG);
		}
		if(op.equals("DIV")) {
			generate("DIV "  + lefthand);
			generate("ASL #16");
			generate("LSR #16");
		} else if(op.equals("DIVM")) {
			generate("DIV "  + lefthand);
			generate("LSR #16");
			
		} else {
			generate(op + " " + lefthand);
		}
		if(!quad.isliveB() &&(!symbolB.location.equals("Memory"))) {
	    		R = symbolB.location;
	    		rt[Integer.parseInt(R)].remove(B);
	    		symbolB.location = "Memory";
		}
		if(!quad.isliveC() &&(!symbolC.location.equals("Memory"))) {
	    		R = symbolC.location;
	    		rt[Integer.parseInt(R)].remove(C);
	    		symbolC.location = "Memory";
		}
	    symbolB = st.getFieldByName(A);
	    symbolB.location = REG +"";
	    rt[REG].add(A);
		
	}
	private boolean isMemory(String s) {
		return s.equals("Memory") || s.startsWith("#");
	}
	public String getCode() {
		return code;
	}
	public void findbasicblocks() {
		leadingblocks = new ArrayList<Integer>();
		leadingblocks.add(1);
		for(int i = 1; i<ql.Size();i++) {//may involve index change for later
			if(ql.getQuad(i).opperation.equals("jmp")||ql.getQuad(i).opperation.equals("jeqz")) {
				leadingblocks.add(Integer.valueOf(ql.getQuad(i).result));
				if(ql.getQuad(i).opperation.equals("jeqz")) {
					leadingblocks.add(i+1);
				}
			}
		}
		if(!leadingblocks.contains(ql.Size()-1)) {
		leadingblocks.add(ql.Size()-1);
		}
		Collections.sort(leadingblocks);
	}
	public void scanblock() {
		st = new Symboltable();
		for(int k = 1; k<ql.Size();k++) {//involve index change later
			Quad quadK = ql.getQuad(k);
			if(quadK.opperand1.length() > 0 && !st.contain(quadK.opperand1)) {
				if(quadK.opperand1.length() > 0 && quadK.opperand1.charAt(0) == ('~')) {
					st.genField(quadK.opperand1,0,"Memory");
					quadK.op1nextuse = 0;
				}
				else if(Character.isLetter(quadK.opperand1.charAt(0))) {
					st.genField(quadK.opperand1,Integer.MAX_VALUE,"Memory");
					quadK.op1nextuse = Integer.MAX_VALUE;
				}
			}
			if(quadK.opperand2.length() > 0 && !st.contain(quadK.opperand2)) {
				if(quadK.opperand2.length() > 0 && quadK.opperand2.charAt(0) == ('~')) {
					st.genField(quadK.opperand2,0,"Memory");
					quadK.op2nextuse = 0;
				}
				else if(quadK.opperand2.length() > 0 && Character.isLetter(quadK.opperand2.charAt(0))) {
					st.genField(quadK.opperand2,Integer.MAX_VALUE,"Memory");
					quadK.op2nextuse = Integer.MAX_VALUE;
				}
			}
			if(!st.contain(quadK.result)) {
				if(quadK.opperand2.length() > 0 && quadK.result.charAt(0) == ('~')) {
					st.genField(quadK.result,0,"Memory");
					quadK.op3nextuse = 0;
				}
				else if(Character.isLetter(quadK.result.charAt(0))){
					st.genField(quadK.result,Integer.MAX_VALUE,"Memory");
					quadK.op3nextuse = Integer.MAX_VALUE;
				}
			}
		}
		for(int i = 0; i<leadingblocks.size()-1;i++) {
			for(int j = leadingblocks.get(i+1);j>leadingblocks.get(i)-1;j--) {//involve index change for later
				updatethenextuse(ql.getQuad(j), st.getFieldByName(ql.getQuad(j).opperand1), st.getFieldByName(ql.getQuad(j).opperand2), st.getFieldByName(ql.getQuad(j).result), j);
			}
		}
		for (int j = 0; j < st.Size(); j++) {
			Nextusefield symbol = st.getfield(j);
		}
	}
	private void reservememory() {
		ArrayList <String> vars = new ArrayList<String>();
		for(int i = 0; i<ql.Size();i++) {
			Quad q = ql.getQuad(i);
			if(validvars(q.opperand1)&&!vars.contains(q.opperand1)) {
				vars.add(q.opperand1);
			}
			if(validvars(q.opperand2)&&!vars.contains(q.opperand2)) {
				vars.add(q.opperand2);
			}
			if(validvars(q.result)&&!vars.contains(q.result)) {
				vars.add(q.result);
			}
		}
		for(int j = 0; j<vars.size();j++) {
			code = code+vars.get(j)+"  DS.B  2\n";
		}
	}
	private boolean validvars(String opperand1) {
		if(opperand1!=null) {
			if(opperand1.length()>0) {
				if(Character.isLetter(opperand1.charAt(0))) {
					return true;
				}
			}
		}
		return false;
	}
	void updatethenextuse(Quad a,Nextusefield op1, Nextusefield op2,Nextusefield result,int index) {
		if(op1!=null) {
			a.op1nextuse = op1.nextuse;
			op1.nextuse = index;
		}
		if(op2!=null) {
			a.op2nextuse = op2.nextuse;
			op2.nextuse = index;
		}
		if(result!=null) {
			a.op3nextuse = result.nextuse;
			result.nextuse = 0;
		}
	}
	/*Harrisons way
	 * ArrayList<String> GetReg(Quad q) {
		if(q.isliveB()&&!(st.getFieldByName(q.opperand1).location=="Memory")) {
			ArrayList<String> test = rt[Integer.parseInt(st.getFieldByName(q.opperand1).location)];
			if(test.size()==1) {
				return test;
			}
			
		}
		else if(returnemptyregister()!=null) {
			return returnemptyregister();
		}
		else {
			return thechosenone();
		}
		return null;
		
	}*/
	int GetReg(Quad q) {
			if(!(q.isliveB()) && (!(st.getFieldByName(q.opperand1).location.equals("Memory")))) {
				int test = Integer.parseInt(st.getFieldByName(q.opperand1).location);
				if(rt[test].size()==1) {
					return test;
				}
				
			}
			if(returnemptyregister()>=0) {
				return returnemptyregister();
			}
			else {
				return thechosenone();
			}
		
	}
	int thechosenone() {
		int thechosen =  returnshortregister();
		for(int i=0; i<rt[thechosen].size();i++) {
			generate("MOVE.L "+ ",D" + st.getFieldByName(rt[thechosen].get(i)).location +","+st.getFieldByName(rt[thechosen].get(i)).Name);
			st.getFieldByName(rt[thechosen].get(i)).location = "Memory";
		}
		rt[thechosen].clear();
		return thechosen;
	}
	
	int returnemptyregister(){
		for(int i=0;i<rt.length;i++) {
			if(rt[i].size()==0) {
				return i;
			}
		}
		return -1;
	}
	int returnshortregister(){
		int testsize = 99999;
		int index = 0;
		for(int i=0;i<rt.length;i++) {
			if(rt[i].size()<testsize) {
				testsize = rt[i].size();
				index = i;
			}
		}
		return index;
	}
	boolean isConstant(String s) {
		boolean ret;
		try {
			int i = Integer.parseInt(s);
			ret = true;
		} catch (Exception e) {
			// TODO: handle exception
			ret = false;
		}
		return ret;
	}
}
