package genetukit.api;

import java.util.ArrayList;
/**
 * This class stores GN result for one gene ID. The information about gene ID, species ID, score and gene mentions can be read by the getters.
 * @author Jingchen Liu
 *
 */
public class GNResultItem {
	private String m_ID = null;
	private String m_speciesID = null;
	private double m_score = 0.0;
	private ArrayList<String> m_gmList = new ArrayList<String>();
	public void setID(String ID) {
		this.m_ID = ID;
	}
	public String getID() {
		return m_ID;
	}
	public void setSpeciesID(String speciesID) {
		this.m_speciesID = speciesID;
	}
	public String getSpeciesID() {
		return m_speciesID;
	}
	public void setScore(double score) {
		this.m_score = score;
	}
	public double getScore() {
		return m_score;
	}
	public void addGeneMention(String gennMentionStr) {
		if(!m_gmList.contains(gennMentionStr))
		{
			m_gmList.add(gennMentionStr);
		}
	}
	public ArrayList<String> getGeneMentionList() {
		return m_gmList;
	}
}
