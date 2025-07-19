package com.onurtas.fleettracker.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.onurtas.fleettracker.ViewModel.VehiclesViewModel;
import com.onurtas.fleettracker.databinding.FragmentVehiclesBinding;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class VehiclesFragment extends Fragment {

    private FragmentVehiclesBinding binding;
    private VehiclesViewModel vehiclesViewModel;
    private ViewPagerStateAdapter viewPagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVehiclesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vehiclesViewModel = new ViewModelProvider(this).get(VehiclesViewModel.class);

        setupViewPager();
        observeViewModel();
    }

    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerStateAdapter(this);
        binding.viewpager.setAdapter(viewPagerAdapter);
    }

    private void observeViewModel() {
        vehiclesViewModel.fragmentClasses.observe(getViewLifecycleOwner(), fragmentClasses -> {
            if (fragmentClasses != null && !fragmentClasses.isEmpty()) {
                viewPagerAdapter.setFragmentClasses(fragmentClasses);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null)
            binding.viewpager.setAdapter(null);
        binding = null;
    }

    static class ViewPagerStateAdapter extends FragmentStateAdapter {
        private final List<Class<? extends Fragment>> fragmentClassList = new ArrayList<>();

        public ViewPagerStateAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public void setFragmentClasses(List<Class<? extends Fragment>> newFragmentClasses) {
            this.fragmentClassList.clear();
            this.fragmentClassList.addAll(newFragmentClasses);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            try {
                return fragmentClassList.get(position).getDeclaredConstructor().newInstance();
            } catch (java.lang.InstantiationException | NoSuchMethodException |
                     IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int getItemCount() {
            return fragmentClassList.size();
        }
    }
}
