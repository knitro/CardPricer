package knitro.betterSearch.database.typoanalysis;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class TypoAnalysis {
	
	/**
	 * Calculates the Difference between 2 such strings.
	 * @param current
	 * @param toCompare
	 * @return
	 */
	public static int getDifference(String current, String toCompare) {
		
		String current_lower = current.toLowerCase();
		String toCompare_lower = toCompare.toLowerCase();
		
		/*Remove Special Characters for "Better Comparisons"*/
		String current_cleaned = removeSpecialChars(current_lower);
		String toCompare_cleaned = removeSpecialChars(toCompare_lower);
		
		return getLevenshteinDistance(current_cleaned, toCompare_cleaned);
	}
	
	public static int getLevenshteinDistance(String current, String toCompare) {
		
		LevenshteinDistance ld = new LevenshteinDistance();
		int distance = ld.apply(current, toCompare);
		return distance;
	}
	
	public static String removeSpecialChars(String s) {
		
		String s_1 = s.replaceAll(",", "");
		String s_2 = s_1.replaceAll(" ", "");
		String s_3 = s_2.replaceAll("'", "");
		
		return s_3;
	}
}
