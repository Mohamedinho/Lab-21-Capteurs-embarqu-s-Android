/**
 * Auteur : Mohamed Douassi
 * Description : Utilitaire pour le formatage des données techniques des capteurs.
 * Ce code transforme les propriétés d'un objet Sensor en une chaîne de caractères lisible.
 */
package ensa.ma.sensors.utils;

import android.hardware.Sensor;

public class SensorFormatter {

    /**
     * Formate les informations d'un capteur sous forme de texte.
     * @param sensor Le capteur à formater.
     * @return Une chaîne de caractères contenant les détails techniques.
     */
    public static String format(Sensor sensor) {
        // Construction de la chaîne avec les métadonnées et caractéristiques physiques
        return "Id : " + sensor.getId() + "\n"
                + "Name : " + sensor.getName() + "\n"
                + "Vendor : " + sensor.getVendor() + "\n"
                + "Version : " + sensor.getVersion() + "\n"
                + "Type : " + sensor.getStringType() + "\n"
                + "Int Type : " + sensor.getType() + "\n" // Type entier utilisé pour l'identification système
                + "Resolution : " + sensor.getResolution() + "\n"
                + "Power : " + sensor.getPower() + " mA\n" // Consommation électrique
                + "Maximum Range : " + sensor.getMaximumRange() + "\n"
                + "Min Delay : " + sensor.getMinDelay() + " µs\n"; // Délai minimal entre deux mesures
    }
}