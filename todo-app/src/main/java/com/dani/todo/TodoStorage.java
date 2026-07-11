package com.dani.todo;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class TodoStorage {

    private static final String PREF_NAME = "TodoPrefs";
    private static final String KEY_TODOS = "todos";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public TodoStorage(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Save todos to SharedPreferences
    public void saveTodos(ArrayList<String> todos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < todos.size(); i++) {
            sb.append(todos.get(i));
            if (i < todos.size() - 1) {
                sb.append("||||"); // Separator
            }
        }
        editor.putString(KEY_TODOS, sb.toString());
        editor.apply();
    }

    // Load todos from SharedPreferences
    public ArrayList<String> loadTodos() {
        ArrayList<String> todos = new ArrayList<String>();
        String data = prefs.getString(KEY_TODOS, "");

        if (!data.isEmpty()) {
            String[] items = data.split("\\|\\|\\|\\|");
            for (String item : items) {
                if (!item.isEmpty()) {
                    todos.add(item);
                }
            }
        }

        return todos;
    }

    // Clear all todos
    public void clearAllTodos() {
        editor.remove(KEY_TODOS);
        editor.apply();
    }
}
