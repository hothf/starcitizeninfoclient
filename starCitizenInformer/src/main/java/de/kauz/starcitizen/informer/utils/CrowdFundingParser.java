package de.kauz.starcitizen.informer.utils;

import java.text.NumberFormat;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A parser for the crowd funding info of the RSI website.
 * 
 * @author MadKauz
 * 
 */
public class CrowdFundingParser {

	private String fansRaised = "-", fundsRaised = "-";

	/**
	 * Create a new parser and parse the info.
	 * 
	 * @param toParse
	 *            the info to parse
	 */
	public CrowdFundingParser(String toParse) {

		JSONTokener tokener = new JSONTokener(toParse);
		try {
			JSONObject json = new JSONObject(tokener);
			JSONObject data = json.getJSONObject("data");

			String funds = data.getString("funds");
			long fans = data.getLong("fans");

			String correctedFunds = funds.substring(0, funds.length() - 2);

			final String fundsText = NumberFormat.getNumberInstance(Locale.US)
					.format(Long.valueOf(correctedFunds));

			setFansRaised((NumberFormat.getNumberInstance(Locale.US)
					.format(fans)));
			setFundsRaised(fundsText);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getFansRaised() {
		return fansRaised;
	}

	public void setFansRaised(String fansRaised) {
		this.fansRaised = fansRaised;
	}

	public String getFundsRaised() {
		return fundsRaised;
	}

	public void setFundsRaised(String fundsRaised) {
		this.fundsRaised = fundsRaised;
	}

}
