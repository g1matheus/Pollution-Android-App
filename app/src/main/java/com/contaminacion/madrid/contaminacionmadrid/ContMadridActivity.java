package com.contaminacion.madrid.contaminacionmadrid;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.StepModel;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.io.InputStream;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;


import android.util.Log;
import android.widget.TableRow;
import android.widget.TextView;

import api.main.ReadFiles;

public class ContMadridActivity extends AppCompatActivity {

    private Spinner spinner1, spHoras;
    private XYPlot plot;
    private float max = 0;
    private int pos=0;
    private float minx = 0;
    private float maxx = 0;

    private float limit_SO2 = 350;
    private float limit_CO = 10;
    private float limit_NO2 = 200;
    private float limit_O3 = 120;
    private float limit_TOL = 10;
    private float limit_BEN = 5;
    private float limit_PM2_5 = 25;
    private float limit_PM10 = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont_madrid);

        final String[] horasDia = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};

        List<String> horasPasadas = new ArrayList<>();

        Calendar calendar = new GregorianCalendar();
        int horaActual =calendar.get(Calendar.HOUR_OF_DAY);

        //obtener una lista con las horas del día pasadas
        for (int i=0;i < horasDia.length;++i) {
            if (Integer.parseInt(horasDia[i]) <= horaActual){
                horasPasadas.add(horasDia[i]+":00");
            }
        }


        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, horasPasadas);

        spHoras = (Spinner)findViewById(R.id.horaMedida);
        spHoras.setAdapter(adapter);
        spHoras.setSelection(horasPasadas.size()-1);
    }


    /** Called when the user clicks the Contaminacion en Madrid button */
    public void buscarDatos(View view) {
        //Variables - Medidas contaminantes
        final TextView dato_SO2 = new TextView(this);
        final TextView dato_CO = new TextView(this);
        final TextView dato_NO2 = new TextView(this);
        final TextView dato_O3 = new TextView(this);
        final TextView dato_TOL = new TextView(this);
        final TextView dato_BEN = new TextView(this);
        final TextView dato_PM2_5 = new TextView(this);
        final TextView dato_PM10 = new TextView(this);

        //Guardamos la estación seleccionada
        final Spinner spinnerEstacion = (Spinner) findViewById(R.id.spinnerEstacion);
        final String estacion_seleccionada = spinnerEstacion.getSelectedItem().toString();

        //Guardamos la hora seleccionada
        final Spinner spinnerHora = (Spinner) findViewById(R.id.horaMedida);
        final int hora_seleccionada = Integer.parseInt(spinnerHora.getSelectedItem().toString().substring(0,2));

        Log.d("myTag", "La hora seleccionada es "+hora_seleccionada);


        //Leemos los datos de la web del ayuntamiento de Madrid
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Resources res = getResources();
                    InputStream estacionesjson = res.openRawResource(R.raw.estaciones);
                    InputStream magnitudesjson = res.openRawResource(R.raw.magnitudes);
                    ReadFiles file_read = new ReadFiles(estacionesjson, "ene", "2016");



                    file_read.read(estacionesjson, magnitudesjson);
                    ArrayList<ReadFiles.MagnitudInfo> list_magnitud = file_read.estacion_map.get(estacion_seleccionada);
                    for(int i=0; i<list_magnitud.size();i++){
                        Log.d("myTag", list_magnitud.get(i).getMagnitud());
                        Log.d("myTag", list_magnitud.get(i).getValueHour(hora_seleccionada));

                        switch (list_magnitud.get(i).getMagnitud()){
                            case "Dióxido de Azufre":
                                dato_SO2.setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))));
                                break;
                            case "Monóxido de Carbono":
                                dato_CO.setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))));
                                break;
                            case "Dióxido de Nitrógeno":
                                dato_NO2.setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))));
                                break;
                            case "Ozono":
                                dato_O3.setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))));
                                break;
                            case "Tolueno":
                                dato_TOL.setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))));
                                break;
                            case "Benceno":
                                dato_BEN.setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))));
                                break;
                            case "Partículas < 2.5 µm":
                                dato_PM2_5.setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))));
                                break;
                            case "Partículas < 10 µm":
                                dato_PM10.setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))));
                                break;
                            default:
                                //do nothing
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("myTag", "Pasa el bucle");
        Calendar calendar = new GregorianCalendar();
        int hora =calendar.get(Calendar.HOUR_OF_DAY);
        int minutos =calendar.get(Calendar.MINUTE);
        Log.d("myTag", "La hora del día es "+hora+":"+minutos);

        //Medida 1
        LinearLayout celda_SO2 = (LinearLayout)findViewById(R.id.col_cont1);
        Log.d("myTag", "Pasa el bucle"+dato_SO2.getText());
        celda_SO2.removeAllViews();
        celda_SO2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(dato_SO2.getText()!=""){
            float value_SO2 = Float.parseFloat(dato_SO2.getText().toString());
            if (value_SO2 < limit_SO2/2){
                celda_SO2.setBackgroundColor(Color.parseColor("#7ec051"));
            }
            else if (value_SO2 < limit_SO2 && value_SO2 >= limit_SO2/2){
                celda_SO2.setBackgroundColor(Color.parseColor("#fcc963"));
            }
            else if (value_SO2 >= limit_SO2){
                celda_SO2.setBackgroundColor(Color.parseColor("#fb3c2e"));
            }

            celda_SO2.addView(dato_SO2);
        }

        //Medida 2
        LinearLayout celda_CO = (LinearLayout)findViewById(R.id.col_cont2);
        Log.d("myTag", "Pasa el bucle"+dato_CO.getText());
        celda_CO.removeAllViews();
        celda_CO.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(dato_CO.getText()!=""){
            float value_CO = Float.parseFloat(dato_CO.getText().toString());
            if (value_CO < limit_CO/2){
                celda_CO.setBackgroundColor(Color.parseColor("#7ec051"));
            }
            else if (value_CO < limit_CO && value_CO >= limit_CO/2){
                celda_CO.setBackgroundColor(Color.parseColor("#fcc963"));
            }
            else if (value_CO >= limit_CO){
                celda_CO.setBackgroundColor(Color.parseColor("#fb3c2e"));
            }

            celda_CO.addView(dato_CO);
        }

        //Medida 3
        LinearLayout celda_NO2 = (LinearLayout)findViewById(R.id.col_cont3);
        Log.d("myTag", "Pasa el bucle"+dato_NO2.getText());
        celda_NO2.removeAllViews();
        celda_NO2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(dato_NO2.getText()!=""){
            float value_NO2 = Float.parseFloat(dato_NO2.getText().toString());
            if (value_NO2 < limit_NO2/2){
                celda_NO2.setBackgroundColor(Color.parseColor("#7ec051"));
            }
            else if (value_NO2 < limit_NO2 && value_NO2 >= limit_NO2/2){
                celda_NO2.setBackgroundColor(Color.parseColor("#fcc963"));
            }
            else if (value_NO2 >= limit_NO2){
                celda_NO2.setBackgroundColor(Color.parseColor("#fb3c2e"));
            }

            celda_NO2.addView(dato_NO2);
        }

        //Medida 4
        LinearLayout celda_O3 = (LinearLayout)findViewById(R.id.col_cont4);
        Log.d("myTag", "Pasa el bucle"+dato_O3.getText());
        celda_O3.removeAllViews();
        celda_O3.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(dato_O3.getText()!=""){
            float value_O3 = Float.parseFloat(dato_O3.getText().toString());
            if (value_O3 < limit_O3/2){
                celda_O3.setBackgroundColor(Color.parseColor("#7ec051"));
            }
            else if (value_O3 < limit_O3 && value_O3 >= limit_O3/2){
                celda_O3.setBackgroundColor(Color.parseColor("#fcc963"));
            }
            else if (value_O3 >= limit_O3){
                celda_O3.setBackgroundColor(Color.parseColor("#fb3c2e"));
            }

            celda_O3.addView(dato_O3);
        }

        //Medida 5
        LinearLayout celda_TOL = (LinearLayout)findViewById(R.id.col_cont5);
        Log.d("myTag", "Pasa el bucle"+dato_TOL.getText());
        celda_TOL.removeAllViews();
        celda_TOL.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(dato_TOL.getText()!=""){
            float value_TOL = Float.parseFloat(dato_TOL.getText().toString());
            if (value_TOL < limit_TOL/2){
                celda_TOL.setBackgroundColor(Color.parseColor("#7ec051"));
            }
            else if (value_TOL < limit_TOL && value_TOL >= limit_TOL/2){
                celda_TOL.setBackgroundColor(Color.parseColor("#fcc963"));
            }
            else if (value_TOL >= limit_TOL){
                celda_TOL.setBackgroundColor(Color.parseColor("#fb3c2e"));
            }

            celda_TOL.addView(dato_TOL);
        }

        //Medida 6
        LinearLayout celda_BEN = (LinearLayout)findViewById(R.id.col_cont6);
        Log.d("myTag", "Pasa el bucle"+dato_BEN.getText());
        celda_BEN.removeAllViews();
        celda_BEN.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(dato_BEN.getText()!=""){
            float value_BEN = Float.parseFloat(dato_BEN.getText().toString());
            if (value_BEN < limit_BEN/2){
                celda_BEN.setBackgroundColor(Color.parseColor("#7ec051"));
            }
            else if (value_BEN < limit_BEN && value_BEN >= limit_BEN/2){
                celda_BEN.setBackgroundColor(Color.parseColor("#fcc963"));
            }
            else if (value_BEN >= limit_BEN){
                celda_BEN.setBackgroundColor(Color.parseColor("#fb3c2e"));
            }

            celda_BEN.addView(dato_BEN);
        }

        //Medida 7
        LinearLayout celda_PM2_5 = (LinearLayout)findViewById(R.id.col_cont7);
        Log.d("myTag", "Pasa el bucle"+dato_PM2_5.getText());
        celda_PM2_5.removeAllViews();
        celda_PM2_5.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(dato_PM2_5.getText()!=""){
            float value_PM2_5 = Float.parseFloat(dato_PM2_5.getText().toString());
            if (value_PM2_5 < limit_PM2_5/2){
                celda_PM2_5.setBackgroundColor(Color.parseColor("#7ec051"));
            }
            else if (value_PM2_5 < limit_PM2_5 && value_PM2_5 >= limit_PM2_5/2){
                celda_PM2_5.setBackgroundColor(Color.parseColor("#fcc963"));
            }
            else if (value_PM2_5 >= limit_PM2_5){
                celda_PM2_5.setBackgroundColor(Color.parseColor("#fb3c2e"));
            }

            celda_PM2_5.addView(dato_PM2_5);
        }

        //Medida 8
        LinearLayout celda_PM10 = (LinearLayout)findViewById(R.id.col_cont8);
        Log.d("myTag", "Pasa el bucle"+dato_PM10.getText());
        celda_PM10.removeAllViews();
        celda_PM10.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(dato_PM10.getText()!=""){
            float value_PM10 = Float.parseFloat(dato_PM10.getText().toString());
            if (value_PM10 < limit_PM10/2){
                celda_PM10.setBackgroundColor(Color.parseColor("#7ec051"));
            }
            else if (value_PM10 < limit_PM10 && value_PM10 >= limit_PM10/2){
                celda_PM10.setBackgroundColor(Color.parseColor("#fcc963"));
            }
            else if (value_PM10 >= limit_PM10){
                celda_PM10.setBackgroundColor(Color.parseColor("#fb3c2e"));
            }

            celda_PM10.addView(dato_PM10);
        }




        /*
        plot = (XYPlot)  findViewById(R.id.plot);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    plot.clear();
                    Resources res = getResources();
                    InputStream estacionesjson = res.openRawResource(R.raw.estaciones);
                    InputStream magnitudesjson = res.openRawResource(R.raw.magnitudes);
                    ReadFiles file_read = new ReadFiles("http://www.mambiente.munimadrid.es/opendata/horario.txt");
                    file_read.read(estacionesjson, magnitudesjson);
                    ArrayList<ReadFiles.MagnitudInfo> list_magnitud = file_read.estacion_map.get(estacion_seleccionada);
                    for(int i=0; i<list_magnitud.size();i++){
                        Log.d("myTag", list_magnitud.get(i).getMagnitud());
                        Log.d("myTag", list_magnitud.get(i).getValueHour(18));
                        //addSeries(Float.parseFloat(list_magnitud.get(i).getValueHour(18)), list_magnitud.get(i).getMagnitud());
                    }
                    //showGraphic();
                    plot.redraw();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Log.d("myTag", "This is my message3");
*/
    }


    /*
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinnerEstacion);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }*/

/*
    public void addSeries(float value, String name){
        if (value > max){
            max = value;
        }

        Log.d("myTag", String.valueOf(pos));
        XYSeries series = new SimpleXYSeries(Arrays.asList(new Number[] {value } ), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,  name);

        if (series.size() < minx){
            minx = series.size();
        }
        if (series.size() > maxx){
            maxx = series.size();
        }

        Random r = new Random();
        BarFormatter formatter = new BarFormatter(Color.rgb(r.nextInt(250), r.nextInt(250), r.nextInt(250)), Color.rgb(100, 0, 0));
        formatter.setMarginLeft(PixelUtils.dpToPix(10));
        formatter.setMarginRight(PixelUtils.dpToPix(10));
        plot.addSeries(series, formatter);

        pos = pos+1;
    }



    public void showGraphic() {
        Log.d("myTag", "This is my message");
        BarRenderer renderer = (BarRenderer)plot.getRenderer(BarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(250));
        renderer.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);
        plot.setRangeBoundaries(0, max+10, BoundaryMode.FIXED);
        plot.setDomainBoundaries(-pos/2-2, pos/2+2, BoundaryMode.FIXED);


    }
    */
}
