package com.silas.meditacao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.models.Meditacao;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DiaMeditacaoActivity extends Activity {
    private MeditacaoDBAdapter mdba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_dia_meditacao);

        TextView tvTitulo = (TextView) findViewById(R.id.tvTitulo);
        TextView tvTextoBiblico = (TextView) findViewById(R.id.tvTextoBiblico);
        TextView tvTexto = (TextView) findViewById(R.id.tvTexto);

        Calendar ca = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sData = sdf.format(ca.getTime());

        mdba = new MeditacaoDBAdapter(getApplicationContext());

        try {
            Meditacao meditacao = mdba.buscaMeditacao(sData);
            if(meditacao == null) {
                startActivity(new Intent(this, LoginActivity.class));
            }
            Log.d("meditacao", meditacao.toString());
            tvTitulo.setText(meditacao.getTitulo());
            tvTextoBiblico.setText(meditacao.getTextoBiblico());
            tvTexto.setText(meditacao.getTexto());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dia_meditacao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
