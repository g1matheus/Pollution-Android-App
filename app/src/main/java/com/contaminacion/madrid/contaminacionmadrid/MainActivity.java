package com.contaminacion.madrid.contaminacionmadrid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user clicks the Contaminantes button */
    public void goEPOC(View view) {
        Intent intent = new Intent(this, EPOCActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Contaminacion en Madrid button */
    public void goContMadrid(View view) {
        Intent intent = new Intent(this, ContMadridActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Datos historicos button */
    public void goAvisosContaminacion(View view) {
        Intent intent = new Intent(this, AvisosContaminacionActivity.class);
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
    public void goSports(View view) {
        Intent intent = new Intent(this, SportsActivity.class);
        startActivity(intent);
    }


}
