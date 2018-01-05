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
package org.fuin.devsupwiz.tasks.gitsetup;

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import static org.fuin.devsupwiz.common.DevSupWizUtils.MDC_TASK_KEY;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Security;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FileUtils;
import org.fuin.devsupwiz.common.DevSupWizUtils;
import org.fuin.devsupwiz.common.SetupTask;
import org.fuin.devsupwiz.common.ValidateInstance;
import org.fuin.utils4j.Utils4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Generates a key pair and adds it to the "~/.ssh/config" file. The public key
 * is also submitted to the git provider (Bitbucket, Github) using a REST API.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = SetupGitSshTask.KEY)
public class SetupGitSshTask implements SetupTask {

    /** Unique normalized name of the task (for example used for FXML file). */
    static final String KEY = "setup-git-ssh";

    private static final Logger LOG = LoggerFactory
            .getLogger(SetupGitSshTask.class);

    @NotEmpty
    @XmlAttribute(name = "id")
    private String id;

    @NotEmpty(message = "{setup-git-ssh.name.empty}")
    @XmlTransient
    private String name;

    @NotEmpty(message = "{setup-git-ssh.password.empty}")
    @XmlTransient
    private String password;

    @NotNull(message = "{setup-git-ssh.provider.null}")
    @XmlAttribute(name = "provider")
    private String provider;

    @NotEmpty(message = "{setup-git-ssh.host.empty}")
    @XmlAttribute(name = "host")
    private String host;

    @NotNull(message = "sshDir==null")
    @XmlTransient
    private File sshDir;

    @XmlTransient
    private boolean postPublicKey;

    @XmlTransient
    private Preferences userPrefs;

    /**
     * Default constructor for JAXB.
     */
    protected SetupGitSshTask() {
        super();
        sshDir = new File(Utils4J.getUserHomeDir(), ".ssh");
        postPublicKey = true;
        userPrefs = Preferences.userRoot();
    }

    /**
     * Constructor for tests.
     * 
     * @param id
     *            Unique task identifier.
     * @param name
     *            User's name.
     * @param password
     *            User's password.
     * @param provider
     *            Git provider.
     * @param host
     *            Host name (Domain without "www").
     * @param postPublicKey
     *            Post public key via REST API to provider.
     */
    public SetupGitSshTask(@NotEmpty final String id,
            @NotEmpty final String name, @NotEmpty final String password,
            @NotNull final GitProvider provider, @NotEmpty final String host,
            final boolean postPublicKey) {
        this(id, name, password, provider, host,
                new File(Utils4J.getUserHomeDir(), ".ssh"), postPublicKey);
    }

    /**
     * Constructor for tests.
     * 
     * @param id
     *            Unique task identifier.
     * @param name
     *            User's name.
     * @param password
     *            User's password.
     * @param provider
     *            Git provider.
     * @param host
     *            Host name (Domain without "www").
     * @param sshDir
     *            SSH directory.
     * @param postPublicKey
     *            Post public key via REST API to provider.
     */
    public SetupGitSshTask(@NotEmpty final String id,
            @NotEmpty final String name, @NotEmpty final String password,
            @NotNull final GitProvider provider, @NotEmpty final String host,
            @NotNull final File sshDir, final boolean postPublicKey) {
        super();
        this.id = id;
        this.name = name;
        this.password = password;
        this.provider = provider.name().toLowerCase();
        this.host = host;
        this.sshDir = sshDir;
        this.postPublicKey = postPublicKey;
        this.userPrefs = Preferences.userRoot();
    }

    /**
     * Returns user's name.
     * 
     * @return First name and last name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets user's name.
     * 
     * @param name
     *            First name and last name.
     */
    public void setName(@NotEmpty final String name) {
        this.name = name;
    }

    /**
     * Returns user's password.
     * 
     * @return Password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets user's password.
     * 
     * @param password
     *            Password.
     */
    public void setPassword(@NotEmpty final String password) {
        this.password = password;
    }

    /**
     * Returns the provider.
     * 
     * @return Git provider.
     */
    public GitProvider getProvider() {
        if (provider == null) {
            return null;
        }
        return GitProvider.valueOf(provider.toUpperCase());
    }

    /**
     * Sets the provider.
     * 
     * @param provider
     *            Git provider.
     */
    public void setProvider(@NotNull final GitProvider provider) {
        this.provider = provider.name().toLowerCase();
    }

    /**
     * Returns host name.
     * 
     * @return Host name (Domain without "www").
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets host name.
     * 
     * @param host
     *            Host name (Domain without "www").
     */
    public void setHost(@NotEmpty final String host) {
        this.host = host;
    }

    @Override
    public boolean alreadyExecuted() {
        return userPrefs.getBoolean(getPrefKey(), false);
    }

    private String getPrefKey() {
        return getType() + "-" + getId();
    }

    @ValidateInstance
    @Override
    public void execute() {

        MDC.put(MDC_TASK_KEY, getTypeId());
        try {

            if (!alreadyExecuted()) {

                init();

                final SshKeyPairGenerator generator = generateKeys(sshDir,
                        getPrivateKeyFile(), getPublicKeyFile());
                if (postPublicKey) {
                    if (GitProvider.BITBUCKET == getProvider()) {
                        postPublicKeyToBitbucket(generator.getPublicKey());
                    } else if (GitProvider.GITHUB == getProvider()) {
                        throw new UnsupportedOperationException("Provider "
                                + GitProvider.GITHUB + " is not supported yet");
                    } else {
                        throw new IllegalStateException(
                                "Provider unknown: " + provider);
                    }
                }
                
                appendToConfig(getPrivateKeyFile());

                if (postPublicKey) {
                    DevSupWizUtils.addToSshKnownHosts(host);
                }

                try {
                    userPrefs.putBoolean(getPrefKey(), true);
                    userPrefs.flush();
                } catch (final BackingStoreException ex) {
                    throw new RuntimeException("Failed to save the setup key '"
                            + getPrefKey() + "'", ex);
                }
                LOG.info("Successfully finished SSH git setup");

            }

        } finally {
            MDC.remove(MDC_TASK_KEY);
        }

    }

    @Override
    public String getResource() {
        return this.getClass().getPackage().getName().replace('.', '/') + "/"
                + KEY;
    }

    @Override
    public String getFxml() {
        return "/" + getResource() + ".fxml";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return KEY;
    }

    @Override
    public String getTypeId() {
        return getType() + "[" + getId() + "]";
    }

    private String getKeyFilename() {
        return name + "-" + host;
    }

    private File getConfigFile() {
        return new File(sshDir, "config");
    }

    private File getPrivateKeyFile() {
        return new File(sshDir, getKeyFilename() + ".prv");
    }

    private File getPublicKeyFile() {
        return new File(sshDir, getKeyFilename() + ".pub");
    }

    private void init() {

        // Make sure BC is available
        if (Security.getProvider("BC") == null) {
            Security.addProvider(
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }

        // Ensure SSH directory exists
        if (!sshDir.exists()) {
            if (!sshDir.mkdirs()) {
                throw new IllegalStateException(
                        "Failed to create directory: " + sshDir);
            }
        }

    }

    private SshKeyPairGenerator generateKeys(final File sshDir,
            final File prvKeyFile, final File pubKeyFile) {
        try {
            final SshKeyPairGenerator generator = new SshKeyPairGenerator(name);

            FileUtils.writeStringToFile(prvKeyFile, generator.getPrivateKey(),
                    Charset.forName("us-ascii"));
            // Only owner is allowed to access private key
            DevSupWizUtils.setFilePermissions(prvKeyFile, OWNER_READ,
                    OWNER_WRITE);

            FileUtils.writeStringToFile(pubKeyFile, generator.getPublicKey(),
                    Charset.forName("us-ascii"));

            LOG.info("Successfully generated and saved ssh keys: {} {}",
                    prvKeyFile, pubKeyFile);

            return generator;
        } catch (final IOException ex) {
            throw new RuntimeException(
                    "Wasn't able to write ssh keys to: " + sshDir, ex);
        }
    }

    private void postPublicKeyToBitbucket(final String publicKey) {
        final String url = "https://api.bitbucket.org/1.0/users/" + name
                + "/ssh-keys";
        try {
            final String hostname = DevSupWizUtils.getHostname();

            final HttpResponse<String> jsonResponse = Unirest.post(url)
                    .basicAuth(name, password)
                    .header("accept", "application/json")
                    .field("label", hostname).field("key", publicKey)
                    .asString();
            LOG.info("[" + jsonResponse.getStatus() + "] "
                    + jsonResponse.getStatusText() + ": " + jsonResponse);
            jsonResponse.getHeaders().forEach((key, values) -> {
                LOG.info(key + "=" + values);
            });
            if (jsonResponse.getStatus() != 200) {
                throw new RuntimeException("Error posting public key: "
                        + jsonResponse.getStatusText() + " ["
                        + jsonResponse.getStatus() + "]");
            }
            LOG.info("Successfully posted public ssh key: {}", url);
            LOG.debug(jsonResponse.getBody());
        } catch (final UnirestException ex) {
            throw new RuntimeException(
                    "Wasn't able to post public ssh key to: " + url, ex);
        }
    }

    private void appendToConfig(final File prvKeyFile) {
        try {

            final String lf = System.lineSeparator();
            final String str = "Host " + host + lf + "    User " + name + lf
                    + "    HostName " + host + lf + "    IdentityFile "
                    + prvKeyFile + lf;

            try (final FileWriter fw = new FileWriter(getConfigFile(), true)) {
                fw.write(str);
            }

            LOG.info("Successfully added entry to ssh config: {}",
                    getConfigFile());

        } catch (final IOException ex) {
            throw new RuntimeException(
                    "Wasn't able to write ssh config: " + getConfigFile(), ex);
        }
    }

}
