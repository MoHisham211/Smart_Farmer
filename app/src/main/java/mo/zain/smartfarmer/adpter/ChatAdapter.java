package mo.zain.smartfarmer.adpter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.ChatModel;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyHolder> {
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ChatModel> chatModelList;
    String imageUrl;
    FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<ChatModel> chatModelList, String imageUrl) {
        this.context = context;
        this.chatModelList = chatModelList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return  new MyHolder(view);
        }else {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return  new MyHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatModelList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatAdapter.MyHolder holder, int position) {

        String message=chatModelList.get(position).getMessage();
        String timeStamp=chatModelList.get(position).getTimestamp();

        //convert time to dd/mm/yyyy hh:mm am/pm
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dataTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
        holder.messageTv.setText(message);
        holder.timeTv.setText(dataTime);
        try {

            Glide.with(context).load(imageUrl).into(holder.profileIv);

        }catch (Exception ex)
        {

        }
        holder.messageLAyout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("Delete message?");
                builder
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMessage(position);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.create().show();
            }
        });
        //set seen or delivered status
        if (position==chatModelList.size()-1)
        {
            if (chatModelList.get(position).isSeen()){
                //Toast.makeText(context, "Seen", Toast.LENGTH_SHORT).show();
                holder.isSeenTv.setText("Seen");
            }else {
                //Toast.makeText(context, "Delivered", Toast.LENGTH_SHORT).show();
                holder.isSeenTv.setText("Delivered");
            }
        }else {
            holder.isSeenTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIv;
        TextView messageTv,timeTv,isSeenTv;
        LinearLayout messageLAyout;
        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profileIv=itemView.findViewById(R.id.profileIv);
            messageTv=itemView.findViewById(R.id.messageTv);
            timeTv=itemView.findViewById(R.id.timeTv);
            isSeenTv=itemView.findViewById(R.id.isSeenTv);
            messageLAyout=itemView.findViewById(R.id.messageLayout);
        }
    }
    private void deleteMessage(final int position) {

        final String myUID=FirebaseAuth.getInstance().getCurrentUser().getUid();

        String msgTimeStamp=chatModelList.get(position).getTimestamp();
        DatabaseReference dbRef= FirebaseDatabase.getInstance()
                .getReference("Chats");
        Query query=dbRef.orderByChild("timestamp")
                .equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren())
                {
                    if (ds.child("sender").getValue().equals(myUID))
                    {
                        ds.getRef().removeValue();
                        FancyToast.makeText(context,"Message deleted",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();

                    }else {
                        FancyToast.makeText(context, "You can delete only your message",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
