package com.wiklosoft.ocf.controlpoint;

import android.app.Application;

import com.wiklosoft.ocf.OcfControlPoint;

/**
 * Created by Pawel Wiklowski on 11.04.16.
 */

public class ApplicationContext extends Application{
    static OcfControlPoint mOcfControlPoint = new OcfControlPoint();

    public static OcfControlPoint getOcfControlPoint(){
        return mOcfControlPoint;
    }
}
