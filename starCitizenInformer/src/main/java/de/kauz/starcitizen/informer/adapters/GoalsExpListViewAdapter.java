package de.kauz.starcitizen.informer.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.model.StretchGoal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Custom Adapter for a expandable ListView. Intended to display Stretch Goal
 * Objects.
 * 
 * @author MadKauz
 * 
 */
public class GoalsExpListViewAdapter extends BaseExpandableListAdapter {

	private Typeface font;
	private Context context;
	private ArrayList<StretchGoal> stretchGoals = new ArrayList<StretchGoal>();
	private HashMap<String, String> goalChildMap = new HashMap<String, String>();

	public GoalsExpListViewAdapter(Context context, Typeface font) {
		this.font = font;
		this.context = context;
	}

	public ArrayList<StretchGoal> getStretchGoals() {
		return this.stretchGoals;
	}

	public void setStretchGoals(ArrayList<StretchGoal> stretchGoals) {
		for (StretchGoal goal : stretchGoals) {
			goalChildMap.put(goal.getTitle(), goal.getDescription());
		}
		this.stretchGoals = stretchGoals;
	}

	@Override
	public int getGroupCount() {
		return this.stretchGoals.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.stretchGoals.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return goalChildMap.get(this.stretchGoals.get(groupPosition).getTitle());
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
	     StretchGoal goal = (StretchGoal) getGroup(groupPosition);
	        if (convertView == null) {
	            LayoutInflater infalInflater = (LayoutInflater) this.context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = infalInflater.inflate(R.layout.list_stretchgoal_header, null);
	        }
	        TextView titleText = (TextView) convertView
	                .findViewById(R.id.goalTitle);
	        titleText.setTypeface(font);
	        titleText.setText(goal.getTitle());
	 
	        return convertView;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
	     final String childText = (String) getChild(groupPosition, childPosition);
	     
	        if (convertView == null) {
	            LayoutInflater infalInflater = (LayoutInflater) this.context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = infalInflater.inflate(R.layout.list_stretchgoal_item, null);
	        }
	        TextView descriptionText = (TextView) convertView
	                .findViewById(R.id.goalDescription);
	        descriptionText.setTypeface(font);
	        descriptionText.setText(childText);
	        return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
