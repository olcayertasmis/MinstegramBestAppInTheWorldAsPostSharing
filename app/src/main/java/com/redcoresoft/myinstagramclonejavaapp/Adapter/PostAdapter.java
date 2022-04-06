package com.redcoresoft.myinstagramclonejavaapp.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.redcoresoft.myinstagramclonejavaapp.Model.Post;
import com.redcoresoft.myinstagramclonejavaapp.databinding.RecycleRowBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList){
        this.postArrayList = postArrayList;
    }



    class PostHolder  extends RecyclerView.ViewHolder{

        RecycleRowBinding recycleRowBinding;

        public PostHolder(RecycleRowBinding recycleRowBinding) {
            super(recycleRowBinding.getRoot());
            this.recycleRowBinding=recycleRowBinding;
        }
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleRowBinding recycleRowBinding = RecycleRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recycleRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recycleRowBinding.txtRecyclerowEmail.setText(postArrayList.get(position).email);
        holder.recycleRowBinding.txtRecyclerowComment.setText(postArrayList.get(position).comment);
        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recycleRowBinding.imgRecycleRowPostImage);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }
}
