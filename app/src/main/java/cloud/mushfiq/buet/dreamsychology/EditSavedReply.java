package cloud.mushfiq.buet.dreamsychology;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditSavedReply extends AppCompatActivity{
    ArrayList<reply> replies;
    RecyclerView list;
    volAdapter adapter;
    LinearLayoutManager LI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saved_reply);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditSavedReply.this);
                LayoutInflater inflater = EditSavedReply.this.getLayoutInflater();
                View xml = inflater.inflate(R.layout.saved_reply, null);
                final EditText a = (EditText) xml.findViewById(R.id.title);
                final EditText b = (EditText) xml.findViewById(R.id.body);
                final CheckBox c=(CheckBox) xml.findViewById(R.id.personalize);

                builder.setView(xml)

                        .setTitle("Add new reply")
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Map<String, Object> data = new HashMap<>();
                                reply ab=new reply();
                                ab.message=b.getText().toString();
                                ab.head=a.getText().toString();
                                ab.person=c.isChecked();

                                data.put("title",a.getText().toString());
                                data.put("message",b.getText().toString());
                                data.put("personalize",c.isChecked());

                                String ide= Calendar.getInstance().getTime().toString();
                                ab.id=ide;

                                CollectionReference replie=FirebaseFirestore.getInstance().collection("Replies");
                                replie.document(ide).set(data);
                                replies.add(ab);
                                adapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.show();
            }
        });

        list =  findViewById(R.id.list);

        LI=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(LI);

        replies=new ArrayList<>();
        adapter=new volAdapter(replies);
        list.setAdapter(adapter);
        list.hasFixedSize();

        FirebaseFirestore.getInstance().collection("Replies").orderBy("title")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                reply s=new reply();
                                s.head=document.getString("title");
                                s.message=document.getString("message");
                                s.person=document.getBoolean("personalize");
                                s.id=document.getId();
                                replies.add(s);
                            }
                            adapter.notifyDataSetChanged();
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
            LayoutInflater layoutInflater = LayoutInflater.from(EditSavedReply.this);
            View view = layoutInflater.inflate(R.layout.vcd,parent,false);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final reply s = mScientists.get(position);
            holder.head.setText(s.head);
            holder.message.setText(s.message);
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditSavedReply.this);
                    LayoutInflater inflater = EditSavedReply.this.getLayoutInflater();
                    View xml = inflater.inflate(R.layout.saved_reply, null);
                    final EditText a = (EditText) xml.findViewById(R.id.title);
                    final EditText b = (EditText) xml.findViewById(R.id.body);
                    final CheckBox c=(CheckBox) xml.findViewById(R.id.personalize);
                    a.setText(s.head);
                    b.setText(s.message);
                    c.setChecked(s.person);

                    builder.setView(xml)

                            .setTitle("Edit reply")
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Map<String, Object> data = new HashMap<>();
                                    reply ab=replies.get(position);
                                    ab.message=b.getText().toString();
                                    ab.head=a.getText().toString();
                                    ab.person=c.isChecked();

                                    data.put("title",a.getText().toString());
                                    data.put("message",b.getText().toString());
                                    data.put("personalize",c.isChecked());

                                    CollectionReference replie=FirebaseFirestore.getInstance().collection("Replies");
                                    replie.document(s.id).set(data);
                                    replies.set(position,ab);
                                    adapter.notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                }
            });
            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditSavedReply.this);
                    builder

                            .setMessage("Are you sure to delete this reply?")
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    CollectionReference replie=FirebaseFirestore.getInstance().collection("Replies");
                                    replie.document(s.id).delete();
                                    replies.remove(position);
                                    adapter.notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mScientists.size();

        }
    }
    private class reply {
        public String head;
        public String message;
        public Boolean person;
        public String id;
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
