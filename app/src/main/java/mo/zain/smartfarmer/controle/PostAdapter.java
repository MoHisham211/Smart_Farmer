package mo.zain.smartfarmer.controle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private Context mCtx;
    private List<Post> postList=new ArrayList<>();

    public PostAdapter(Context mCtx, List<Post> postList) {
        this.mCtx = mCtx;
        this.postList = postList;
    }

    @NonNull
    @NotNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new PostViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_post_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.title
                .setText(post.getTitle());
        if (!post.getDescription().equals(""))
        {
            holder.des.setVisibility(View.VISIBLE);
            holder.des
                    .setText(post.getDescription());
        }
        if (!post.getImage().equals(""))
        {
            holder.imagePost.setVisibility(View.VISIBLE);
            Glide.with(mCtx)
                    .load(post.getImage())
                    .into(holder.imagePost);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        TextView title,des;
        ImageView imagePost;
        Button love,comment,share;
        public PostViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            des=itemView.findViewById(R.id.description);
            imagePost=itemView.findViewById(R.id.Image);
            love=itemView.findViewById(R.id.buttonlove);
            comment=itemView.findViewById(R.id.buttoncomment);
            share=itemView.findViewById(R.id.buttonShare);
        }
    }
}
