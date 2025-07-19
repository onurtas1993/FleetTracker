package com.onurtas.fleettracker.Helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.onurtas.fleettracker.Model.Trip;
import com.onurtas.fleettracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TripsListAdaptor extends BaseAdapter implements Filterable {
    Context context;
    private ArrayList<Trip> originalRowItems;
    private ArrayList<Trip> displayedRowItems;

    public TripsListAdaptor(Activity context, ArrayList<Trip> items) {
        this.context = context;
        this.originalRowItems = new ArrayList<>(items);
        this.displayedRowItems = new ArrayList<>(items);
    }

    public void updateData(ArrayList<Trip> newTrips) {
        this.originalRowItems.clear();
        if (newTrips != null) {
            this.originalRowItems.addAll(newTrips);
        }

        this.displayedRowItems.clear();
        if (newTrips != null) {
            this.displayedRowItems.addAll(newTrips);
        }
        notifyDataSetChanged(); // Crucial: notify that the data has changed
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_trip_list_item, null);
            holder = new ViewHolder();


            holder.from_location = convertView.findViewById(R.id.txtFromLocation);
            holder.start_time = convertView.findViewById(R.id.txtStartDate);
            holder.to_location = convertView.findViewById(R.id.txtEndLocation);
            holder.end_time = convertView.findViewById(R.id.txtEndTime);
            holder.distance = convertView.findViewById(R.id.txtDistance);
            holder.fuel_consumption = convertView.findViewById(R.id.txtFuelConsumption);
            holder.elapsed_time = convertView.findViewById(R.id.txtElapsedTime);
            holder.txtPlate = convertView.findViewById(R.id.txtPlate);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // final ViewHolder holder2 = holder; // Not needed, holder is already effectively final
        // Ensure displayedRowItems is not empty and position is valid
        if (displayedRowItems != null && position < displayedRowItems.size()) {
            Trip rowItem = displayedRowItems.get(position); // Get item from displayedRowItems

            Date start_date = new Date(rowItem.start_epoch_date * 1000);
            Date end_date = new Date(rowItem.end_epoch_date * 1000);

            SimpleDateFormat localDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String start_nice_time = localDateFormat.format(start_date);
            String end_nice_time = localDateFormat.format(end_date);


            holder.end_time.setText(end_nice_time);
            holder.start_time.setText(start_nice_time);
            holder.from_location.setText(rowItem.friendly_start_street_name);
            holder.to_location.setText(rowItem.friendly_end_street_name);
            holder.distance.setText(String.valueOf(rowItem.distance));
            holder.txtPlate.setText(rowItem.plate);
            holder.fuel_consumption.setText(String.valueOf(rowItem.fuel_consumption));
            holder.elapsed_time.setText(String.valueOf(rowItem.elapsed_time_in_minutes));
        }


        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values instanceof ArrayList) {
                    displayedRowItems = (ArrayList<Trip>) results.values;
                } else {
                    displayedRowItems = new ArrayList<>();
                }
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Trip> FilteredArrList = new ArrayList<Trip>();

                if (originalRowItems == null) {
                    // This case should ideally not happen if updateData is used correctly.
                    // Initialize to an empty list to prevent NullPointerException.
                    originalRowItems = new ArrayList<>();
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = originalRowItems.size();
                    results.values = new ArrayList<>(originalRowItems);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Trip trip : originalRowItems) {
                        String plate = (trip.plate != null) ? trip.plate.toLowerCase() : "";
                        String driver = (trip.driver_name != null) ? trip.driver_name.toLowerCase() : "";

                        Date start_date = new Date(trip.start_epoch_date * 1000);
                        Date end_date = new Date(trip.end_epoch_date * 1000);
                        SimpleDateFormat localDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        String start_nice_time = localDateFormat.format(start_date).toLowerCase();
                        String end_nice_time = localDateFormat.format(end_date).toLowerCase();

                        if (plate.contains(filterPattern) ||
                                driver.contains(filterPattern) ||
                                start_nice_time.contains(filterPattern) ||
                                end_nice_time.contains(filterPattern)) {
                            FilteredArrList.add(trip);
                        }
                    }
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    @Override
    public int getCount() {
        return displayedRowItems != null ? displayedRowItems.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // Add bounds checking
        if (displayedRowItems != null && position >= 0 && position < displayedRowItems.size()) {
            return displayedRowItems.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // Using position directly is often safer if items can be duplicated or reordered
        return position;
    }

    private class ViewHolder {
        TextView from_location;
        TextView start_time;
        TextView to_location;
        TextView end_time;
        TextView distance;
        TextView fuel_consumption;
        TextView elapsed_time;
        TextView txtPlate;
    }
}
