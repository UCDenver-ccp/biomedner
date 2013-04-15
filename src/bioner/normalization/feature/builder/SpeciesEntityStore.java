package bioner.normalization.feature.builder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.normalization.data.index.IndexConfig;
import bioner.normalization.data.index.LuceneIndexSpeicesIDFinder;
import bioner.normalization.data.index.LuceneSpeciesIndexBuilder;
import bioner.tools.NERFactory;
import bioner.tools.NERProcessor;
import bioner.tools.linnaeus.LinnaeusSpeciesNER;

public class SpeciesEntityStore {
	//private static NERProcessor m_speciesNER = NERFactory.getSpeciesIndexNER();
	private static NERProcessor m_speciesLinnaeusNER = LinnaeusSpeciesNER.getLinneausSpeciesNER();
	private static BioNERDocument m_currentDocument = null;
	private static Vector<BioNEREntity> m_currentVector = null;
	private static LuceneIndexSpeicesIDFinder m_idFinder = new LuceneIndexSpeicesIDFinder();
	private static Vector<String> m_filterVector = getFilterVector("./data/filter/tabulist_species.txt");
	private static Vector<String> m_commonSpeciesVector = LuceneSpeciesIndexBuilder.readCommonSpeciesList(IndexConfig.COMMON_SPEICIES_FILENAME); 
	private static Vector<String> getFilterVector(String filename)
	{
		Vector<String> vector = new Vector<String>();
		
		try {
			BufferedReader freader = new BufferedReader(new FileReader(filename));
			String line;
			while((line=freader.readLine())!=null)
			{
				vector.add(line.toLowerCase().trim());
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return vector;
	}
	
	/*private static Vector<BioNEREntity> speciesNER(BioNERSentence sentence)
	{
		Vector<BioNEREntity> vector = new Vector<BioNEREntity>();
		BioNEREntity[] entities = m_speciesNER.recognizeSentence(sentence);
		for(BioNEREntity entity : entities)
		{
			vector.add(entity);
		}
		BioNEREntity[] entities = m_speciesLinnaeusNER.recognizeSentence(sentence);
		for(BioNEREntity entity : entities)
		{
			vector.add(entity);
		}
		Vector<BioNEREntity> uncoverdEntityVector = new Vector<BioNEREntity>();
		for(BioNEREntity entity : vector)
		{
			boolean covered = false;
			for(BioNEREntity otherEntity : uncoverdEntityVector)
			{
				if(otherEntity==entity) continue;
				if(otherEntity.getTokenBeginIndex()<= entity.getTokenBeginIndex()
						&& otherEntity.getTokenEndIndex() >= entity.getTokenEndIndex())
				{
					covered = true;
					if(otherEntity.getTokenBeginIndex()== entity.getTokenBeginIndex()
							&& otherEntity.getTokenEndIndex() == entity.getTokenEndIndex())
					for(String id : entity.getID())
					{
						otherEntity.addID(id);
					}
					break;
				}
				if(otherEntity.getTokenBeginIndex()> entity.getTokenBeginIndex()
						&& otherEntity.getTokenEndIndex() < entity.getTokenEndIndex())
				{
					covered = true;
					otherEntity.clearID();
					for(String id : entity.getID())
					{
						otherEntity.addID(id);
					}
					otherEntity.setTokenBeginIndex(entity.getTokenBeginIndex());
					otherEntity.setTokenEndIndex(entity.getTokenEndIndex());
					break;
				}
			}
			if(!covered)
			{
				uncoverdEntityVector.add(entity);
			}
		}
		return uncoverdEntityVector;
	}*/
	
	private static Vector<BioNEREntity> speciesNER(BioNERSentence sentence)
	{
		Vector<BioNEREntity> vector = new Vector<BioNEREntity>();
		BioNEREntity[] entities = m_speciesLinnaeusNER.recognizeSentence(sentence);
		for(BioNEREntity entity : entities)
		{
			vector.add(entity);
		}
		return vector;
	}
	public static Vector<BioNEREntity> getSpeciesEntities(BioNERDocument document)
	{
		if(document==m_currentDocument) return m_currentVector;
		m_currentDocument = document;
		m_currentVector = new Vector<BioNEREntity>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			Vector<BioNEREntity> entities = speciesNER(sentence);
			for(BioNEREntity entity : entities)
			{
				m_currentVector.add(entity);
			}
		}
		disambiguateSpecies(m_currentVector);
		return m_currentVector;
	}
	/*public static Vector<BioNEREntity> getSpeciesEntities(BioNERDocument document)
	{
		if(document==m_currentDocument) return m_currentVector;
		m_currentDocument = document;
		m_currentVector = new Vector<BioNEREntity>();
		for(BioNERSentence sentence : document.getAllSentence())
		{
			Vector<BioNEREntity> entities = speciesNER(sentence);
			for(BioNEREntity entity : entities)
			{
				for(String id : entity.getID())
				{
					if(id.length()>0)
					{
						if(m_commonSpeciesVector.contains(id))
						{
							m_currentVector.add(entity);
							break;
						}
						else if(!m_filterVector.contains(entity.getText().trim().toLowerCase())
								&& !entity.getText().matches("[a-z\\d]\\W+[a-z\\d]+")
								&& !entity.getText().matches(".*[\\(\\)\\\\\\/\\-\\+\\,].*")
								&& !entity.getText().matches("[A-Z]+|[a-z][A-Z]\\w+|\\w\\W+\\b.*[A-Z].*\\b")
								&& correct(entity.getText()))
						{
							m_currentVector.add(entity);
							break;
						}
					}
				}
			}
		}
		disambiguateSpecies(m_currentVector);
		return m_currentVector;
	}*/
	
	
	private static boolean correct(String name)
	{
		if(name.matches("[A-Z]\\s[a-z]{3,3}|\\s.*")) return false;
		if(name.toLowerCase().matches("(aa|unidentified|hybrid|[Tt]his can|small but|cancers?|bias|name|([Aa] )?major|[Bb]iases|indicator|codon|argon|goes|helix|permit|comet|cis)s?")) return false;
		return true;
	}
	private static void disambiguateSpecies(Vector<BioNEREntity> speciesVector)
	{
		for(BioNEREntity entity : speciesVector)
		{
			if(entity.getIDNum()>1)
			{
				String entityText = entity.getText();
				Vector<String> oldIDArray = entity.getID();
				Vector<String> newIDVector = new Vector<String>();
				for(String oldID : oldIDArray)
				{
					for(BioNEREntity otherEntity : speciesVector)
					{
						String otherEntityText = otherEntity.getText();
						if(otherEntity.getIDNum()==1 && !entityText.equals(otherEntityText) && otherEntity.hasID(oldID))
						{
							newIDVector.add(oldID);
							break;
						}
					}
				}
				if(!newIDVector.isEmpty())
				{
					entity.setID(newIDVector);
				}
			}
			if(entity.getIDNum()>1)
			{
				Vector<String> oldIDArray = entity.getID();
				Vector<String> newIDVector = new Vector<String>();
				for(String oldID : oldIDArray)
				{
					if(m_commonSpeciesVector.contains(oldID))
					{
						newIDVector.add(oldID);
					}
				}
				if(!newIDVector.isEmpty())
				{
					entity.setID(newIDVector);
				}
			}
		}
	}
}
