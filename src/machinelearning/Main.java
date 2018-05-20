package machinelearning;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import zemberek.core.logging.Log;

public class Main {

	public static void main(String[] args)
	{
		Preprocessor preprocessor;
		try {
			PrintStream out;
			try {
				out = new PrintStream(new FileOutputStream("log.txt"));
				System.setOut(out);
				//System.setErr(out);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			
			String dataSetPath = "C:/Users/doga/workspace-luna/Zemberek/res/raw_texts";;
			preprocessor = new Preprocessor();
			
			Log.info("Starting preprocessing.");
			HashMap<String, ArrayList<String>> files = preprocessor.preprocessAllFiles(dataSetPath);
			Log.info("Preprocessing complete.");
			
			Set<String> keys = files.keySet();
			Log.info(keys.size() + " categories total:");
			Log.info(Arrays.deepToString(keys.toArray()));
			
			for(String key : keys) Log.info("Data count for category " + key + ": " + files.get(key).size());
			
			Log.info("Starting tokenization.");
			NGram nGram = new NGram();
			HashMap<String, Integer> nGrams = nGram.getAllNGrams(files, 2);
			ArrayList<TextFile> tokenizedFiles = nGram.getFiles();
			Log.info("Tokenization complete.");
			Set<String> nGramKeys = nGrams.keySet();
			
			for(String gram : nGramKeys)
			{
				System.out.println(gram + ": " + nGrams.get(gram));
			}
			
			int percentage = 75;
			Log.info("Starting partitioning. " + percentage + "% of the files will be in the learning set.");
			Partitioner partitioner = new Partitioner();
			partitioner.partition(tokenizedFiles, percentage);
			ArrayList<TextFile> exampleSet = partitioner.getTeachingSet();
			ArrayList<TextFile> guessSet = partitioner.getGuessSet();
			Log.info("Partitioning complete.");
			
			Log.info("# of total files: " + (int)(exampleSet.size() + guessSet.size()));
			Log.info("# of teaching files: " + exampleSet.size());
			Log.info("# of files to guess: " + guessSet.size());
			System.out.println("# of total files: " + (int)(exampleSet.size() + guessSet.size()));
			System.out.println("# of files in the teaching set: " + exampleSet.size());
			System.out.println("# of files in the guess set: " + guessSet.size());
			
			Log.info("Starting teaching.");
			NaiveBayes bayes = new NaiveBayes();
			bayes.teach(tokenizedFiles, nGrams);
			Log.info("Teaching done.");
			Log.info("Starting guessing.");
			bayes.guess(guessSet);
			Log.info("Guessing done.");
			bayes.printPerformanceMeasures();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
