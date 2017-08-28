package com.github.pekoto.fastfuzzystringmatcher;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.pekoto.fastfuzzystringmatcher.EditDistanceCalculator;

public class EditDistanceCalculatorTest {

	private 	EditDistanceCalculator distanceCalculator = new EditDistanceCalculator();
	
	@Test
	public void testShortDistance() {
		String s1 = "Hat";
		String s2 = "Cat";
		
		int distance = distanceCalculator.calculateEditDistance(s1, s2);
		
		assertEquals(1, distance);
	}
	
	@Test
	public void testLongDistance() {
		String s1 = "This is a long string";
		String s2 = "Th1s is a l0ng str1ng";
		
		int distance = distanceCalculator.calculateEditDistance(s1, s2);
		
		assertEquals(3, distance);
	}
	
	@Test
	public void testStringOneLonger() {
		String s1 = "This string is longer";
		String s2 = "This is shorter";
		
		int distance = distanceCalculator.calculateEditDistance(s1, s2);
		
		assertEquals(10, distance);
	}
	
	@Test
	public void testStringTwoLonger() {
		String s1 = "This is shorter";
		String s2 = "This string is longer";
		
		int distance = distanceCalculator.calculateEditDistance(s1, s2);
		
		assertEquals(10, distance);
	}
	
	@Test
	public void testCaseInsensitive() {
		String s1 = "Test";
		String s2 = "test";
		
		int distance = distanceCalculator.calculateEditDistance(s1, s2);
		
		assertEquals(0, distance);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testStringOneNull() {
		String s1 = null;
		String s2 = "Test";
		
		// Throws IllegalArgumentException
		distanceCalculator.calculateEditDistance(s1, s2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testStringTwoNull() {
		String s1 = "Test";
		String s2 = null;
		
		// Throws IllegalArgumentException
		distanceCalculator.calculateEditDistance(s1, s2);
	}
	
	@Test
	public void testStringTwoEmpty() {
		String s1 = "Test";
		String s2 = "";
		
		int distance = distanceCalculator.calculateEditDistance(s1, s2);
		
		assertEquals(4, distance);
	}
	
	@Test
	public void testStringOneEmpty() {
		String s1 = "";
		String s2 = "Test";
		
		int distance = distanceCalculator.calculateEditDistance(s1, s2);
		
		assertEquals(4, distance);
	}
	
	@Test
	public void testStringsSame() {
		String s1 = "test";
		String s2 = "test";
		
		int distance = distanceCalculator.calculateEditDistance(s1, s2);
		
		assertEquals(0, distance);
	}
}
