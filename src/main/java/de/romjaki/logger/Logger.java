package de.romjaki.logger;

import de.romjaki.logger.impl.LoggerImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static de.romjaki.logger.Level.*;

/**
 * Created by RGR on 17.08.2017.
 */
public abstract class Logger {
    private static String format = "[{LEVEL}][{DATE}]{NAME}: {TEXT}";
    private static ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();
    private String name;
    private Level logLevel = WARNING;
    private Set<LogHandler> handlers = new HashSet<>();
    private DateFormat localDateFormat = new SimpleDateFormat(format);

    protected Logger() {
        this("");
    }

    protected Logger(String name) {
        this.name = name;
    }

    public static Logger getLogger(String name) {
        return loggers.computeIfAbsent(name.toLowerCase(), LoggerImpl::new);
    }

    public static Logger getLogger() {
        return getLogger(getCallingClass().getSimpleName());
    }

    private static Class<?> getCallingClass() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = 1; i < trace.length; i++) {
            StackTraceElement ste = trace[i];
            if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                try {
                    return Class.forName(ste.getClassName());
                } catch (ClassNotFoundException e) { // should never happen. THIS CLASS FUCKING CALLED US!
                    e.printStackTrace();
                }
            }
        }
        return Logger.class; // Apparently we called ourself since we traversed the entire stack without finding any other classes
    }

    public void setDateFormat(String newFormat) {
        setDateFormat(new SimpleDateFormat(newFormat));
    }

    private void setDateFormat(DateFormat dateFormat) {
        this.localDateFormat = dateFormat;
    }

    public void addEventHandler(LogHandler handler) {
        handlers.add(handler);
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

            final Object locker = new Object();

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

    private void doLog(Level level, String text) {
        LogEvent event = new LoggedEvent(getCallingClass(), level, text);
        dispatchEvent(event);
        if (!event.isInterrupted()) {
            println(format
                    .replace("{LEVEL}", level.name())
                    .replace("{DATE}", localDateFormat.format(new Date()))
                    .replace("{NAME}", name != null ? name : "")
                    .replace("{TEXT}", text));
        }
    }

    protected void println(String text) {
        print(text + System.lineSeparator());
    }

    protected abstract void print(String text);

    protected void dispatchEvent(LogEvent event) {
        handlers.forEach(handler -> dispatchEvent(event, handler));
    }

    protected void dispatchEvent(LogEvent event, LogHandler handler) {
        for (Method method : handler.getClass().getMethods()) {
            invokeSingle(method, event, handler);
        }
    }

    private void invokeSingle(Method method, LogEvent event, LogHandler handler) {
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

}
