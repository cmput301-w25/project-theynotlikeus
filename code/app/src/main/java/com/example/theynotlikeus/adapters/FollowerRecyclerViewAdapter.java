package com.example.theynotlikeus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.controller.FollowRequestController;
import com.example.theynotlikeus.model.Request;
import java.util.List;

public class FollowerRecyclerViewAdapter extends RecyclerView.Adapter<FollowerRecyclerViewAdapter.MyViewHolder> {
    private List<Request> requestList;
    private Context context;
    private FollowRequestController followRequestController;

    public FollowerRecyclerViewAdapter(Context context, List<Request> requestList, FollowRequestController followRequestController) {
        this.requestList = requestList;
        this.context = context;
        this.followRequestController = followRequestController;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_follower_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.textViewUsername.setText(request.getFollower());

        holder.buttonAcceptRequest.setOnClickListener(v -> {
            followRequestController.acceptRequest(request, () -> {
                Toast.makeText(context, "Follow request accepted", Toast.LENGTH_SHORT).show();
                int pos = holder.getAdapterPosition();
                requestList.remove(pos);
                notifyItemRemoved(pos);
            }, e -> {
                Toast.makeText(context, "Error accepting follow request", Toast.LENGTH_SHORT).show();
            });
        });

        holder.buttonDeclineRequest.setOnClickListener(v -> {
            followRequestController.declineRequest(request, () -> {
                Toast.makeText(context, "Follow request declined", Toast.LENGTH_SHORT).show();
                int pos = holder.getAdapterPosition();
                requestList.remove(pos);
                notifyItemRemoved(pos);
            }, e -> {
                Toast.makeText(context, "Error declining follow request", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        Button buttonAcceptRequest;
        Button buttonDeclineRequest;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textview_FragmentFollowerLayout_username);
            buttonAcceptRequest = itemView.findViewById(R.id.button_FragmentFollowerLayout_acceptButton);
            buttonDeclineRequest = itemView.findViewById(R.id.button_FragmentFollowerLayout_declineButton);
        }
    }
}
