package com.contaminacion.madrid.contaminacionmadrid;

import android.content.res.Resources;
import android.graphics.Color;
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

import api.main.ReadFiles;


public class EPOCActivity extends AppCompatActivity {

    final String[] meses = new String[] {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};
    Integer[] flag_contX = new Integer[] {R.id.flag_cont1, R.id.flag_cont2, R.id.flag_cont3, R.id.flag_cont4, R.id.flag_cont5};
    Integer[] percent_contX = new Integer[] {R.id.percent_cont1, R.id.percent_cont2, R.id.percent_cont3, R.id.percent_cont4, R.id.percent_cont5};
    Integer[] cont_X = new Integer[] {R.id.cont1, R.id.cont2, R.id.cont3, R.id.cont4, R.id.cont5};
    private int numMagnitudesAlmacenadas = 0;
    private float[] limites = new float[] {350, 180, 120, 25, 50};;
    Float[] datos_cont = new Float[5];
    int hora_seleccionada;
    int num_flags_red, num_flags_orange, num_flags_green;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epoc);
    }

    public void getEPOC(View view){
        final Spinner spinnerGradoEPOC = (Spinner) findViewById(R.id.spinnerGradoEPOC);
        final String gradoEPOC = spinnerGradoEPOC.getSelectedItem().toString();

        final Spinner spinnerEstacion = (Spinner) findViewById(R.id.spinnerEstacion);
        final String station_selected = spinnerEstacion.getSelectedItem().toString();

        numMagnitudesAlmacenadas = 0;
        num_flags_red = 0;
        num_flags_orange = 0;
        num_flags_green = 0;

        final TextView[] flag_celdas = new TextView[5];
        final TextView[] percent_celdas = new TextView[5];
        final TextView[] cont_celdas = new TextView[5];
        final TextView resultado = new TextView(this);
        for(int i=0;i<5;i++){
            flag_celdas[i] = new TextView(this);
            percent_celdas[i] = new TextView(this);
            cont_celdas[i] = new TextView(this);
            datos_cont[i] = 0F;
        }
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
                    ArrayList<ReadFiles.MagnitudInfo> list_magnitud = file_read.estacion_map.get(station_selected);

                    for(int i=0; i<list_magnitud.size();i++){
                        if(String.valueOf(Integer.parseInt(list_magnitud.get(i).getDay())).equals(Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))){
                            numMagnitudesAlmacenadas++;
                            switch (list_magnitud.get(i).getMagnitud()){
                                case "Dióxido de Azufre":
                                    datos_cont[0] = Float.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                case "Dióxido de Nitrógeno":
                                    datos_cont[1] = Float.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                case "Ozono":
                                    datos_cont[2] = Float.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                case "Partículas 2.5 µm":
                                    datos_cont[3] = Float.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                case "Partículas 10 µm":
                                    datos_cont[4] = Float.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada)));
                                    break;
                                default:
                                    //do nothing
                            }
                        }
                    }
                    Log.d("myTag", "Lista de magnitudes almacenadas: " + numMagnitudesAlmacenadas);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        //SystemClock.sleep(500);
        try {
            thread.join();

            for(int cont = 0; cont < 5; cont++){
                LinearLayout celda_flag = (LinearLayout)findViewById(flag_contX[cont]);
                Log.d("myTag", "Flag: "+ flag_contX[cont]);
                LinearLayout celda_percent = (LinearLayout)findViewById(percent_contX[cont]);
                LinearLayout contX = (LinearLayout)findViewById(cont_X[cont]);
                LinearLayout celda_resultado = (LinearLayout)findViewById(R.id.text_resultado);

                celda_flag.removeAllViews();
                celda_percent.removeAllViews();

                Log.d("myTag", "datos_cont[cont]: "+ datos_cont[cont]);
                if(datos_cont[cont]!=0){

                    float value = datos_cont[cont];

                    float percent = (value/limites[cont])*100;
                    percent_celdas[cont].setText(String.format("%.1f", percent)+" %");
                    percent_celdas[cont].setTextSize(17);
                    celda_percent.addView(percent_celdas[cont]);

                    if (value < limites[cont]/2){
                        Log.d("myTag", "verde");
                        num_flags_green++;
                        //flag_celdas[cont].setBackgroundColor(Color.parseColor("#7ec051"));
                        celda_flag.setBackgroundColor(Color.parseColor("#7ec051"));
                    }
                    else if ((gradoEPOC.equals("Grado 1") && value < 0.9*limites[cont] && value >= limites[cont]/2)
                            || (gradoEPOC.equals("Grado 2") && value < 0.85*limites[cont] && value >= limites[cont]/2)
                            || (gradoEPOC.equals("Grado 3") && value < 0.8*limites[cont] && value >= limites[cont]/2)
                            || (gradoEPOC.equals("Grado 4") && value < 0.75*limites[cont] && value >= limites[cont]/2)){
                        Log.d("myTag", "amarillo");
                        num_flags_orange++;
                        celda_flag.setBackgroundColor(Color.parseColor("#fcc963"));
                        //flag_celdas[cont].setBackgroundColor(Color.parseColor("#fcc963"));
                    }
                    else if ((gradoEPOC.equals("Grado 1") && value >= 0.9*limites[cont])
                            || (gradoEPOC.equals("Grado 2") && value >= 0.85*limites[cont])
                            || (gradoEPOC.equals("Grado 3") && value >= 0.8*limites[cont])
                            || (gradoEPOC.equals("Grado 4") && value >= 0.75*limites[cont])){
                        Log.d("myTag", "rojo");
                        num_flags_red++;
                        celda_flag.setBackgroundColor(Color.parseColor("#fb3c2e"));
                        //flag_celdas[cont].setBackgroundColor(Color.parseColor("#fb3c2e"));
                    }
                    celda_flag.addView(flag_celdas[cont]);
                }else{
                    celda_flag.setBackgroundColor(Color.parseColor("#D9D9D9"));
                    celda_flag.addView(flag_celdas[cont]);
                    percent_celdas[cont].setText("-");
                    percent_celdas[cont].setTextSize(17);
                    celda_percent.addView(percent_celdas[cont]);
                }

                // Para poner el texto final
                if(num_flags_red == 0 && num_flags_orange < 2){
                    resultado.setText("Contaminación baja. Buen día para salir a la calle!!");
                }
                else if(num_flags_red == 0 && num_flags_orange >= 2){
                    resultado.setText("Contaminación moderada. Salir con precaución.");
                }
                else if(num_flags_red >= 1){
                    resultado.setText("Contaminación alta. Buen día para ver una peli en casa!!");
                }
                resultado.setTextSize(17);
                celda_resultado.removeAllViews();
                celda_resultado.addView(resultado);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}


