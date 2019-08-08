package com.revolut.fundtransfer.rest;

import com.revolut.fundtransfer.model.UserAccount;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;

import static org.junit.Assert.assertTrue;

public class TestUserAccountRest extends TestRest {

    @Test
    public void testGetUserAccountByName() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/ac/1").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("GET USER ACCOUNT BY NAME failed", statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        UserAccount account = objMapper.readValue(jsonString, UserAccount.class);
        assertTrue("GET USER ACCOUNT BY NAME failed", account.getUserName().equals("JOHN"));
    }

    @Test
    public void testGetAllAccounts() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/ac/all").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("GET ALL USER ACCOUNTS failed", statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        UserAccount[] accounts = objMapper.readValue(jsonString, UserAccount[].class);
        assertTrue("GET ALL USER ACCOUNTS failed", accounts.length > 0);
    }

    @Test
    public void testGetAccountBalance() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/ac/1/balance").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("GET ACCOUNT BALANCE failed", statusCode == 200);
        String balance = EntityUtils.toString(response.getEntity());
        BigDecimal res = new BigDecimal(balance).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal db = new BigDecimal(1000).setScale(4, RoundingMode.HALF_EVEN);
        assertTrue("GET ACCOUNT BALANCE failed", res.equals(db));
    }

    @Test
    public void testCreateAccount() throws Exception {
        Thread.sleep(2000);
        URI uri = builder.setPath(BASE_URI + "/ac/create").build();
        BigDecimal balance = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        UserAccount acc = new UserAccount("test250", balance, "CNY");
        String jsonInString = objMapper.writeValueAsString(acc);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("CREATE ACCOUNT failed", statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        UserAccount createdAc = objMapper.readValue(jsonString, UserAccount.class);
        assertTrue("CREATE ACCOUNT failed", createdAc.getUserName().equals("test250"));
        assertTrue("CREATE ACCOUNT failed", createdAc.getCurrencyCode().equals("CNY"));
    }

    @Test
    public void testCreateExistingAccount() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/ac/create").build();
        UserAccount acc = new UserAccount("JOHN", new BigDecimal(0), "USD");
        String jsonInString = objMapper.writeValueAsString(acc);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("CREATE EXISTING ACCOUNT failed",statusCode == 500);
    }

    @Test
    public void testDeleteAccount() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/ac/3").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("DELETE ACCOUNT failed", statusCode == 200);
    }

    @Test
    public void testDeleteNonExistingAccount() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/ac/300").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("DELETE NON EXISTING ACCOUNT failed", statusCode == 404);
    }
}
