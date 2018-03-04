import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

public class RunnerNick extends PL40603{

	public RunnerNick(InputStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		QuadList qList = new QuadList();
		RunnerNick jj;
		String [] items = new String[6];
		items[0] = "return";
		items[1] = "continue";
		items[2] = "break";
		items[3] = "if";
		items[4] = "else";
		items[5] = "while";
		String inputFile;
		Scanner reader = new Scanner(System.in);
		
		
		System.out.println("Enter the name of a file to test: ");
		
		//Need a try-catch block here?
		inputFile = "./" + reader.next();
		
		try {
			jj = new RunnerNick(new FileInputStream(new File(inputFile)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Enter a valid file name");
			e.printStackTrace();
			return;
		}	
		
		
		try {
		jj.Input();
		} catch(ParseException e) {
			
			String a = e.toString();
			String [] errorarray = a.split("\n");
			Integer b = e.currentToken.next.beginLine;
			Integer c = e.currentToken.next.beginColumn;
			if(Arrays.stream(items).parallel().anyMatch(a::contains)) {
				System.out.println("Illegal use of reversed words on line "+b);
				System.exit(0);
			}
			else if(errorarray[2].contains("}")) {
				System.out.println("Illegal name of identifier on line "+b);
				System.exit(0);
			}
			else if(errorarray[2].contains(";")) {
				System.out.println("Miss semicolon on line "+b);
				System.exit(0);
			}
			else {
				System.out.println("Invalid formatting\n Program expecting: "+errorarray[2]+"on line "+b+"\n But "+errorarray[0].substring(16)+"\n");
				System.exit(0);
			}
			return;
		}
		String outputFile1 = inputFile.substring(0, inputFile.length()-4) +"-outputQuad"+".txt";
		String outputFile2 = inputFile.substring(0, inputFile.length()-4) +"-outputCode"+".txt";
		PrintWriter writer = null;
		PrintWriter writer2 = null;
		try {
			writer = new PrintWriter(outputFile1, "UTF-8");
			writer2 = new PrintWriter(outputFile2, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		System.out.println("Quads");
		writer.println("Quads:");
		String s = quads.print();
		writer.println(s);
		writer.close();
		try {
			qList.readInQuads(s);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		Codegen cg = new Codegen(qList);
		System.out.println("CODE:");
		cg.findbasicblocks();
		cg.codeGeneration();
		
		writer2.println("CODE:");
		writer2.println(cg.code);
		

		writer2.close();
	    return;
	}
 
}
