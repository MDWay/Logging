package de.romjaki.logger.impl;

import de.romjaki.logger.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class StreamLogger extends Logger {
    private Writer writer;

    public StreamLogger(String name, OutputStream outputStream) {
        this(name, new OutputStreamWriter(outputStream));
    }

    public StreamLogger(String name, Writer writer) {
        super(name);
        this.writer = writer;
    }

    public StreamLogger(OutputStream outputStream) {
        this(new OutputStreamWriter(outputStream));
    }

    public StreamLogger(Writer writer) {
        this.writer = writer;
    }

    @Override
    protected void print(String text) {
        try {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
