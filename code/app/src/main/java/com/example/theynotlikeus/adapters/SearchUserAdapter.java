package com.example.theynotlikeus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.User;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of users to search for users.
 * It also:
 * Binds user data to the RecyclerView.
 * Displays the users username.
 * Handles click events on user items.
 */
public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyViewHolder> {
    private List<User> userList; // List of users
    private Context context; // Context to inflate layouts and show Toast messages
    private OnUserClickListener listener; // Listener for click functionality on list items

    /**
     * Interface to handle the click on user items.
     */
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    /**
     * Constructor to initialize the adapter with data and listener.
     *
     * @param context
     * @param userList
     * @param listener
     */
    public SearchUserAdapter(Context context, List<User> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    /**
     * Creates a new ViewHolder by inflating the user item layout and returning a new instance of MyViewHolder.
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_user_layout, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Binds the user data to the ViewHolder. Also sets the username to the TextView and sets the click listener for each item.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewUsername.setText(user.getUsername());

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    /**
     * Returns the total amount of users in the list.
     *
     * @return A count of the users in the list
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * Updates the list of users and notifies the adapter that the data has changed.
     *
     * @param newList new list of users to display
     */
    public void updateList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for holding references to the views that display user data.
     *
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;

        /**
         * Constructor for initializing the ViewHolder with the itemView.
         *
         * @param itemView The view that represents each individual item in the RecyclerView.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textview_ActivitySearchUserLayout_username);
        }
    }
}
