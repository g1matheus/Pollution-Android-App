package com.contaminacion.madrid.contaminacionmadrid;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import api.main.ReadFiles;

public class SportsActivity extends AppCompatActivity {

    final String[] meses = new String[] {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};
    private int numMagnitudesAlmacenadas = 0;
    private float[] limites = new float[] {180, 120};;
    Float[] datos_cont_Retiro = new Float[2];
    Float[] datos_cont_Casa_Campo = new Float[2];
    Float[] datos_cont_Juan_Carlos_I = new Float[2];
    int hora_seleccionada;
    Float porcentaje_Retiro, porcentaje_Casa_Campo, porcentaje_Juan_Carlos_I;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);
    }

    public void getParkSport(View view){
        numMagnitudesAlmacenadas = 0;
        datos_cont_Retiro[0]=0F; datos_cont_Retiro[1]=0F;
        datos_cont_Juan_Carlos_I[0]=0F; datos_cont_Juan_Carlos_I[1]=0F;
        datos_cont_Casa_Campo[0]=0F; datos_cont_Casa_Campo[1]=0F;
        porcentaje_Retiro=0F; porcentaje_Casa_Campo=0F; porcentaje_Juan_Carlos_I=0F;
        final TextView parque = new TextView(this);
        final TextView porcentaje = new TextView(this);
        final TextView bandera = new TextView(this);
        final TextView mensaje = new TextView(this);
        String parque_select = "";
        Float porcentaje_select = 0F;

        if(Integer.valueOf(Calendar.getInstance().get(Calendar.MINUTE)) >30){
            hora_seleccionada = Integer.valueOf(Calendar.getInstance().get(Calendar.HOUR));
        } else{
            hora_seleccionada = Integer.valueOf(Calendar.getInstance().get(Calendar.HOUR))-1;
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

                    ArrayList<ReadFiles.MagnitudInfo> list_magnitud_Retiro = file_read.estacion_map.get("Retiro");

                    for(int i=0; i<list_magnitud_Retiro.size();i++){
                        if(String.valueOf(Integer.parseInt(list_magnitud_Retiro.get(i).getDay())).equals(Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))){
                            switch (list_magnitud_Retiro.get(i).getMagnitud()){
                                case "Dióxido de Nitrógeno":
                                    datos_cont_Retiro[0] = Float.valueOf(Float.parseFloat(list_magnitud_Retiro.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                case "Ozono":
                                    datos_cont_Retiro[1] = Float.valueOf(Float.parseFloat(list_magnitud_Retiro.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                default:
                                    //do nothing
                            }
                        }
                    }

                    ArrayList<ReadFiles.MagnitudInfo> list_magnitud_Juan_Carlos_I = file_read.estacion_map.get("Parque Juan Carlos I");

                    for(int i=0; i<list_magnitud_Juan_Carlos_I.size();i++){
                        if(String.valueOf(Integer.parseInt(list_magnitud_Juan_Carlos_I.get(i).getDay())).equals(Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))){
                            switch (list_magnitud_Juan_Carlos_I.get(i).getMagnitud()){
                                case "Dióxido de Nitrógeno":
                                    datos_cont_Juan_Carlos_I[0] = Float.valueOf(Float.parseFloat(list_magnitud_Juan_Carlos_I.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                case "Ozono":
                                    datos_cont_Juan_Carlos_I[1] = Float.valueOf(Float.parseFloat(list_magnitud_Juan_Carlos_I.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                default:
                                    //do nothing
                            }
                        }
                    }

                    ArrayList<ReadFiles.MagnitudInfo> list_magnitud_Casa_Campo = file_read.estacion_map.get("Casa de Campo");

                    for(int i=0; i<list_magnitud_Casa_Campo.size();i++){
                        if(String.valueOf(Integer.parseInt(list_magnitud_Casa_Campo.get(i).getDay())).equals(Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))){
                            switch (list_magnitud_Casa_Campo.get(i).getMagnitud()){
                                case "Dióxido de Nitrógeno":
                                    datos_cont_Casa_Campo[0] = Float.valueOf(Float.parseFloat(list_magnitud_Casa_Campo.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                case "Ozono":
                                    datos_cont_Casa_Campo[1] = Float.valueOf(Float.parseFloat(list_magnitud_Casa_Campo.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                default:
                                    //do nothing
                            }
                        }
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

            LinearLayout celda_parque = (LinearLayout)findViewById(R.id.park);
            LinearLayout celda_porcentaje = (LinearLayout)findViewById(R.id.porcentaje);
            LinearLayout celda_flag = (LinearLayout)findViewById(R.id.flag);
            LinearLayout celda_mensaje = (LinearLayout)findViewById(R.id.mensaje);

            celda_parque.removeAllViews();
            celda_porcentaje.removeAllViews();
            celda_flag.removeAllViews();
            celda_mensaje.removeAllViews();


            porcentaje_Retiro = (datos_cont_Retiro[0]/limites[0])*50 + (datos_cont_Retiro[1]/limites[1])*50;
            porcentaje_Casa_Campo = (datos_cont_Casa_Campo[0]/limites[0])*50 + (datos_cont_Casa_Campo[1]/limites[1])*50;
            porcentaje_Juan_Carlos_I = (datos_cont_Juan_Carlos_I[0]/limites[0])*50 + (datos_cont_Juan_Carlos_I[1]/limites[1])*50;

            //Selección de parque
            if(porcentaje_Retiro>=porcentaje_Casa_Campo && porcentaje_Retiro>porcentaje_Juan_Carlos_I){
                parque.setText("Parque del Retiro");
                parque_select = "Parque del Retiro";
                parque.setTextSize(17);
                celda_parque.addView(parque);
            }
            else if(porcentaje_Casa_Campo>=porcentaje_Juan_Carlos_I && porcentaje_Casa_Campo>porcentaje_Retiro){
                parque.setText("Casa de Campo");
                parque_select = "Casa de Campo";
                parque.setTextSize(17);
                celda_parque.addView(parque);
            }
            else if(porcentaje_Juan_Carlos_I>=porcentaje_Retiro && porcentaje_Juan_Carlos_I>porcentaje_Casa_Campo){
                parque.setText("Parque Juan Carlos I");
                parque_select = "Parque Juan Carlos I";
                parque.setTextSize(17);
                celda_parque.addView(parque);
            }

            //Pintamos el porcentaje
            if(parque_select.equals("Parque del Retiro")){
                porcentaje.setText("Nivel de contaminación: " + String.format("%.1f", porcentaje_Retiro)+" %");
                porcentaje_select = porcentaje_Retiro;
                porcentaje.setTextSize(17);
                celda_porcentaje.addView(porcentaje);
            }
            else if(parque_select.equals("Casa de Campo")){
                porcentaje.setText("Nivel de contaminación: " + String.format("%.1f", porcentaje_Casa_Campo)+" %");
                porcentaje_select = porcentaje_Casa_Campo;
                porcentaje.setTextSize(17);
                celda_porcentaje.addView(porcentaje);
            }
            else if(parque_select.equals("Parque Juan Carlos I")){
                porcentaje.setText("Nivel de contaminación: " + String.format("%.1f", porcentaje_Juan_Carlos_I)+" %");
                porcentaje_select = porcentaje_Juan_Carlos_I;
                porcentaje.setTextSize(17);
                celda_porcentaje.addView(porcentaje);
            }

            //Pintamos la bandera y el mensaje
            if(porcentaje_select<50){
                bandera.setBackgroundColor(Color.parseColor("#7ec051"));
                celda_flag.addView(bandera);
                mensaje.setText("Contaminación baja. Buen día para salir a hacer deporte!!");
                mensaje.setTextSize(17);
                celda_mensaje.addView(mensaje);
            }
            else if(porcentaje_select>=50 && porcentaje_select<100){
                bandera.setBackgroundColor(Color.parseColor("#fcc963"));
                celda_flag.addView(bandera);
                mensaje.setText("Contaminación moderada. Hacer deporte con precaución.");
                mensaje.setTextSize(17);
                celda_mensaje.addView(mensaje);
            }
            else if(porcentaje_select >= 100){
                bandera.setBackgroundColor(Color.parseColor("#fb3c2e"));
                celda_flag.addView(bandera);
                mensaje.setText("Contaminación alta. Mejor ir al gimnasio.");
                mensaje.setTextSize(17);
                celda_mensaje.addView(mensaje);
            }

            // Para poner la imagen final
            TextView imagen = (TextView)findViewById(R.id.img_park);
            if(parque_select.equals("Parque del Retiro")){
                imagen.setBackgroundResource(R.drawable.retiro);
            }
            else if(parque_select.equals("Casa de Campo")){
                imagen.setBackgroundResource(R.drawable.casacampo);
            }
            else if(parque_select.equals("Parque Juan Carlos I")){
                imagen.setBackgroundResource(R.drawable.juancarlosi);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}