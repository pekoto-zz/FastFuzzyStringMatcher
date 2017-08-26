package com.github.pekoto.faststringmatcher;

import org.junit.Test;

public class EditDistanceCalculatorTest {

	@Test
	public void test() {
		EditDistanceCalculator distanceCalculator = new EditDistanceCalculator();
		String s1 = "Hat";
		String s2 = "Cat";
		
		distanceCalculator.calculateEditDistance(s1, s2);
	}
}
