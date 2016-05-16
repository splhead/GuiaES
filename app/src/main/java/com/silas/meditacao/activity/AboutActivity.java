package com.silas.meditacao.activity;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.TextView;

import com.silas.guiaes.activity.R;

import java.util.Calendar;

/**
 * Created by silas on 24/06/15.
 */
public class AboutActivity extends ThemedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PackageInfo pInfo;
        String version = "";
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView tvCopyright = (TextView) findViewById(R.id.copyright_textView);
        String out = "\u00A9 " + getString(R.string.title_activity_main) +
                " " + Calendar.getInstance().get(Calendar.YEAR) +
                " - versão: " + version;
        tvCopyright.setText(out);
    }
}
