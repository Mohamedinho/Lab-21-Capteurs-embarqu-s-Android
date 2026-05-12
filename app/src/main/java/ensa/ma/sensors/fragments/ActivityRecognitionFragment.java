/**
 * Auteur : Mohamed Douassi
 * Description : Fragment de reconnaissance d'activité basé sur l'accéléromètre.
 * Utilise un filtre passe-bas pour isoler le mouvement linéaire et l'analyse statistique
 * (moyenne, écart-type) pour classifier l'activité de l'utilisateur.
 */
package ensa.ma.sensors.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.LinkedList;
import java.util.Queue;

public class ActivityRecognitionFragment extends Fragment
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView resultView;

    // Vecteur de gravité estimé pour le filtrage
    private final float[] gravity = new float[3];
    // Fenêtre glissante pour l'analyse temporelle du mouvement
    private final Queue<Float> movementWindow = new LinkedList<>();

    private static final int WINDOW_SIZE = 30; // Nombre d'échantillons pour la décision
    private static final float ALPHA = 0.8f;   // Facteur du filtre passe-bas

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        resultView = new TextView(requireContext());
        resultView.setTextSize(22);
        resultView.setPadding(24, 24, 24, 24);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        accelerometer =
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        return resultView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (accelerometer != null) {
            // Utilisation du délai GAME pour une meilleure réactivité d'analyse
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        } else {
            resultView.setText("Accéléromètre indisponible.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // --- FILTRAGE PASSE-BAS ---
        // Isole la composante constante (pesanteur)
        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

        // Soustrait la pesanteur pour obtenir l'accélération linéaire pure
        float linearX = x - gravity[0];
        float linearY = y - gravity[1];
        float linearZ = z - gravity[2];

        // Intensité instantanée du mouvement
        float movement = (float) Math.sqrt(
                linearX * linearX
                        + linearY * linearY
                        + linearZ * linearZ);

        addMovementValue(movement);

        // Classification basée sur l'analyse de la fenêtre de données
        String activity = classifyActivity(x, y, z);

        resultView.setText(
                "X : " + String.format("%.2f", x) + "\n"
                        + "Y : " + String.format("%.2f", y) + "\n"
                        + "Z : " + String.format("%.2f", z) + "\n\n"
                        + "Mouvement : " + String.format("%.2f", movement) + "\n\n"
                        + "Activité détectée : " + activity);
    }

    /**
     * Gère la fenêtre glissante des valeurs de mouvement.
     */
    private void addMovementValue(float movement) {
        if (movementWindow.size() >= WINDOW_SIZE) {
            movementWindow.poll();
        }
        movementWindow.add(movement);
    }

    /**
     * Logique de classification par seuillage heuristique.
     */
    private String classifyActivity(float x, float y, float z) {

        if (movementWindow.size() < WINDOW_SIZE) {
            return "Calibration en cours...";
        }

        float mean = 0f;
        float max = 0f;

        for (float value : movementWindow) {
            mean += value;
            max = Math.max(max, value);
        }

        mean = mean / movementWindow.size();

        // Calcul de la variance pour détecter l'agitation (marche)
        float variance = 0f;
        for (float value : movementWindow) {
            variance += (value - mean) * (value - mean);
        }
        variance = variance / movementWindow.size();
        float standardDeviation = (float) Math.sqrt(variance);

        // --- CRITÈRES DE DÉCISION ---
        if (max > 12f) {
            return "SAUT DÉTECTÉ";
        }

        if (standardDeviation > 1.2f) {
            return "MARCHE";
        }

        if (Math.abs(z) > 8.5f) {
            return "STABLE (À plat)";
        }

        if (Math.abs(y) > 7.5f || Math.abs(x) > 7.5f) {
            return "STABLE (Vertical/Incliné)";
        }

        return "POSITION STABLE";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}