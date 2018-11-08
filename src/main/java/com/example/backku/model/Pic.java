package com.example.backku.model;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;

@Getter
@Setter
public class Pic {
    private BufferedImage image;
    private String name;

    public Pic(String name, BufferedImage bufferedImage) {
        this.name = name;
        this.image = bufferedImage;
    }
}
