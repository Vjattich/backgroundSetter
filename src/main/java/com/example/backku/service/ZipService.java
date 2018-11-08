package com.example.backku.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.Files.*;
import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;

@Service
public class ZipService {

    @Value("${pics.path}")
    private String PATH;
    @Value("${zip.path}")
    private String ZIP_PATH;

    public File getPicsZip() throws IOException {
        String pathname = ZIP_PATH + "pics.zip";
        zipPics(pathname);
        return new File(pathname);
    }

    private void zipPics(String zipPath) throws IOException {
        try (ZipOutputStream zs = getZipStream(zipPath)) {
            Path pp = get(PATH);
            walk(pp)
                    .filter(path -> !isDirectory(path))
                    .forEach(zipPic(zs, pp));
        }
    }

    private ZipOutputStream getZipStream(String path) throws IOException {
        return new ZipOutputStream(newOutputStream(createFile(get(path))));
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
