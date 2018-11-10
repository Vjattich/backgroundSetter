package com.example.backku.service;

import com.example.backku.model.Pic;
import com.example.backku.model.PicContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static javax.imageio.ImageIO.read;

@Service
public class PicService {

    @Value("${temp.path}")
    private String TEMP_PATH;
    @Value("${cores}")
    private Integer CORES;

    public List<Future<Pic>> mixPics(PicContainer container) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(CORES);
        BufferedImage overlay = container.getBackground().getImage();
        return executorService.invokeAll(
                container.getPics().stream()
                        .map(pic -> (Callable<Pic>) () -> {
                            BufferedImage image = pic.getImage();
                            int w = Math.max(image.getWidth(), overlay.getWidth());
                            int h = Math.max(image.getHeight(), overlay.getHeight());
                            BufferedImage mix = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            Graphics g = mix.getGraphics();
                            g.drawImage(overlay, 0, 0, null);
                            g.drawImage(image, 0, 0, null);
                            return new Pic(pic.getName(), mix);
                        })
                        .collect(Collectors.toList())
        );
    }

    public PicContainer getPic(List<MultipartFile> pics, MultipartFile back) throws IOException {
        Pic background = createPic(back, TEMP_PATH + back.getOriginalFilename());
        ArrayList<Pic> images = new ArrayList<>();
        for (MultipartFile file : pics) {
            String path = TEMP_PATH + file.getOriginalFilename();
            images.add(createPic(file, path));
        }

        return new PicContainer(background, images);
    }

    private Pic createPic(MultipartFile multipartFile, String path) throws IOException {
        File file = new File(path);
        multipartFile.transferTo(file);
        BufferedImage bufferedImage = read(file);
        return new Pic(file.getName(), bufferedImage);
    }


}
