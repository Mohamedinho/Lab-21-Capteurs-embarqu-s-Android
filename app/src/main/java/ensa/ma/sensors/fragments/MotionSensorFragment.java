/**
 * Auteur : Mohamed Douassi
 * Description : Fragment dédié aux capteurs de mouvement (Accéléromètre, Gravité, Gyroscope).
 * Calcule et affiche les composantes X, Y, Z ainsi que la norme du vecteur résultant.
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ensa.ma.sensors.views.LineChartView;

public class MotionSensorFragment extends Fragment implements SensorEventListener {

    private static final String ARG_SENSOR_TYPE = "sensor_type";
    private static final String ARG_TITLE = "title";

    private SensorManager sensorManager;
    private Sensor sensor;

    private TextView valuesView;
    private LineChartView chartView;

    private int sensorType;
    private String title;

    /**
     * Instanciation du fragment avec configuration du type de capteur de mouvement.
     */
    public static MotionSensorFragment newInstance(int sensorType, String title) {
        MotionSensorFragment fragment = new MotionSensorFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SENSOR_TYPE, sensorType);
        args.putString(ARG_TITLE, title);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        sensorType = requireArguments().getInt(ARG_SENSOR_TYPE);
        title = requireArguments().getString(ARG_TITLE);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(sensorType);

        // Organisation de l'interface en colonnes (Vertical)
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        TextView titleView = new TextView(requireContext());
        titleView.setText(title);
        titleView.setTextSize(22);

        valuesView = new TextView(requireContext());
        valuesView.setTextSize(18);
        valuesView.setPadding(0, 24, 0, 24);

        chartView = new LineChartView(requireContext());
        chartView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        600));

        layout.addView(titleView);
        layout.addView(valuesView);
        layout.addView(chartView);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Enregistrement de l'écouteur si le capteur est présent
        if (sensor != null) {
            sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            valuesView.setText("Capteur indisponible sur ce dispositif.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Désactivation pour optimiser la consommation
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Récupération des trois axes
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Calcul de la norme du mouvement (Intensité globale)
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

        valuesView.setText(
                "X : " + x + "\n"
                        + "Y : " + y + "\n"
                        + "Z : " + z + "\n"
                        + "Norme : " + magnitude);

        // Mise à jour du graphique avec la norme
        chartView.addValue(magnitude);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}