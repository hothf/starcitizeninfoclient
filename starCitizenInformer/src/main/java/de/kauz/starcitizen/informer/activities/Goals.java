package de.kauz.starcitizen.informer.activities;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.JsoupDownloadActivity;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.ViewHelper;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Detail view containing detailed info of all stretch-goals.
 * 
 * @author MadKauz
 * 
 */
public class Goals extends JsoupDownloadActivity implements OnClickListener {

	public static final String EXTRA_FUNDS = "_#ExtrasFunds#_";
	public static final String EXTRA_PERCENTAGE = "_#ExtrasPercentage#_";
	public static final String EXTRA_GOAL_FUNDS = "_#ExtrasGoalFunds#_";
	public static final String EXTRA_PERCENTAGE_INT = "_#ExtrasGoalPercInt#_";

	private TextView goalsLoading, goalCurrentFunds, goalCurrentPercentage,
			goalCurrentGoalFunds;

	private ProgressBar goalCurrentLoadingProgress;

	private Typeface font;

	private RelativeLayout goalCurrentContainer;
	private Button goalCurrentDescriptionButton, goalShowTimeline;

	private ArrayList<String> goalsDescriptions = new ArrayList<String>();
	private ArrayList<String> goalsDates = new ArrayList<String>();
	private ArrayList<String> goalsNames = new ArrayList<String>();

	private String url = InformerConstants.URL_NEWS_GOALS,
			currentGoalDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goals);

		this.goalCurrentLoadingProgress = (ProgressBar) findViewById(R.id.GoalCurrentLoadingProgress);
		this.goalCurrentContainer = (RelativeLayout) findViewById(R.id.goalCurrentContainer);
		this.goalCurrentDescriptionButton = (Button) findViewById(R.id.GoalCurrentDescriptionButton);
		this.goalShowTimeline = (Button) findViewById(R.id.GoalShowTimelineButton);

		this.goalsLoading = (TextView) findViewById(R.id.GoalLoading);
		this.goalCurrentFunds = (TextView) findViewById(R.id.GoalCurrentFundsText);
		this.goalCurrentPercentage = (TextView) findViewById(R.id.GoalCurrentPercentageText);
		this.goalCurrentGoalFunds = (TextView) findViewById(R.id.GoalCurrentGoalFunds);

		this.font = Typeface.createFromAsset(getBaseContext().getAssets(),
				"Electrolize-Regular.ttf");

		this.goalsLoading.setTypeface(font);
		this.goalCurrentFunds.setTypeface(font);
		this.goalCurrentPercentage.setTypeface(font);
		this.goalCurrentGoalFunds.setTypeface(font);
		this.goalCurrentDescriptionButton.setTypeface(font);

		this.goalCurrentDescriptionButton.setOnClickListener(this);

		setTitle(R.string.goalsTitle);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			String mainFunds = extras.getString(EXTRA_FUNDS);
			String mainPercentage = extras.getString(EXTRA_PERCENTAGE);
			String mainCurrentFunds = extras.getString(EXTRA_GOAL_FUNDS);
			int percentInt = extras.getInt(EXTRA_PERCENTAGE_INT);

			getSupportActionBar().setIcon(R.drawable.ic_goals);

			ViewHelper.fadeIn(goalCurrentContainer);
			this.goalCurrentFunds.setText(getResources().getString(
					R.string.goalCurrentCollectedPref)
					+ "   " + mainFunds);
			this.goalCurrentPercentage.setText(mainPercentage);
			this.goalCurrentGoalFunds.setText(getResources().getString(
					R.string.goalNextGoalCollectedPref)
					+ " " + mainCurrentFunds);
			animateCurrentGoal(percentInt);

			if (MyApp.getInstance().isOnline(this)) {
				onStartDownloading(goalsLoading, url);
			}

		}

		this.goalShowTimeline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Goals.this, Timeline.class);

				if (goalsDescriptions.size() > 0
						&& goalsDescriptions.size() == goalsDates.size()
						&& goalsDescriptions.size() == goalsNames.size()) {
					intent.putStringArrayListExtra(Timeline.EXTRA_VALUES_DATES,
							goalsDates);
					intent.putStringArrayListExtra(Timeline.EXTRA_VALUES_NAMES,
							goalsNames);
					intent.putStringArrayListExtra(
							Timeline.EXTRA_VALUES_DESCRIPTION,
							goalsDescriptions);
				}

				startActivity(intent);

			}
		});

	}

	/**
	 * Shows a progression animation.
	 * 
	 * @param progress
	 *            to be animated
	 */
	private void animateCurrentGoal(final int progress) {
		this.goalCurrentLoadingProgress.setMax(100);
		this.goalCurrentLoadingProgress.setProgress(0);

		final Handler handler = new Handler();

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i <= progress; i++) {

					final int progression = i;
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							goalCurrentLoadingProgress.setProgress(progression);
						}
					}, progression * 15);
				}

			}
		}, 500);
	}

	// parsing stuff:
	@Override
	public void onDownloadComplete(Document doc) {

		if (doc != null) {
			try {
				// sadly, was removed from the website:
				
				// Elements currentElements = doc
				// .select("div.current-stretchgoal");
				// Elements currentElements = doc
				// .select("div[class=wrapper stretchgoal-list]");
				// try {

				// final String currentDescription = currentElements.get(0)
				// .child(0).child(0).child(2).text();

				// final String currentDescription =
				// currentElements.get(currentElements.size()-1);
				//
				//

				// other stretch goals
				try {

					Elements goalElements = doc
							.select("div[class=wrapper stretchgoal-list]");

					for (int i = 0; i < goalElements.first().children().size(); i++) {

						Element goal = goalElements.get(0).child(i);
						String date = goal.select("div.date").text();
						String name = goal.select("div.amount").text();
						String description = goal.select("div.stripes").get(0)
								.text();
						if (description == null) {
							description = "-";
						}
						goalsDates.add(date);
						goalsDescriptions.add(description);
						goalsNames.add(name);
					}

					final String currentDescription = goalElements
							.first()
							.child((goalElements.first().children().size() - 1))
							.child(1).text();

					if (currentDescription.length() > 0) {
						ViewHelper.fadeIn(goalCurrentDescriptionButton);
						currentGoalDescription = currentDescription;
					}

					ViewHelper.fadeIn(goalShowTimeline);

				} catch (NullPointerException e) {
					e.printStackTrace();
					MyApp.getInstance().showError(Goals.this,
							getResources().getString(R.string.errorParseFault));
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
					MyApp.getInstance().showError(Goals.this,
							getResources().getString(R.string.errorParseFault));
				}

			} catch (NullPointerException e) {
				e.printStackTrace();
				MyApp.getInstance().showError(Goals.this,
						getResources().getString(R.string.errorParseFault));
			}
		} else {
			MyApp.getInstance().showError(Goals.this,
					getResources().getString(R.string.errorServerFault));
		}
		super.onDownloadComplete(doc);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.GoalCurrentDescriptionButton:

			if (currentGoalDescription != null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						v.getContext());
				builder.setTitle(getResources().getString(
						R.string.goalCurrentDescriptionDialogTitle));
				builder.setMessage(currentGoalDescription);
				builder.show();
			}

			break;

		default:
			break;
		}

	}
}
