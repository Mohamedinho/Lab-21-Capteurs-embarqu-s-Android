/**
 * Auteur : Mohamed Douassi
 * Description : Fragment implémentant une boussole numérique.
 * Combine les données de l'accéléromètre et du magnétomètre pour calculer l'orientation.
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

public class CompassFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private TextView textView;

    // Tableaux pour stocker les mesures brutes
    private final float[] gravityValues = new float[3];
    private final float[] magneticValues = new float[3];

    private boolean hasGravity = false;
    private boolean hasMagnetic = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        textView = new TextView(requireContext());
        textView.setTextSize(22);
        textView.setPadding(24, 24, 24, 24);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        // La boussole nécessite deux capteurs distincts
        accelerometer =
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer =
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return textView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Enregistrement des deux écouteurs
        if (accelerometer != null) {
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI);
        }

        if (magnetometer != null) {
            sensorManager.registerListener(
                    this,
                    magnetometer,
                    SensorManager.SENSOR_DELAY_UI);
        }

        if (accelerometer == null || magnetometer == null) {
            textView.setText("Boussole indisponible : capteurs manquants.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Libération des capteurs
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Stockage des données selon le type de capteur reçu
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravityValues, 0, 3);
            hasGravity = true;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magneticValues, 0, 3);
            hasMagnetic = true;
        }

        // Calcul de l'orientation si les deux sources de données sont prêtes
        if (hasGravity && hasMagnetic) {
            float[] rotationMatrix = new float[9];
            float[] orientation = new float[3];

            // Obtention de la matrice de rotation mondiale
            boolean success = SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    gravityValues,
                    magneticValues);

            if (success) {
                // Extraction de l'azimut, du tangage et du roulis
                SensorManager.getOrientation(rotationMatrix, orientation);

                float azimuthRadians = orientation[0];
                float azimuthDegrees = (float) Math.toDegrees(azimuthRadians);

                // Normalisation de l'angle sur [0, 360]
                if (azimuthDegrees < 0) {
                    azimuthDegrees += 360;
                }

                textView.setText(
                        "Azimut : " + (int)azimuthDegrees + "°\n"
                                + "Direction : " + getDirectionName(azimuthDegrees));
            }
        }
    }

    /**
     * Convertit l'angle de l'azimut en nom de direction cardinale.
     */
    private String getDirectionName(float degree) {
        if (degree >= 337.5 || degree < 22.5) return "Nord";
        if (degree < 67.5) return "Nord-Est";
        if (degree < 112.5) return "Est";
        if (degree < 157.5) return "Sud-Est";
        if (degree < 202.5) return "Sud";
        if (degree < 247.5) return "Sud-Ouest";
        if (degree < 292.5) return "Ouest";
        return "Nord-Ouest";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}