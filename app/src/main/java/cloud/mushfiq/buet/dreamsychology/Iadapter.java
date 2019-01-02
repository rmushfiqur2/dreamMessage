package cloud.mushfiq.buet.dreamsychology;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HP on 20-Mar-18.
 */

public class Iadapter extends RecyclerView.Adapter<Iadapter.ViewHolder> {
    private List<msg_preview> messages;
    Context cn;

    public Iadapter( List<msg_preview> M, Context c){
        messages=M;
        cn=c;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        final msg_preview ab = messages.get(position);
        holder.name.setText(ab.name);
        holder.text.setText(ab.text);
        if(ab.newmsg){
            holder.text.setTypeface(null, Typeface.BOLD);
        }
        else{
            holder.text.setTypeface(null, Typeface.NORMAL);
        }
        holder.image.setText(ab.namekey);
        if(ab.our_msg){
            holder.text.setText("We: "+ab.text);
        }
        holder.time.setText(ab.time);
        if(ab.isTaken){
            holder.image1.setImageResource(R.drawable.onetoone);
            holder.taker.setText(ab.taker);
        }
        else{
            holder.image1.setImageResource(R.drawable.public_icon);
            holder.taker.setText("open");
        }
        holder.all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((listToChat) cn).openChat(ab.email,ab.name,ab.isTaken);
                //utils.toast(holder.all.getContext(),ab.name);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_unit, parent, false);
        return new ViewHolder(v);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView time;
        public TextView text;
        public TextView taker;
        public TextView image;
        public ImageView image1;
        public View all;

        public ViewHolder(View v) {
            super(v);
            all=v;
            name=v.findViewById(R.id.name);
            time=v.findViewById(R.id.time);
            text=v.findViewById(R.id.text);
            taker=v.findViewById(R.id.taker);
            image=v.findViewById(R.id.he);
            image1=v.findViewById(R.id.mark);

        }
    }
}
