package com.onurtas.fleettracker.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.onurtas.fleettracker.Helper.LocaleHelper;
import com.onurtas.fleettracker.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;
    RadioButton trRadioButton;
    RadioButton enRadioButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        RadioGroup radioGroup = binding.languagesRadioGroup;

        trRadioButton = binding.radioTR;
        enRadioButton = binding.radioUK;

        SharedPreferences sharedPref = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String savedLang = sharedPref.getString("LANGUAGE", "en");

        if (savedLang.equals("tr")) {
            trRadioButton.setChecked(true);
        } else if (savedLang.equals("en")) {
            enRadioButton.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
            RadioButton rb = rootView.findViewById(checkedId);

            String name = rb.getText().toString();
            if (name.equals("English")) {
                editor.putString("LANGUAGE", "en");
            } else {
                editor.putString("LANGUAGE", "tr");
            }
            editor.commit();

            String newLang = name.equals("English") ? "en" : "tr";
            if (!savedLang.equals(newLang)) {
                editor.putString("LANGUAGE", newLang);
                editor.apply();
                LocaleHelper.updateResources(getActivity(), newLang);
                getActivity().recreate();

            }
        });

        return rootView;
    }

}
