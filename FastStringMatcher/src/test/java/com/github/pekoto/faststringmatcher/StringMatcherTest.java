package com.github.pekoto.faststringmatcher;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class StringMatcherTest {

	private static StringMatcher<String> stringMatcher = new StringMatcher<String>();
	
	@BeforeClass
	public static void setupStringMatcher() {
		stringMatcher.Add("0123456789", "10 digit long string");
		stringMatcher.Add("012345678", "9 digit long string");
		stringMatcher.Add("01234567", "8 digit long string");
		stringMatcher.Add("0123456", "7 digit long string");
		stringMatcher.Add("012345", "6 digit long string");
		stringMatcher.Add("01234", "5 digit long string");
		stringMatcher.Add("0123", "4 digit long string");
		stringMatcher.Add("012", "3 digit long string");
		stringMatcher.Add("01", "2 digit long string");
		stringMatcher.Add("0", "1 digit long string");
		
		stringMatcher.Add("Test", "String with uppercase char");
		stringMatcher.Add("test", "String with all lowercase chars");
		stringMatcher.Add("This is a test", "Multiple word string");
		stringMatcher.Add("Cat", "Short string");
		stringMatcher.Add("Hat", "Similar short string");
	}
	
	@Test
	public void testOneHundredPercentMatch() {
		List<StringSearchResult<String>> results = stringMatcher.search("01234", 100.0f);
		
		assertEquals(1, results.size());
		assertEquals("01234", results.get(0).getKeyword());
	}
	
	// TODO 75% match
	
	// TODO 50% match
	
	// TODO 25% match
	
	// TODO edit distance match
	
	// TODO duplicate strings
	
	// TODO empty strings
	
	// TODO null strings
	
	// TODO case sensitive
	
	// TODO multiple words
	
	@Test
	public void testAssociatedData() {
		List<StringSearchResult<String>> results = stringMatcher.search("01234", 100.0f);
		
		assertEquals(1, results.size());
		assertEquals("5 digit long string", results.get(0).getAssociatedData());
	}
}