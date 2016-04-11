package ocfcontrolpoint.wiklosoft.iotcontrolpoint;

import android.app.Application;

import ocfcontrolpoint.wiklosoft.libocf.OcfControlPoint;

/**
 * Created by Pawel Wiklowski on 11.04.16.
 */

public class ApplicationContext extends Application{
    static OcfControlPoint mOcfControlPoint = new OcfControlPoint();

    public static OcfControlPoint getOcfControlPoint(){
        return mOcfControlPoint;
    }
}
