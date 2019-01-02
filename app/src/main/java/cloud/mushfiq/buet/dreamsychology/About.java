package cloud.mushfiq.buet.dreamsychology;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button bt=(Button) findViewById(R.id.text5);
       final EditText et=(EditText) findViewById(R.id.text4);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = et.getText().toString().trim();
                if (msg.length() > 0) {
                    et.setText("");
                    Map<String, Object> data = new HashMap<>();

                    data.put("report", msg);

                    CollectionReference conversations = FirebaseFirestore.getInstance().collection("Reports");
                    conversations.add(data);
                    utils.toast(About.this,"Report Sent!");
                }
            }
            });
    }
}
