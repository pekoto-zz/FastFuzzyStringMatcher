package com.github.pekoto.fastfuzzystringmatcher;

/**
 * A result returned after searching the BK tree.
 * 
 * @author Graham McRobbie
 *
 * @param <T> The type of data associated with each string keyword.
 */
public class StringSearchResult<T> {
	private CharSequence keyword;
	private T associatedData;
	private float matchPercentage;
	
	public StringSearchResult() { }
	
	public StringSearchResult(CharSequence keyword, T associatedData, float matchPercentage) {
		this.keyword = keyword;
		this.associatedData = associatedData;
		this.matchPercentage = matchPercentage;
	}

	public CharSequence getKeyword() {
		return keyword;
	}

	public T getAssociatedData() {
		return associatedData;
	}

	public float getMatchPercentage() {
		return matchPercentage;
	}
	
	@Override
	public String toString() {
		return String.format("%s/%s/%f", keyword, associatedData, matchPercentage);
	}
}