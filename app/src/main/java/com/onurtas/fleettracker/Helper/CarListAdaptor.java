package com.onurtas.fleettracker.Helper;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.onurtas.fleettracker.Model.Car;
import com.onurtas.fleettracker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CarListAdaptor extends BaseAdapter implements Filterable {
    Context context;
    ArrayList<Car> originalRowItems;
    ArrayList<Car> displayedRowItems;

    public CarListAdaptor(Activity context, ArrayList<Car> items) {
        this.context = context;
        this.originalRowItems = items;
        this.displayedRowItems = items;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                displayedRowItems = (ArrayList<Car>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Car> FilteredArrList = new ArrayList<Car>();

                if (originalRowItems == null) {
                    originalRowItems = new ArrayList<Car>(originalRowItems); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = originalRowItems.size();
                    results.values = originalRowItems;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < originalRowItems.size(); i++) {
                        String data = originalRowItems.get(i).plate;
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(new Car(originalRowItems.get(i)));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_car_grid_entry, null);
            holder = new ViewHolder();


            holder.txtBrand = convertView.findViewById(R.id.txtBrand);
            holder.txtModel = convertView.findViewById(R.id.txtModel);
            holder.txtYear = convertView.findViewById(R.id.txtYear);
            holder.txtColor = convertView.findViewById(R.id.txtColor);
            holder.txtTransmission = convertView.findViewById(R.id.txtTransmission);
            holder.txtPlate = convertView.findViewById(R.id.txtPlate);
            holder.picture = convertView.findViewById(R.id.imageView);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ViewHolder holder2 = holder;
        Car rowItem = (Car) getItem(position);


        holder2.txtBrand.setText(rowItem.brand);
        holder2.txtModel.setText(rowItem.model);
        holder2.txtYear.setText("" + rowItem.year);
        holder2.txtColor.setText(rowItem.color);
        holder2.txtTransmission.setText(rowItem.transmission);
        holder2.txtPlate.setText(rowItem.plate);
        try {
            Picasso.get().load(rowItem.picture).into(holder2.picture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return displayedRowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return displayedRowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return displayedRowItems.indexOf(getItem(position));
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtBrand;
        TextView txtModel;
        TextView txtYear;
        TextView txtColor;
        TextView txtTransmission;
        TextView txtPlate;
        ImageView picture;
    }


}