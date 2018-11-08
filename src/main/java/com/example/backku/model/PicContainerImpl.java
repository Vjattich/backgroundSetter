package com.example.backku.model;

import javafx.util.Pair;

import java.util.List;

public class PicContainerImpl extends Pair<Pic, List<Pic>> implements PicContainer {

    public PicContainerImpl(Pic key, List<Pic> value) {
        super(key, value);
    }

    public Pic getBackground() {
        return getKey();
    }

    public List<Pic> getImages() {
        return getValue();
    }

}


