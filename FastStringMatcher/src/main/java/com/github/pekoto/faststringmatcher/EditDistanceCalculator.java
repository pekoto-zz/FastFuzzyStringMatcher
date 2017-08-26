package com.github.pekoto.faststringmatcher;

/**
 * Calculates the number of operations it takes to turn one string into another.
 * Also known as the Levenshtein distance.  
 * 
 * This implementation uses the iterative approach with two matrix rows.
 * Case insensitive.
 * 
 * See <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Wikipedia</a>.
 * 
 * <p>
 * Available operations are:
 * <ul>
 * 	<li>substitution
 *  <li>insertion
 * 	<li>deletion
 * </ul>
 * <p>
 * <strong>Example:</strong><br>
 * To turn kitten --> sitting:
 * <p>
 * 	kitten --> sitten (substitute "s" for "k")<br>
 *  sitten --> sittin (substitute "e" for "i")<br>
 *  sittin --> sitting (insert "g")<br>
 *  <p>
 * Edit distance = 3
 * <p> 
 * 
 * @author Graham McRobbie
 * 
 */
public class EditDistanceCalculator {

	public int calculateEditDistance(CharSequence str1, CharSequence str2) {                          
	    
		if (str1 == null || str2 == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		
		if (str1.length() == 0) {
			return str2.length();
		}
		
		if (str2.length() == 0) {
			return str1.length();
		}
		
		int str1Length = str1.length() + 1;                                                     
	    int str2Length = str2.length() + 1;                                                     
	                                                                                    
	    int[] previousRow = new int[str1Length];                                                     
	    int[] currentRow = new int[str1Length];                                                  
	                                                                                    
	    // Initialise the first row of the distance matrix.
	    for (int i = 0; i < str1Length; i++) {
	    		previousRow[i] = i;                                     
	    }
	                                                                                    	                                                                                    
	    for (int rowIndex = 1; rowIndex < str2Length; rowIndex++) {                                                
	        // Initialise the first column of the distance matrix                         
	        currentRow[0] = rowIndex;                                                             
	                                                                                    
	        for(int colIndex = 1; colIndex < str1Length; colIndex++) {
	        		char str1Char = Character.toLowerCase(str1.charAt(colIndex-1));
	        		char str2Char = Character.toLowerCase(str2.charAt(rowIndex-1));
	        	
	            int swapCharsCost = (str1Char == str2Char) ? 0 : 1;
	            
	            int substitutionCost = previousRow[colIndex-1] + swapCharsCost;
	            int insertionCost  = previousRow[colIndex] + 1;
	            int deletionCost  = currentRow[colIndex-1] + 1;
                              
	            // Store the least costly edit operation
	            currentRow[colIndex] = min(insertionCost, deletionCost, substitutionCost);
	        }

	        int[] swap = previousRow;
	        previousRow = currentRow;
	        currentRow = swap;
	    }
	                                                                                    
	    // The distance is the last element of the last row        
	    return previousRow[str1Length-1];                                                          
	}
	
	private int min(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}
}