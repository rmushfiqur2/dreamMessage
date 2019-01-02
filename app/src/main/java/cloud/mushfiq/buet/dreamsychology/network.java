package cloud.mushfiq.buet.dreamsychology;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by HP on 12-Mar-18.
 */

public class network extends BroadcastReceiver {
    private static final String TAG = "NetworkStateReceiver";
    private chat_user a;
    boolean is_A;
    boolean is_B;
    private inbox b;
    private inbox_vol c;

    public network(chat_user activity) {
        super();
        a=activity;
        is_A=true;
    }
    public network(inbox activity){
        super();
        b=activity;
        is_A=false;
        is_B=true;
    }
    public network(inbox_vol activity){
        super();
        c=activity;
        is_A=false;
        is_B=false;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.d(TAG, "Network connectivity change");

        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnectedOrConnecting()) {
                if(is_A){
                    a.hidebanner();
                }
                else if(is_B){
                    b.hidebanner();
                }
                else{
                    c.hidebanner();
                }
                //Log.i(TAG, "Network " + ni.getTypeName() + " connected");
                //Toast.makeText(context, "Network " + ni.getTypeName() + " connected", Toast.LENGTH_SHORT).show();
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                if(is_A){
                    a.showbanner();
                }
                else if(is_B){
                    b.showbanner();
                }
                else{
                    c.showbanner();
                }
                //Log.d(TAG, "There's no network connectivity");
                //Toast.makeText(context, "There's no network connectivity", Toast.LENGTH_SHORT).show();
            }
        }
    }
}