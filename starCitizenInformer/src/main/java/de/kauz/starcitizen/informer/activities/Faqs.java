package de.kauz.starcitizen.informer.activities;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.JsoupDownloadActivity;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.Translator;
import de.kauz.starcitizen.informer.utils.ViewHelper;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * FAQ view containing additional FAQ items; select one through a spinner.
 * 
 * @author MadKauz
 * 
 */
public class Faqs extends JsoupDownloadActivity {

	private TextView faqsLoading, text1;
	private ScrollView faqsScroll;
	private View indicator;
	private Spinner faqsSelectionSpinner;

	private Typeface font;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faqs);

		this.faqsLoading = (TextView) findViewById(R.id.FaqsLoading);
		this.faqsSelectionSpinner = (Spinner) findViewById(R.id.FaqsSelectionSpinner);
		this.text1 = (TextView) findViewById(R.id.FAQText1);
		this.faqsScroll = (ScrollView) findViewById(R.id.scrollFaq);
		this.indicator = findViewById(R.id.glowLeftTop);

		this.font = Typeface.createFromAsset(getAssets(),
				"Electrolize-Regular.ttf");
		this.faqsLoading.setTypeface(font);
		this.text1.setTypeface(font);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(this, R.array.faqsSpinnerArray,
						R.layout.spinner_item);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.faqsSelectionSpinner.setAdapter(spinnerAdapter);

		this.faqsSelectionSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> adapterView,
							View view, int position, long id) {

						String download = null;

						download = InformerConstants.FAQURLS[position];

						if (MyApp.getInstance().isOnline(getBaseContext())
								&& download != null) {
							onStartDownloading(faqsLoading, download);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					};
				});

		getSupportActionBar().setTitle(
				getResources().getString(R.string.faqs_title));
		getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[11]);
	}

	@Override
	public void onDownloadComplete(Document doc) {
		if (doc != null) {
			try {
				Elements contentElements = doc.select("div.segment");
				this.text1.setText(Translator.translateContent(contentElements
						.first().html()));
				ViewHelper.fadeIn(this.faqsScroll);
				ViewHelper.fadeIn(this.indicator);
			} catch (NullPointerException e) {
				e.printStackTrace();
				MyApp.getInstance().showError(this,
						getResources().getString(R.string.errorParseFault));
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				MyApp.getInstance().showError(this,
						getResources().getString(R.string.errorParseFault));
			}
		}
		super.onDownloadComplete(doc);
	}

}
