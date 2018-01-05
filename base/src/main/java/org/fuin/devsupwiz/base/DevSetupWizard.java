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
package org.fuin.devsupwiz.base;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fuin.devsupwiz.common.BootstrapBean;
import org.fuin.ext4logback.LogbackStandalone;
import org.fuin.utils4j.PropertiesFilePreferencesFactory;
import org.fuin.utils4j.Utils4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.Unirest;

import de.perdoctus.fx.Bundle;
import de.perdoctus.fx.FxWeldApplication;
import javafx.application.Application.Parameters;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Developer setup JavaFX application.
 */
public class DevSetupWizard extends FxWeldApplication {

    private static final Logger LOG = LoggerFactory
            .getLogger(DevSetupWizard.class);

    private static final String RESOURCE_PATH = "org/fuin/devsupwiz/base";

    @Inject
    @Bundle(RESOURCE_PATH + "/main")
    private FXMLLoader fxmlLoader;

    @Inject
    private BootstrapBean bootstrapBean;

    private void initLogbackXml(final File logbackXmlFile) {
        if (!logbackXmlFile.exists()) {
            try {
                final String xml = IOUtils.resourceToString(
                        "/template-logback.xml", Charset.forName("utf-8"));
                FileUtils.write(logbackXmlFile, xml, Charset.forName("utf-8"));
            } catch (final IOException ex) {
                throw new RuntimeException(
                        "Error creating logback config: " + logbackXmlFile, ex);
            }
        }
    }

    @Override
    public void init() throws Exception {

        // Initialize logging
        try {
            final File logbackXmlFile = new File(
                    "dev-setup-wizard-logback.xml");
            initLogbackXml(logbackXmlFile);
            new LogbackStandalone().init(logbackXmlFile);
        } catch (final RuntimeException ex) {
            System.err.println("Error initializing logging");
            ex.printStackTrace(System.err);
            System.exit(1);
        }

        // Initialize user preferences
        final File userPrefDir = new File(Utils4J.getUserHomeDir(),
                ".dev-setup");
        if (!userPrefDir.exists()) {
            userPrefDir.mkdir();
        }
        System.setProperty(PropertiesFilePreferencesFactory.USER_PREF_DIR,
                userPrefDir.toString());
        System.setProperty("java.util.prefs.PreferencesFactory",
                PropertiesFilePreferencesFactory.class.getName());
    }

    @Override
    public void start(final Stage stage, final Parameters parameters)
            throws IOException {

        LOG.info("Start application");

        // Catch all exceptions in UI thread
        Thread.currentThread()
                .setUncaughtExceptionHandler((thread, throwable) -> {
                    final Logger log = LoggerFactory
                            .getLogger(DevSetupWizard.class);
                    log.error("Uncaught Exception in " + thread, throwable);
                });

        // Set bootstrap information
        bootstrapBean.setParameters(parameters);

        // Start UI
        final ResourceBundle resources = fxmlLoader.getResources();
        fxmlLoader.setLocation(
                getClass().getResource("/" + RESOURCE_PATH + "/main.fxml"));
        final Parent parent = fxmlLoader.load();
        final Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle(resources.getString("title"));
        stage.show();

    }

    @Override
    public void stop() throws Exception {
        Unirest.shutdown();
    }

}
