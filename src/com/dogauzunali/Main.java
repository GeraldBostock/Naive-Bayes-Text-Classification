package com.dogauzunali;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Main {

	public static void main(String[] args)
	{
		Preprocessor preprocessor;
		try {
			preprocessor = new Preprocessor();
			
			HashMap<String, ArrayList<String>> files = preprocessor.preprocessAllFiles("C:/Users/doga/workspace-luna/Zemberek/res/raw_texts");
			
			Set<String> keys = files.keySet();
			System.out.println("Original key set:");
			System.out.println(Arrays.deepToString(keys.toArray()));
			
			for(String key : keys)
			{
				System.out.print("Data count for key " + key + ": ");
				System.out.println(files.get(key).size());
			}
			
			PrintStream out;
			try {
				out = new PrintStream(new FileOutputStream("ngram.txt"));
				System.setOut(out);
				System.setErr(out);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			NGram nGram = new NGram();
			HashMap<String, Integer> nGram2 = nGram.getAllNGrams(files, 2);
			ArrayList<TextFile> tokenizedFiles = nGram.getFiles();
			Set<String> nGramKeys = nGram2.keySet();
			
			for(String gram : nGramKeys)
			{
				System.out.println(gram + ": " + nGram2.get(gram));
			}
			
			HashMap<String, Integer> nGram3 = nGram.getAllNGrams(files, 3);
			Set<String> nGramKeys3 = nGram3.keySet();
			for(String gram : nGramKeys3)
			{
				System.out.println(gram + ": " + nGram3.get(gram));
			}
			
			Partitioner partitioner = new Partitioner();
			partitioner.partition(tokenizedFiles, 75);
			ArrayList<TextFile> exampleSet = partitioner.getTeachingSet();
			ArrayList<TextFile> guessSet = partitioner.getGuessSet();
			
			System.out.println("# of teaching files: " + exampleSet.size());
			System.out.println("# of files to guess: " + guessSet.size());
			
			NaiveBayes bayes = new NaiveBayes();
			bayes.teach(tokenizedFiles, nGram2);
			bayes.guess(guessSet);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
