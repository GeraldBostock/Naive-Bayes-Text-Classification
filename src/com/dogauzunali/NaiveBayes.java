package com.dogauzunali;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import zemberek.core.logging.Log;

public class NaiveBayes {
	
	private HashMap<String, Float> categoryProbs;
	private HashMap<String, HashMap<String, Float>> gramProbsByCategory;
	private Set<String> categories;
	
	private static final int TRUE_POSITIVE = 0;
	private static final int FALSE_NEGATIVE = 1;
	private static final int FALSE_POSITIVE = 2;
	private static final int TRUE_NEGATIVE = 3;
	/*
	 * Index 0 True Positive
	 * Index 1 False Negative
	 * Index 2 False Positive
	 * Index 3 True Negative
	 */
	private HashMap<String, int[]> confusionMatrices;
	/*
	 * Index 0 Precision
	 * Index 1 Recall
	 * Index 2 F-Measure
	 */
	private HashMap<String, float[]> performanceMeasures;

	public void teach(ArrayList<TextFile> files, HashMap<String, Integer> vocabulary)
	{
		HashMap<String, ArrayList<TextFile>> categorizedFiles = new HashMap<String, ArrayList<TextFile>>();
		categoryProbs = new HashMap<String, Float>();
		performanceMeasures = new HashMap<String, float[]>();
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
		confusionMatrices = new HashMap<String, int[]>();
		for(String category : categories)
		{
			int[] confusionMatrix = {0, 0, 0, 0};
			confusionMatrices.put(category, confusionMatrix);
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
			
			String fileCategory = file.getCategory();
			System.out.println("\nFile category is " + fileCategory);
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
			
			if(winnerCategory == fileCategory)
			{
				confusionMatrices.get(winnerCategory)[TRUE_POSITIVE]++;
			}
			else
			{
				confusionMatrices.get(fileCategory)[FALSE_NEGATIVE]++;
				confusionMatrices.get(winnerCategory)[FALSE_POSITIVE]++;
			}
		}
		
		calculatePerformance();
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
	
	private void calculatePerformance()
	{
		float[] total = {0 ,0 ,0};
		
		for(String category : categories)
		{
			float[] performanceMeasuresArray = {0, 0, 0};
			int[] confusionMatrix = confusionMatrices.get(category);
			
			//Precision
			float presicion = (float)confusionMatrix[TRUE_POSITIVE] / (float)(confusionMatrix[TRUE_POSITIVE] + confusionMatrix[FALSE_POSITIVE]);
			performanceMeasuresArray[0] = presicion;
			//Recall
			float recall = (float)confusionMatrix[TRUE_POSITIVE] / (float)(confusionMatrix[TRUE_POSITIVE] + confusionMatrix[FALSE_NEGATIVE]);
			performanceMeasuresArray[1] = recall;
			//F-Measure
			float beta = 0.5f;
			float fmeasure = 1 / (beta * (1 / presicion) + ((1 - beta) * (1 / recall)));
			performanceMeasuresArray[2] = fmeasure;
			
			total[0] += presicion;
			total[1] += recall;
			total[2] += fmeasure;
			
			performanceMeasures.put(category, performanceMeasuresArray);
		}
		
		total[0] /= (float)categories.size();
		total[1] /= (float)categories.size();
		total[2] /= (float)categories.size();
		performanceMeasures.put("average", total);
	}
	
	public void printPerformanceMeasures()
	{
		
		for(String category : categories)
		{
			System.out.println(category + ": { Precision: " + performanceMeasures.get(category)[0] * 100 + "%, Recall: " + performanceMeasures.get(category)[1] * 100 + "%, F-Measure: " + performanceMeasures.get(category)[2] * 100 + "% }");
			Log.info(category + ": { Precision: " + performanceMeasures.get(category)[0] * 100 + "%, Recall: " + performanceMeasures.get(category)[1] * 100 + "%, F-Measure: " + performanceMeasures.get(category)[2] * 100 + "% }");
		}
		
		float[] averages = performanceMeasures.get("average");
		System.out.println("Averages: { Precision: " + averages[0] * 100 + "%, Recall: " + averages[1] * 100 + "%, F-Measure: " + averages[2] * 100 + "% }");
		Log.info("Averages: { Precision: " + averages[0] * 100 + "%, Recall: " + averages[1] * 100 + "%, F-Measure: " + averages[2] * 100 + "% }");
	}
}
