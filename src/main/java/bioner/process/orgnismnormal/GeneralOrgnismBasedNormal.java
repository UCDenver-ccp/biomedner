///////////////////////////////////////////////////////////////////
//Usage: This is a implement for OrgnismBasedNormalizer. It normalize one protein by two way: 1.by a simple rule, get the nearest organism entity
//		before one protein entity in the same sentence, and keep only the id with the orgnism. 2. if step 1 didn't work, count the organism entity in the whole document
//		and use the most one or the one beyond a threshold as the organism.
//Author: Liu Jingchen
//Date: 2009/12/11
///////////////////////////////////////////////////////////////////
package bioner.process.orgnismnormal;

import java.util.Hashtable;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.orgnismnormal.OrgnismBasedNormalizer;
import bioner.process.orgnismnormal.OrgnismEntity;
import bioner.process.orgnismnormal.ProteinOrgnismTableBuilder;

public class GeneralOrgnismBasedNormal implements OrgnismBasedNormalizer {

	@Override
	public void normalizeNEREntity(BioNERDocument doc,
			OrgnismEntity[] orgnismEntities) {
		// TODO Auto-generated method stub
		Hashtable<String,String> ac_orgTable = ProteinOrgnismTableBuilder.getTable();
		
		//First, try step 1: the nearest organism before protein in the same sentence.
		normalRuleSentences(doc.getAllSentence(), ac_orgTable);
		
		
		//Then, try step 2: normalize according the number of organism in the whole document.
		normalSentences(doc.getAllSentence(), ac_orgTable, orgnismEntities);
		
	}
	
	private void normalRuleSentences(BioNERSentence[] sentences, Hashtable<String,String> ac_orgTable)
	{
		for(BioNERSentence sentence : sentences)
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				if(entity.get_Type().equals(GlobalConfig.PROTEIN_TYPE_LABEL)&&entity.getIDNum()>1)
				{
					normalRuleEntity(sentence, entity, ac_orgTable);
				}
			}
		}
	}
	private void normalRuleEntity(BioNERSentence sentence, BioNEREntity entity,Hashtable<String,String> ac_orgTable)
	{
		BioNEREntity formerOrgEntity = null;
		int minDis = Integer.MAX_VALUE;
		for(BioNEREntity otherEntity : sentence.getAllEntities())
		{
			if(otherEntity == entity&&!otherEntity.get_Type().equals(GlobalConfig.ORGANISM_TYPE_LABEL)) continue;
			int dis = otherEntity.get_Begin() - entity.get_Begin();
			if(dis<0) dis = -dis;
			if(dis<minDis)
			{
				minDis = dis;
				formerOrgEntity = otherEntity;
			}
		}
		if(formerOrgEntity!=null)
		{
			Vector<String> acArray = entity.getID();
			Vector<String> normalIDVector = new Vector<String>();
			for(String id : acArray)
			{
				String orgID = ac_orgTable.get(id);
				if(orgID!=null)
				{
					Vector<String> orgACArray = formerOrgEntity.getID();
					for(String orgAC : orgACArray)
					{
						if(orgAC.equals(orgID))
						{
							normalIDVector.add(id);
							break;
						}//if
					}//for
				}//if
			}//for
			//If the new ids is less and not empty, replace the entity's ids to them.
			if(normalIDVector.size()<entity.getIDNum()&&normalIDVector.size()>0)
			{
				entity.clearID();
				for(String id : normalIDVector)
				{
					entity.addID(id);
				}
			}
		}
	}
	
	
	private void normalSentences(BioNERSentence[] sentences, Hashtable<String,String> ac_orgTable,OrgnismEntity[] orgnismEntities)
	{
		for(BioNERSentence sentence : sentences)
		{
			for(BioNEREntity entity : sentence.getAllEntities())
			{
				if(entity.get_Type().equals(GlobalConfig.PROTEIN_TYPE_LABEL)&&entity.getIDNum()>1)
				{
					normalEntity(entity, ac_orgTable, orgnismEntities);
				}
			}
		}
	}
	
	private void normalEntity(BioNEREntity entity, Hashtable<String,String> ac_orgTable,OrgnismEntity[] orgnismEntities)
	{
		Vector<String> acArray = entity.getID();
		Vector<String> normalIDVector = new Vector<String>();
		for(String id : acArray)
		{
			String orgID = ac_orgTable.get(id);
			if(orgID!=null)
			{
				for(OrgnismEntity orgEntity : orgnismEntities)
				{
					if(orgEntity.getID().equals(orgID))
					{
						normalIDVector.add(id);
						break;
					}//if
				}//for
			}//if
		}//for
		
		//If the new ids is less and not empty, replace the entity's ids to them.
		if(normalIDVector.size()<entity.getIDNum()&&normalIDVector.size()>0)
		{
			entity.clearID();
			for(String id : normalIDVector)
			{
				entity.addID(id);
			}
		}
	}

}
