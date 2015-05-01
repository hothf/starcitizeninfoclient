package de.kauz.starcitizen.informer.activities;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.adapters.GoalsExpListViewAdapter;
import de.kauz.starcitizen.informer.model.StretchGoal;
import de.kauz.starcitizen.informer.utils.JsoupDownloadActivity;
import de.kauz.starcitizen.informer.utils.ViewHelper;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * Detail view containing all info of previous stretch-goals in a expandable list.
 * 
 * @author MadKauz
 * 
 */
public class Timeline extends JsoupDownloadActivity implements OnClickListener {

	public static final String EXTRA_VALUES_DATES = "_#ExtrasValsDates#_";
	public static final String EXTRA_VALUES_DESCRIPTION = "_#ExtrasValsDescription#_";
	public static final String EXTRA_VALUES_NAMES = "_#ExtrasValsNames#_";

	private ExpandableListView goalListView;
	private TextView emptyText;

	private Typeface font;

	private View glowLeft;

	private GoalsExpListViewAdapter listAdapter;
	private ArrayList<StretchGoal> goals = new ArrayList<StretchGoal>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		this.goalListView = (ExpandableListView) findViewById(R.id.TimelineListView);
		this.glowLeft = findViewById(R.id.glowLeftTop);
		this.emptyText = (TextView) findViewById(R.id.EmptyText);

		this.font = Typeface.createFromAsset(getBaseContext().getAssets(),
				"Electrolize-Regular.ttf");


		this.listAdapter = new GoalsExpListViewAdapter(this, font);
		this.goalListView.setAdapter(listAdapter);
		this.emptyText.setTypeface(font);

		setTitle(R.string.goalsTimeline);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			ArrayList<String> descriptions = new ArrayList<String>();
			ArrayList<String> dates = new ArrayList<String>();
			ArrayList<String> names = new ArrayList<String>();
			
			descriptions = extras.getStringArrayList(EXTRA_VALUES_DESCRIPTION);
			names = extras.getStringArrayList(EXTRA_VALUES_NAMES);
			dates = extras.getStringArrayList(EXTRA_VALUES_DATES);
			
			getSupportActionBar().setIcon(R.drawable.ic_goals);
			
			for (int i=0; i < names.size(); i++){
				StretchGoal goal = new StretchGoal(names.get(i), true, descriptions.get(i), dates.get(i));
				goals.add(goal);
			}

			listAdapter.setStretchGoals(goals);
			listAdapter.notifyDataSetChanged();

			ViewHelper.fadeIn(goalListView);
			ViewHelper.fadeIn(glowLeft);
		} else {
			this.emptyText.setText(R.string.empty);
			this.emptyText.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onDownloadComplete(Document doc) {
		super.onDownloadComplete(doc);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}

	}
}
