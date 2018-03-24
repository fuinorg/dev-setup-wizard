package org.fuin.devsupwiz.common;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application.Parameters;

/**
 * Factory for configuration.
 */
@ApplicationScoped
public class ConfigProducer {

    private static final Logger LOG = LoggerFactory
            .getLogger(ConfigProducer.class);

    @Inject
    private BootstrapBean setupContext;

    /**
     * Creates an application scoped configuration.
     * 
     * @return Config.
     */
    @Loggable
    @ApplicationScoped
    @Produces
    public Config create() {
        final Parameters parameters = setupContext.getParameters();
        final File file = getConfigFile(parameters);
        return ConfigImpl.load(file);
    }

    private File getConfigFile(final Parameters parameters) {
        final String name = getConfigName(parameters);
        LOG.info("Configuration name: {}", name);
        final File file = new File("./" + name);
        if (!file.exists()) {
            throw new IllegalArgumentException(
                    "Configuration file does not exist: " + file);
        }
        return file;
    }

    private String getConfigName(final Parameters parameters) {
        if (parameters == null || parameters.getUnnamed() == null
                || parameters.getUnnamed().size() == 0) {
            return "project-setup.xml";
        }
        return parameters.getUnnamed().get(0);
    }

}
