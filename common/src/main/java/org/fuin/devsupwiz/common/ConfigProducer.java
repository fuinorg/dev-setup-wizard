package org.fuin.devsupwiz.common;

import java.net.MalformedURLException;
import java.net.URL;

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
        if (parameters == null || parameters.getUnnamed() == null
                || parameters.getUnnamed().size() == 0) {
            throw new RuntimeException("No configuration URL provided");
        }
        final String urlStr = parameters.getUnnamed().get(0);
        final URL url = url(urlStr);
        return ConfigImpl.load(url);
    }

    /**
     * Returns the URL.
     * 
     * @return Config URL.
     */
    private URL url(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException ex) {
            throw new IllegalStateException("Invalid URL: " + url, ex);
        }
    }

}
