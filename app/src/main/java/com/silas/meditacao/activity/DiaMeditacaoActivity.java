package com.silas.meditacao.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.models.Meditacao;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DiaMeditacaoActivity extends Activity {
    private MeditacaoDBAdapter mdba;
    private Meditacao meditacao;
    private Calendar ca = Calendar.getInstance();

    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
//            Log.i("", "Longpress detected");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, meditacao.getTitulo() + "\n\n"
                    + dataPorExtenso(ca) + "\n\n" + meditacao.getTextoBiblico()
                    + "\n\n" + meditacao.getTexto());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        }
    });

    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
            getActionBar().hide();
        }
        setContentView(R.layout.activity_dia_meditacao);

        TextView tvTitulo = (TextView) findViewById(R.id.tvTitulo);
        TextView tvTextoBiblico = (TextView) findViewById(R.id.tvTextoBiblico);
        TextView tvTexto = (TextView) findViewById(R.id.tvTexto);
        TextView tvLink = (TextView) findViewById(R.id.tvLinks);
        tvLink.setMovementMethod(LinkMovementMethod.getInstance());

        TextView tvData = (TextView) findViewById(R.id.tvData);

        String data =  dataPorExtenso(ca);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sData = sdf.format(ca.getTime());

        mdba = new MeditacaoDBAdapter(getApplicationContext());

        try {
            meditacao = mdba.buscaMeditacao(sData);
            if(meditacao == null) {
                //se for dia 30, 31 de qualquer mẽs ou 28 de fevereiro não atualiza
                if((ca.get(Calendar.MONTH) == 2 && ca.get(Calendar.DAY_OF_MONTH) < 28)
                        || ca.get(Calendar.DAY_OF_MONTH) < 30) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    this.finish();
                }
                else {
                    tvTitulo.setText("Erro!");
                    tvTitulo.setTextColor(Color.parseColor("#CC0000"));
                    tvTitulo.setBackgroundColor(Color.parseColor("#FFCACA"));
                    tvData.setText(data);
                    tvData.setTextColor(Color.parseColor("#FFCACA"));
                    tvData.setBackgroundColor(Color.parseColor("#CC0000"));
                    tvTextoBiblico.setText("Tente atualizar no início do próximo mês!");
                    tvTextoBiblico.setTextColor(Color.parseColor("#CC0000"));
                    tvTexto.setText("");
                }
            }
            else {
//                Log.i(getClass().getSimpleName(), meditacao.toString());
                tvTitulo.setText(meditacao.getTitulo());
                tvData.setText(data);
                tvTextoBiblico.setText(meditacao.getTextoBiblico());
                tvTexto.setText(meditacao.getTexto());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String dataPorExtenso(Calendar ca) {
        int d = ca.get(Calendar.DAY_OF_MONTH);
        int m = ca.get(Calendar.MONTH);
        int a = ca.get(Calendar.YEAR);

        String mes [] = new String[] {"janeiro", "fevereiro", "março", "abril", "maio", "junho",
                "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
        return d + " de " + mes[m] + " de " + a;
    }

    /*@Override
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
    }*/
}
