package de.romjaki.logger;

/**
 * Created by RGR on 17.08.2017.
 */
public class LoggedEvent extends LogEvent {
    private final Level level;
    private final String text;

    public LoggedEvent(Class<?> clazz, Level level, String text) {
        super(clazz);
        this.level = level;
        this.text = text;
    }

    public Level getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }
}
