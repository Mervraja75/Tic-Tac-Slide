package com.example.slide;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Locale;

/**
 * Activity to display the settings/preferences screen.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d("SettingsActivity", "Settings screen launched");

        // Back button functionality
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish()); // Close the activity and return to the game

        // Load the settings fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Fragment to display user preferences.
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load preferences from XML resource
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // Listen for changes in language preference
            ListPreference languagePref = findPreference("language_selector");
            if (languagePref != null) {
                languagePref.setOnPreferenceChangeListener((preference, newValue) -> {
                    // Change the app language
                    String selectedLanguage = newValue.toString();
                    setAppLanguage(requireContext(), selectedLanguage);

                    // Use getContext() to obtain the current context
                    if (getContext() != null) {
                        setAppLanguage(getContext(), selectedLanguage);

                        // Restart the activity to apply the language change
                        if (getActivity() != null) {
                            getActivity().recreate();
                        }
                    }
                    return true;
                });
            }

        }

        /**
         * Directly sets the app language based on the selected language code.
         * @param context The application context.
         * @param languageCode The language code (e.g., "en", "haw", "ton").
         */
        private void setAppLanguage(Context context, String languageCode) {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            Resources resources = context.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Close the activity and go back to the previous screen
        return true;
    }
}
