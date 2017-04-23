package api.main;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;

import com.contaminacion.madrid.contaminacionmadrid.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import api.libs.JSONObject;
import api.main.ReadFiles.MagnitudInfo;




public class ReadFiles {

	public class MagnitudInfo {

		public ArrayList<String> hours_list = new ArrayList<String>();
		public ArrayList<Boolean> isValid = new ArrayList<Boolean>();
		public String magnitud_name;
		public String tecnica;
		public String year, month, day;
		public String unidad;

		public void setMagnitudInfo(String magnitud_name, String tecnica, String year, String month, String day, ArrayList<String> hours_list, ArrayList<Boolean> isValid, String unidad) {
			this.magnitud_name = magnitud_name;
			this.tecnica = tecnica;
			this.year = year;
			this.month = month;
			this.day = day;
			this.hours_list = hours_list;
			this.isValid = isValid;
			this.unidad = unidad;
		}

		public int modulo( int m, int n ){
			int mod =  m % n ;
			return ( mod < 0 ) ? mod + n : mod;
		}

		public String getValueHour(int hour) {
			return hours_list.get(modulo((hour-1),24));
		}

		public Boolean getDataValidator(int hour) {
			return isValid.get(modulo((hour-1),24));
		}

		public ArrayList<String> getHours_list() { return hours_list; }

		public ArrayList<Boolean> getValidators_list() { return isValid;}

		public String getMagnitud() {
			return magnitud_name;
		}

		public String getYear() {
			return year;
		}

		public String getMonth() {
			return month;
		}

		public String getDay() {
			return day;
		}

		public String getUnidad(){ return unidad;}
	}

	protected String nombre_file;
	protected String tagFile;
	protected InputStream file;
	public Map<String, ArrayList<MagnitudInfo>> estacion_map = null;
	protected JSONObject estacion_jsonobj = null;
	protected JSONObject magnitud_jsonobj = null;
	protected JSONObject file_jsonobj = null;
	protected boolean is_zip;
	private InputStreamReader estacionfiletxt;
	protected ZipInputStream zipinstream;

	/////////////////
	// CONSTRUCTOR //
	/////////////////
	public ReadFiles(InputStream files, String StringMonth, String year) throws Exception {
		String[] meses = new String[] {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};

		//Vemos si la fecha es de este mes y año y ponemos tagDate = hoy
		Log.d("myTag", "La fecha que pensamos actual es: " + Calendar.getInstance().getTime());
		Log.d("myTag", "Comparamos: " + Integer.toString(Calendar.getInstance().get(Calendar.YEAR)) + " con " + year);
		//Log.d("myTag", "Comparamos: " + Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1) + " con " + Integer.parseInt(StringMonth));
		if(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)).equals(year)
				&& meses[Calendar.getInstance().get(Calendar.MONTH)].equals(StringMonth)){
			tagFile = "esteMes";
		}
		else{
			tagFile = year;
		}

		//String mes = meses[Integer.parseInt(month)];

		this.nombre_file = getFile(tagFile, files);
		//this.filePath = filePath;
		Log.d("myTag", "Nombre de fichero: " + nombre_file);
		if (nombre_file.endsWith(".txt")){
			Log.d("myTag", "El fichero es un txt porque es de marzo 17");
			URL oracle = new URL(nombre_file);
			URLConnection yc = oracle.openConnection();
			this.estacionfiletxt = new InputStreamReader(yc.getInputStream(), Charset.forName("UTF-8"));
			this.is_zip = false;
		}else{
			Log.d("myTag", "El fichero es un zip porque es anterior a marzo 17");
			File root = android.os.Environment.getExternalStorageDirectory();
			File dir = new File(root.getAbsolutePath() + "/cont/databases");
			if (dir.exists() == false) {
				dir.mkdirs();
			}

			URL url = new URL(nombre_file);

			URLConnection uconn = url.openConnection();
			uconn.setConnectTimeout(300);

			InputStream is = uconn.getInputStream();
			zipinstream = new ZipInputStream(new BufferedInputStream(is),Charset.forName("UTF-8"));
			ZipEntry zipEntry;
			boolean found = false;
			while (((! found) && (zipEntry = zipinstream.getNextEntry()) != null)){
				if (zipEntry.getName().toLowerCase().contains(StringMonth.toLowerCase())) {
					this.estacionfiletxt = new InputStreamReader(zipinstream, Charset.forName("UTF-8"));
					found = true;
				}
			}
			this.is_zip = true;
		}
	}


	public String convertStreamToString(InputStream entityResponse) throws IOException {
		java.util.Scanner s = new java.util.Scanner(entityResponse, "UTF-8").useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";

	}

	///////////////////
	//  CSV METHODS  //
	///////////////////
	public String getFile(String file_n, InputStream jsonfile) throws Exception {
		if (file_jsonobj == null) {
			String response = convertStreamToString(jsonfile);
			file_jsonobj = new JSONObject(response.toString());
		}
		String file_json = file_jsonobj.getString(file_n);
		return file_json;
	}

	public String getEstacion(String estacion_n, InputStream jsonfile) throws Exception {
		if (estacion_jsonobj == null) {
			String response = convertStreamToString(jsonfile);
			estacion_jsonobj = new JSONObject(response.toString());
		}
		String estacion_json;
		if(estacion_jsonobj.has(estacion_n)){
			estacion_json = estacion_jsonobj.getString(estacion_n);
		}
		else{
			estacion_json = "Not Found";
		}
		return estacion_json;
	}


	public JSONObject getMagnitud(String magnitud_n, InputStream jsonfile) throws Exception {
		if (magnitud_jsonobj == null) {
			String response = convertStreamToString(jsonfile);
			magnitud_jsonobj = new JSONObject(response);
		}
		JSONObject magnitud_json = null;
		if (magnitud_jsonobj.has(magnitud_n)) {
			magnitud_json = magnitud_jsonobj.getJSONObject(magnitud_n);
		}
		return magnitud_json;
	}


	public ArrayList<MagnitudInfo> getValuesEstacion(String estacion) {
		return estacion_map.get(estacion);
	}

	public ArrayList<String> getValuesEstacionNO2(String estacion){
		ArrayList<String> valuesEstacionNO2 = null;
		for(int i=0; i<estacion_map.get(estacion).size();i++){
			if(estacion_map.get(estacion).get(i).getMagnitud().equals("Dióxido de Nitrógeno")){
				return estacion_map.get(estacion).get(i).getHours_list();
			}
		}
		return valuesEstacionNO2;
	}

	public ArrayList<Boolean> getValidatorsEstacionNO2(String estacion){
		ArrayList<Boolean> validatorsEstacionNO2 = null;
		for(int i=0; i<estacion_map.get(estacion).size();i++){
			if(estacion_map.get(estacion).get(i).getMagnitud().equals("Dióxido de Nitrógeno")){
				return estacion_map.get(estacion).get(i).getValidators_list();
			}
		}
		return validatorsEstacionNO2;
	}


	/**
	 * This method read a row of the CSV file and returns a map with the name
	 * of the column and its value.
	 *
	 * @param
	 * @return
	 * @throws IOException
	 */
	public void read(InputStream estacionfile, InputStream magnitudfile) throws Exception {
		String tecnica, horario, year, month, day;
		String previous_estacion = "";
		ArrayList<MagnitudInfo> magnitud_info_list = null;
		estacion_map = new HashMap<String, ArrayList<MagnitudInfo>>();

		boolean end = false;
		int i;

		while (!end){
			String line = "";
			i = this.estacionfiletxt.read();
			while((i!=-1)&&((char)i!='\n')){
				char c = (char)i;
				line = line+c;
				i = this.estacionfiletxt.read();
			}
			if(i==-1){
				end = true;
			}
			if (line.length()>12) {
				line = line.replace(",", "");
				String nombre_estacion = getEstacion(line.substring(0, 8), estacionfile);
				if (previous_estacion != nombre_estacion) {
					if (previous_estacion != "") {
						estacion_map.put(previous_estacion, magnitud_info_list);
					}
					previous_estacion = nombre_estacion;
					//crear nueva lista
					magnitud_info_list = new ArrayList<MagnitudInfo>();
				}
				MagnitudInfo magnitud_info = new MagnitudInfo();
				JSONObject magnitud_object = getMagnitud(line.substring(8, 10), magnitudfile);
				String nombre_magnitud = "";
				String unidad = "";
				if (magnitud_object != null) {
					nombre_magnitud = magnitud_object.getString("nombre");
					unidad = magnitud_object.getString("unidad");
				}

				tecnica = line.substring(10, 12);
				horario = line.substring(12, 14);

				int resta = 0;
				if (is_zip) {
					resta = 2;
				}
				year = line.substring(14, 18 - resta);
				month = line.substring(18 - resta, 20 - resta);
				day = line.substring(20 - resta, 22 - resta);

				/*
				Log.d("Tag", "Estación: " + nombre_estacion);
				Log.d("Tag", "Magnitud: " + nombre_magnitud);
				Log.d("Tag", "Día: " + day);
				Log.d("Tag", "Mes: " + month);
				Log.d("Tag", "Año: " + year);
				*/

				ArrayList<String> hours_list = new ArrayList<String>();
				ArrayList<Boolean> dataValidator_list = new ArrayList<Boolean>();
				for (int hora = 1; hora < 25; hora++) {
					int inicio = 22 - resta + (6 * (hora - 1));
					hours_list.add(line.substring(inicio, inicio + 5));
					String validador = line.substring(inicio + 5, inicio + 6);
					if(validador.equals("V")){
						dataValidator_list.add(true);
					}
					else{
						dataValidator_list.add(false);
					}
				}
				magnitud_info.setMagnitudInfo(nombre_magnitud, tecnica, year, month, day, hours_list, dataValidator_list, unidad);
				magnitud_info_list.add(magnitud_info);
				//System.out.println("estacion: "+nombre_estacion+" magnitud: "+nombre_magnitud+" tecnica: "+tecnica+" horario: "+horario);
			}

		}
		//zipinstream.closeEntry();
		if (previous_estacion != "") {
			estacion_map.put(previous_estacion, magnitud_info_list);
		}

	}


	/**
	 * This method resets the file and lets start reading the file from the beginning,
	 *
	 * @throws IOException
	 */
	public void reset() throws IOException {
		// Close the file
		this.file = null;
		// Reopen the file
		this.file = new FileInputStream(this.nombre_file);
		//this.buffer = new BufferedReader(new InputStreamReader(this.file, Charset.forName("UTF-8")));
	}



	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
		List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		//LinkedHashMap will keep the keys in the order they are inserted
		//which is currently sorted on natural ordering
		Map<K,V> sortedMap = new LinkedHashMap<K,V>();

		for(Map.Entry<K,V> entry: entries){
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}



	public Map<String, Float> valoresDiaContaminante(String contaminante){
		Iterator itr = estacion_map.keySet().iterator();
		Map<String, Float> estacion_sum_contaminante = new HashMap<String, Float>();
		while(itr.hasNext()) {
			Object estacion = itr.next();
			ArrayList<MagnitudInfo> list_magnitud = estacion_map.get(estacion);
			Iterator itr_magnitud = list_magnitud.iterator();
			while(itr_magnitud.hasNext()) {
				Object magnitud = itr_magnitud.next();
				float sum= 0;
				if(((MagnitudInfo)magnitud).getMagnitud().equals(contaminante)){
					for(int hour=0; hour<24; hour++){
						if(((MagnitudInfo)magnitud).getDataValidator(hour)){
							sum = sum + Float.valueOf(((MagnitudInfo)magnitud).getValueHour(hour));
						}
					}
					estacion_sum_contaminante.put((String)estacion, sum);
				}
			}
		}
		estacion_sum_contaminante = sortByValues(estacion_sum_contaminante);
		return estacion_sum_contaminante;
	}


}