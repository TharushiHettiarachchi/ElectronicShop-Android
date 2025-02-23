package lk.webstudio.elecshop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class BroadcastCheck extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGpsEnabled || isNetworkEnabled) {

            Log.i("ElecLog","Location ON");
        } else {

            Toast.makeText(context,"Please Turn on Location",Toast.LENGTH_LONG).show();
            Log.i("ElecLog","Location OFF");
        }
    }
}
