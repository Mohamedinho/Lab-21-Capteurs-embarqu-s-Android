/**
 * Auteur : Mohamed Douassi
 * Description : Activité principale gérant la navigation et le cycle de vie de l'application.
 * Elle assure le lien entre le menu latéral et les différents fragments de capteurs.
 */
package ensa.ma.sensors;

import android.hardware.Sensor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import ensa.ma.sensors.fragments.ActivityRecognitionFragment;
import ensa.ma.sensors.fragments.CompassFragment;
import ensa.ma.sensors.fragments.MotionSensorFragment;
import ensa.ma.sensors.fragments.SensorGraphFragment;
import ensa.ma.sensors.fragments.SensorsListFragment;
import ensa.ma.sensors.fragments.StepCounterFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Configuration de la barre d'outils (Toolbar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Configuration du bouton d'action flottant (FAB)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Système de capteurs opérationnel", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Mise en place du tiroir de navigation (Navigation Drawer)
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        
        // Définition des destinations de haut niveau pour la barre d'application
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.menu_sensors, R.id.menu_temperature, R.id.menu_humidity, R.id.menu_proximity, 
                R.id.menu_magnetic, R.id.menu_accelerometer, R.id.menu_gravity, R.id.menu_gyroscope,
                R.id.menu_steps, R.id.menu_compass, R.id.menu_activity)
                .setDrawerLayout(drawer)
                .build();
        
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        
        // Attribution de l'écouteur de sélection du menu
        navigationView.setNavigationItemSelectedListener(this);
        
        // Affichage du fragment de la liste des capteurs par défaut au démarrage
        if (savedInstanceState == null) {
            openFragment(new SensorsListFragment());
        }
    }

    /**
     * Gère la transaction pour remplacer le fragment actuel dans le conteneur.
     * @param fragment Le nouveau fragment à afficher.
     */
    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }

    /**
     * Intercepte les clics sur les items du menu de navigation.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Routage vers le fragment approprié selon l'ID du menu sélectionné
        if (id == R.id.menu_sensors) {
            openFragment(new SensorsListFragment());
        } else if (id == R.id.menu_temperature) {
            openFragment(SensorGraphFragment.newInstance(Sensor.TYPE_AMBIENT_TEMPERATURE, "Température ambiante", "FIRST_VALUE"));
        } else if (id == R.id.menu_humidity) {
            openFragment(SensorGraphFragment.newInstance(Sensor.TYPE_RELATIVE_HUMIDITY, "Humidité relative", "FIRST_VALUE"));
        } else if (id == R.id.menu_proximity) {
            openFragment(SensorGraphFragment.newInstance(Sensor.TYPE_PROXIMITY, "Capteur de proximité", "FIRST_VALUE"));
        } else if (id == R.id.menu_magnetic) {
            openFragment(SensorGraphFragment.newInstance(Sensor.TYPE_MAGNETIC_FIELD, "Champ magnétique", "MAGNITUDE"));
        } else if (id == R.id.menu_accelerometer) {
            openFragment(MotionSensorFragment.newInstance(Sensor.TYPE_ACCELEROMETER, "Accéléromètre : x, y, z"));
        } else if (id == R.id.menu_gravity) {
            openFragment(MotionSensorFragment.newInstance(Sensor.TYPE_GRAVITY, "Gravité : x, y, z"));
        } else if (id == R.id.menu_gyroscope) {
            openFragment(MotionSensorFragment.newInstance(Sensor.TYPE_GYROSCOPE, "Gyroscope : rad/s"));
        } else if (id == R.id.menu_steps) {
            openFragment(new StepCounterFragment());
        } else if (id == R.id.menu_compass) {
            openFragment(new CompassFragment());
        } else if (id == R.id.menu_activity) {
            openFragment(new ActivityRecognitionFragment());
        }

        // Fermeture automatique du tiroir après sélection
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}