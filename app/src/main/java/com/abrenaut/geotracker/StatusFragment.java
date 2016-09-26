package com.abrenaut.geotracker;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by arthurbrenaut on 26/09/2016.
 */
public class StatusFragment extends ListFragment {

    private static final int LIMIT = 20;

    private static final LinkedList<String> messages = new LinkedList<String>();

    private static final Set<ArrayAdapter<String>> adapters = new HashSet<ArrayAdapter<String>>();

    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status, container, false);
    }


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, messages);
        setListAdapter(adapter);
        adapters.add(adapter);
    }

    @Override
    public void onDestroy() {
        adapters.remove(adapter);
        super.onDestroy();
    }
}
