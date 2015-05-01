package de.kauz.starcitizen.informer.utils;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * A parser for the official Star Citizen homepage.
 *
 * @author MadKauz
 * 
 */
public class SCDocumentParser {

	private Document doc;
	private SCDocumentParserListener listener;

	public enum ErrorTypes {
		ERROR_NPE, ERROR_OUT_OF_BOUNDS
	}

	/**
	 * This parser is intended to parse specific pages of the official Star
	 * Citizen Homepage.
	 */
	public SCDocumentParser(Document doc) {
		this.doc = doc;
	}

	/**
	 * This parser is intended to parse specific pages of the official Star
	 * Citizen Homepage.
	 */
	public SCDocumentParser(Document doc, SCDocumentParserListener listener) {
		this.doc = doc;
		this.listener = listener;
	}

	/**
	 * Listener for parsing.
	 * 
	 * @author MadKauz
	 * 
	 */
	public interface SCDocumentParserListener {

		/**
		 * Called when parsing is completed
		 * 
		 */
		void onParsingFeaturedComplete(String[] imageUrls, String[] texts,
				String[] urls, String[] descriptions);

		/**
		 * Called when parsing failed
		 */
		void onParsingError(ErrorTypes type);

	}

	/**
	 * Parses featured content of the RSI-Website.
	 */
	public void parseFeatured() {
		if (this.doc != null) {
			try {
				Elements featuredElements = doc.select("div.content-block1");

				String[] imageUrls = new String[featuredElements.size()];
				String[] texts = new String[featuredElements.size()];
				String[] urls = new String[featuredElements.size()];
				String[] descriptions = new String[featuredElements.size()];

				for (int i = 0; i < featuredElements.size(); i++) {
					// img urls
					String style = featuredElements.get(i).child(0)
							.attr("style");
					String[] splitstyles = style.split("url");
					if (splitstyles.length > 1) {
						String imageUrl = InformerConstants.URL_MAIN_HOMEPAGE
								+ splitstyles[1].substring(3,
										splitstyles[1].length() - 3);

						imageUrls[i] = imageUrl;
					}
					// urls
					String longurl = featuredElements.get(i).child(1)
							.select("a").attr("href");
					String url = InformerConstants.URL_MAIN_HOMEPAGE
							+ longurl.substring(1);
					urls[i] = url;
					// texts
					String text = featuredElements.get(i).child(1).select("a")
							.text();
					texts[i] = text;
					// descriptions
					String description = featuredElements.get(i).select("p")
							.text();
					descriptions[i] = description;
				}
				if (listener != null) {
					listener.onParsingFeaturedComplete(imageUrls, texts, urls,
							descriptions);
				}
			} catch (NullPointerException e) {
				if (listener != null) {
					listener.onParsingError(ErrorTypes.ERROR_NPE);
				}
			} catch (IndexOutOfBoundsException e) {
				if (listener != null) {
					listener.onParsingError(ErrorTypes.ERROR_OUT_OF_BOUNDS);
				}
			}
		}

	}

	/**
	 * Sets the listener for this parser.
	 * 
	 * @param listener
	 */
	public void setSCDocumentParserListener(SCDocumentParserListener listener) {
		this.listener = listener;
	}

}
