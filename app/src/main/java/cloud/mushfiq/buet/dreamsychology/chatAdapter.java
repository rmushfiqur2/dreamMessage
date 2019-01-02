package cloud.mushfiq.buet.dreamsychology;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HP on 14-Mar-18.
 */

public class chatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<message> messages;

    public chatAdapter( List<message> M){
        messages=M;
    }

    @Override
    public int getItemViewType(int position) {
        message ab=messages.get(position);
        return ab.type;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater LI=LayoutInflater.from(parent.getContext());
        if (viewType == 1) {
            view = LI.inflate(R.layout.histext, parent, false);
            return new ViewHolder1(view);

        }
        else if (viewType == 2) {
            view = LI.inflate(R.layout.mytext, parent, false);
            return new ViewHolder2(view);

        } else if (viewType == 3) {
            view = LI.inflate(R.layout.hisimage, parent, false);
            return new ViewHolder3(view);

        } else {
            view = LI.inflate(R.layout.myimage, parent, false);
            return new ViewHolder4(view);

        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {
        message ab=messages.get(position);

        switch(holder.getItemViewType()){
            case 1:
                ViewHolder1 VH1=(ViewHolder1)holder;
                VH1.histext.setText(ab.text);
                VH1.date.setText(ab.time);
                if(ab.seenicon){
                    VH1.histextseen.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                ViewHolder2 VH2=(ViewHolder2)holder;
                VH2.mytext.setText(ab.text);
                VH2.date.setText(ab.time);
                if(ab.seenicon){
                    VH2.mytextseen.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                ViewHolder3 VH3=(ViewHolder3)holder;
                VH3.hisimage=ab.image;
                VH3.date.setText(ab.time);
                if(ab.seenicon){
                    VH3.hisimageseen.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                ViewHolder4 VH4=(ViewHolder4)holder;
                VH4.myimage=ab.image;
                VH4.date.setText(ab.time);
                if(ab.seenicon){
                    VH4.myimageseen.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class ViewHolder1 extends RecyclerView.ViewHolder {
        public TextView histext;
        public ImageView histextseen;
        public TextView date;
        public ImageView he;

        public ViewHolder1(View v) {
            super(v);
            histext=v.findViewById(R.id.histext);
            histextseen=v.findViewById(R.id.histextseen);
            date=v.findViewById(R.id.date);
            he=v.findViewById(R.id.he);

        }
    }
    public class ViewHolder2 extends RecyclerView.ViewHolder {
        public TextView mytext;
        public ImageView mytextseen;
        public TextView date;

        public ViewHolder2(View v) {
            super(v);
            mytext=v.findViewById(R.id.mytext);
            mytextseen=v.findViewById(R.id.mytextseen);
            date=v.findViewById(R.id.date);

        }
    }
    public class ViewHolder3 extends RecyclerView.ViewHolder {
        public ImageView hisimage;
        public ImageView hisimageseen;
        public TextView date;
        public ImageView he;

        public ViewHolder3(View v) {
            super(v);
            hisimage=v.findViewById(R.id.hisimage);
            hisimageseen=v.findViewById(R.id.hisimageseen);

            date=v.findViewById(R.id.date);
            he=v.findViewById(R.id.he);

        }
    }
    public class ViewHolder4 extends RecyclerView.ViewHolder {
        public ImageView myimage;
        public ImageView myimageseen;
        public TextView date;

        public ViewHolder4(View v) {
            super(v);
            myimage=v.findViewById(R.id.myimage);
            myimageseen=v.findViewById(R.id.myimageseen);
            date=v.findViewById(R.id.date);

        }
    }
}
