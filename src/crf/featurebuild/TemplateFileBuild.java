package crf.featurebuild;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TemplateFileBuild {

	/**
	 * @param args
	 */
	public static int currentNum = 1;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int featureNum = 53;
		
		try {
			BufferedWriter fwriter = new BufferedWriter(new FileWriter("../../BC2GM/template.txt"));
			for(int i=0; i<featureNum; i++)
			{
				
					WriterOneFeature(fwriter,"U",-2,i);
					WriterOneFeature(fwriter,"U",-1,i);
					WriterOneFeature(fwriter,"U",0,i);
					WriterOneFeature(fwriter,"U",1,i);
					WriterOneFeature(fwriter,"U",2,i);
				
				//WriterTwoFeature(fwriter,"U",-2,-1,i);
				//WriterTwoFeature(fwriter,"U",-1,0,i);
				//WriterTwoFeature(fwriter,"U",0,1,i);
				//WriterTwoFeature(fwriter,"U",1,2,i);
				//WriterThreeFeature(fwriter,"U",-2,-1,0,i);
				//WriterThreeFeature(fwriter,"U",-1,0,1,i);
				//WriterThreeFeature(fwriter,"U",0,1,2,i);
			}
			currentNum = 1;
			for(int i=0; i<featureNum; i++)
			{
				//if(i==37) continue;//Protein Dict Num
				//if(i==38) continue;//Org Dict Num
				//if(i==40) continue;//BIO
				
					WriterOneFeature(fwriter,"B",-3,i);
					WriterOneFeature(fwriter,"B",-2,i);
					WriterOneFeature(fwriter,"B",-1,i);
					WriterOneFeature(fwriter,"B",0,i);
					WriterOneFeature(fwriter,"B",1,i);
				
				//WriterTwoFeature(fwriter,"B",-2,-1,i);
				//WriterTwoFeature(fwriter,"B",-1,0,i);
				//WriterTwoFeature(fwriter,"B",0,1,i);
				//WriterTwoFeature(fwriter,"B",1,2,i);
				//WriterThreeFeature(fwriter,"B",-2,-1,0,i);
				//WriterThreeFeature(fwriter,"B",-1,0,1,i);
				//WriterThreeFeature(fwriter,"B",0,1,2,i);
			}
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void WriterOneFeature(BufferedWriter fwriter, String prefix, int pos, int num) throws IOException
	{
		Integer UNum = currentNum;
		currentNum++;
		String line = prefix+UNum+":";
		line += "%x["+pos+","+num+"]";
		fwriter.write(line);
		fwriter.newLine();
	}
	public static void WriterTwoFeature(BufferedWriter fwriter, String prefix, int pos_1, int pos_2, int num) throws IOException
	{
		Integer UNum = currentNum;
		currentNum++;
		String line = prefix+UNum+":";
		line += "%x["+pos_1+","+num+"]/%x["+pos_2+","+num+"]";
		fwriter.write(line);
		fwriter.newLine();
	}
	public static void WriterThreeFeature(BufferedWriter fwriter, String prefix, int pos_1, int pos_2, int pos_3, int num) throws IOException
	{
		Integer UNum = currentNum;
		currentNum++;
		String line = prefix+UNum+":";
		line += "%x["+pos_1+","+num+"]/%x["+pos_2+","+num+"]/%x["+pos_3+","+num+"]";
		fwriter.write(line);
		fwriter.newLine();
	}
}
