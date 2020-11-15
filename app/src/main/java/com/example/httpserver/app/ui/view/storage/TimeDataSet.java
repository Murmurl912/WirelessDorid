package com.example.httpserver.app.ui.view.storage;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class TimeDataSet extends LineDataSet {

    private final int size;
    private final List<Entry> entries;
    private float DEFAULT_Y_VALUE = 0;

    public TimeDataSet(int size, String label) {
        super(new ArrayList<>(size), label);
        entries = mValues;
        this.size = size;
        setup();
    }

    private void setup() {
        for (int i = 0; i < size; i++) {
            entries.add(new Entry(i, DEFAULT_Y_VALUE));
        }
        notifyDataSetChanged();
    }

    public void add(float value) {
        if (entries.size() == 0) {
            return;
        }

        // remove last entry and update it
        Entry updateEntry = entries.remove(entries.size() - 1);
        updateEntry.setX(0);
        updateEntry.setY(value);

        // update remain entry
        for (Entry entry : entries) {
            entry.setX(entry.getX() + 1);
        }

        // add removed entry back
        entries.add(0, updateEntry);
    }

    @Override
    public void addEntryOrdered(Entry e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEntryByXValue(float xValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEntry(Entry e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEntry(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEntry(Entry e) {
        throw new UnsupportedOperationException();
    }

}
