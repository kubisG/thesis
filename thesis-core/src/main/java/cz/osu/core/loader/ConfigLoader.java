package cz.osu.core.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import cz.osu.core.exception.FileRuntimeException;
import cz.osu.core.util.PathBuilderUtils;

/**
 * Project: thesis
 * Created by Jakub on 18. 7. 2017.
 *
 * Class which is used to load configuration file in XML format. Configuration
 * file contains paths to directories input, processed and bad.
 */
@Component
public class ConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

    private static final String DEFAULT_BATCH_CONFIG_PATH = "DEFAULT";

    /**
     * Method which parse XML configuration file and return Map.
     * @return Map. In this case - key is directory type (input, processed or bad) and
     * value is absolute path to directories.
     */
    public Map<String, String> loadPathsFromXML(String configPath) {
        Map<String, String> mappedPaths = new HashMap<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(getConfigPath(configPath));
            XPathFactory xpFactory = XPathFactory.newInstance();
            XPath xPath = xpFactory.newXPath();
            XPathExpression ex = xPath.compile("//directories//directory");

            Node root = doc.getDocumentElement();
            NodeList nl = (NodeList) ex.evaluate(root, XPathConstants.NODESET);
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n instanceof Element) {
                    Element parent = (Element) n;
                    String key = parent.getAttribute("type");
                    NodeList l = parent.getElementsByTagName("path");
                    String value = l.item(0).getFirstChild().getNodeValue();
                    mappedPaths.put(key, value);
                }
            }
        } catch (SAXException | DOMException | IOException |
                XPathExpressionException | ParserConfigurationException ex) {
            // LOGGER.debug(ExceptionMessage.CONFIG_XML_FAIL);
            LOGGER.trace("Inner message: ", ex);
            throw new FileRuntimeException("Fail during loading configuration file."
                    + "Without config file this program is unable to continue.", ex);
        }
        return mappedPaths;
    }

    private String getConfigPath(String configPath) throws IOException {
        if (!DEFAULT_BATCH_CONFIG_PATH.equals(configPath)) {
            return configPath;
        }
        // find path to jar represented the tool, go up to parent folder, go to config folder and find config.xml
        return PathBuilderUtils.buildPath("config", "config.xml");
    }
}
