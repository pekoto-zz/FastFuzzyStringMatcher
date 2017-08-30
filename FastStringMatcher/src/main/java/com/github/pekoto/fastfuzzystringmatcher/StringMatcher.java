package com.github.pekoto.fastfuzzystringmatcher;

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
	private MatchingOption matchingOption = MatchingOption.NONE;
	
	public StringMatcher() { }
	
	public StringMatcher(MatchingOption matchingOption) {
		this.matchingOption = matchingOption;
	}
	
	public void Add(CharSequence keyword, T associatedData) {
		if(keyword == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}
		
		if(keyword.length() == 0) {
			throw new IllegalArgumentException("Strings must not be empty");
		}
		
		CharSequence normalizedKeyword = getNormalizedString(keyword);
				
		if(root == null) {
			root = new Node<T>(keyword, normalizedKeyword, associatedData);
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
			
			current.addChild(distance, keyword, normalizedKeyword, associatedData);
		}
	}
	
	private CharSequence getNormalizedString(CharSequence str) 
	{
		if(matchingOption == MatchingOption.REMOVE_SPACING_AND_LINEBREAKS) {
			return removeSpacesAndLinebreaks(str);
		} else {
			return str;			
		}
	}
	
	private CharSequence removeSpacesAndLinebreaks(CharSequence str) {
		return str.toString().replaceAll("[\\t\\n\\r\\s]", "");
	}
	
	public SearchResultList<T> search(CharSequence keyword, float matchPercentage) {
		keyword = getNormalizedString(keyword);
		int distanceThreshold = convertPercentageToEditDistance(keyword, matchPercentage);
		
		return searchTree(keyword, distanceThreshold);
	}
	
	private int convertPercentageToEditDistance(CharSequence keyword, float matchPercentage) {
		return keyword.length() - (Math.round((keyword.length() * matchPercentage)/100.0f));
	}
	
	public SearchResultList<T> search(CharSequence keyword, int distanceThreshold) {
		keyword = getNormalizedString(keyword);
		return searchTree(keyword, distanceThreshold);
	}
	
	private SearchResultList<T> searchTree(CharSequence keyword, int distanceThreshold) {
		SearchResultList<T> results = new SearchResultList<T>();
		
		searchTree(root, keyword, distanceThreshold, results);
		
		results.sortByClosestMatch();
		
		return results;
	}
	
	// Recursively search the tree, adding any data from nodes within the edit distance threshold.
	// Results are stored in the results parameter. This is a bit dirty functionally, but since this
	// method is recursive, it saves a new collection being created/copied with every call.
	private void searchTree(Node<T> node, CharSequence keyword, int distanceThreshold, List<SearchResult<T>> results) {
		int currentDistance = distanceCalculator.calculateEditDistance(node.normalizedKeyword, keyword);
		int minDistance = currentDistance - distanceThreshold;
		int maxDistance = currentDistance + distanceThreshold;
		
		if(currentDistance <= distanceThreshold) {
			float percentageDifference = getPercentageDifference(node.normalizedKeyword, keyword, currentDistance);
			SearchResult<T> result = new SearchResult<T>(node.originalKeyword, node.associatedData, percentageDifference);
			results.add(result);
		}
		
		List<Integer> childKeysWithinDistanceThreshold = node.getChildKeysWithinDistance(minDistance, maxDistance);
		
		for(Integer childKey: childKeysWithinDistanceThreshold) {
			Node<T> child = node.getChild(childKey);
			searchTree(child, keyword, distanceThreshold, results);
		}
	}
	
	private float getPercentageDifference(CharSequence keyword, CharSequence wordToMatch, int editDistance) {
		int longestWordLength = Math.max(keyword.length(), wordToMatch.length());
		return 100.0f - (((float)editDistance/longestWordLength) * 100.0f);
	}
	
	public void printTree() {
		root.printHierarchy(0);
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
		
		public Node(CharSequence keyword, CharSequence normalizedKeyword, T associatedData) {
			this.originalKeyword = keyword;
			this.normalizedKeyword = normalizedKeyword;
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
		
		public void addChild(int key, CharSequence keyword, CharSequence normalizedKeyword, T associatedData) {
			if(children == null) {
				children = new HashMap<Integer, Node<T>>();
			}
			
			Node<T> child = new Node<T>(keyword, normalizedKeyword, associatedData);
			children.put(key, child);
		}
		
		@Override
		public String toString() {
			return String.format("%s/%s/%s", originalKeyword, normalizedKeyword, associatedData);
		}
		
		public void printHierarchy(int level) {
			for(int i = 0; i < level; i++) {
				System.out.print("\t");
			}
			
			System.out.println(String.format("-- %s", originalKeyword));
			
			if(children != null) {
				for(Node<T> child: children.values()) {
					child.printHierarchy(level+1);
				}
			}
		}
	}
}
