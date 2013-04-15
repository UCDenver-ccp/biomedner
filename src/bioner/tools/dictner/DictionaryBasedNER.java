////////////////////////////////////////////////////////
//Usage: This is a implement for SentenceNER. It use the dictionary based method to NER from one sentence.
//Author: Liu Jingchen
//Date: 2009/12/2
////////////////////////////////////////////////////////
package bioner.tools.dictner;

import java.util.Vector;

import org.apache.tools.ant.taskdefs.condition.IsTrue;

import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.data.document.BioNERToken;
import bioner.tools.dictionary.BioNERDictionary;
import bioner.tools.dictionary.BioNERTerm;
import bioner.tools.dictionary.WordJudge;
import bioner.tools.nlptools.NLPToolsFactory;
import bioner.tools.nlptools.TokenPOSTagger;
import bioner.tools.strnormal.StringNormalizer;
import bioner.tools.strnormal.StringNormalizerFactory;

public class DictionaryBasedNER implements SentenceNER {

	private BioNERDictionary proteinDict;
	
	public DictionaryBasedNER(BioNERDictionary dict)
	{
		proteinDict = dict;
	}
	@Override
	public BioNEREntity[] SentenceBasedNER(BioNERSentence sentence) {
		// TODO Auto-generated method stub
		BioNERToken[] tokens = sentence.getTokens();
		int tokenNum =tokens.length;
		String[] tokenStrs = new String[tokenNum];
		for(int i=0; i<tokenNum; i++)
		{
			tokenStrs[i] = tokens[i].getText();
		}
		
		BioNERTerm[][] termSetList = getTermsForEachToken(tokens);
		BioNEREntity[] entities = getEntities(tokens, termSetList,sentence);
		return entities;
	}
	
	//Look up the terms for each token in the dictionary. They will be stored in BioNERTerm[][].
	//The terms for BioNERToken[i] is in BioNERTerm[i][], it will be null for no result.
	private BioNERTerm	[][] getTermsForEachToken(BioNERToken[] tokens)
	{
		BioNERTerm[][] termSetList = new BioNERTerm[tokens.length][];
		for(int i=0; i<tokens.length; i++)
		{
			BioNERToken token = tokens[i];
			BioNERTerm[] termSet = null;
			String word = token.getNormalText();
			if(WordJudge.isWordIndex(word))
			{
				termSet = proteinDict.getTermsByIndex(word);
			}
			termSetList[i] = termSet;
		}
		
		//Add terms to each token. These terms in each token might be used as CRF feature.
		for(int i=0; i<tokens.length; i++)
		{
			if(termSetList[i]!=null)
			{
				for(BioNERTerm term : termSetList[i])
				{
					tokens[i].addTerm(term);
				}
			}
		}
		
		return termSetList;
	}
	
	//Make entities according to the tokens and terms for each token.
	private BioNEREntity[] getEntities(BioNERToken[] tokens,BioNERTerm[][] termSetList,BioNERSentence sentence)
	{ 
		Vector<BioNEREntity> entityVector = new Vector<BioNEREntity>();
		
		int begin_pos = 0;
		int end_pos = 0;
		while(true)
		{
			while(begin_pos<tokens.length&&termSetList[begin_pos]==null) begin_pos++;//Find first place is not null
			end_pos = begin_pos;
			//while(end_pos<tokens.length-1&&termSetList[end_pos+1]!=null) end_pos++;
			while(true)
			{
				if(end_pos<tokens.length-1&&termSetList[end_pos+1]!=null)
				{
					end_pos++;
				}
				else if(end_pos<tokens.length-2)
				{
					String tokenStr = tokens[end_pos].getText();
					if((tokenStr.matches("\\W")||tokenStr.equals("and")) && termSetList[end_pos+2]!=null)
					{
						end_pos += 2;
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			
			
			if(end_pos>=tokens.length) break;
			posRecorder.clear();
			processOneSeqTerms(tokens, termSetList, sentence, begin_pos, end_pos, entityVector);
			begin_pos = end_pos+1;
		}
		
		
		
		int size = entityVector.size();
		BioNEREntity[] entities = new BioNEREntity[size];
		for(int i=0; i<size; i++)
		{
			entities[i] = entityVector.elementAt(i);
			entities[i].set_Type(proteinDict.getType());
		}
		return entities;
	}
	
	private Vector<BioNERTerm> getCommonTerm(BioNERTerm[][] termSetList, int begin, int end)
	{
		int min = Integer.MAX_VALUE;
		int minIndex = begin;
		for(int i=begin; i<=end; i++)
		{
			if(termSetList[i]!=null && termSetList[i].length<min)
			{
				minIndex = i;
				min = termSetList[i].length;
			}
		}
		Vector<BioNERTerm>[] termVectorArray = new Vector[termSetList.length];
		for(int i=0; i<termVectorArray.length; i++)
		{
			if(i>=begin && i<=end && termSetList[i]!=null)
			{
				termVectorArray[i] = getTermVector(termSetList[i]);
			}
			else
			{
				termVectorArray[i] = null;
			}
		}
		Vector<BioNERTerm> smallTermVector = termVectorArray[minIndex];
		Vector<BioNERTerm> commonTermVector = new Vector<BioNERTerm>();
		for(BioNERTerm term : smallTermVector)
		{
			boolean isCommon = true;
			for(int i=begin; i<=end; i++)
			{
				if(i != minIndex && termVectorArray[i]!=null)
				{
					Vector<BioNERTerm> nextTermVector = termVectorArray[i];
					//if(!isTermVectorContains(nextTermVector, term))
					if(!nextTermVector.contains(term))
					{
						isCommon =false;
						break;
					}
				}
			}
			if(isCommon)
			{
				commonTermVector.add(term);
			}
		}
		return commonTermVector;
	}
	private boolean isTermVectorContains(Vector<BioNERTerm> termVector, BioNERTerm term)
	{
		String id_2 = term.getId();
		for(BioNERTerm termInVector : termVector)
		{
			String id_1 = termInVector.getId();
			
			if(id_1.equals(id_2)) return true;
		}
		return false;
	}
	private Vector<BioNERTerm> getTermVector(BioNERTerm[] termArray)
	{
		Vector<BioNERTerm> vector = new Vector<BioNERTerm>();
		for(int i=0; i<termArray.length; i++)
		{
			vector.add(termArray[i]);
		}
		return vector;
	}
	
	private PostionPairRecorder posRecorder = new PostionPairRecorder();
	private void processOneSeqTerms(BioNERToken[] tokens, BioNERTerm[][] termSetList,BioNERSentence sentence,int begin, int end, Vector<BioNEREntity> entityVector)
	{
		if(posRecorder.contains(begin, end))
		{
			return;
		}
		else
		{
			posRecorder.addPair(begin, end);
		}
		if(begin==end) 
		{
			addEntityForOneToken(tokens, termSetList, sentence, begin, entityVector);
		}
		else if(begin<end)
		{
			if(end-begin>=5)
			{
				processOneSeqTerms(tokens, termSetList, sentence, begin+1, end, entityVector);
				processOneSeqTerms(tokens, termSetList, sentence, begin, end-1, entityVector);
				return;
			}
			if(termSetList[begin]==null)
			{
				processOneSeqTerms(tokens, termSetList, sentence, begin+1, end, entityVector);
				return;
			}
			if(termSetList[end]==null)
			{
				processOneSeqTerms(tokens, termSetList, sentence, begin, end-1, entityVector);
				return;
			}
			Vector<BioNERTerm> commonTermVector = getCommonTerm(termSetList, begin, end);
			boolean isEntity = false;
			if(!commonTermVector.isEmpty())
			{
				isEntity = addEntityFromMultiToken(tokens, commonTermVector, sentence, begin, end, entityVector);
				
			}
			if(!isEntity)
			{
				processOneSeqTerms(tokens, termSetList, sentence, begin+1, end, entityVector);
				processOneSeqTerms(tokens, termSetList, sentence, begin, end-1, entityVector);
			}
		}
	}
	
	
	private boolean addEntityForOneToken(BioNERToken[] tokens,BioNERTerm[][] termSetList,BioNERSentence sentence,int index, Vector<BioNEREntity> entityVector)
	{
		if(termSetList[index]==null) return false;
		//if(!posTags[index].startsWith("NN")) return false;
		BioNERToken token = tokens[index];
		Vector<BioNERTerm> termVector = getTermVector(termSetList[index]);
		//if(termVector.size()>20) return false;
		BioNEREntity entity = new BioNEREntity();
		entity.set_Sentence(sentence);
		entity.set_position(token.getBegin(), token.getEnd());
		
		Vector<BioNERTerm> matchedTermVector = getExactMatchedTerm(entity, termVector);
		//Vector<BioNERTerm> matchedTermVector = termVector;
		if(!matchedTermVector.isEmpty())
		{
			for(BioNERTerm term : matchedTermVector)
			{
				entity.addID(term.getId());
			}
			
			entityVector.add(entity);
			return true;
		}
		return false;
	}
	private boolean addEntityFromMultiToken(BioNERToken[] tokens, Vector<BioNERTerm> commonTermVector,BioNERSentence sentence,int begin, int end, Vector<BioNEREntity> entityVector)
	{
		
		/*boolean hasNN = false;
		for(int i=begin; i<=end; i++)
		{
			if(posTags[i].startsWith("NN"))
			{
				hasNN = true;
				break;
			}
		}
		if(!hasNN) return false;*/
		
		
		BioNERToken tokenBegin = tokens[begin];
		BioNERToken tokenEnd = tokens[end];
		BioNEREntity entity = new BioNEREntity();
		entity.set_Sentence(sentence);
		entity.set_position(tokenBegin.getBegin(), tokenEnd.getEnd());
		
		Vector<BioNERTerm> matchedTermVector = getExactMatchedTerm(entity, commonTermVector);
		//Vector<BioNERTerm> matchedTermVector = commonTermVector;
		if(!matchedTermVector.isEmpty())
		{
			for(BioNERTerm term : matchedTermVector)
			{
				entity.addID(term.getId());
			}
			
			entityVector.add(entity);
			return true;
		}
		return false;
	}
	
	private StringNormalizer strNormalizer = StringNormalizerFactory.getStringNormalizer();
	private Vector<BioNERTerm> getExactMatchedTerm(BioNEREntity entity, Vector<BioNERTerm> commonTermVector)
	{
		//return commonTermVector;
		Vector<BioNERTerm> matchedTermVector = new Vector<BioNERTerm>();
		String entityWord = entity.getText().toLowerCase();
		entityWord = strNormalizer.normalizeString(entityWord);
		for(BioNERTerm term : commonTermVector)
		{
			Vector<String> describ = term.getDescribe();
			if(describ.contains(entityWord))
			{
				matchedTermVector.add(term);
			}
		}
		return matchedTermVector;
	}
}
