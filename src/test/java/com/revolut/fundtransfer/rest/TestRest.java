package com.revolut.fundtransfer.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.fundtransfer.BootstrapApp;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class TestRest {

    protected static String BASE_URI = "/revolut/fundtransfer";
    protected static PoolingHttpClientConnectionManager conMgr = new PoolingHttpClientConnectionManager();
    protected static HttpClient httpClient;
    protected ObjectMapper objMapper = new ObjectMapper();
    protected URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:9090");

    @BeforeClass
    public static void init() throws Exception {
        BootstrapApp.bootstrap(false);
        conMgr.setDefaultMaxPerRoute(150);
        conMgr.setMaxTotal(250);
        httpClient = HttpClients.custom().setConnectionManager(conMgr).setConnectionManagerShared(true).build();
    }

    @AfterClass
    public static void closeServerAndClient() throws Exception {
        HttpClientUtils.closeQuietly(httpClient);
        BootstrapApp.stopServer();
    }
}

