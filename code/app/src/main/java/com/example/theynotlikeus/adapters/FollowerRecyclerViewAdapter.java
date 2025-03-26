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

/**
 * RecyclerView Adapter for displaying a list of follower requests in a follower screen.
 * This adapter handles displaying the follower's username and providing options to accept or decline
 * the follow request. It communicates with the FollowRequestController to manage said requests.
 */
public class FollowerRecyclerViewAdapter extends RecyclerView.Adapter<FollowerRecyclerViewAdapter.MyViewHolder> {
    private List<Request> requestList; // List of requests
    private Context context; // Context to inflate layouts and show Toast messages
    private FollowRequestController followRequestController; // The controller to manage the requests

    /**
     * Constructor to initialize the adapter with the context, list of requests, and controller.
     *
     * @param context                  The context used for inflating views and showing Toast messages.
     * @param requestList              The list of follow requests to be displayed.
     * @param followRequestController  The controller to handle accepting and declining requests.
     */
    public FollowerRecyclerViewAdapter(Context context, List<Request> requestList, FollowRequestController followRequestController) {
        this.requestList = requestList;
        this.context = context;
        this.followRequestController = followRequestController;
    }

    /**
     * Creates a new ViewHolder for each request item in the list by inflating the item layout.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new instance of MyViewHolder
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_follower_layout, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Binds the data to the ViewHolder. Sets up the username and handles the click events for accepting or declining the request.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
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

    /**
     * Returns the total amount of requests in the list.
     *
     * @return A count of the requets from the list
     */
    @Override
    public int getItemCount() {
        return requestList.size();
    }

    /**
     * ViewHolder class that holds references to the views for displaying the request data (username)
     * and the buttons for accepting or declining the request.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        Button buttonAcceptRequest;
        Button buttonDeclineRequest;

        /**
         * Constructor for initializing ViewHolder with the itemView.
         *
         * @param itemView The view that represents each individual item in the RecyclerView.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textview_FragmentFollowerLayout_username);
            buttonAcceptRequest = itemView.findViewById(R.id.button_FragmentFollowerLayout_acceptButton);
            buttonDeclineRequest = itemView.findViewById(R.id.button_FragmentFollowerLayout_declineButton);
        }
    }
}
