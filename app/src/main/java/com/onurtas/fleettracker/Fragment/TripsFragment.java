package com.onurtas.fleettracker.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.onurtas.fleettracker.Activity.TripDetailsActivity;
import com.onurtas.fleettracker.Activity.TripsFilterActivity;
import com.onurtas.fleettracker.Helper.TripsListAdaptor;
import com.onurtas.fleettracker.Model.Trip;
import com.onurtas.fleettracker.R;
import com.onurtas.fleettracker.ViewModel.TripsViewModel;
import com.onurtas.fleettracker.databinding.FragmentTripsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TripsFragment extends Fragment {

    private final ArrayList<Trip> trips = new ArrayList<>();
    private FragmentTripsBinding binding;
    private TripsViewModel viewModel;
    private TripsListAdaptor adapter;
    private ActivityResultLauncher<Intent> filterActivityResultLauncher;
    private long currentStartDateMillis = 0;
    private long currentEndDateMillis = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTripsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TripsViewModel.class);

        adapter = new TripsListAdaptor(getActivity(), trips);
        binding.tripsList.setAdapter(adapter);

        setupUI();
        setupObservers();

        viewModel.fetchTrips();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filterActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            long startDateMillis = data.getLongExtra(TripsFilterActivity.EXTRA_START_DATE, -1L);
                            long endDateMillis = data.getLongExtra(TripsFilterActivity.EXTRA_END_DATE, -1L);

                            if (startDateMillis != -1L && endDateMillis != -1L) {
                                Calendar startDateCal = Calendar.getInstance();
                                startDateCal.setTimeInMillis(startDateMillis);
                                // Set time to the beginning of the day for start date
                                startDateCal.set(Calendar.HOUR_OF_DAY, 0);
                                startDateCal.set(Calendar.MINUTE, 0);
                                startDateCal.set(Calendar.SECOND, 0);
                                startDateCal.set(Calendar.MILLISECOND, 0);

                                Calendar endDateCal = Calendar.getInstance();
                                endDateCal.setTimeInMillis(endDateMillis);
                                // Set time to the end of the day for end date for inclusive filtering
                                endDateCal.set(Calendar.HOUR_OF_DAY, 23);
                                endDateCal.set(Calendar.MINUTE, 59);
                                endDateCal.set(Calendar.SECOND, 59);
                                endDateCal.set(Calendar.MILLISECOND, 999);

                                // Store them if you need to re-apply filter or pre-fill next time
                                currentStartDateMillis = startDateMillis;
                                currentEndDateMillis = endDateMillis;

                                // Call ViewModel to filter trips
                                viewModel.filterTripsByDate(startDateCal, endDateCal);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String dateRange = "Filtering from " + sdf.format(startDateCal.getTime()) + " to " + sdf.format(endDateCal.getTime());
                                Toast.makeText(getContext(), dateRange, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getContext(), "Filter dates not received properly.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "No filter data returned.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Toast.makeText(getContext(), "Filter cancelled.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupUI() {
        binding.tripsList.setOnItemClickListener((adapterView, view, i, l) -> {
            Trip trip = (Trip) adapter.getItem(i);
            if (trip != null) {
                Intent video_intent = new Intent(requireContext(), TripDetailsActivity.class);
                video_intent.putExtra("start_lat", trip.start_lat);
                video_intent.putExtra("start_lon", trip.start_lon);
                video_intent.putExtra("distance", trip.distance);
                startActivity(video_intent);
            }
        });

        MaterialButtonToggleGroup toggleGroup = binding.filterToggleGroup;
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.allButton) {
                    viewModel.clearDateFilter();
                    currentStartDateMillis = 0;
                    currentEndDateMillis = 0;
                } else {
                    // Handle other toggle buttons (Daily, Weekly, Monthly)
                    // need to calculate the appropriate start/end dates etc.
                }
            }
        });

        binding.pullToRefresh.setOnRefreshListener(() -> {
            viewModel.fetchTrips(); // This will fetch and re-apply the current filter if set
            binding.pullToRefresh.setRefreshing(false);
        });

        EditText searchInput = binding.searchText;
        searchInput.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                String query = v.getText().toString();
                adapter.getFilter().filter(query);
                return true;
            }
            return false;
        });

        ImageButton filterButtonIcon = binding.filterButton;
        filterButtonIcon.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), TripsFilterActivity.class);
            if (currentStartDateMillis != 0 && currentEndDateMillis != 0) {
                i.putExtra(TripsFilterActivity.EXTRA_START_DATE, currentStartDateMillis);
                i.putExtra(TripsFilterActivity.EXTRA_END_DATE, currentEndDateMillis);
            }
            filterActivityResultLauncher.launch(i);
        });
    }

    private void setupObservers() {
        viewModel.getTripsLiveData().observe(getViewLifecycleOwner(), tripList -> {
            if (adapter != null) {
                adapter.updateData(tripList != null ? tripList : new ArrayList<>());
            }

            if (tripList == null || tripList.isEmpty()) {
                if (binding != null) { // Check binding as view might be destroyed
                    binding.tripsList.setVisibility(View.GONE);
                }
            } else {
                if (binding != null) {
                    binding.tripsList.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
