package bioner.application.bc2gn;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.application.api.BioNERDocumentBuilder;
import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;
import bioner.process.crf.ProcessImpCRFPP;
import bioner.tools.dictionary.WordJudge;
import bioner.tools.enju.EnjuParser;

public class BC2GNBuildTrainRelatedWordList {

	public static void writerGMClassificationDataFile(String dataDir,String outputFile) throws IOException
	{
		long begintime = System.currentTimeMillis();
		BioNERDocumentBuilder docBuilder = new BC2GNDocumentBuilder(dataDir);
		BioNERDocument[] documents = docBuilder.buildDocuments();
		BioNERProcess[] pipeline = new BioNERProcess[2];
		pipeline[0] = new ProcessImpCRFPP(GlobalConfig.CRF_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_CRF);
		pipeline[1] = new ProcessImpCRFPP(GlobalConfig.CRF_INEXACT_MODEL_FILEPATH, GlobalConfig.ENTITY_LABEL_INEXACT_CRF);
		HashMap<String, Integer> wordMap = new HashMap<String, Integer>();
		for(int i=0; i<documents.length; i++)
		{
			System.out.print("Build related words. Processing #"+i+"....");
			BioNERDocument document = documents[i];
			for(int j=0; j<pipeline.length; j++)
			{
				pipeline[j].Process(document);
			}
			for(BioNERSentence sentence : document.getAllSentence())
			{
				for(BioNEREntity entity : sentence.getAllEntities())
				{
					Vector<String> relatedWordTextVector = EnjuParser.getGMAllRelatedWordTexts(sentence, entity);
					for(String wordText : relatedWordTextVector)
					{
						if(!isUsefulWord(wordText)) continue;
						Integer count = wordMap.get(wordText);
						if(count==null)
						{
							count=0;
						}
						count++;
						
						wordMap.put(wordText, count);
					}
					
				}//entity
				
			}//sentence
			
			
			
			
			System.out.println("Finished!");
		}
		
		int size = wordMap.keySet().size();
		WordNode[] wordArray = new WordNode[size];
		int i=0;
		for(String wordText : wordMap.keySet())
		{
			int count = wordMap.get(wordText);
			wordArray[i] = new WordNode();
			wordArray[i].text=wordText;
			wordArray[i].count=count;
			i++;
		}
		
		RankWord.Rank(wordArray);
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(outputFile));
		for(i=0; i<wordArray.length; i++)
		{
			fwriter.write(wordArray[i].text+"\t"+wordArray[i].count);
			fwriter.newLine();
		}
		fwriter.close();
		long endtime = System.currentTimeMillis();
		long time = endtime - begintime;
		System.out.println("Time spent: "+time+" ms");
		///////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	private static boolean isUsefulWord(String wordStr)
	{
		if(!wordStr.matches("[a-z]{2,}")) return false;
		if(wordStr.matches("alpha|beta|zeta|gamma|delta|mu|epsilon")) return false;
		return WordJudge.isWordIndex(wordStr);
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String dataDir = "../../BC2GN/data/trainingData";
		writerGMClassificationDataFile(dataDir, "../../BC2GN/RelatedWords.txt");
	}

}
class WordNode {
	public String text;
	public int count;

}

class RankWord {
	public static void Rank(WordNode[] genes)
	{
		quickSort(genes);
	}
	
	private static void quickSort(WordNode[] array) {
	        quickSort(array, 0, array.length - 1);
	}

    private static void quickSort(WordNode[] array, int low, int high) {
        if (low < high) {
            int p = partition(array, low, high);
            quickSort(array, low, p - 1);
            quickSort(array, p + 1, high);
        }

    }

    private static int partition(WordNode[] array, int low, int high) {
    	int s = array[high].count;
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j].count > s) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, ++i, high);
        return i;
    }

    private static void swap(WordNode[] array, int i, int j) {
    	WordNode temp;
        temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}