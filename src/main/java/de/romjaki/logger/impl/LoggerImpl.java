package de.romjaki.logger.impl;


public class LoggerImpl extends StreamLogger {
    public LoggerImpl(String name) {
        super(name, System.out);
    }

    public LoggerImpl() {
        super(System.out);
    }
}
