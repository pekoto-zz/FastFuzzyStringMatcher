package com.github.pekoto.faststringmatcher;

import java.util.ArrayList;


/**
 * A simple wrapper for returning results from the string matcher.
 * 
 * @author Graham McRobbie
 *
 * @param <T> The data associated with each result.
 */
public class StringSearchResults<T> extends ArrayList<StringSearchResult<T>> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 5315812187969680611L;
	
	public boolean containsKeyword(String keyword) {
		return this.stream().filter(r -> r.getKeyword().equals(keyword)).count() > 0;
	}
}
