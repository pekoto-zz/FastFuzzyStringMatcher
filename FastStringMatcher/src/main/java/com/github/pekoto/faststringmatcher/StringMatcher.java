package com.github.pekoto.faststringmatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds tree based on edit distance that allows quick fuzzy searching of string keywords, case insensitive.
 * Also known as a BK Tree.
 * See <a href="https://en.wikipedia.org/wiki/BK-tree">Wikipedia</a>
 * <dl>
 * <dt><span class="strong">Percentage Matching</span></dt>
 * <dd>
 * This implementation also allows the retrieval of strings using percentage matching.
 * This is generally much more practical than searching purely on edit distance,
 * unless all of your strings are of fixed length.		
 * </dd>
 * <p>
 * <dt><span class="strong">Generic Type/Associated Data</span></dt>
 * <dd>
 * You can store some associated data with each string keyword.
 * The generic parameters refers to this data type.
 * <p>
 * <strong>Example uses:</strong>
 * <ul>
 * <li>Search for file name --> Return associated paths of files that match 70%
 * <li>Search fund code X000 -->  Return fund names/price for all funds starting with X000
 * <li>Search for data in a foreign language --> Return all translations that match 85%, etc.
 * <ul>
 * </dd>
 * <dt><span class="strong">Line Breaks/Spaces</span></dt>
 * <dd>
 * Large chunks of text can appear different due to line break/white space differences. In most cases,
 * we only want to compare the text itself, so the class allows you to remove line breaks and spaces
 * for comparison purposes.
 * </dd>

 * </dl>
 * @author Graham McRobbie
 *
 * @param <T> The type of data associated with each string keyword.
 */
public class StringMatcher<T> {
	private Node<T> root;
	private EditDistanceCalculator distanceCalculator = new EditDistanceCalculator();
	
	public void Add(CharSequence keyword, T associatedData) {
		
		if(keyword == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}
		
		if(keyword.length() == 0) {
			throw new IllegalArgumentException("Strings must not be empty");
		}
		
		if(root == null) {
			root = new Node<T>(keyword, associatedData);
		} else {
			Node<T> current = root;
			int distance = distanceCalculator.calculateEditDistance(current.normalizedKeyword, keyword);
			
			while(current.containsChildWithDistance(distance)) {				
				current = current.getChild(distance);
				distance = distanceCalculator.calculateEditDistance(current.normalizedKeyword, keyword);
				
				if(distance == 0) {
					return;	// Duplicate
				}
			}
			
			current.addChild(distance, keyword, associatedData);
		}
	}
	
	public StringSearchResults<T> search(String keyword, float matchPercentage) {
		int distanceThreshold = convertPercentageToEditDistance(keyword, matchPercentage);
		
		return search(keyword, distanceThreshold);
	}
	
	public StringSearchResults<T> search(String keyword, int distanceThreshold) {
		StringSearchResults<T> results = new StringSearchResults<T>();
		
		keyword = keyword.toLowerCase();
		searchTree(root, keyword, distanceThreshold, results);
		
		results.sortByClosestMatch();
		
		return results;
	}
	
	// Recursively search the tree, adding any data from nodes within the edit distance threshold.
	// Results are stored in the results parameter. This is a bit dirty functionally, but since this
	// method is recursive, it saves a new collection being created/copied with every call.
	private void searchTree(Node<T> node, String keyword, int distanceThreshold, List<StringSearchResult<T>> results) {
		int currentDistance = distanceCalculator.calculateEditDistance(node.normalizedKeyword, keyword);
		int minDistance = currentDistance - distanceThreshold;
		int maxDistance = currentDistance + distanceThreshold;
		
		if(currentDistance <= distanceThreshold) {
			float percentageDifference = getPercentageDifference(node.normalizedKeyword, keyword, currentDistance);
			StringSearchResult<T> result = new StringSearchResult<T>(node.originalKeyword, node.associatedData, percentageDifference);
			results.add(result);
		}
		
		List<Integer> childKeysWithinDistanceThreshold = node.getChildKeysWithinDistance(minDistance, maxDistance);
		
		for(Integer childKey: childKeysWithinDistanceThreshold) {
			Node<T> child = node.getChild(childKey);
			searchTree(child, keyword, distanceThreshold, results);
		}
	}
	
	private int convertPercentageToEditDistance(CharSequence keyword, float matchPercentage) {
		return keyword.length() - (Math.round((keyword.length() * matchPercentage)/100.0f));
	}
	
	private float getPercentageDifference(CharSequence keyword, CharSequence wordToMatch, int editDistance) {
		int longestWordLength = Math.max(keyword.length(), wordToMatch.length());
		return 100.0f - (((float)editDistance/longestWordLength) * 100.0f);
	}
	
	/**
	 * A node in the BK Tree.
	 *
	 * @param <T> The type of data associated with each string keyword.
	 */
	private static class Node<T> {
		private CharSequence originalKeyword;
		private CharSequence normalizedKeyword;
		private T associatedData;
		private HashMap<Integer, Node<T>> children;
		
		public Node(CharSequence keyword, T associatedData) {
			this.originalKeyword = keyword;
			this.normalizedKeyword = keyword.toString().toLowerCase();
			this.associatedData = associatedData;
		}
		
		public Node<T> getChild(int key) {
			if(containsChildWithDistance(key)) {
				return children.get(key);
			} else {
				return null;
			}
		}
		
		public List<Integer> getChildKeysWithinDistance(int minDistance, int maxDistance) {
			if(children == null) {
				return new ArrayList<Integer>(0);
			} else {
				return children.keySet().stream().filter(n -> n >= minDistance && n <= maxDistance)
												 .collect(Collectors.toList());
			}
		}
		
		public boolean containsChildWithDistance(int key) {
			return children != null && children.containsKey(key);
		}
		
		public void addChild(int key, CharSequence keyword, T associatedData) {
			if(children == null) {
				children = new HashMap<Integer, Node<T>>();
			}
			
			Node<T> child = new Node<T>(keyword, associatedData);
			children.put(key, child);
		}
		
		@Override
		public String toString() {
			return String.format("%s/%s/%s", originalKeyword, normalizedKeyword, associatedData);
		}
	}
}
