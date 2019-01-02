package cloud.mushfiq.buet.dreamsychology;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HP on 17-Apr-18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        /*Log.d("Refreshed token", "Refreshed token: " + refreshedToken);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference clients = FirebaseFirestore.getInstance().collection("Tokens");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("token", refreshedToken);
        clients.document("Iamhere").set(data1, SetOptions.merge());*/

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }

}
