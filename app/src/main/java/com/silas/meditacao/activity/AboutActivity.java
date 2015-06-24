package com.silas.meditacao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.silas.guiaes.activity.R;

import java.util.Calendar;

/**
 * Created by silas on 24/06/15.
 */
public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView tvCopyright = (TextView) findViewById(R.id.copyright_textView);
        tvCopyright.setText("\u00A9 " + getString(R.string.title_activity_main) + " " + Calendar.getInstance().get(Calendar.YEAR));
    }
}
