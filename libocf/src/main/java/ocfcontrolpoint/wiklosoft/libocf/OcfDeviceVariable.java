package ocfcontrolpoint.wiklosoft.libocf;

import android.util.Log;

/**
 * Created by Pawel Wiklowski on 05.04.16.
 */
public class OcfDeviceVariable {
    private String miff;
    private String mResourceType;
    private String mHref;

    static {
        System.loadLibrary("test");
    }
    public OcfDeviceVariable(String iff, String href, String rt){
        miff= iff;
        mResourceType = rt;
        mHref = href;
    }

    String getHref(){
        return mHref;
    }


}
