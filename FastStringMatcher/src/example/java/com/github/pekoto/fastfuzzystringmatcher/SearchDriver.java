package com.github.pekoto.fastfuzzystringmatcher;

import java.io.IOException;

/**
 * Shows how the StringMatcher class can be used to build a translation memory dictionary.
 * The dictionary entries are taken from the JMDict project and are not optimised for
 * English lookup, but this class should show some applications.
 * <p>
 * See <a href="http://www.edrdg.org/jmdict/j_jmdict.html">The JMDict Project</a>.
 * <p>
 * See the unit test classes in src/test/java for further examples of usage.
 * 
 * @author Graham McRobbie
 *
 */
public class SearchDriver {

	public static void main(String[] args) {
		
		try
		{
			EnglishJapaneseDictionarySearcher engJpnDict = new EnglishJapaneseDictionarySearcher();
			System.out.println(String.format("Finished loading %d terms", engJpnDict.getSize()));
			
			System.out.println("Search for \"Diplomat\", matching at 80%:");
			SearchResultList<String> results = engJpnDict.search("Diplomat", 80.0f);
			results.print();
			
			System.out.println("Search for \"Parent\", matching at 85%");
			results = engJpnDict.search("Parent", 85.0f);
			results.print();
			
			// Search for partial word
			System.out.println("Search for \"ock\", matching at 75%");
			results = engJpnDict.search("ock", 75.0f);
			results.print();
		}
		catch(IOException ioe)
		{
			System.out.println("Failed to load dictionary file.");
		}	
	}
}
