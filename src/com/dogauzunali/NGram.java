package com.dogauzunali;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NGram {
	
	private int n;
	private HashMap<String, Integer> nGrams;
	private ArrayList<TextFile> files;
	
	public HashMap<String, Integer> getAllNGrams(HashMap<String, ArrayList<String>> dataSet, int n)
	{
		this.n = n;
		nGrams = new HashMap<String, Integer>();
		files = new ArrayList<TextFile>();
		
		Set<String> keySet = dataSet.keySet();
		
			
		for(String key : keySet)
		{
			ArrayList<String> texts = dataSet.get(key);
				
			for(String text : texts)
			{
				TextFile file = new TextFile();
				file.setText(text);
				file.setCategory(key);
				calculateNGrams(text, file);
				files.add(file);
			}
		}
			
		
		return nGrams;
	}
	
	private void calculateNGrams(String text, TextFile file)
	{
		HashMap<String, Integer> fileNGrams = new HashMap<String, Integer>();
		for(int i = 0; i < text.length() - n; i++)
		{
			String gram = "";
			for(int j = i; j < n + i; j++)
			{
				gram += text.charAt(j);
			}
			addToNGrams(gram, nGrams);
			addToNGrams(gram, fileNGrams);
		}
		file.setNGrams(fileNGrams);
	}
	
	private void addToNGrams(String gram, HashMap<String, Integer> list)
	{
		if(list.containsKey(gram))
		{
			int count = list.get(gram);
			list.put(gram, count + 1);
		}
		else
		{
			list.put(gram, 1);
		}
	}

	public ArrayList<TextFile> getFiles()
	{
		return this.files;
	}
	
}
