package com.example.backku.model;

import javafx.util.Pair;

import java.util.List;

public class PicContainer {

    private final Pair<Pic, List<Pic>> pair;

    public PicContainer(Pic key, List<Pic> value) {
        this.pair = new Pair<>(key, value);
    }

    public Pic getBackground() {
        return pair.getKey();
    }

    public List<Pic> getPics() {
        return pair.getValue();
    }

}


