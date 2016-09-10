package com.abrenaut.geotracker;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class StatusActivity extends ListActivity {

    private static final int LIMIT = 20;

    private static final LinkedList<String> messages = new LinkedList<String>();
    private static final Set<ArrayAdapter<String>> adapters = new HashSet<ArrayAdapter<String>>();

    private static void notifyAdapters() {
        for (ArrayAdapter<String> adapter : adapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public static void addMessage(String message) {
        DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
        message = format.format(new Date()) + " - " + message;
        messages.add(message);
        while (messages.size() > LIMIT) {
            messages.removeFirst();
        }
        notifyAdapters();
    }

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, messages);
        setListAdapter(adapter);
        adapters.add(adapter);
    }

    @Override
    protected void onDestroy() {
        adapters.remove(adapter);
        super.onDestroy();
    }

}