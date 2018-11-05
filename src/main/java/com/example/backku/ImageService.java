package com.example.backku;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.Files.*;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;
import static org.apache.tomcat.util.http.fileupload.FileUtils.cleanDirectory;

@Service
public class ImageService {

    private final String PATH = "/home/vjattich/IdeaProjects/backku/src/main/resources/pics/";
    private final String ZIP_PATH = "/home/vjattich/IdeaProjects/backku/src/main/resources/static/";

    File setBackground(List<MultipartFile> pics, MultipartFile back) throws IOException {
        cleanFolders();
        insertPics(pics, back);
        return getPicsZip();
    }

    private void cleanFolders() throws IOException {
        cleanDirectory(new File(PATH));
        cleanDirectory(new File(ZIP_PATH));
    }

    private void insertPics(List<MultipartFile> pics, MultipartFile back) throws IOException {
        BufferedImage overlay = read(back.getInputStream());
        for (MultipartFile file : pics) {
            BufferedImage image = read(file.getInputStream());
            int w = Math.max(image.getWidth(), overlay.getWidth());
            int h = Math.max(image.getHeight(), overlay.getHeight());
            BufferedImage mix = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics g = mix.getGraphics();
            g.drawImage(overlay, 0, 0, null);
            g.drawImage(image, 0, 0, null);
            toFile(file.getOriginalFilename(), mix);
        }
    }

    private void toFile(String picName, BufferedImage mix) throws IOException {
        write(mix, "PNG", new File(PATH, picName));
    }

    private File getPicsZip() throws IOException {
        String pathname = ZIP_PATH + "pics.zip";
        zipPics(pathname);
        return new File(pathname);
    }

    private void zipPics(String zipPath) throws IOException {
        try (ZipOutputStream zs = getZipStream(zipPath)) {
            Path pp = Paths.get(PATH);
            walk(pp)
                    .filter(path -> !isDirectory(path))
                    .forEach(zipPic(zs, pp));
        }
    }

    private ZipOutputStream getZipStream(String path) throws IOException {
        return new ZipOutputStream(newOutputStream(createFile(Paths.get(path))));
    }

    private Consumer<Path> zipPic(ZipOutputStream zs, Path pp) {
        return path -> {
            ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
            try {
                zs.putNextEntry(zipEntry);
                copy(path, zs);
                zs.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }


}
