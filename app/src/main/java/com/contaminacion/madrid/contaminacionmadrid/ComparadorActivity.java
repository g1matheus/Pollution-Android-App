package com.contaminacion.madrid.contaminacionmadrid;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.androidplot.xy.XYPlot;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import api.main.ReadFiles;

public class ComparadorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private Spinner spinner1, spHoras;
    private XYPlot plot;
    private float max = 0;
    private int pos=0;
    private float minx = 0;
    private float maxx = 0;

    private String dia;
    private String mes;
    private String ano;
    private TextView dia_seleccionado, fecha_seleccionada;

    private float limit_SO2 = 350;
    private float limit_CO = 10;
    private float limit_NO2 = 200;
    private float limit_O3 = 120;
    private float limit_TOL = 10;
    private float limit_BEN = 5;
    private float limit_PM2_5 = 25;
    private float limit_PM10 = 50;
    private int showDateX = 0;
    private int horaMedidaX = 0;
    private int numMagnitudesAlmacenadas = 0;
    private boolean isCorrectData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparador);

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

        int errorMessagesX = 0;
        int spinnerEstacionX = 0;
        int horaMedidaX = 0;
        Integer[] celda_cont = new Integer[] {0, 0, 0, 0, 0, 0, 0, 0};
        isCorrectData = false;
        numMagnitudesAlmacenadas = 0;


        //Identificamos que buscador es
        String buscador = view.getTag().toString();

        //Actualizamos las variables para el buscardor 1
        if(buscador.equals("measures1")){
            errorMessagesX = R.id.errorMessages1;
            spinnerEstacionX = R.id.spinnerEstacion1;
            horaMedidaX = R.id.horaMedida1;
            showDateX = R.id.showDate1;
            celda_cont = new Integer[] {R.id.buscador1_cont1, R.id.buscador1_cont2, R.id.buscador1_cont3, R.id.buscador1_cont4, R.id.buscador1_cont5, R.id.buscador1_cont6, R.id.buscador1_cont7, R.id.buscador1_cont8};
        }
        //Actualizamos las variables para el buscardor 2
        else if (buscador.equals("measures2")){
            errorMessagesX = R.id.errorMessages2;
            spinnerEstacionX = R.id.spinnerEstacion2;
            horaMedidaX = R.id.horaMedida2;
            showDateX = R.id.showDate2;
            celda_cont = new Integer[] {R.id.buscador2_cont1, R.id.buscador2_cont2, R.id.buscador2_cont3, R.id.buscador2_cont4, R.id.buscador2_cont5, R.id.buscador2_cont6, R.id.buscador2_cont7, R.id.buscador2_cont8};
        }

        for(int i=0; i<8;i++){
            LinearLayout celdaX = (LinearLayout)findViewById(celda_cont[i]);
            celdaX.removeAllViews();
        }
        fecha_seleccionada = (TextView)findViewById(showDateX);

        if(fecha_seleccionada.getText() != ""){
            ((TextView) findViewById(errorMessagesX)).setText("");

            //Guardamos la estación seleccionada
            final Spinner spinnerEstacion = (Spinner) findViewById(spinnerEstacionX);
            final String estacion_seleccionada = spinnerEstacion.getSelectedItem().toString();

            //Guardamos la hora seleccionada
            final Spinner spinnerHora = (Spinner) findViewById(horaMedidaX);
            final int hora_seleccionada = Integer.parseInt(spinnerHora.getSelectedItem().toString().substring(0,2));
            Log.d("myTag", "La hora seleccionada es "+hora_seleccionada);
            Log.d("myTag", "El día seleccionado es "+dia);
            Log.d("myTag", "El mes seleccionado es "+mes);
            Log.d("myTag", "El año seleccionado es "+ano);


            //Leemos los datos de la web del ayuntamiento de Madrid
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try  {
                        Resources res = getResources();
                        InputStream estacionesjson = res.openRawResource(R.raw.estaciones);
                        InputStream magnitudesjson = res.openRawResource(R.raw.magnitudes);

                        InputStream downloadsjson = res.openRawResource(R.raw.downloads);
                        // String fichero = getFichero(ano, downloadsjson);
                        Log.d("myTag", "Fecha del sistema: "+Calendar.getInstance().getTime());

                        //ReadFiles file_read = new ReadFiles("http://www.mambiente.munimadrid.es/opendata/horario.zip", mes);
                        ReadFiles file_read = new ReadFiles(downloadsjson, mes, ano);
                        file_read.read(estacionesjson, magnitudesjson);
                        ArrayList<ReadFiles.MagnitudInfo> list_magnitud = file_read.estacion_map.get(estacion_seleccionada);
                        Log.d("myTag", "Lista de magnitudes size: " + list_magnitud.size());

                        for(int i=0; i<list_magnitud.size();i++){
                            if(list_magnitud.get(i).getDataValidator(hora_seleccionada)){
                                isCorrectData = true;
                            }
                            if(String.valueOf(Integer.parseInt(list_magnitud.get(i).getDay())).equals(dia)
                                    && list_magnitud.get(i).getDataValidator(hora_seleccionada)){
                                numMagnitudesAlmacenadas++;
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("myTag", "Pasa el bucle");
            Calendar calendar = new GregorianCalendar();
            int hora =calendar.get(Calendar.HOUR_OF_DAY);
            int minutos =calendar.get(Calendar.MINUTE);
            Log.d("myTag", "La hora del día es "+hora+":"+minutos);

            if(numMagnitudesAlmacenadas == 0){
                ((TextView) findViewById(errorMessagesX)).setText("No se han encontrado resultados para esta búsqueda.");
            }
            else{
                //Medida 1
                LinearLayout celda_SO2 = (LinearLayout)findViewById(celda_cont[0]);
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
                LinearLayout celda_CO = (LinearLayout)findViewById(celda_cont[1]);
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
                LinearLayout celda_NO2 = (LinearLayout)findViewById(celda_cont[2]);
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
                LinearLayout celda_O3 = (LinearLayout)findViewById(celda_cont[3]);
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
                LinearLayout celda_TOL = (LinearLayout)findViewById(celda_cont[4]);
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
                LinearLayout celda_BEN = (LinearLayout)findViewById(celda_cont[5]);
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
                LinearLayout celda_PM2_5 = (LinearLayout)findViewById(celda_cont[6]);
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
                LinearLayout celda_PM10 = (LinearLayout)findViewById(celda_cont[7]);
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
            }

        }
        else{
            ((TextView) findViewById(errorMessagesX)).setText("Por favor, seleccione una fecha.");
        }

    }


    /**
     * This callback method, call DatePickerFragment class,
     * DatePickerFragment class returns calendar view.
     * @param view
     */

    public void datePicker(View view){
        String showDate = view.getTag().toString();

        if(showDate.equals("showDate1")){
            showDateX = R.id.showDate1;
            horaMedidaX = R.id.horaMedida1;
        }
        else if (showDate.equals("showDate2")){
            showDateX = R.id.showDate2;
            horaMedidaX = R.id.horaMedida2;
        }
        DatePickerFragment fragment_date = new DatePickerFragment();
        fragment_date.show(getSupportFragmentManager(),"datePicker");

    }

    /**
     * To set date on TextView
     * @param calendar
     */
    private void setDate(final Calendar calendar) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, new Locale("es"));
        ((TextView) findViewById(showDateX)).setText(dateFormat.format(calendar.getTime()));

        //Guardamos el día seleccionado
        dia_seleccionado = (TextView)findViewById(showDateX);
        Log.d("myTag", "La fecha seleccionada es "+dia_seleccionado.getText());
        dia = dia_seleccionado.getText().toString().split(" ")[0];
        mes = dia_seleccionado.getText().toString().split(" ")[1].replace(".","");
        ano = dia_seleccionado.getText().toString().split(" ")[2];

        //Rellenamos el combos de horas en función de la fecha seleccionada
        final String[] horasDia = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "00"};
        String[] meses = new String[] {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};
        List<String> horasPasadas = new ArrayList<>();
        Calendar calendarHoy = new GregorianCalendar();
        int horaActual =calendarHoy.get(Calendar.HOUR_OF_DAY);


        //obtener una lista con las horas del día pasadas
        for (int i=0;i < horasDia.length;++i) {
            if(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)).equals(ano)
                    && meses[Calendar.getInstance().get(Calendar.MONTH)].equals(mes)
                    && String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).equals(dia)){
                if((Integer.parseInt(horasDia[i]) <= horaActual) && Integer.parseInt(horasDia[i]) != 0){
                    horasPasadas.add(horasDia[i]+":00");
                }
            }
            else {
                horasPasadas.add(horasDia[i]+":00");
            }
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, horasPasadas);

        spHoras = (Spinner)findViewById(horaMedidaX);
        spHoras.setAdapter(adapter);
        spHoras.setSelection(horasPasadas.size()-1);
    }

    /**
     * To receive a callback when the user sets the date.
     * @param view
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);
    }

    /**
     * Create a DatePickerFragment class that extends DialogFragment.
     * Define the onCreateDialog() method to return an instance of DatePickerDialog
     */
    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Locale locale = new Locale("es_ES");
            final Calendar cmin = Calendar.getInstance();
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener)
                            getActivity(), year, month, day);

            cmin.set(2001,0,1);
            Log.d("myTag", "La fecha mínima essssss: "+cmin.getTime());
            dpd.getDatePicker().setMinDate(cmin.getTimeInMillis());

            c.getTime();
            Log.d("myTag", "La fecha máxima es: "+c.getTime());
            dpd.getDatePicker().setMaxDate(c.getTimeInMillis());


            // Return the DatePickerDialog
            return  dpd;

        }

    }
}
