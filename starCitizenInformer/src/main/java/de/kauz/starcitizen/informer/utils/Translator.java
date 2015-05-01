package de.kauz.starcitizen.informer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

/**
 * Helps translating HTML to human readable strings.
 * 
 * @author MadKauz
 * 
 */
public class Translator {

	/**
	 * Translates HTML content to a more human readable string. After a
	 * uppercase letter following a point, a backspace is inserted.
	 * 
	 * @param html
	 *            the input
	 * @return the readable string
	 */
	public static String translateContent(String html) {
		String readableString;

		readableString = Jsoup.parse(html).text();

		Pattern pattern = Pattern.compile("\\. [A-Z]+");
		Matcher matcher = pattern.matcher(readableString);
		String returnString = "";
		while (matcher.find()) {
			String replacement = Matcher.quoteReplacement(".\n\n");
			String searchString = Pattern.quote(". ");
			returnString = readableString.replaceAll(searchString, replacement);
		}

		return returnString;
	}

	/**
	 * Gets rid of HTML break lines.
	 * 
	 * @param stringContainingBr
	 *            String with break lines
	 * @return the break line removed String
	 */
	public static String removeBreaks(String stringContainingBr) {

		if (stringContainingBr.length() > 6) {
			String replacement = Matcher.quoteReplacement("\n");
			String searchString = Pattern.quote("<br />");
			String returnString = stringContainingBr.replaceAll(searchString,
					replacement);
			return returnString;
		} else {
			return stringContainingBr;
		}

	}

}
