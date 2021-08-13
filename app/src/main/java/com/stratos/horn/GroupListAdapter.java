package com.stratos.horn;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GroupListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] groupName;

    public GroupListAdapter(Activity context, String[] groupName){
        super(context, R.layout.group_list_item, groupName);

        this.context = context;
        this.groupName = groupName;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.group_list_item, null, true);

        TextView textViewGroupName = rowView.findViewById(R.id.groupName);
        textViewGroupName.setText(groupName[position]);

        return rowView;
    }
}
