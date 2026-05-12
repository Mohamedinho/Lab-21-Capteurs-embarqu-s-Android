/**
 * Auteur : Mohamed Douassi
 * Description : Fragment polyvalent pour l'affichage graphique de capteurs à valeur unique 
 * (Température, Humidité, Proximité) ou calculée (Champ magnétique).
 * Inclut un moteur de simulation pour les tests sur émulateur.
 */
package ensa.ma.sensors.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ensa.ma.sensors.views.LineChartView;

public class SensorGraphFragment extends Fragment implements SensorEventListener {

    // Clés pour les arguments du fragment
    private static final String ARG_SENSOR_TYPE = "sensor_type";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MODE = "mode";

    private SensorManager sensorManager;
    private Sensor sensor;

    private TextView valueView;
    private LineChartView chartView;

    private int sensorType;
    private String title;
    private String mode;

    // Gestion de la simulation logicielle
    private final Handler simulationHandler = new Handler(Looper.getMainLooper());
    private float simulationTime = 0f;

    /**
     * Méthode statique pour instancier le fragment avec des paramètres.
     */
    public static SensorGraphFragment newInstance(
            int sensorType,
            String title,
            String mode) {

        SensorGraphFragment fragment = new SensorGraphFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SENSOR_TYPE, sensorType);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MODE, mode);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        // Récupération des paramètres passés lors de la création
        sensorType = requireArguments().getInt(ARG_SENSOR_TYPE);
        title = requireArguments().getString(ARG_TITLE);
        mode = requireArguments().getString(ARG_MODE);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(sensorType);

        // Construction dynamique de l'interface utilisateur
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        TextView titleView = new TextView(requireContext());
        titleView.setText(title);
        titleView.setTextSize(22);
        titleView.setPadding(0, 0, 0, 20);

        valueView = new TextView(requireContext());
        valueView.setTextSize(18);
        valueView.setPadding(0, 0, 0, 20);

        // Initialisation de notre graphique personnalisé
        chartView = new LineChartView(requireContext());
        chartView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        600));

        layout.addView(titleView);
        layout.addView(valueView);
        layout.addView(chartView);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Démarrage de l'écoute du capteur ou de la simulation
        if (sensor != null) {
            sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            valueView.setText("Capteur indisponible. Simulation activée.");
            startSimulation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Libération des ressources pour économiser la batterie
        sensorManager.unregisterListener(this);
        simulationHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Traitement de la donnée reçue
        float value = extractValue(event.values);
        updateUi(value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Extrait la valeur d'intérêt selon le mode (Magnitude pour champ magnétique ou valeur brute).
     */
    private float extractValue(float[] values) {
        if ("MAGNITUDE".equals(mode)) {
            // Calcul de la norme du vecteur pour les capteurs 3 axes
            return (float) Math.sqrt(
                    values[0] * values[0]
                            + values[1] * values[1]
                            + values[2] * values[2]);
        }
        return values[0]; // Première valeur pour les capteurs scalaires
    }

    /**
     * Met à jour l'affichage texte et le graphique.
     */
    private void updateUi(float value) {
        valueView.setText("Valeur détectée : " + value);
        chartView.addValue(value);
    }

    /**
     * Moteur de simulation pour pallier l'absence de capteurs réels sur certains dispositifs.
     */
    private void startSimulation() {
        simulationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                simulationTime++;
                float value;

                // Génération de données fictives cohérentes selon le type de capteur
                if (sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    value = 24f + (float) Math.sin(simulationTime / 5f) * 3f;
                } else if (sensorType == Sensor.TYPE_RELATIVE_HUMIDITY) {
                    value = 55f + (float) Math.sin(simulationTime / 7f) * 15f;
                } else if (sensorType == Sensor.TYPE_PROXIMITY) {
                    value = simulationTime % 6 < 3 ? 0f : 5f;
                } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                    value = 45f + (float) Math.sin(simulationTime / 4f) * 10f;
                } else {
                    value = (float) Math.sin(simulationTime);
                }

                updateUi(value);
                simulationHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}