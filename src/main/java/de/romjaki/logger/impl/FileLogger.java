package de.romjaki.logger.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileLogger extends StreamLogger {
    public FileLogger(String name, File file) throws IOException {
        super(name, new FileOutputStream(createFile(file)));
    }

    public FileLogger(File file) throws IOException {
        super(new FileOutputStream(createFile(file)));
    }

    private static File createFile(File file) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


}
