package com.github.pekoto.fastfuzzystringmatcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * An English --> Japanese dictionary with fuzzy lookup.
 * Built using the StringMatcher class.
 * 
 * @author Graham McRobbie
 *
 */
public class EnglishJapaneseDictionarySearcher {

	private StringMatcher<String> stringMatcher;
	private String fileName = "JMDict_po.txt";
	private long size;
	
	public EnglishJapaneseDictionarySearcher() throws IOException {
		 stringMatcher = new StringMatcher<String>();
		 loadDataFromPoFile();
	}
	
	public long getSize() {
		return size;
	}
	
	private void loadDataFromPoFile() throws IOException {
		String path = getClass().getClassLoader().getResource(fileName).getPath();
		BufferedReader br = new BufferedReader(new FileReader(path));
		
		String englishTerm = "";
		String line;
		
		while ((line = br.readLine()) != null) {
	        if(line == null || line.isEmpty()) {
	        		continue;
	        }
	        
	        if(line.startsWith("msgid")) {
	        		englishTerm = getParsedTerm(line);
	        } else if (line.startsWith("msgstr")) {
	        		stringMatcher.add(englishTerm, getParsedTerm(line));
	        		size++;
	        }
	     }
	    
	    br.close();
	}
	
	private String getParsedTerm(String line) {
		int beginIndex = line.indexOf('"') + 1;
		int endIndex = line.lastIndexOf('"');
		
		return line.substring(beginIndex, endIndex);
	}
	
	public SearchResultList<String> search(CharSequence word, float matchPercentage) {
		return stringMatcher.search(word, matchPercentage);
	}
}