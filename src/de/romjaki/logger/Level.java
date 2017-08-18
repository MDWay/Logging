package de.romjaki.logger;

/**
 * Created by RGR on 17.08.2017.
 */
public enum Level {
    ALL,
    FAILURE,
    ERROR,
    WARNING,
    INFO,
    DEBUG,
    OFF;

    public boolean logs(Level level) {
        return this.ordinal() <= level.ordinal();
    }
}
