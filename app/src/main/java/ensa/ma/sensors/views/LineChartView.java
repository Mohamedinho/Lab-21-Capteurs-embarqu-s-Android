/**
 * Auteur : Mohamed Douassi
 * Description : Composant graphique personnalisé pour dessiner des courbes de données.
 * Développé sans bibliothèque externe pour optimiser les performances.
 */
package ensa.ma.sensors.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LineChartView extends View {

    // Liste des points de données à afficher
    private final List<Float> values = new ArrayList<>();
    private final int maxPoints = 80; // Limite de points pour éviter la surcharge graphique

    // Définition des pinceaux pour le dessin
    private final Paint axisPaint = new Paint();
    private final Paint linePaint = new Paint();
    private final Paint textPaint = new Paint();

    public LineChartView(Context context) {
        super(context);

        // Configuration des axes (couleur grise)
        axisPaint.setColor(Color.LTGRAY);
        axisPaint.setStrokeWidth(3);

        // Configuration de la ligne de données (couleur bleue Android)
        linePaint.setColor(Color.rgb(33, 150, 243));
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.STROKE);

        // Configuration du texte (légendes Min/Max)
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(30);
    }

    /**
     * Ajoute une nouvelle valeur et déclenche la mise à jour du graphique.
     */
    public void addValue(float value) {
        if (values.size() >= maxPoints) {
            values.remove(0); // Supprime le plus vieux point (principe de FIFO)
        }

        values.add(value);
        invalidate(); // Demande à Android de redessiner la vue
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Dessin des axes X et Y
        canvas.drawLine(40, height - 40, width - 20, height - 40, axisPaint);
        canvas.drawLine(40, 20, 40, height - 40, axisPaint);

        // Message d'attente si pas assez de données
        if (values.size() < 2) {
            canvas.drawText("En attente des données...", 60, height / 2, textPaint);
            return;
        }

        // Recherche des bornes Min et Max pour normaliser le graphique
        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;

        for (float value : values) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        // Empêche la division par zéro si toutes les valeurs sont identiques
        if (max == min) {
            max = min + 1;
        }

        // Création du chemin (Path) reliant tous les points
        Path path = new Path();

        for (int i = 0; i < values.size(); i++) {
            // Calcul des coordonnées X et Y normalisées par rapport à la taille de la vue
            float x = 40 + i * ((width - 80f) / (maxPoints - 1));
            float normalizedValue = (values.get(i) - min) / (max - min);
            float y = height - 40 - normalizedValue * (height - 80);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        // Dessin effectif de la courbe et affichage des légendes
        canvas.drawPath(path, linePaint);
        canvas.drawText("Min : " + min + " | Max : " + max, 60, 40, textPaint);
    }
}