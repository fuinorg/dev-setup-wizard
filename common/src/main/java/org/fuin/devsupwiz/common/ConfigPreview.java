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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Extract from the configuration XML.
 */
public class ConfigPreview {

    private final String xml;

    private List<String> classNames;

    /**
     * Constructor with XML.
     * 
     * @param xml
     *            Config XML.
     */
    public ConfigPreview(@NotNull final String xml) {
        super();
        this.xml = xml;
    }

    /**
     * Returns the list of full qualified class names.
     * 
     * @return Class list.
     */
    public List<String> getClassNames() {
        if (classNames == null) {
            throw new IllegalStateException(
                    "Please call 'load()' before reading the classes");
        }
        return classNames;
    }

    /**
     * Loads the XML from the URL provided in the constructor.
     * 
     * @return This instance.
     */
    public ConfigPreview load() {
        classNames = new ArrayList<>();
        try {
            try (final InputStream in = new ByteArrayInputStream(
                    xml.getBytes())) {
                final DocumentBuilder builder = DocumentBuilderFactory
                        .newInstance().newDocumentBuilder();
                final Document doc = builder.parse(in);
                final XPath xPath = XPathFactory.newInstance().newXPath();

                final NodeList taskClassAttributes = (NodeList) xPath
                        .compile("//@task-class")
                        .evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < taskClassAttributes.getLength(); i++) {
                    final Node node = taskClassAttributes.item(i);
                    final String name = node.getTextContent();
                    classNames.add(name);
                }

            }
            return this;

        } catch (final ParserConfigurationException ex) {
            throw new RuntimeException(
                    "Error constructing XML parser for: " + xml, ex);
        } catch (final XPathExpressionException ex) {
            throw new RuntimeException(
                    "Error compiling XPath expression for XML parser for: "
                            + xml,
                    ex);
        } catch (final IOException | SAXException ex) {
            throw new RuntimeException("Error reading XML config: " + xml, ex);
        }
    }

}
