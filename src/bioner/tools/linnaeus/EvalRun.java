package bioner.tools.linnaeus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class EvalRun {

	public static void eval(ArrayList<Node> resultList, ArrayList<Node> goldList, ArrayList<Node> tpList, ArrayList<Node> fpList)
	{
		tpList.clear();
		fpList.clear();
		for(Node node : resultList)
		{
			if(isContained(node, goldList))
			{
				tpList.add(node);
			}
			else
			{
				fpList.add(node);
			}
		}
	}
	public static boolean isContained(Node node, ArrayList<Node> list)
	{
		for(Node listNode : list)
		{
			if(node.equals(listNode)) return true;
		}
		return false;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<Node> tpList = new ArrayList<Node>();
		ArrayList<Node> fpList = new ArrayList<Node>();
		ArrayList<Node> fnList = new ArrayList<Node>();
		ArrayList<Node> resultList = Node.readFile("/home/ljc/software/LINNAEUS/manual-corpus-species-1.0/result.txt");
		ArrayList<Node> goldList = Node.readFile("/home/ljc/software/LINNAEUS/manual-corpus-species-1.0/tags.tsv");
		eval(resultList, goldList, tpList, fpList);
		eval(goldList, resultList, tpList, fnList);
		int TP = tpList.size();
		int FP = fpList.size();
		int FN = fnList.size();
		double precision = (double)TP/(double)(TP+FP);
		double recall = (double)TP/(double)(TP+FN);
		double f = 2*precision*recall/(precision+recall);
		String outputFile = "/home/ljc/software/LINNAEUS/manual-corpus-species-1.0/eval.txt";
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFile));
		fwriter.write("TP="+TP+" FP="+FP+" FN="+FN);
		fwriter.newLine();
		fwriter.write("Precision="+precision+" Recall="+recall+" F="+f);
		fwriter.newLine();
		for(Node node : tpList)
		{
			fwriter.write("TP|"+node.toString());
			fwriter.newLine();
		}
		for(Node node : fpList)
		{
			fwriter.write("FP|"+node.toString());
			fwriter.newLine();
		}
		BufferedWriter addFwriter = new BufferedWriter(new FileWriter("/home/ljc/software/LINNAEUS/manual-corpus-species-1.0/species_add.txt"));
		ArrayList<String> addLineList = new ArrayList<String>();
		for(Node node : fnList)
		{
			String addLine = node.getID()+"\t"+node.getText();
			if(!addLineList.contains(addLine))
			{
				addLineList.add(addLine);
				if(!node.getID().equals("species:ncbi:0"))
				{
					addFwriter.write(addLine);
					addFwriter.newLine();
				}
			}
			fwriter.write("FN|"+node.toString());
			fwriter.newLine();
		}
		addFwriter.close();
		fwriter.close();
	}

}

class Node{
	private String m_id;
	private String m_docID;
	private String m_text;
	private int m_begin;
	private int m_end;
	public void setID(String m_id) {
		this.m_id = m_id;
	}
	public String getID() {
		return m_id;
	}
	public void setDocID(String m_docID) {
		this.m_docID = m_docID;
	}
	public String getDocID() {
		return m_docID;
	}
	public void setText(String m_text) {
		this.m_text = m_text;
	}
	public String getText() {
		return m_text;
	}
	public void setBegin(int m_begin) {
		this.m_begin = m_begin;
	}
	public int getBegin() {
		return m_begin;
	}
	public void setEnd(int m_end) {
		this.m_end = m_end;
	}
	public int getEnd() {
		return m_end;
	}
	public boolean equals(Node otherNode)
	{
		return m_id.equals(otherNode.getID()) && m_docID.equals(otherNode.getDocID()) /*&& m_text.equals(otherNode.getText())*/;
		//&& m_begin==otherNode.getBegin() && m_end==otherNode.getEnd();
	}
	public String toString()
	{
		return m_id+"\t"+m_docID+"\t"+m_begin+"\t"+m_end+"\t"+m_text;
	}
	public static Node readLine(String line)
	{
		Node node = new Node();
		String[] parts = line.split("\\t");
		node.setID(parts[0]);
		node.setDocID(parts[1]);
		node.setBegin(Integer.parseInt(parts[2]));
		node.setEnd(Integer.parseInt(parts[3]));
		node.setText(parts[4]);
		return node;
	}
	public static ArrayList<Node> readFile(String filename) throws IOException
	{
		BufferedReader freader = new BufferedReader(new FileReader(filename));
		ArrayList<Node> list = new ArrayList<Node>();
		String line;
		while((line=freader.readLine())!=null)
		{
			if(line.startsWith("#") || line.length()<=0) continue;
			Node node = readLine(line);
			list.add(node);
		}
		freader.close();
		return list;
	}
}