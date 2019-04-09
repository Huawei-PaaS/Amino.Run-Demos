package com.sandro.openalprsample;

import android.os.AsyncTask;

import com.openalpr.jni.MicroServiceAccess;


/**
 * Created by SMoon on 2/27/2018.
 */
public class OpenAlprMicroServiceInit extends AsyncTask<Void, Void, String> {

    MicroServiceAccess sa;
    public OpenAlprMicroServiceInit(MicroServiceAccess sa) {
        this.sa = sa;
    }

    @Override
    protected String doInBackground(Void... params) {
        sa.initialize();
        return null;
    }
}
