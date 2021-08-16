package com.stratos.horn;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GroupListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> groupNames;

    public GroupListAdapter(Activity context, ArrayList<String> groupNames){
        super(context, R.layout.group_list_item, groupNames);

        this.context = context;
        this.groupNames = groupNames;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.group_list_item, null, true);

        TextView textViewGroupName = rowView.findViewById(R.id.groupName);
        textViewGroupName.setText(groupNames.get(position));

        return rowView;
    }
}
