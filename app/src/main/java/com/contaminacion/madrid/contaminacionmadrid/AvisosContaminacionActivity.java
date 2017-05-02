package com.contaminacion.madrid.contaminacionmadrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import api.main.ReadFiles;

public class AvisosContaminacionActivity extends AppCompatActivity {

    final String[] meses = new String[] {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};
    Integer[] celda_estacion = new Integer[] {R.id.estacion1, R.id.estacion2, R.id.estacion3, R.id.estacion4, R.id.estacion5, R.id.estacion6, R.id.estacion7, R.id.estacion8, R.id.estacion9, R.id.estacion10, R.id.estacion11, R.id.estacion12, R.id.estacion13, R.id.estacion14, R.id.estacion15, R.id.estacion16, R.id.estacion17, R.id.estacion18, R.id.estacion19, R.id.estacion20, R.id.estacion21, R.id.estacion22, R.id.estacion23, R.id.estacion24};
    Integer[] flag_estacion = new Integer[] {R.id.flag_estacion1, R.id.flag_estacion2, R.id.flag_estacion3, R.id.flag_estacion4, R.id.flag_estacion5, R.id.flag_estacion6, R.id.flag_estacion7, R.id.flag_estacion8, R.id.flag_estacion9, R.id.flag_estacion10, R.id.flag_estacion11, R.id.flag_estacion12, R.id.flag_estacion13, R.id.flag_estacion14, R.id.flag_estacion15, R.id.flag_estacion16, R.id.flag_estacion17, R.id.flag_estacion18, R.id.flag_estacion19, R.id.flag_estacion20, R.id.flag_estacion21, R.id.flag_estacion22, R.id.flag_estacion23, R.id.flag_estacion24};
    String[] colores = new String[24];
    int contador_verde, contador_naranja, contador_rojo;
    Activity avisos = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avisos_contaminacion);
    }

    public void getContaminacionNO2(View view){
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
                    contador_verde = 0;
                    contador_naranja = 0;
                    contador_rojo = 0;

                    for(int i=0; i<24;i++){
                        TextView estacion = (TextView) findViewById(celda_estacion[i]);
                        ArrayList<String> valuesNO2Estacion = file_read.getValuesEstacionNO2(estacion.getText().toString());
                        Log.d("myTag", "Estación: "+estacion.getText().toString()+" Datos: "+valuesNO2Estacion);
                        boolean flag_anterior = false;
                        boolean flag_rojo = false;
                        for(int h=0; h<24;h++){
                            int value = Integer.parseInt(valuesNO2Estacion.get(h));
                            boolean value_isValid = file_read.getValidatorsEstacionNO2(estacion.getText().toString()).get(h);
                            if(value_isValid && value >= 180 && flag_anterior){
                                colores[i] = "Rojo";
                                flag_rojo = true;
                            }
                            else if(!flag_rojo && value_isValid && value >= 180 && !flag_anterior){
                                flag_anterior = true;
                                colores[i] = "Amarillo";
                            }
                            else if(!flag_rojo && value_isValid && value < 180){
                                colores[i] = "Verde";
                                flag_anterior = false;
                            }
                        }
                        Log.d("myTag", "Color: "+i +" " +colores[i]);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        //SystemClock.sleep(500);
        try {
            thread.join();

            for(int i = 0; i < 24; i++) {
                LinearLayout celda_n = (LinearLayout) findViewById(flag_estacion[i]);
                //celda_n.removeAllViews();
                //celda_n.setBackgroundResource(R.drawable.border);
                //GradientDrawable drawable = (GradientDrawable) celda_n.getBackground();

                if(colores[i].equals("Verde")){
                    Log.d("myTag", "Es verde");
                    celda_n.setBackgroundColor(Color.parseColor("#7ec051"));
                    contador_verde++;
                    celda_n.setOnClickListener(onClickListenerGreen);
                    //celda_n.setBackgroundColor(Color.parseColor("#5fb5fb"));
                }
                else if(colores[i].equals("Amarillo")){
                    Log.d("myTag", "Es amarillo");
                    celda_n.setBackgroundColor(Color.parseColor("#fcc963"));
                    contador_naranja++;
                    celda_n.setOnClickListener(onClickListenerOrange);
                    //drawable.setColor(Color.parseColor("#fcc963"));
                    //celda_n.setBackgroundColor(Color.parseColor("#fcc963"));
                }
                else if(colores[i].equals("Rojo")){
                    Log.d("myTag", "Es rojo");
                    celda_n.setBackgroundColor(Color.parseColor("#fb3c2e"));
                    contador_rojo++;
                    celda_n.setOnClickListener(onClickListenerRed);
                    //celda_n.setBackgroundColor(Color.parseColor("#fb3c2e"));
                }
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    private View.OnClickListener onClickListenerGreen = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AvisosContaminacionActivity.this);

            builder.setMessage("Esta estación no ha superado el limite máximo de contaminación.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder.create();
            builder.show();
        }
    };


    private View.OnClickListener onClickListenerOrange = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AvisosContaminacionActivity.this);

            builder.setMessage("Esta estación supera el límite de contaminación.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder.create();
            builder.show();
        }
    };


    private View.OnClickListener onClickListenerRed = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AvisosContaminacionActivity.this);

            builder.setMessage("Esta estación ha superado el límite de contaminación durante 2 o más horas seguidas.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder.create();
            builder.show();
        }
    };

}
