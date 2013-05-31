package bioner.application.webtool;

import java.io.File;
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
import bioner.tools.nlptools.NLPToolsFactory;
import bioner.tools.nlptools.SentenceSpliter;

import crf.featurebuild.FeatureBuildDocumentBuilder;

public class BC3GNDataFileReader implements FeatureBuildDocumentBuilder {

	DocumentBuilderFactory domfac;
	DocumentBuilder dombuilder;
	private String m_fileDir = "../../BC3GN/xmls/";

	public BC3GNDataFileReader()
	{
		domfac = DocumentBuilderFactory.newInstance();
		domfac.setValidating(false);
		

		try {
			domfac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dombuilder = domfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
            throw new RuntimeException(e);
		}
	}

	public BC3GNDataFileReader(String fileDir)
	{
		domfac = DocumentBuilderFactory.newInstance();
		domfac.setValidating(false);
		
		m_fileDir = fileDir;
		try {
			domfac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dombuilder = domfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		}
	}
	
	@Override
	public BioNERDocument[] buildDocuments() {
		// TODO Auto-generated method stub
		File[] files = (new File(m_fileDir)).listFiles();
		BioNERDocument[] documents = new BioNERDocument[files.length];
		try {
			int num=0;
			for (File file : files) {
                if (file.isFile()) {
					String filename = file.getName();
					int pos = filename.indexOf('.');
					String docID = filename.substring(0, pos);
					System.out.print("Reading #"+num+" "+docID+"...");
					InputStream is = new FileInputStream(file.getAbsolutePath());
					Document doc=dombuilder.parse(is);
					BioNERDocument document = new BioNERDocument();
					Element root=doc.getDocumentElement();
					for(Node node = root.getFirstChild(); node!=null; node = node.getNextSibling())
					{
						if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
						parseDocumentTree(node, document);
					}
					
					
					document.setID(docID);
					document.linkComponent();
					documents[num] = document;
					num++;
					System.out.println("Finished!");
                }
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		}
		return documents;
	}
	
	public BioNERDocument getOneDocument(File file)
	{
		String filename = file.getName();
		int pos = filename.indexOf('.');
		String docID = filename.substring(0, pos);
	System.err.println("XXXXXX docID is:\"" + docID + "\"");	
		InputStream is;
		try {
			is = new FileInputStream(file.getAbsolutePath());
			Document doc=dombuilder.parse(is);
			BioNERDocument document = new BioNERDocument();
			Element root=doc.getDocumentElement();
			for(Node node = root.getFirstChild(); node!=null; node = node.getNextSibling())
			{
				if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
				parseDocumentTree(node, document);
			}
			
			
			document.setID(docID);
			document.linkComponent();
			return document;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		}
		
	}
	
	public BioNERDocument getOneDocument(InputStream inputStream)
	{
		InputStream is;
		try {
			is = inputStream;
			Document doc=dombuilder.parse(is);
			BioNERDocument document = new BioNERDocument();
			Element root=doc.getDocumentElement();
			for(Node node = root.getFirstChild(); node!=null; node = node.getNextSibling())
			{
				if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
				parseDocumentTree(node, document);
			}
			
			
			document.setID("0");
			document.linkComponent();
			return document;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RuntimeException(e);
		}
		
	}
	
	
	private void parseDocumentTree(Node root, BioNERDocument document)
	{
		for(Node node=root.getFirstChild(); node!=null; node=node.getNextSibling())
		{
			if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
			String nodeName = node.getNodeName();
			if(nodeName.equals("article-title"))
			{
				String nodeValue = getNodeFullString(node);
				if(nodeValue!=null && document.getTitle()==null)
				{
					BioNERSentence sentence = new BioNERSentence(nodeValue, 0);
					document.setTitle(sentence);
				}
			}
			else if(nodeName.equals("abstract"))
			{
				BioNERSection absSection = document.getAbstractSection();
				if(absSection==null)
				{
					absSection = new BioNERSection();
					absSection.setType("abstract");
					document.setAbstractSection(absSection);
				}
				parseSectionTree(node, absSection);
			}
			else if(nodeName.equals("sec"))
			{
				NamedNodeMap attributeMap = node.getAttributes();
				Node typeNode = attributeMap.getNamedItem("sec-type");
				if(typeNode==null)
				{
					BioNERSection section = new BioNERSection();
					parseSectionTree(node, section);
					section.setType("text");
					document.addSection(section);
				}
				else
				{
					String sec_typeStr = typeNode.getNodeValue().toLowerCase();
					//if(!sec_typeStr.contains("reference")
					//		&& !sec_typeStr.contains("method")
					//		&& !sec_typeStr.contains("material"))
					{
						BioNERSection section = new BioNERSection();
						parseSectionTree(node, section);
						section.setType(sec_typeStr);
						document.addSection(section);
					}
				}
			}
			else
			{
				parseDocumentTree(node, document);
			}
		}
	}
	private void parseSectionTree(Node root, BioNERSection section)
	{
		for(Node node=root.getFirstChild(); node!=null; node=node.getNextSibling())
		{
			if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
			String nodeName = node.getNodeName();
			if(nodeName.equals("title"))
			{
				if(node.getFirstChild()!=null && section.getTitleSentence()==null)
				{
					String nodeValue = getNodeFullString(node);
					if(nodeValue!=null)
					{
						BioNERSentence sentence = new BioNERSentence(nodeValue, 0);
						section.setTitleSentence(sentence);
					}
				}
			}
			else if(nodeName.equals("p"))
			{
				BioNERParagraph paragraph = new BioNERParagraph();
				parseParagraphTree(node, paragraph);
				section.addParagraph(paragraph);
			}
			else if(nodeName.equals("sec"))
			{
				BioNERSection subSection = new BioNERSection();
				parseSectionTree(node, subSection);
				section.addSubSection(subSection);
			}
			else
			{
				parseSectionTree(node, section);
			}
		}
	}
	
	private SentenceSpliter sentenceSpliter = NLPToolsFactory.getSentenceSpliter();
	private void parseParagraphTree(Node root, BioNERParagraph paragraph)
	{
		

		Vector<String> italicVector = new Vector<String>();
		for(Node node = root.getFirstChild(); node!=null; node = node.getNextSibling())
		{
			if(node.getFirstChild()!=null)
			{
				String nodeValue = getNodeFullString(node);
				if(node.getNodeName().equals("italic") && nodeValue!=null)
				{
					String[] nodeValueSentences = sentenceSpliter.sentenceSplit(nodeValue);
					nodeValue = "";
					for(int i=0; i<nodeValueSentences.length; i++)
					{
						nodeValue += nodeValueSentences[i]+" ";
					}
					nodeValue = nodeValue.trim();
					
					if(!italicVector.contains(nodeValue) && nodeValue.length()>1)
					{
						italicVector.add(nodeValue);
					}
				}
			}
		}
		String paraText = getNodeFullString(root);
		String[] sentenceTexts = sentenceSpliter.sentenceSplit(paraText);
		paragraph.setText(paraText);
		paragraph.setSentence(sentenceTexts);
		
		//Add the italic text to the sentence as the gene mentions.
		Vector<BioNERSentence> sentences = paragraph.getSentence();
		for(BioNERSentence sentence : sentences)
		{
			String sentenceText = sentence.getSentenceText();
			for(String italicText : italicVector)
			{
				int pos = sentenceText.indexOf(italicText);
				while(pos>=0)
				{
					int end = pos + italicText.length()-1;
					BioNEREntity entity = new BioNEREntity();
					entity.set_Sentence(sentence);
					entity.set_position(pos, end);
					entity.addLabel(GlobalConfig.ENTITY_LABEL_ITATIC);
					sentence.addEntity(entity);
					pos = sentenceText.indexOf(italicText, end+1);
				}
			}
		}
	}
	
	//To return a node's full text from its all sub nodes.
	private String getNodeFullString(Node root)
	{
		String value = root.getNodeValue();
		if(value==null) value = "";
		
		value = value.replaceAll("α", "alpha");
		value = value.replaceAll("β", "beta");
		value = value.replaceAll("γ|Γ", "gamma");
		value = value.replaceAll("δ|Δ", "delta");
		value = value.replaceAll("ε", "epsilon");
		value = value.replaceAll("ζ", "zeta");
		value = value.replaceAll("η", "eta");
		value = value.replaceAll("Θ|θ", "theta");
		value = value.replaceAll("κ", "kappa");
		value = value.replaceAll("Λ|λ", "lammda");
		value = value.replaceAll("μ", "mu");
		
		
		for(Node node=root.getFirstChild(); node!=null; node=node.getNextSibling())
		{
			//if(node.getNodeType()!=Node.ELEMENT_NODE) continue;
			value += getNodeFullString(node);
		}
		
		
		char[] charArray = value.toCharArray();
		for(int i=0; i<charArray.length; i++)
		{
			char c = value.charAt(i);
			if(c>127 || c<32)
			{
				if(c > 65280 && c < 65375)
					charArray[i] = (char)(c - 65248);
				else
					charArray[i]=' ';
			}
			
		}
		return String.valueOf(charArray);
	}
}
