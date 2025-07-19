package com.onurtas.fleettracker.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.onurtas.fleettracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TripsFilterActivity extends AppCompatActivity {

    public static final String EXTRA_START_DATE = "START_DATE";
    public static final String EXTRA_END_DATE = "END_DATE";

    private TextView txtStartDate;
    private TextView txtEndDate;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_filter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        updateStartDateText();
        updateEndDateText();
    }

    private void updateStartDateText() {
        txtStartDate.setText(dateFormatter.format(startDate.getTime()));
    }

    private void updateEndDateText() {
        txtEndDate.setText(dateFormatter.format(endDate.getTime()));
    }

    public void chooseStartDate(View view) {
        final Calendar currentCalendar = Calendar.getInstance();
        int year = startDate.get(Calendar.YEAR); // Use selected date for initial picker display
        int month = startDate.get(Calendar.MONTH);
        int day = startDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(TripsFilterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDate.set(Calendar.YEAR, year);
                        startDate.set(Calendar.MONTH, monthOfYear);
                        startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateStartDateText();
                        // Optional: Add validation (e.g., startDate <= endDate)
                        if (startDate.after(endDate)) {
                            endDate.setTimeInMillis(startDate.getTimeInMillis()); // Set endDate to startDate
                            updateEndDateText();
                            Toast.makeText(TripsFilterActivity.this, getString(R.string.end_date_adjusted), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, year, month, day);

        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.select_button_text), dpd);
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel_button_text), dpd);
        dpd.show();
    }

    public void chooseEndDate(View view) {
        final Calendar currentCalendar = Calendar.getInstance();
        int year = endDate.get(Calendar.YEAR); // Use selected date for initial picker display
        int month = endDate.get(Calendar.MONTH);
        int day = endDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(TripsFilterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDate.set(Calendar.YEAR, year);
                        endDate.set(Calendar.MONTH, monthOfYear);
                        endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateEndDateText();
                        // validation (endDate >= startDate)
                        if (endDate.before(startDate)) {
                            Toast.makeText(TripsFilterActivity.this, getString(R.string.end_date_cannot_be_before_start_date), Toast.LENGTH_LONG).show();
                            // Reset endDate to a valid value
                            endDate.setTimeInMillis(startDate.getTimeInMillis());
                            updateEndDateText();
                        }
                    }
                }, year, month, day);

        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.select_button_text), dpd);
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel_button_text), dpd);
        dpd.getDatePicker().setMinDate(startDate.getTimeInMillis()); // Prevent selecting end date before start date
        dpd.show();
    }

    public void apply(View v) {

        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_START_DATE, startDate.getTimeInMillis());
        returnIntent.putExtra(EXTRA_END_DATE, endDate.getTimeInMillis());

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}