package com.dani.todo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText todoInput;
    private Button addBtn;
    private ListView todoList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> todos;
    private TodoStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        todoInput = findViewById(R.id.todoInput);
        addBtn = findViewById(R.id.addBtn);
        todoList = findViewById(R.id.todoList);

        // Initialize storage
        storage = new TodoStorage(this);

        // Initialize todo list
        todos = storage.loadTodos();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todos);
        todoList.setAdapter(adapter);

        // Add button listener
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTodo();
            }
        });

        // Long click to delete
        todoList.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });
    }

    private void addTodo() {
        String todo = todoInput.getText().toString().trim();

        if (todo.isEmpty()) {
            Toast.makeText(this, "Masukkan todo terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add to list
        todos.add(todo);
        adapter.notifyDataSetChanged();

        // Save to storage
        storage.saveTodos(todos);

        // Clear input
        todoInput.setText("");
        Toast.makeText(this, "Todo ditambahkan", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Todo");
        builder.setMessage("Yakin ingin menghapus: " + todos.get(position) + "?");

        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                todos.remove(position);
                adapter.notifyDataSetChanged();
                storage.saveTodos(todos);
                Toast.makeText(MainActivity.this, "Todo dihapus", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }
}
