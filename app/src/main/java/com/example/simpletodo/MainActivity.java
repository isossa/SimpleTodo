package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.io.FileUtils.writeLines;

public class MainActivity extends AppCompatActivity
{
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;
    Button buttonAdd;
    EditText editTextItem;
    RecyclerView recyclerViewItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = findViewById(R.id.buttonAdd);
        editTextItem = findViewById(R.id.editTextItem);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);

        items = new ArrayList<String>();

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener()
        {
            @Override
            public void onItemLongClicked(int position)
            {
                // Delete item from the recycler view
                items.remove(position);
                // Notify adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener()
        {
            @Override
            public void onItemClicked(int position)
            {
                Log.d("MainActivity", "Single click at position " + position);
                // Create new activity
                Intent intent = new Intent(MainActivity.this, EditActivity.class);

                // Pass data being edited
                intent.putExtra(KEY_ITEM_TEXT, items.get(position));
                intent.putExtra(KEY_ITEM_POSITION, position);

                // Display the activity
                startActivityForResult(intent, EDIT_TEXT_CODE);
            }
        };

        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        recyclerViewItems.setAdapter(itemsAdapter);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        buttonAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String todoItem = editTextItem.getText().toString();
                // Add item to model
                items.add(todoItem);
                // Notify adapter that new item is to be inserted
                itemsAdapter.notifyItemInserted(items.size() - 1);
                editTextItem.setText("");
                Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    // Handle the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // Extract the original position of the edited item from position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // Update the model at the right position with new item
            items.set(position, itemText);

            // Notify the adapter
            itemsAdapter.notifyItemChanged(position);

            // Persist the change
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_LONG).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile()
    {
        return new File(getFilesDir(), "data.txt");
    }

    // Load items from data fie
    private void loadItems()
    {
        try
        {
            items = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        }
        catch (IOException ioe)
        {
            Log.e("MainActivity", "Error reading items", ioe);
            items = new ArrayList<>();
        }
    }

    // Save items by writing them to file
    private void saveItems()
    {
        try
        {
            FileUtils.writeLines(getDataFile(), items);
        }
        catch (IOException ioe)
        {
            Log.e("MainActivity", "Error writing items", ioe);
        }
    }
}