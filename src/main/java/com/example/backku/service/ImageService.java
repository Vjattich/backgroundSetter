package com.example.backku.service;


import com.example.backku.model.Pic;
import com.example.backku.model.PicContainer;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static javax.imageio.ImageIO.write;
import static org.apache.tomcat.util.http.fileupload.FileUtils.cleanDirectory;

@Service
public class ImageService {

    @Value("${pics.path}")
    private String PATH;
    @Value("${zip.path}")
    private String ZIP_PATH;
    @Value("${temp.path}")
    private String TEMP_PATH;

    private final PicService picService;
    private final ZipService zipService;

    public ImageService(PicService picService, ZipService zipService) {
        this.picService = picService;
        this.zipService = zipService;
    }

    @SneakyThrows
    public File setBackground(List<MultipartFile> pics, MultipartFile back) {
        cleanFolders();
        PicContainer container = picService.getPic(pics, back);
        toFile(picService.mixPics(container));
        return zipService.getPicsZip();
    }

    private void cleanFolders() throws IOException {
        cleanDirectory(new File(PATH));
        cleanDirectory(new File(ZIP_PATH));
        cleanDirectory(new File(TEMP_PATH));
    }


    private void toFile(List<Future<Pic>> pics) throws IOException, ExecutionException, InterruptedException {
        for (Future<Pic> future : pics) {
            Pic pic = future.get();
            write(pic.getImage(), "PNG", new File(PATH, pic.getName()));
        }
    }


}
