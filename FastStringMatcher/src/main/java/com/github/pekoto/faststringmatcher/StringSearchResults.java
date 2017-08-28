package com.github.pekoto.faststringmatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple wrapper for returning results from the string matcher.
 * 
 * @author Graham McRobbie
 *
 * @param <T>
 *            The data associated with each result.
 */
public class StringSearchResults<T> extends ArrayList<StringSearchResult<T>> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 5315812187969680611L;

	public boolean containsKeyword(String keyword) {
		return this.stream().filter(r -> r.getKeyword().equals(keyword)).count() > 0;
	}

	public void sortByClosestMatch() {
        Collections.sort(this, new SortByClosestMatchComparator());
	}
	
	// check this is the best place for comparator -- might be better in results?
	private class SortByClosestMatchComparator implements Comparator<StringSearchResult<T>> {
		public int compare(StringSearchResult<T> s1, StringSearchResult<T> s2) {
			if (s1.getMatchPercentage() < s2.getMatchPercentage()) {
				return 1;
			} else if (s1.getMatchPercentage() > s2.getMatchPercentage()) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
