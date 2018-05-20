package com.dogauzunali;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NaiveBayes {
	
	private HashMap<String, Float> categoryProbs;
	private HashMap<String, HashMap<String, Float>> gramProbsByCategory;
	private Set<String> categories;

	public void teach(ArrayList<TextFile> files, HashMap<String, Integer> vocabulary)
	{
		HashMap<String, ArrayList<TextFile>> categorizedFiles = new HashMap<String, ArrayList<TextFile>>();
		categoryProbs = new HashMap<String, Float>();
		gramProbsByCategory = new HashMap<String, HashMap<String, Float>>();
		
		for(TextFile file : files)
		{
			String category = file.getCategory();
			if(!categorizedFiles.containsKey(category))
			{
				ArrayList<TextFile> filesInCategory = new ArrayList<TextFile>();
				filesInCategory.add(file);
				categorizedFiles.put(category, filesInCategory);
			}
			else
			{
				ArrayList<TextFile> filesInCategory = categorizedFiles.get(category);
				filesInCategory.add(file);
				categorizedFiles.put(category, filesInCategory);
			}
		}
		
		categories = categorizedFiles.keySet();
		for(String category : categories)
		{
			ArrayList<TextFile> filesInCategory = categorizedFiles.get(category);
			float categoryProb = (float)filesInCategory.size() / (float)files.size();
			categoryProbs.put(category, categoryProb);
		}
		
		HashMap<String, HashMap<String, Integer>> frequencies = categorizeFrequencies(categorizedFiles, vocabulary);
		
		Set<String> uniqueGrams = vocabulary.keySet();
		for(String category : categories)
		{
			int categoryGramCount = 0;
			
			HashMap<String, Float> gramProbs = new HashMap<String, Float>();
			
			Set<String> gramsInCategory = frequencies.get(category).keySet();
			for(String gram : gramsInCategory) categoryGramCount += frequencies.get(category).get(gram);
			
			for(String gram : uniqueGrams)
			{
				int gramCountInCategory;
				if(frequencies.get(category).get(gram) == null)
				{
					gramCountInCategory = 0;
				}
				else gramCountInCategory = frequencies.get(category).get(gram);
				float prob = (float)(gramCountInCategory + 1) / (float)(categoryGramCount + vocabulary.size());
				gramProbs.put(gram, prob);
			}
			gramProbsByCategory.put(category, gramProbs);
		}
	}
	
	public void guess(ArrayList<TextFile> files)
	{
		for(TextFile file : files)
		{
			HashMap<String, Float> classProbs = new HashMap<String, Float>();
			HashMap<String, Integer> gramCountsInFile = file.getNGrams();
			
			for(String category : categories)
			{
				float classProb = (float) Math.log(categoryProbs.get(category));
				for(String gram : gramCountsInFile.keySet())
				{
					float gramProbByCategory;
					if(gramProbsByCategory.get(category).get(gram) == null) gramProbByCategory = 0.0000000001f;
					else gramProbByCategory = gramProbsByCategory.get(category).get(gram);
						
					classProb += Math.log(gramProbByCategory);
				}
				classProbs.put(category, classProb);
			}
			
			System.out.println("\nFile category is " + file.getCategory());
			System.out.print("Given category is ");
			
			float minProb = -999999999.0f;
			String winnerCategory = "";
			
			for(String category : categories)
			{
				if(classProbs.get(category) > minProb)
				{
					minProb = classProbs.get(category);
					winnerCategory = category;
				}
			}
			
			System.out.println(winnerCategory);
		}
	}
	
	private HashMap<String, HashMap<String, Integer>> categorizeFrequencies(HashMap<String, ArrayList<TextFile>> files, HashMap<String, Integer> vocabulary)
	{
		HashMap<String, HashMap<String, Integer>> gramFrequencies = new HashMap<String, HashMap<String, Integer>>();
		
		Set<String> categories = files.keySet();
		for(String category : categories)
		{
			ArrayList<TextFile> filesInCategory = files.get(category);
			for(TextFile file : filesInCategory)
			{
				HashMap<String, Integer> gramsInFile = file.getNGrams();
				Set<String> grams = gramsInFile.keySet();
				for(String gram : grams)
				{
					if(vocabulary.get(gram) >= 50)
					{
						HashMap<String, Integer> categoryGrams;
						if(gramFrequencies.get(category) == null)
						{
							categoryGrams = new HashMap<String, Integer>();
						}
						else categoryGrams = gramFrequencies.get(category);
						
						int count;
						if(categoryGrams.containsKey(gram))
						{
							count = categoryGrams.get(gram);
							count += gramsInFile.get(gram);
							categoryGrams.put(gram, count);
						}
						else
						{
							count = gramsInFile.get(gram);
						}
						categoryGrams.put(gram, count);
						gramFrequencies.put(category, categoryGrams);
					}
				}
				
			}
		}
		
		return gramFrequencies;
	}
}
