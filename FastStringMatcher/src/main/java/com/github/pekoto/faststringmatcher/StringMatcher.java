package com.github.pekoto.faststringmatcher;

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
 */
public class StringMatcher {

}
