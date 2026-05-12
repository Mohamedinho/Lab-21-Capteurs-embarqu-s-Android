/**
 * Auteur : Mohamed Douassi
 * Description : Fragment affichant la liste exhaustive de tous les capteurs matériels disponibles.
 */
package ensa.ma.sensors.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ensa.ma.sensors.utils.SensorFormatter;

import java.util.List;

public class SensorsListFragment extends Fragment {

    private SensorManager sensorManager;
    private LinearLayout container;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        // Utilisation d'un ScrollView pour permettre le défilement si la liste est longue
        ScrollView scrollView = new ScrollView(requireContext());

        // Conteneur vertical pour les TextView de chaque capteur
        container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(24, 24, 24, 24);

        scrollView.addView(container);

        // Initialisation du service système pour l'accès aux capteurs
        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        // Déclenchement de la récupération et de l'affichage
        displaySensors();

        return scrollView;
    }

    /**
     * Récupère tous les capteurs et les injecte dynamiquement dans la vue.
     */
    private void displaySensors() {
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : sensors) {
            // Création d'un bloc texte pour chaque capteur
            TextView textView = new TextView(requireContext());
            textView.setText(SensorFormatter.format(sensor));
            textView.setTextSize(14);
            textView.setPadding(16, 16, 16, 16);

            container.addView(textView);

            // Ajout d'une ligne de séparation graphique pour la lisibilité
            View separator = new View(requireContext());
            separator.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            2));

            separator.setBackgroundColor(0xFFE0E0E0);
            container.addView(separator);
        }
    }
}