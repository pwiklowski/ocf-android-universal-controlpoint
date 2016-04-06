package ocfcontrolpoint.wiklosoft.libocf;

import android.util.Log;

/**
 * Created by Pawel Wiklowski on 05.04.16.
 */
public class OcfDeviceVariable {
    private String miff;
    private String mResourceType;
    private String mHref;

    public OcfDeviceVariable(String iff, String rt, String href){
        miff= iff;
        mResourceType = rt;
        mHref = href;
    }


}
