package bioner.application.bc3gn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class CrossValidationDataBuilder {

	private String m_dataDir = null;
	public CrossValidationDataBuilder(String dataDir)
	{
		m_dataDir = dataDir;
		if(!m_dataDir.endsWith("/")) m_dataDir += "/";
	}
	private void copyFile( String oldPath, String newPath )
    {
        try
        {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File( oldPath );
            if ( oldfile.exists() )
            { //when the old file exists
                InputStream inStream = new FileInputStream( oldPath );
                FileOutputStream fs = new FileOutputStream( newPath );
                byte[] buffer = new byte[ 1444 ];
                while ( ( byteread = inStream.read( buffer ) ) != -1 )
                {
                    bytesum += byteread;
                    //System.out.println( bytesum );
                    fs.write( buffer, 0, byteread );
                }
                inStream.close();
            }
        }
        catch ( Exception e )
        {
            System.out.println( "error in copy!" );
            e.printStackTrace();
        }

    }

	private String[] docIDArray = null;
	private HashMap<String, Vector<String>> goldTable = new HashMap<String, Vector<String>>();
	public void readGoldFile(String filename)
	{
		Vector<String> docIDVector = new Vector<String>();
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine()) != null)
			{
				String[] parts = line.split("\\s+");
				if(parts.length>=2)
				{
					Vector<String> geneIDVector = goldTable.get(parts[0]);
					if(geneIDVector == null)
					{
						geneIDVector = new Vector<String>();
						goldTable.put(parts[0], geneIDVector);
					}
					if(!docIDVector.contains(parts[0]))
					{
						docIDVector.add(parts[0]);
					}
					if(!geneIDVector.contains(parts[1]))
					{
						geneIDVector.add(parts[1]);
					}
				}
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int size = docIDVector.size();
		docIDArray = new String[size];
		for(int i=0; i<size; i++)
		{
			docIDArray[i] = docIDVector.elementAt(i);
		}
	}

	public void randomIDArray()
	{
		int size  = docIDArray.length;
		int time = size * 10;
		Random r = new Random();
		for(int i=0; i<time; i++)
		{
			int pos_1 = r.nextInt(size);
			int pos_2 = r.nextInt(size);
			String temp = docIDArray[pos_1];
			docIDArray[pos_1] = docIDArray[pos_2];
			docIDArray[pos_2] = temp;
		}
	}
	
	public void buildFoldData(int fold, int foldNum,String dir)
	{
		if(!dir.endsWith("/")) dir += "/";
		File dirFile = new File(dir+fold+"/");
		if(!dirFile.exists())
		{
			dirFile.mkdirs();
		}
		
		dirFile = new File(dir+fold+"/training/");
		if(!dirFile.exists())
		{
			dirFile.mkdirs();
		}
		
		dirFile = new File(dir+fold+"/testing/");
		if(!dirFile.exists())
		{
			dirFile.mkdirs();
		}
		try {
			BufferedWriter trainingWriter = new BufferedWriter(new FileWriter(dir+fold+"/TrainingSet.txt"));
			BufferedWriter testingWriter = new BufferedWriter(new FileWriter(dir+fold+"/TestingSet.txt"));
			for(int i=0; i<docIDArray.length; i++)
			{
				String targetDir;
				BufferedWriter fwriter;
				if(i%foldNum==fold)
				{
					targetDir = dir+fold+"/testing/";
					fwriter = testingWriter;
				}
				else
				{
					targetDir = dir+fold+"/training/";
					fwriter = trainingWriter;
				}
				String docID = docIDArray[i];
				copyFile(m_dataDir+docID+".nxml", targetDir+docID+".nxml");
				Vector<String> geneIDVector = goldTable.get(docID);
				for(String geneID : geneIDVector)
				{
					fwriter.write(docID+"\t"+geneID);
					fwriter.newLine();
				}
			}
			trainingWriter.close();
			testingWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dataDir = "../../BC3GN/32_data/";
		String goldFile = "../../BC3GN/TrainingSet1.txt";
		String outputDir = "../../BC3GN/cross/";
		int foldNum = 4;
		if(args.length==4)
		{
			dataDir = args[0];
			goldFile = args[1];
			outputDir = args[2];
			foldNum = Integer.parseInt(args[3]);
		}
		CrossValidationDataBuilder builder = new CrossValidationDataBuilder(dataDir);
		builder.readGoldFile(goldFile);
		builder.randomIDArray();
		for(int i=0; i<foldNum; i++)
		{
			builder.buildFoldData(i, foldNum, outputDir);
		}
	}

}
