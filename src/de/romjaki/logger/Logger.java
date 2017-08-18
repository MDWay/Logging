package de.romjaki.logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.romjaki.logger.Level.*;

/**
 * Created by RGR on 17.08.2017.
 */
public class Logger {
    static String format = "[{LEVEL}][{DATE}]{NAME}: {TEXT}";
    static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    String name;
    Level logLevel = WARNING;
    List<LogHandler> handlers = new ArrayList<>();

    public Logger() {
        this("");
    }

    public Logger(String name) {
        this.name = name;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public String getName() {
        return name;
    }

    public void log(Level level, String text) {
        if (logLevel.logs(level)) {
            doLog(level, text);
        }
    }

    public void logf(Level level, String format, Object... fillIn) {
        log(level, String.format(format, fillIn));
    }

    public void info(String text) {
        log(INFO, text);
    }

    public void infof(String format, Object... fillIn) {
        logf(INFO, format, fillIn);
    }

    public void warn(String text) {
        log(WARNING, text);
    }

    public void warnf(String format, Object... fillIn) {
        logf(WARNING, format, fillIn);
    }

    public void debug(String text) {
        log(DEBUG, text);
    }

    public void debugf(String format, Object... fillIn) {
        logf(DEBUG, format, fillIn);
    }

    public void error(String text) {
        log(ERROR, text);
    }

    public void errorf(String format, Object... fillIn) {
        logf(ERROR, format, fillIn);
    }

    public void failure(String text) {
        log(FAILURE, text);
    }

    public void failuref(String format, Object... fillIn) {
        logf(Level.FAILURE, format, fillIn);
    }

    public PrintStream getAsPrintStream() {
        return getAsPrintStream(ALL);
    }

    public PrintStream getAsPrintStream(Level level) {
        return getAsPrintStream(level, System.lineSeparator());
    }

    public PrintStream getAsPrintStream(Level level, String sep) {
        return new PrintStream(new OutputStream() {
            StringBuffer buffer = new StringBuffer();

            Object locker = new Object();

            @Override
            public void write(int b) throws IOException {
                char c = (char) b;
                synchronized (locker) {
                    buffer.append(c);
                    if (buffer.indexOf(sep) != -1) {
                        String[] splits = buffer.toString().split(sep, 2);
                        log(level, splits[0]);
                        if (splits.length > 1) {
                            buffer = new StringBuffer(splits[1].replace(sep, ""));
                        } else {
                            buffer = new StringBuffer();
                        }
                    }
                }
            }
        }, true);
    }

    public void exception(Throwable t) {
        t.printStackTrace(getAsPrintStream(ERROR));
    }

    void doLog(Level level, String text) {
        LogEvent event = new LoggedEvent(getCallingClass(), level, text);
        dispatchEvent(event);
        if (!event.isInterrupted()) {
            System.out.println(format
                    .replace("{LEVEL}", level.name())
                    .replace("{DATE}", dateFormat.format(new Date()))
                    .replace("{NAME}", name != null ? name : "")
                    .replace("{TEXT}", text));
        }
    }

    public void dispatchEvent(LogEvent event) {
        handlers.forEach(handler -> dispatchEvent(event, handler));
    }

    void dispatchEvent(LogEvent event, LogHandler handler) {
        for (Method method : handler.getClass().getMethods()) {
            invokeSingle(method, event, handler);
        }
    }

    void invokeSingle(Method method, LogEvent event, LogHandler handler) {
        if (method.isAnnotationPresent(EventHandler.class)) {
            Class<?>[] args = method.getParameterTypes();
            if (args.length == 1 && args[0].isInstance(event)) {
                try {
                    method.invoke(handler, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Class<?> getCallingClass() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = 1; i < trace.length; i++) {
            StackTraceElement ste = trace[i];
            if (!ste.getClass().equals(Logger.class) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return ste.getClass();
            }
        }
        return null;
    }

}
