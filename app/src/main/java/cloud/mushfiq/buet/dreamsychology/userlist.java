package cloud.mushfiq.buet.dreamsychology;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class userlist extends AppCompatActivity {
    ArrayList<reply> replies;
    RecyclerView list;
    volAdapter adapter;
    LinearLayoutManager LI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        list =  findViewById(R.id.list);

        LI=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(LI);

        replies=new ArrayList<>();
        adapter=new volAdapter(replies);
        list.setAdapter(adapter);
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list.hasFixedSize();

        FirebaseFirestore.getInstance().collection("Clients").orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                reply m= new reply();
                                m.email=document.getString("e-mail");
                                m.name=document.getString("name");

                                replies.add(m);
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private class volAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<reply> mScientists;
        public volAdapter(ArrayList<reply> Scientists){
            mScientists = Scientists;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(userlist.this);
            View view = layoutInflater.inflate(R.layout.vcd,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final reply s = mScientists.get(position);
            holder.head.setText(s.name);
            holder.message.setText(s.email);
            holder.del.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return mScientists.size();

        }
    }
    private class reply {
        public String name;
        public String email;
    }
    private class ViewHolder extends RecyclerView.ViewHolder {

        public TextView head;
        public TextView message;
        ImageButton del;
        ImageButton edit;

        public ViewHolder(View v) {
            super(v);
            head=v.findViewById(R.id.heading);
            message=v.findViewById(R.id.message);
            del=v.findViewById(R.id.delete);
            edit=v.findViewById(R.id.edit);
        }
    }
}
