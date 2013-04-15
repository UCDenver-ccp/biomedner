package bioner.application.webtool;

import genetukit.api.GNProcessor;
import genetukit.api.GNResultItem;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.global.GlobalConfig;
import bioner.normalization.ProcessImpFilterAfterGetCandidate;
import bioner.normalization.ProcessImpFilterGeneMention;
import bioner.normalization.ProcessImpFirstRankByListNet;
import bioner.normalization.ProcessImpGetCandidateID;
import bioner.normalization.candidate.CandidateFinder;
import bioner.normalization.data.BioNERCandidate;
import bioner.normalization.rerank.BuildGeneIDVectorMap;
import bioner.normalization.rerank.GeneRerankByLogistic;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.process.proteinner.ProcessImpProteinABNER;
import bioner.process.proteinner.ProcessImpProteinBANNER;
import bioner.process.proteinner.ProcessImpProteinIndexNER;

public class GNRun {
	public static int rank = 1;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length!=2 && args.length!=3)
		{
			System.err.println("Args num error!");
			System.exit(1);
		}
		
		
		
		PrintStream consoleStream = System.out;
		PrintStream tempStream = new PrintStream(new OutputStream() {
			
			@Override
			public void write(int arg0) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		System.setOut(tempStream);
		
		GNProcessor.FileType fileType = null;
		if(args[0].equals("-x"))
		{
			fileType = GNProcessor.FileType.NXML;
		}
		else if(args[0].equals("-p"))
		{
			fileType = GNProcessor.FileType.PLAIN;
		}
		else
		{
			System.err.println("Arg 1 error!");
			System.exit(1);
		}
		
		
		
		boolean useBanner;
		if(args.length==3&&args[2].equals("-banner"))
		{
			useBanner = true;
		}
		else
			useBanner = false;
		
		GNProcessor processor = new GNProcessor();
		processor.open(useBanner);
		File rootFile = new File(args[1]);
		File[] files;
		if(rootFile.isDirectory())
		{
			files = rootFile.listFiles();
		}
		else
		{
			files = new File[]{rootFile};
		}
		for(int i=0; i<files.length; i++)
		{
			long beginTime = System.currentTimeMillis();
			File file = files[i];
			GNResultItem[] items = processor.process(file.getAbsolutePath(), fileType);
			
			System.setOut(consoleStream);
			System.out.println("Results for "+file.getAbsolutePath()+" :");
			for(int j=0; j<items.length; j++)
			{
				StringBuffer sb = new StringBuffer();
				sb.append(items[j].getID());
				sb.append("\t");
				for(int k=0; k<items[j].getGeneMentionList().size(); k++)
				{
					if(k!=0) sb.append("|");
					sb.append(items[j].getGeneMentionList().get(k));
				}
				sb.append("\t");
				sb.append(items[j].getSpeciesID());
				sb.append("\t");
				sb.append(items[j].getScore());
				System.out.println(sb.toString());
			}
			System.out.println();
			System.setOut(tempStream);
			long endTime = System.currentTimeMillis();
			long time = endTime - beginTime;
			System.err.println("Finished! "+time+" ms");
		}
		processor.close();
		
	}
	
	public static String getGeneIDStr(BioNERCandidate candidate, HashMap<String, Vector<BioNEREntity>> geneIDMap)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(candidate.getRecordID()+"\t");
		
		Vector<BioNEREntity> entityVector = geneIDMap.get(candidate.getRecordID());
		Vector<String> gmVector = new Vector<String>();
		for(BioNEREntity entity : entityVector)
		{
			String gmText = entity.getText();
			if(!gmVector.contains(gmText))
				gmVector.add(gmText);
		}
		
		for(int i=0; i<gmVector.size(); i++)
		{
			if(i!=0) sb.append("|");
			String gmStr = gmVector.elementAt(i);
			gmStr = gmStr.replaceAll("\"", "\\\"");
			sb.append(gmVector.elementAt(i));
		}
		sb.append("\t");
		sb.append(candidate.getScore());
		return sb.toString();
	}
}
