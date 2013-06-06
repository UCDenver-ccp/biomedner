package bioner.application.bc3gn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class Pick50DataFiles {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader freader = new BufferedReader(new FileReader("../../BC3GNTest/test50_annotations.txt"));
		Vector<String> idVector = new Vector<String>();
		String line;
		while((line=freader.readLine())!=null)
		{
			String[] parts = line.split("\\t");
			if(!idVector.contains(parts[0]))
			{
				idVector.add(parts[0]);
			}
		}
		File[] files = (new File("../../BC3GNTest/507_xmls")).listFiles();
		for(File file : files)
		{
			String id = file.getName();
			int pos = id.indexOf('.');
			id = id.substring(0, pos);
			if(idVector.contains(id))
			{
				copyFile(file, new File("../../BC3GNTest/"+id+".nxml"));
			}
		}
	}
	private static void copyFile(File oldFile, File newFile) throws IOException
	{
		BufferedReader freader = new BufferedReader(new FileReader(oldFile));
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(newFile));
		String line;
		while((line=freader.readLine())!=null)
		{
			fwriter.write(line);
			fwriter.newLine();
		}
		freader.close();
		fwriter.close();
	}
}
