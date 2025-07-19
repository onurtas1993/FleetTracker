package com.onurtas.fleettracker.Helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onurtas.fleettracker.Model.Error;
import com.onurtas.fleettracker.R;

import java.util.ArrayList;


public class ErrorsListAdaptor extends BaseAdapter {
    Context context;
    ArrayList<Error> rowItems;

    public ErrorsListAdaptor(Activity context, ArrayList<Error> items) {
        this.context = context;
        this.rowItems = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_fault_item, null);
            holder = new ViewHolder();


            holder.txtErrorCode = convertView.findViewById(R.id.txtErrorCode);
            holder.txtErrorExplanation = convertView.findViewById(R.id.txtErrorExplanation);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ViewHolder holder2 = holder;
        Error rowItem = (Error) getItem(position);

        holder2.txtErrorCode.setText(rowItem.ErrorCode);
        holder2.txtErrorExplanation.setText(rowItem.ErrorExplanation);

        return convertView;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    private class ViewHolder {
        TextView txtErrorCode;
        TextView txtErrorExplanation;
    }


}