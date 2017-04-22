package com.contaminacion.madrid.contaminacionmadrid;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import api.main.ReadFiles;

public class RankingActivity extends AppCompatActivity {

    final String[] meses = new String[] {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};
    Integer[] celda_category = new Integer[] {R.id.category1, R.id.category2, R.id.category3, R.id.category4, R.id.category5, R.id.category6, R.id.category7, R.id.category8, R.id.category9, R.id.category10, R.id.category11, R.id.category12, R.id.category13, R.id.category14, R.id.category15, R.id.category16, R.id.category17, R.id.category18, R.id.category19, R.id.category20, R.id.category21, R.id.category22, R.id.category23};
    Integer[] estacion_ranking = new Integer[] {R.id.estacion1, R.id.estacion2, R.id.estacion3, R.id.estacion4, R.id.estacion5, R.id.estacion6, R.id.estacion7, R.id.estacion8, R.id.estacion9, R.id.estacion10, R.id.estacion11, R.id.estacion12, R.id.estacion13, R.id.estacion14, R.id.estacion15, R.id.estacion16, R.id.estacion17, R.id.estacion18, R.id.estacion19, R.id.estacion20, R.id.estacion21, R.id.estacion22, R.id.estacion23};
    final String[] estaciones = new String[]{};
    Map<String, Float> estacion_sum_contaminante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
    }

    public void getRanking(View view){
        final Spinner spinnerContRanking = (Spinner) findViewById(R.id.spinnerContRanking);
        final String contaminante_selected = spinnerContRanking.getSelectedItem().toString();

        final TextView[] tag_celdas = new TextView[23];
        final TextView[] datos_celdas = new TextView[23];
        for(int i=0;i<23;i++){
            tag_celdas[i] = new TextView(this);
            datos_celdas[i] = new TextView(this);
        }


        //Leemos los datos de la web del ayuntamiento de Madrid
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Resources res = getResources();
                    InputStream estacionesjson = res.openRawResource(R.raw.estaciones);
                    InputStream magnitudesjson = res.openRawResource(R.raw.magnitudes);
                    InputStream downloadsjson = res.openRawResource(R.raw.downloads);

                    ReadFiles file_read = new ReadFiles(downloadsjson, meses[Calendar.getInstance().get(Calendar.MONTH)], Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
                    file_read.read(estacionesjson, magnitudesjson);

                    estacion_sum_contaminante = file_read.valoresDiaContaminante(contaminante_selected);



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        //SystemClock.sleep(500);
        try {
            thread.join();

            Log.d("myTag", "Tamaño lista: " + estacion_sum_contaminante.size());
            Iterator itr = estacion_sum_contaminante.keySet().iterator();
            //while(itr.hasNext()) {
            for(int rank = 0; rank < 23; rank++){
                LinearLayout celda1 = (LinearLayout)findViewById(celda_category[rank]);
                LinearLayout celda2 = (LinearLayout)findViewById(estacion_ranking[rank]);

                celda1.removeAllViews();
                celda2.removeAllViews();
                if(rank < estacion_sum_contaminante.size()){
                    Object estacion = itr.next();
                    float valor = estacion_sum_contaminante.get(estacion);
                    Log.d("myTag", "Estacion: " + estacion);
                    Log.d("myTag", "Suma: " + valor);

                    tag_celdas[rank].setText("#"+(rank+1));
                    celda1.addView(tag_celdas[rank]);
                    tag_celdas[rank].setTypeface(Typeface.DEFAULT_BOLD);
                    tag_celdas[rank].setTextSize(21);

                    datos_celdas[rank].setText((String)estacion);
                    celda2.addView(datos_celdas[rank]);
                    datos_celdas[rank].setTextSize(21);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
