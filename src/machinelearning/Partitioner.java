package machinelearning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Partitioner {
	
	private ArrayList<TextFile> teachingDataSet;
	private ArrayList<TextFile> guessDataSet;

	public void partition(ArrayList<TextFile> files, int percentage)
	{
		HashMap<String, ArrayList<TextFile>> data = new HashMap<String, ArrayList<TextFile>>();
		teachingDataSet = new ArrayList<TextFile>();
		guessDataSet = new ArrayList<TextFile>();
		
		if(percentage > 75)
		{
			System.out.println("Percentage cannot be bigger than 75. Setting percentage to 75.");
			percentage = 75;
		}
		
		for(TextFile file : files)
		{
			String category = file.getCategory();
			if(!data.containsKey(category))
			{
				ArrayList<TextFile> categoryFileList = new ArrayList<TextFile>();
				categoryFileList.add(file);
				data.put(category, categoryFileList);
			}
			else
			{
				ArrayList<TextFile> categoryFileList = data.get(category);
				categoryFileList.add(file);
				data.put(category, categoryFileList);
			}
		}
		
		for(String category : data.keySet())
		{
			ArrayList<TextFile> textFiles = data.get(category);
			Collections.shuffle(textFiles);
			int fileCount = textFiles.size();
			int teachingDataCount = percentage * fileCount / 100;
			
			int i = 0;
			for(TextFile textFile : textFiles)
			{
				if(i < teachingDataCount) teachingDataSet.add(textFile);
				else if(i >= teachingDataCount) guessDataSet.add(textFile);
				i++;
			}
		}
		
		/*if(percentage > 75)
		{
			System.out.println("Percentage cannot be bigger than 75. Setting percentage to 75.");
			percentage = 75;
		}
		
		exampleData = new HashMap<String, ArrayList<String>>();
		dataToGuess = new HashMap<String, ArrayList<String>>();
		
		Set<String> keySet = dataSet.keySet();
		for(String key : keySet)
		{
			ArrayList<String> unpartitionedSet = dataSet.get(key);
			int dataCount = unpartitionedSet.size();
			int toExampleSetCount = percentage * dataCount / 100;
			
			Collections.shuffle(unpartitionedSet);
			
			ArrayList<String> toExampleSet = new ArrayList<String>();
			ArrayList<String> toGuessSet = new ArrayList<String>();
			
			int i = 0;
			
			for(String data : unpartitionedSet)
			{
				if(i < toExampleSetCount) toExampleSet.add(data);
				else if(i >= toExampleSetCount) toGuessSet.add(data);
				i++;
			}
			
			exampleData.put(key, toExampleSet);
			dataToGuess.put(key, toGuessSet);
		}*/
	}
	
	public ArrayList<TextFile> getTeachingSet()
	{
		return teachingDataSet;
	}
	
	public ArrayList<TextFile> getGuessSet()
	{
		return guessDataSet;
	}
	
}
