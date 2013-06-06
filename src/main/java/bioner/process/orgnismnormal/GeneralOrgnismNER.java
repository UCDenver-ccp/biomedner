package bioner.process.orgnismnormal;

import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.orgnismnormal.OrgnismEntity;
import bioner.process.orgnismnormal.OrgnismRecognizer;

public class GeneralOrgnismNER implements OrgnismRecognizer {

	@Override
	public OrgnismEntity[] recognizeOrgnisms(BioNERDocument doc) {
		// TODO Auto-generated method stub
		Vector<String> orgIDVector = new Vector<String>();
		Vector<Integer> orgNumVector = new Vector<Integer>();
		
		//For each entity in the abstract
		for(BioNERSentence sentence : doc.getAbstractSentences())
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				//if it is a organism
				if(!entity.get_Type().equals(GlobalConfig.ORGANISM_TYPE_LABEL)) continue;
				
				Vector<String> entityIDs = entity.getID();
				for(String entityID : entityIDs)
				{
					int size = orgIDVector.size();
					
					//Try to find it in the vector
					boolean exists = false;
					for(int i=0; i<size; i++)
					{
						String currentOrgID = orgIDVector.elementAt(i);
						if(currentOrgID.equals(entityID))
						{
							//If we find it, plus its number with 25
							int idNum = orgNumVector.elementAt(i);
							idNum += 25;
							orgNumVector.set(i, idNum);
							exists = true;
							break;
						}
					}
					if(!exists)
					{
						//If we didn't find it, add it with the initial number 25
						orgIDVector.add(entityID);
						orgNumVector.add(25);
					}
				}
			}
		}
		//For each entity in the abstract
		for(BioNERSentence sentence : doc.getFullTextSentences())
		{
			//if it is a organism
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				if(!entity.get_Type().equals(GlobalConfig.ORGANISM_TYPE_LABEL)) continue;
				
				Vector<String> entityIDs = entity.getID();
				for(String entityID : entityIDs)
				{
					int size = orgIDVector.size();
					//Try to find it in the vector
					boolean exists = false;
					for(int i=0; i<size; i++)
					{
						String currentOrgID = orgIDVector.elementAt(i);
						if(currentOrgID.equals(entityID))
						{
							//If we find it, plus its number with 1
							int idNum = orgNumVector.elementAt(i);
							idNum += 1;
							orgNumVector.set(i, idNum);
							exists = true;
							break;
						}
					}
					if(!exists)
					{
						//If we didn't find it, add it with the initial number 1
						orgIDVector.add(entityID);
						orgNumVector.add(1);
					}
				}
			}
		}
		
		Vector<OrgnismEntity> orgVector = getFinalOrgnismEntity(orgIDVector, orgNumVector);
		int size = orgVector.size();
		OrgnismEntity[] entities = new OrgnismEntity[size];
		for(int i=0; i<size; i++)
		{
			entities[i] = orgVector.elementAt(i);
		}
		return entities;
	}
	private Vector<OrgnismEntity> getFinalOrgnismEntity(Vector<String> orgIDVector, Vector<Integer> orgNumVector)
	{
		Vector<OrgnismEntity> orgVector = new Vector<OrgnismEntity>();
		
		//Get the max number in all
		int max = Integer.MIN_VALUE;
		for(int num : orgNumVector)
		{
			if(num > max)
			{
				max = num;
			}
		}
		//Set the threshold according to the max
		int threshold = max / 3;
		
		//Add the organisms beyond the threshold
		int size = orgIDVector.size();
		for(int i=0; i<size; i++)
		{
			int num = orgNumVector.elementAt(i);
			if(num>=threshold)
			{
				String orgID = orgIDVector.elementAt(i);
				OrgnismEntity orgEntity = new OrgnismEntity();
				orgEntity.setID(orgID);
				orgVector.add(orgEntity);
			}
		}
		return orgVector;
	}
}
