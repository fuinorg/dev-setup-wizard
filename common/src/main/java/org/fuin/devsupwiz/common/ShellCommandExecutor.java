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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.fuin.utils4j.Utils4J;

/**
 * Executes a shell command.
 * 
 * See for APT:
 * http://www.microhowto.info/howto/perform_an_unattended_installation_of_a_debian_package.html
 * apt-get install -q -y -o Dpkg::Options::="--force-confdef" -o
 * Dpkg::Options::="--force-confold" apache2 mysql-server
 *
 */
public final class ShellCommandExecutor {

    private final String pw;

    private final String cmd;

    private final long timeoutSeconds;

    private final Map<String, String> env;

    private final OutputStream out;

    private final OutputStream err;

    private final File workingDir;

    /**
     * Constructor without password and default working directory (user home).
     * 
     * @param cmd
     *            Command to execute.
     * @param timeoutSeconds
     *            Timeout in seconds.
     * @param env
     *            Environment variables.
     * @param out
     *            Standard output stream.
     * @param err
     *            Error output stream.
     */
    public ShellCommandExecutor(@NotEmpty final String cmd,
            final long timeoutSeconds, @NotNull final Map<String, String> env,
            @NotNull final OutputStream out, @NotNull final OutputStream err) {
        this(null, cmd, timeoutSeconds, env, out, err,
                Utils4J.getUserHomeDir());
    }

    /**
     * Constructor without password.
     * 
     * @param cmd
     *            Command to execute.
     * @param timeoutSeconds
     *            Timeout in seconds.
     * @param env
     *            Environment variables.
     * @param out
     *            Standard output stream.
     * @param err
     *            Error output stream.
     */
    public ShellCommandExecutor(@NotEmpty final String cmd,
            final long timeoutSeconds, @NotNull final Map<String, String> env,
            @NotNull final OutputStream out, @NotNull final OutputStream err,
            @NotNull final File workingDir) {
        this(null, cmd, timeoutSeconds, env, out, err, workingDir);
    }

    /**
     * Constructor with password (for "sudo" commands) and default working
     * directory (user home).
     * 
     * @param pw
     *            Password for "sudo".
     * @param cmd
     *            Command to execute.
     * @param timeoutSeconds
     *            Timeout in Seconds.
     * @param env
     *            Environment variables.
     * @param out
     *            Standard output stream.
     * @param err
     *            Error output stream.
     */
    public ShellCommandExecutor(@Nullable final String pw,
            @NotEmpty final String cmd, final long timeoutSeconds,
            @NotNull final Map<String, String> env,
            @NotNull final OutputStream out, @NotNull final OutputStream err) {

        this(pw, cmd, timeoutSeconds, env, out, err, Utils4J.getUserHomeDir());

    }

    /**
     * Constructor with password (for "sudo" commands).
     * 
     * @param pw
     *            Password for "sudo".
     * @param cmd
     *            Command to execute.
     * @param timeoutSeconds
     *            Timeout in Seconds.
     * @param env
     *            Environment variables.
     * @param out
     *            Standard output stream.
     * @param err
     *            Error output stream.
     */
    public ShellCommandExecutor(@Nullable final String pw,
            @NotEmpty final String cmd, final long timeoutSeconds,
            @NotNull final Map<String, String> env,
            @NotNull final OutputStream out, @NotNull final OutputStream err,
            @NotNull final File workingDir) {
        super();
        this.pw = pw;
        this.cmd = cmd;
        this.timeoutSeconds = timeoutSeconds;
        this.out = out;
        this.err = err;
        this.env = env;
        this.workingDir = workingDir;
    }

    /**
     * Executes the command.
     * 
     * @return Exit code.
     * 
     * @throws ExecuteException
     *             Execution failed.
     */
    public final int execute() {

        try {

            final Map<String, String> envMap = EnvironmentUtils
                    .getProcEnvironment();
            envMap.put("DEBIAN_FRONTEND", "noninteractive");
            envMap.putAll(env);

            final CommandLine command = new CommandLine("/bin/sh");
            final String cmdStr = createSubCmd();
            command.addArguments(new String[] { "-c", cmdStr }, false);

            final DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(new PumpStreamHandler(out, err));
            executor.setWorkingDirectory(workingDir);
            executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
            executor.setWatchdog(new ExecuteWatchdog(timeoutSeconds * 1000));

            return executor.execute(command, envMap);

        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private String createSubCmd() {
        if (pw == null) {
            return cmd;
        }
        return "echo " + pw + " | sudo -S " + cmd;
    }

}
