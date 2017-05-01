package com.contaminacion.madrid.contaminacionmadrid;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import api.main.ReadFiles;

public class ContMadridActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private Spinner spinner1, spHoras;
    private String dia;
    private String mes;
    private String ano;
    private TextView dia_seleccionado, fecha_seleccionada;
    private float[] limites = new float[] {350, 10, 180, 120, 10, 5, 25, 50};
    private String[] names = new String[] {"SO2", "CO", "NO2", "O3", "TOL", "BEN", "PM 2,5", "PM 10"};
    private int showDateX = 0;
    private int horaMedidaX = 0;
    private int numMagnitudesAlmacenadas = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont_madrid);

    }


    /** Called when the user clicks the Contaminacion en Madrid button */
    public void buscarDatos(View view) {


        //Variables - Medidas contaminantes
        final TextView[] datos_cont = new TextView[8];
        final TextView[] names_cont = new TextView[8];
        final TextView[] barra = new TextView[8];
        final TextView[] porcentaje = new TextView[8];
        for(int i=0;i<8;i++){
            datos_cont[i] = new TextView(this);
            names_cont[i] = new TextView(this);
            barra[i] = new TextView(this);
            porcentaje[i] = new TextView(this);
        }

        int errorMessagesX = 0;
        int spinnerEstacionX = 0;
        int horaMedidaX = 0;
        Integer[] celda_name = new Integer[] {0, 0, 0, 0, 0, 0, 0, 0};
        Integer[] celda_cont = new Integer[] {0, 0, 0, 0, 0, 0, 0, 0};
        Integer[] celda_porc_barra = new Integer[] {0, 0, 0, 0, 0, 0, 0, 0};
        Integer[] celda_porc_num = new Integer[] {0, 0, 0, 0, 0, 0, 0, 0};
        Integer[] celda_porc = new Integer[] {0, 0, 0, 0, 0, 0, 0, 0};
        numMagnitudesAlmacenadas = 0;


        //Identificamos que buscador es
        String buscador = view.getTag().toString();

        //Actualizamos las variables para el buscardor 1
        if(buscador.equals("measures1")){
            errorMessagesX = R.id.errorMessages1;
            spinnerEstacionX = R.id.spinnerEstacion1;
            horaMedidaX = R.id.horaMedida1;
            showDateX = R.id.showDate1;
            celda_name = new Integer[] {R.id.category1, R.id.category2, R.id.category3, R.id.category4, R.id.category5, R.id.category6, R.id.category7, R.id.category8};
            celda_cont = new Integer[] {R.id.buscador1_cont1, R.id.buscador1_cont2, R.id.buscador1_cont3, R.id.buscador1_cont4, R.id.buscador1_cont5, R.id.buscador1_cont6, R.id.buscador1_cont7, R.id.buscador1_cont8};
            celda_porc = new Integer[] {R.id.buscador1_cont1_porcentaje, R.id.buscador1_cont2_porcentaje, R.id.buscador1_cont3_porcentaje, R.id.buscador1_cont4_porcentaje, R.id.buscador1_cont5_porcentaje, R.id.buscador1_cont6_porcentaje, R.id.buscador1_cont7_porcentaje, R.id.buscador1_cont8_porcentaje};
            celda_porc_barra = new Integer[] {R.id.buscador1_cont1_barra, R.id.buscador1_cont2_barra, R.id.buscador1_cont3_barra, R.id.buscador1_cont4_barra, R.id.buscador1_cont5_barra, R.id.buscador1_cont6_barra, R.id.buscador1_cont7_barra, R.id.buscador1_cont8_barra};
            celda_porc_num = new Integer[] {R.id.buscador1_cont1_porc, R.id.buscador1_cont2_porc, R.id.buscador1_cont3_porc, R.id.buscador1_cont4_porc, R.id.buscador1_cont5_porc, R.id.buscador1_cont6_porc, R.id.buscador1_cont7_porc, R.id.buscador1_cont8_porc};


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

                        Log.d("myTag", "Lista de magnitudes size: " + estacion_seleccionada);

                        ArrayList<ReadFiles.MagnitudInfo> list_magnitud = file_read.estacion_map.get(estacion_seleccionada);
                        Log.d("myTag", "Lista de magnitudes size: " + list_magnitud.size());

                        for(int i=0; i<list_magnitud.size();i++){
                            if(String.valueOf(Integer.parseInt(list_magnitud.get(i).getDay())).equals(dia)
                                    && list_magnitud.get(i).getDataValidator(hora_seleccionada)){
                                numMagnitudesAlmacenadas++;
                                switch (list_magnitud.get(i).getMagnitud()){
                                    case "Dióxido de Azufre":
                                        datos_cont[0].setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))) + " " + list_magnitud.get(i).getUnidad());
                                        break;
                                    case "Monóxido de Carbono":
                                        datos_cont[1].setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))) + " " + list_magnitud.get(i).getUnidad());
                                        break;
                                    case "Dióxido de Nitrógeno":
                                        datos_cont[2].setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))) + " " + list_magnitud.get(i).getUnidad());
                                        break;
                                    case "Ozono":
                                        datos_cont[3].setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))) + " " + list_magnitud.get(i).getUnidad());
                                        break;
                                    case "Tolueno":
                                        datos_cont[4].setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))) + " " + list_magnitud.get(i).getUnidad());
                                        break;
                                    case "Benceno":
                                        datos_cont[5].setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))) + " " + list_magnitud.get(i).getUnidad());
                                        break;
                                    case "Partículas 2.5 µm":
                                        datos_cont[6].setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))) + " " + list_magnitud.get(i).getUnidad());
                                        break;
                                    case "Partículas 10 µm":
                                        datos_cont[7].setText(String.valueOf(Float.parseFloat(list_magnitud.get(i).getValueHour(hora_seleccionada))) + " " + list_magnitud.get(i).getUnidad());
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
            Calendar calendar = new GregorianCalendar();
            int hora =calendar.get(Calendar.HOUR_OF_DAY);
            int minutos =calendar.get(Calendar.MINUTE);
            Log.d("myTag", "La hora del día es "+hora+":"+minutos);

            if(numMagnitudesAlmacenadas == 0){
                ((TextView) findViewById(errorMessagesX)).setText("No se han encontrado resultados para esta búsqueda.");
                //Pintamos por pantalla las medidas
                for(int i=0;i<8;i++) {
                    names_cont[i].setText(names[i]);
                    LinearLayout celda_nombre = (LinearLayout) findViewById(celda_name[i]);
                    LinearLayout celda = (LinearLayout) findViewById(celda_cont[i]);
                    TextView celda_barra = (TextView) findViewById(celda_porc_barra[i]);
                    TextView celda_numero = (TextView) findViewById(celda_porc_num[i]);
                    celda.removeAllViews();
                    celda_barra.setBackgroundColor(Color.TRANSPARENT);
                    celda.setBackgroundColor(Color.TRANSPARENT);
                    celda_numero.setText("");
                }

            }
            else{

                //Pintamos por pantalla las medidas
                for(int i=0;i<8;i++){
                    names_cont[i].setText(names[i]);
                    LinearLayout celda_nombre = (LinearLayout)findViewById(celda_name[i]);
                    LinearLayout celda = (LinearLayout)findViewById(celda_cont[i]);
                    TextView celda_barra = (TextView)findViewById(celda_porc_barra[i]);
                    TextView celda_numero = (TextView)findViewById(celda_porc_num[i]);
                    celda_nombre.removeAllViews();
                    celda.removeAllViews();

                    celda.setBackgroundColor(Color.TRANSPARENT);
                    celda_barra.setBackgroundColor(Color.TRANSPARENT);
                    celda_numero.setText("");
                    if((datos_cont[i]!=null) && (datos_cont[i].getText()!="")){
                        float value = Float.parseFloat(datos_cont[i].getText().toString().split(" ")[0]);
                        if (value < limites[i]/2){
                            celda_barra.setBackgroundColor(Color.parseColor("#7ec051"));
                        }
                        else if (value < limites[i] && value >= limites[i]/2){
                            celda_barra.setBackgroundColor(Color.parseColor("#fcc963"));
                        }
                        else if (value >= limites[i]){
                            celda_barra.setBackgroundColor(Color.parseColor("#fb3c2e"));
                        }
                        //cambia la longitud de la barra del porcentaje
                        float porcentaje_value = value * 100 / limites[i];
                        ViewGroup.LayoutParams params=celda_barra.getLayoutParams();
                        float width = 7*porcentaje_value;
                        params.width= (int)width;
                        celda_barra.setLayoutParams(params);
                        celda_numero.setTextSize(17);
                        celda_numero.setText(String.format("%.2f", porcentaje_value)+"%");
                        datos_cont[i].setTextSize(17);
                        celda.addView(datos_cont[i]);
                    }
                    else if((datos_cont[i]==null) || (datos_cont[i].getText()=="")){
                        celda.setBackgroundColor(Color.parseColor("#E0E0E0"));
                        datos_cont[i].setText("-");
                        datos_cont[i].setTextSize(20);
                        celda.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                        celda.addView(datos_cont[i]);
                    }
                    names_cont[i].setTextSize(18);
                    celda_nombre.addView(names_cont[i]);
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

