package mo.zain.smartfarmer.controle;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.Comment;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    Context activity;
    List<Comment> commentList=new ArrayList<>();

    public CommentAdapter(Context activity, List<Comment> commentList) {
        this.activity = activity;
        this.commentList = commentList;
    }

    @NonNull
    @NotNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_items, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentAdapter.CommentViewHolder holder, int position) {

        Comment comment=commentList.get(position);
        holder.userName.setText(comment.getUserName());
        holder.Commenttxt.setText(comment.getCommenttxt());

        try {
            if (!comment.getMyDp().equals(""))
                Glide.with(activity)
                        .load(comment.getMyDp())
                        .into(holder.proImaage);
            else
                Glide.with(activity)
                        .load(R.drawable.ic_profile)
                        .into(holder.proImaage);
        }catch (Exception e)
        {
            Glide.with(activity)
                    .load(R.drawable.ic_profile)
                    .into(holder.proImaage);
        }

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        CircleImageView proImaage;
        TextView userName,Commenttxt;

        public CommentViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            proImaage=itemView.findViewById(R.id.commentImage);
            userName=itemView.findViewById(R.id.userName);
            Commenttxt=itemView.findViewById(R.id.commentKK);

        }
    }
}
