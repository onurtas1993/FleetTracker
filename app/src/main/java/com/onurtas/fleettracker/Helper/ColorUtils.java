package com.onurtas.fleettracker.Helper;

import android.graphics.Color;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class ColorUtils {

    /**
     * Assigns a color to the CircularProgressBar based on a score value (0-100).
     * The color transitions from Red (0) through Yellow (50) to Green (100).
     *
     * @param progressBar The CircularProgressBar to color.
     * @param score       The score value (expected to be between 0 and 100).
     */
    public static void assignScoreColorToProgressBar(CircularProgressBar progressBar, int score) {
        // Clamp the score to be within the 0-100 range
        score = Math.max(0, Math.min(100, score));

        int redValue;
        int greenValue;
        int blueValue = 0; // Keep blue at 0 for Red-Yellow-Green spectrum

        if (score <= 50) {
            // From Red (255,0,0) to Yellow (255,255,0)
            // Red stays full, Green increases
            float fraction = score / 50.0f;
            redValue = 255;
            greenValue = (int) (fraction * 255);
        } else {
            // From Yellow (255,255,0) to Green (0,255,0)
            // Green stays full, Red decreases
            float fraction = (score - 50) / 50.0f; // fraction from 0 to 1 for this range
            redValue = (int) (255 * (1 - fraction));
            greenValue = 255;
        }

        int dynamicColor = Color.rgb(redValue, greenValue, blueValue);
        progressBar.setProgressBarColor(dynamicColor);
    }

}