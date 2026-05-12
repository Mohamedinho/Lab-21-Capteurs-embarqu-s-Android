/**
 * Auteur : Mohamed Douassi
 * Description : Fragment gérant le compteur de pas.
 * Utilise le capteur matériel TYPE_STEP_COUNTER pour suivre l'activité physique.
 */
package ensa.ma.sensors.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class StepCounterFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private TextView textView;

    // Mémorise la valeur initiale pour calculer les pas de la session
    private float initialSteps = -1;

    // Gestionnaire de demande de permission dynamique pour Android 10+
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            startSensor();
                        } else {
                            textView.setText("Permission d'activité physique refusée.");
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        // Affichage simple du texte pour les statistiques de pas
        textView = new TextView(requireContext());
        textView.setTextSize(22);
        textView.setPadding(24, 24, 24, 24);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        // Récupération du capteur spécifique au comptage de pas
        stepCounterSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        return textView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (stepCounterSensor == null) {
            textView.setText("Capteur de pas absent sur ce téléphone.");
            return;
        }

        // Vérification de la permission ACTIVITY_RECOGNITION pour les versions récentes d'Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {

            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
        } else {
            startSensor();
        }
    }

    /**
     * Active l'écouteur du capteur de pas.
     */
    private void startSensor() {
        sensorManager.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Arrêt impératif pour préserver l'autonomie du dispositif
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // La valeur brute correspond aux pas depuis le dernier boot
        float totalStepsSinceBoot = event.values[0];

        // Initialisation de la référence de session lors du premier événement
        if (initialSteps < 0) {
            initialSteps = totalStepsSinceBoot;
        }

        // Calcul de la différence
        int sessionSteps = (int) (totalStepsSinceBoot - initialSteps);

        textView.setText(
                "Total (depuis boot) : " + (int) totalStepsSinceBoot
                        + "\n\nPas de la session : " + sessionSteps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}