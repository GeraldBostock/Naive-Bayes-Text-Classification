package machinelearning;

import java.util.HashMap;

public class TextFile {

	private String text;
	private String category;
	private HashMap<String, Integer> nGrams;
	
	public TextFile()
	{
		
	}
	
	public TextFile(String text, String category, HashMap<String, Integer> nGrams)
	{
		this.text = text;
		this.category = category;
		this.nGrams = nGrams;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getCategory()
	{
		return this.category;
	}
	
	public void setCategory(String category)
	{
		this.category = category;
	}
	
	public HashMap<String, Integer> getNGrams()
	{
		return this.nGrams;
	}
	
	public void setNGrams(HashMap<String, Integer> nGrams)
	{
		this.nGrams = nGrams;
	}
}
