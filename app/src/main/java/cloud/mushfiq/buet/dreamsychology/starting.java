package cloud.mushfiq.buet.dreamsychology;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Text;

public class starting extends AppCompatActivity {
    String email1;
    int role;
    String name;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        View web=(View)findViewById(R.id.web);
        View youtube=(View)findViewById(R.id.youtube);
        View message=(View)findViewById(R.id.message);
        View appoint=(View)findViewById(R.id.appoint);
        TextView fb = (TextView)findViewById(R.id.facebook);
        TextView join = (TextView)findViewById(R.id.joinus);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            email1=extras.getString("email");
            role=extras.getInt("role");
        }
        if(role==3){
            name=extras.getString("name");
        }
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in;
                in= new Intent(starting.this,Web.class);
                in.putExtra("key",true);
                startActivity(in);
            }
        });
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in;
                in= new Intent(starting.this,Web.class);
                in.putExtra("key",false);
                startActivity(in);
            }
        });
        appoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in;
                in= new Intent(starting.this,Appoint.class);
                startActivity(in);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(role==3){
                    Intent in;
                    in= new Intent(starting.this,chat_user.class);
                    in.putExtra("email",email1);
                    in.putExtra("name",name);
                    in.putExtra("role",role);
                    startActivity(in);
                }
                if(role==2){
                    Intent in;
                    in= new Intent(starting.this,inbox_vol.class);
                    in.putExtra("email",email1);
                    startActivity(in);
                }

            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/dreampsychology/"));
                startActivity(browserIntent);
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/11uPyGpOvG-yScPoseJnZwllTu74hS7uLJ7av7HBY54s/"));
                startActivity(browserIntent);
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.VISIBLE);

    }

}
