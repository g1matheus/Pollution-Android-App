package com.contaminacion.madrid.contaminacionmadrid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user clicks the Contaminantes button */
    public void goContaminantes(View view) {
        Intent intent = new Intent(this, ContaminantesActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Contaminacion en Madrid button */
    public void goContMadrid(View view) {
        Intent intent = new Intent(this, ContMadridActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Datos historicos button */
    public void goDatosHistoricos(View view) {
        Intent intent = new Intent(this, DatosHistoricosActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Comparador button */
    public void goComparador(View view) {
        Intent intent = new Intent(this, ComparadorActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Rankings button */
    public void goRanking(View view) {
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Normativa button */
    public void goNormativa(View view) {
        Intent intent = new Intent(this, NormativaActivity.class);
        startActivity(intent);
    }


}
