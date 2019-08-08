package com.revolut.fundtransfer.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public enum AppConfigHelper {

    ACH;
    private final Logger LOGGER = LogManager.getLogger(AppConfigHelper.class);
    private Properties properties = new Properties();

    AppConfigHelper() {
        try {
            LOGGER.info("loading app properties");
            final InputStream fis = AppConfigHelper.class.getClassLoader().getResourceAsStream("app.properties");
            properties.load(fis);
            fis.close();
        } catch (Exception ex) {
            LOGGER.error("error while loading app properties : {}", ex.getMessage());
            System.exit(0);
        }
    }

    public String getPropValue(String key) {
        return properties.getProperty(key)  == null ? "" : properties.getProperty(key);
    }
}
