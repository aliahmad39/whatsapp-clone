package  com.UniqueBulleteSolutions.whatsapp.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.UniqueBulleteSolutions.whatsapp.Models.Calllist;
import com.UniqueBulleteSolutions.whatsapp.R;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.Holder> {
    private List<Calllist> list;
    private Context context;


    public CallListAdapter(List<Calllist> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_call_list , parent , false);

        return new Holder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Calllist calllist=list.get(position);

        holder.tvName.setText(calllist.getUserName());
        holder.tvDate.setText(calllist.getDate());


        if(calllist.getCallType().equals("missed")){
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_red_dark));
        }else if(calllist.getCallType().equals("income"))
            {
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));
        }else{
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_upward));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));
        }


        Glide.with(context).load(calllist.getUrlProfile()).into(holder.profile);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class Holder extends RecyclerView.ViewHolder{
       TextView  tvName , tvDate;
       CircleImageView profile;
       ImageView ivcall , arrow;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvName =itemView.findViewById(R.id.calluserName);
            ivcall =itemView.findViewById(R.id.ivcall);
            arrow =itemView.findViewById(R.id.iv_calltype);
            tvDate =itemView.findViewById(R.id.callDate);
            profile =itemView.findViewById(R.id.profile_image);

        }
    }

}
