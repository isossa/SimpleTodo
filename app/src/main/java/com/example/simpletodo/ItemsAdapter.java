package com.example.simpletodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Responsible for displaying data from the model into a row  in the recycler view
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder>
{
    public interface OnClickListener
    {
        void onItemClicked (int position);
    }
    public interface OnLongClickListener
    {
        void onItemLongClicked (int position);
    }

    List<String> items;
    OnLongClickListener longClickListener;
    OnClickListener clickListener;

    public ItemsAdapter(List<String> itemsIn, OnLongClickListener longClickListenerIn, OnClickListener clickListenerIn)
    {
        items = itemsIn;
        longClickListener = longClickListenerIn;
        clickListener = clickListenerIn;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Use layout inflater to inflate a view
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        // wrap it inside a view holder and return it
        return new ViewHolder(todoView);
    }

    // Binds data to a particular view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        // Grab the item at specified position
        String item = items.get(position);

        // Bind item to specified view holder
        holder.bind(item);
    }

    // Count number of items in the list
    @Override
    public int getItemCount()
    {
        return items.size();
    }

    /*
     * Define view holder as a container to provide easy access
     * to views representing each row in list
     */
    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewItem;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            textViewItem = itemView.findViewById(android.R.id.text1);
        }

        // Update the view inside of the view holder with this data
        public void bind(String item)
        {
            textViewItem.setText(item);
            textViewItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });
            textViewItem.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    // Notify listener which position was long pressed
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
