package com.dogauzunali;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.normalization.TurkishSpellChecker;

public class Preprocessor {

	private TurkishMorphology morphology;
	private TurkishSpellChecker spellChecker;
	
	public Preprocessor() throws IOException
	{
		morphology = TurkishMorphology.createWithDefaults();
		spellChecker = new TurkishSpellChecker(morphology);
	}
	
	/*
	 * Find all of the directory names in folder
	 * Get all of the file names those directories contain
	 * Spell check those files
	 */
	public HashMap<String, ArrayList<String>> preprocessAllFiles(String folderPath)
	{
		HashMap<String, ArrayList<String>>  preprocessedFiles = new HashMap<String, ArrayList<String>>();
		
		File rootFolder = new File(folderPath);
		String[] directories = rootFolder.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		
		for(String directory : directories)
		{
			ArrayList<String> fileNames = getAllFileNames(folderPath + "/" + directory);
			ArrayList<String> spellCheckedFiles = new ArrayList<String>();
			for(String fileName : fileNames)
			{
				try {
					spellCheckedFiles.add(getSpellCheckedFile(fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			preprocessedFiles.put(directory, spellCheckedFiles);
		}
		
		return preprocessedFiles;
	}
	
	/*
	 * Read the text file word by word
	 * Get rid of the punctuation marks at the start and end of the words. They dont matter.
	 * Check if the word is spelled correctly. If not, replace that word with correctly spelt one
	 * and add that to the list of words
	 * And lastly form a single string from processed words so it can be nGrammed more easily
	 * Oh and return that string 
	 */
	private String getSpellCheckedFile (String filePath) throws IOException
	{
		
		File file = new File(filePath);
		Scanner input = new Scanner(file);
		ArrayList<String> words = new ArrayList<String>();

		int count = 0;
	    while (input.hasNext()) {
	      String word  = input.next();
	      
	      word = word.replaceAll("^[,.\"\']*+", "");
	      word = word.replaceAll("[,.\"\']*+$", "");
	      
	      if(!spellChecker.check(word))
	      {
	    	  List<String> suggestions = spellChecker.suggestForWord(word);
	    	  if(suggestions.size() > 0)
	    	  {
	    		  word = suggestions.get(0);
	    	  }
	      }
	      
	      words.add(word);
	      count = count + 1;
	    }
	    
	    input.close();
	    
	    String spellCheckedFileString = "";
	    
	    for(String word : words)
	    {
	    	spellCheckedFileString += word + " ";
	    }
		
		return spellCheckedFileString.replaceAll("[^a-zA-ZþÞÝýüÜöÖçÇðÐ ]", "").trim().toLowerCase();
	}
	
	private ArrayList<String> getAllFileNames(String directoryPath)
	{
		
		ArrayList<String> filesToPreprocess = new ArrayList<String>();
		
		File directory = new File(directoryPath);
		File[] files = directory.listFiles();
		
		for(File file : files)
		{
			if(file.isFile() && file.getName().endsWith(".txt")) 
			{
				filesToPreprocess.add(directoryPath + "/" + file.getName());
			}
		}
		
		return filesToPreprocess;
	}
}
