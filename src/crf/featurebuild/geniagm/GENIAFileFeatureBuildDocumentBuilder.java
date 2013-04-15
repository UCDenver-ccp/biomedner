package crf.featurebuild.geniagm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERParagraph;
import bioner.data.document.BioNERSection;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import crf.featurebuild.FeatureBuildDocumentBuilder;

public class GENIAFileFeatureBuildDocumentBuilder implements
		FeatureBuildDocumentBuilder {
	DocumentBuilderFactory domfac;
	DocumentBuilder dombuilder;
	public GENIAFileFeatureBuildDocumentBuilder()
	{
		domfac = DocumentBuilderFactory.newInstance();
		try {
			dombuilder = domfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public BioNERDocument[] buildDocuments() {
		// TODO Auto-generated method stub
		String filename = "data/GENIA_corp/GENIAcorpus3.02.xml";
		try {
			InputStream is=new FileInputStream(filename);
			Document doc=dombuilder.parse(is);
			Element root=doc.getDocumentElement();
			Vector<BioNERDocument> docVector = null;
			for(Node node = root.getFirstChild(); node!=null; node = node.getNextSibling())
			{
				if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
				if(node.getNodeName()=="set")
				{
					docVector = parseTree(node);
				}
			}
			docVector = parseTree(root);
			if(docVector==null)
			{
				throw new NullPointerException();
			}
			
			int size = docVector.size();
			BioNERDocument[] docArray = new BioNERDocument[size];
			for(int i=0; i<size; i++)
			{
				docArray[i] = docVector.elementAt(i);
			}
			return docArray;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private Vector<BioNERDocument> parseTree(Node root)
	{
		Vector<BioNERDocument> docVector = new Vector<BioNERDocument>();
		int num=0;
		for(Node subTreeRoot = root.getFirstChild(); subTreeRoot!=null; subTreeRoot=subTreeRoot.getNextSibling())
		{
			if(subTreeRoot.getNodeType()!=Node.ELEMENT_NODE) continue;
			num++;
			System.out.print("Reading document #"+num+"....");
			if(subTreeRoot.getNodeName()=="article" )
			{
				BioNERDocument doc = parseOneDocument(subTreeRoot);
				docVector.add(doc);
			}
			System.out.println("Finished!");
		}
		
		return docVector;
	}
	private BioNERDocument parseOneDocument(Node root)
	{
		BioNERDocument doc = new BioNERDocument();
		
		for(Node node=root.getFirstChild(); node!=null; node=node.getNextSibling())
		{
			if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
			walkTroughtTree(node, doc);
		}
		return doc;
		
	}
	private void walkTroughtTree(Node root,BioNERDocument doc)
	{
		String nodeName = root.getNodeName();
		if(nodeName.equals("bibliomisc"))
		{
			String pmid = root.getFirstChild().getNodeValue();
			int pos = pmid.indexOf(":");
			pmid = pmid.substring(pos+1);
			doc.setID(pmid);
		}
		else if(nodeName.equals("sentence"))
		{
			BioNERSection abstractSection = doc.getAbstractSection();
			if(abstractSection == null)
			{
				abstractSection = new BioNERSection();
				abstractSection.addParagraph(new BioNERParagraph());
				doc.setAbstractSection(abstractSection);
			}
			BioNERParagraph paragraph = abstractSection.getParagraph().elementAt(0);
			BioNERSentence sentence = new BioNERSentence("",0);
			walkTroughtSentenceTree(root,sentence);
			paragraph.addSentence(sentence);
		}
		
		for(Node node=root.getFirstChild(); node!=null; node=node.getNextSibling())
		{
			if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
			walkTroughtTree(node, doc);
		}
		
	}
	private void walkTroughtSentenceTree(Node root,BioNERSentence sentence)
	{
		
		String label = getNodeEntityLable(root);
		
		//If this node is cons which should be a entity
		if(label.equals("PRO")||label.equals("ORG"))
		{
			//Get the full text from its all sub nodes
			String text = getNodeFullString(root);
			
			//Build a BioNEREntity and add it into current sentence.
			BioNEREntity entity = new BioNEREntity();
			entity.set_Sentence(sentence);
			if(label.equals("PRO"))entity.set_Type(GlobalConfig.PROTEIN_TYPE_LABEL);
			else if(label.equals("ORG"))entity.set_Type(GlobalConfig.ORGANISM_TYPE_LABEL);
			String sentenceText = sentence.getSentenceText();
			int beginPos = sentenceText.length();
			int endPos = beginPos + text.length()-1;
			sentenceText += text;
			sentence.setSentenceText(sentenceText);
			
			entity.set_position(beginPos, endPos);
			sentence.addEntity(entity);	
			//if(text!=null)System.out.println("Entity "+label+":"+text);
			return;
		}
		
		//If this node is not a entity, just add the text into the sentence.
		String value = root.getNodeValue();
		if(value==null) value = "";
		String sentenceText = sentence.getSentenceText();
		sentenceText += value;
		sentence.setSentenceText(sentenceText);
		//if(value!=null)System.out.println(value);
		
		for(Node node=root.getFirstChild(); node!=null; node=node.getNextSibling())
		{
			//if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
			walkTroughtSentenceTree(node, sentence);
		}
		
	}
	
	//To return a node's full text from its all sub nodes.
	private String getNodeFullString(Node root)
	{
		String value = root.getNodeValue();
		if(value==null) value = "";
		
		for(Node node=root.getFirstChild(); node!=null; node=node.getNextSibling())
		{
			//if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
			value += getNodeFullString(node);
		}
		return value;
	}
	
	//To judge if a node is a entity. If it is, return a string label showing its type. Or, return a "O" label.
	private String getNodeEntityLable(Node root)
	{
		String label = "O";
		if(root.getNodeType()!=Node.ELEMENT_NODE||!root.getNodeName().equals("cons")) return label;
		NamedNodeMap nodeMap = root.getAttributes();
		Node semNode = nodeMap.getNamedItem("sem");
		if(semNode!=null)
		{
			String semValue = semNode.getNodeValue();
			if(semValue.equals("G#protein_molecule")) label = "PRO";
			else if(semValue.equals("G#DNA_domain_or_region")) label = "PRO";
			//else if(semValue.equals("G#protein_family_or_group")) label = "PRO";
			//else if(semValue.equals("G#multi_cell")) label = "ORG";
			//else if(semValue.equals("G#mono_cell")) label = "ORG";
			//else if(semValue.equals("G#virus")) label = "ORG";
			//if(semValue.contains("protein") || semValue.contains("DNA") || semValue.contains("RNA")) label = "PRO";
		}
		return label;
	}
	

}
