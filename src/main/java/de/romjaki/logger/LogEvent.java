package de.romjaki.logger;

/**
 * Created by RGR on 17.08.2017.
 */
public abstract class LogEvent {

    boolean interrupted = false;
    Class<?> actor;

    public LogEvent(Class<?> actor) {
        this.actor = actor;
    }

    public void interrupt() {
        interrupted = true;
    }

    public Class<?> getActor() {
        return actor;
    }

    public boolean isInterrupted() {
        return interrupted;
    }
}
