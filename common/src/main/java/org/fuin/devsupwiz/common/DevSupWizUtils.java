/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. 
 * http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see http://www.gnu.org/licenses/.
 */
package org.fuin.devsupwiz.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import org.fuin.utils4j.JandexUtils;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

/**
 * Helper methods.
 */
public final class DevSupWizUtils {

    /** Key used for the MDC 'task' value. */
    public static final String MDC_TASK_KEY = "task";

    private static final Logger LOG = LoggerFactory.getLogger(DevSupWizUtils.class);

    private DevSupWizUtils() {
    }

    /**
     * Verifies if a property with the given name has an error.
     * 
     * @param violations
     *            All violations.
     * @param name
     *            Name of the property to locate.
     * 
     * @return <code>true</code> if there is an error for the property with that
     *         name.
     * 
     * @param <T>
     *            Type of the setup task.
     */
    public static <T extends SetupTask> boolean violated(final Set<ConstraintViolation<T>> violations, final String name) {
        for (ConstraintViolation<T> violation : violations) {
            final Path.Node node = violation.getPropertyPath().iterator().next();
            if (node.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prints the message on the stream.
     * 
     * @param out
     *            Output stream to use.
     * @param message
     *            Message to write.
     */
    public static void println(final OutputStream out, final String message) {
        try {
            out.write(message.getBytes());
            out.write(System.lineSeparator().getBytes());
        } catch (final IOException ex) {
            throw new RuntimeException("Wasn't able to write message to output: " + message, ex);
        }
    }

    /**
     * Tries to find an appender with the given name.
     * 
     * @param name
     *            Name of appender to locate.
     * 
     * @return Appender or <code>null</code> if no appender with that name was
     *         found.
     * 
     * @param <T>
     *            Expected appender type.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Appender<ILoggingEvent>> T findAppender(final String name) {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
                final Appender<ILoggingEvent> appender = index.next();
                if (name.equals(appender.getName())) {
                    return (T) appender;
                }
            }
        }
        return null;
    }

    /**
     * Reads a resource message and replaces parameters.
     * 
     * @param bundle
     *            Bundle to read.
     * @param key
     *            Key to read.
     * @param params
     *            Parameters to replace.
     * 
     * @return Message.
     */
    public static String getString(final ResourceBundle bundle, final String key, final Object... params) {
        return MessageFormat.format(bundle.getString(key), params);
    }

    /**
     * Reads the host name (using shell command 'hostname').
     * 
     * @return Host name
     */
    public static String getHostname() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ShellCommandExecutor executor = new ShellCommandExecutor("hostname", 5, new HashMap<String, String>(), bos,
                new LogOutputStream(Level.ERROR));
        final int result = executor.execute();
        final String text = new String(bos.toByteArray());
        if (result == 0) {
            return text.trim();
        } else {
            throw new RuntimeException("Error # " + result + " reading hostname: " + text);
        }
    }

    /**
     * Sets the posix file permissions for the given file.
     * 
     * @param file
     *            File to set permissions for.
     * @param permissions
     *            Permissions to set.
     */
    public static void setFilePermissions(final File file, final PosixFilePermission... permissions) {
        try {
            final Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
            for (final PosixFilePermission permission : permissions) {
                perms.add(permission);
            }
            Files.setPosixFilePermissions(file.toPath(), perms);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static List<String> implementors(final Index index, final Class<?> clasz) {
        final List<String> classNames = new ArrayList<>();
        final Set<ClassInfo> implementors = index.getAllKnownImplementors(DotName.createSimple(clasz.getName()));
        for (final ClassInfo ci : implementors) {
            classNames.add(ci.name().toString());
        }
        return classNames;
    }   

    /**
     * Returns all classes from the classpath (*.jar or *.class) that implement
     * the {@link SetupTask} interface.
     * 
     * @return List of full qualified class names.
     */
    public static List<String> findSetupTasksInClasspath() {
        final List<File> knownFiles = new ArrayList<File>();
        final Indexer indexer = new Indexer();
        JandexUtils.indexClasspath((URLClassLoader) DevSupWizFxUtils.class.getClassLoader(), indexer, knownFiles);
        final Index index = indexer.complete();
        return implementors(index, SetupTask.class);

    }

    /**
     * Tries to load all classes.
     * 
     * @param classNames
     *            Fully qualified class names.
     * 
     * @return Classes.
     */
    public static List<Class<?>> loadClasses(final List<String> classNames) {

        final List<Class<?>> classes = new ArrayList<>();
        for (final String className : classNames) {
            try {
                classes.add(Class.forName(className));
            } catch (final ClassNotFoundException ex) {
                throw new RuntimeException("Failed to load : " + className, ex);
            }
        }
        return classes;

    }

}
