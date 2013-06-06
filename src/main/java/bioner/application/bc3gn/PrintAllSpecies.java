package bioner.application.bc3gn;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.normalization.data.index.IndexConfig;
import bioner.normalization.feature.builder.SpeciesEntityStore;

public class PrintAllSpecies {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		IndexConfig.ReadConfigFile();
		BC3GNDataFileReader docBuilder = new BC3GNDataFileReader("../../BC3GN/50_data/");
		BioNERDocument[] documents = docBuilder.buildDocuments();
		BufferedWriter fwriter = new BufferedWriter(new FileWriter("../../BC3GN/all_species.txt"));
		for(int i=0; i<documents.length; i++)
		{
			//if(!documents[i].getID().equals("2048754")) continue;
			Vector<BioNEREntity> speciesVector = SpeciesEntityStore.getSpeciesEntities(documents[i]);
			Vector<String> textVector = new Vector<String>();
			HashMap<String, Integer> speciesNumTable = new HashMap<String, Integer>();
			for(BioNEREntity entity : speciesVector)
			{
				for(String id : entity.getID())
				{
					String line = entity.getText()+"\t"+id;
					if(!textVector.contains(line))
					{
						textVector.add(line);
						fwriter.write(documents[i].getID());
						fwriter.write("\t");
						fwriter.write(line);
						fwriter.newLine();
						System.out.println(line);
					}
					String speciesID = id;
					Integer num = speciesNumTable.get(speciesID);
					if(num==null) num=0;
					num++;
					speciesNumTable.put(speciesID, num);
				}
			}
			System.out.println("Document #"+i+"Finished!");
			
			/*for(String speciesID : speciesNumTable.keySet())
			{
				fwriter.write(documents[i].getID());
				fwriter.write("\t");
				fwriter.write(speciesID);
				fwriter.write("\t");
				Integer num = speciesNumTable.get(speciesID);
				fwriter.write(num.toString());
				fwriter.newLine();
			}*/
		}
		fwriter.close();
	}

}
