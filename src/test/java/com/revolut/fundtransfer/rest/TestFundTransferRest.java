package com.revolut.fundtransfer.rest;


import com.revolut.fundtransfer.model.FundTransferTransaction;
import com.revolut.fundtransfer.model.UserAccount;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;

import static org.junit.Assert.assertTrue;


public class TestFundTransferRest extends TestRest {

    @Test
    public void testDeposit() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/ac/1/deposit/1000").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("DEPOSIT AMOUNT failed", statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        UserAccount updatedAc = objMapper.readValue(jsonString, UserAccount.class);
        assertTrue("DEPOSIT AMOUNT failed", updatedAc.getBalanceAmount().equals(new BigDecimal(2000).setScale(4, RoundingMode.HALF_EVEN)));
    }

    @Test
    public void testWithDrawWithSufficientFund() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/ac/2/withdraw/1000").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("WITHDRAW WITH SUFFICIENT AMOUNT failed", statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        UserAccount updatedAc = objMapper.readValue(jsonString, UserAccount.class);
        assertTrue("WITHDRAW WITH SUFFICIENT AMOUNT failed", updatedAc.getBalanceAmount().equals(new BigDecimal(1000).setScale(4, RoundingMode.HALF_EVEN)));
    }

    @Test
    public void testWithDrawNonSufficientFund() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/ac/2/withdraw/1000.23456").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());
        assertTrue("WITHDRAW INSUFFICIENT AMOUNT failed", statusCode == 500);
        assertTrue("WITHDRAW INSUFFICIENT AMOUNT failed", responseBody.contains("Not sufficient Fund"));
    }

    @Test
    public void testFundTransferWithEnoughFund() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/trans").build();
        BigDecimal amount = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        FundTransferTransaction transaction = new FundTransferTransaction("EUR", amount, 3L, 4L);

        String jsonInString = objMapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("ENOUGH FUND TRANSFER failed", statusCode == 200);
    }

    @Test
    public void testFundTransferWithoutEnoughFund() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/trans").build();
        BigDecimal amount = new BigDecimal(100000).setScale(4, RoundingMode.HALF_EVEN);
        FundTransferTransaction transaction = new FundTransferTransaction("EUR", amount, 3L, 4L);

        String jsonInString = objMapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("WITHOUT ENOUGH FUND failed",statusCode == 500);
    }

    @Test
    public void testFundTransferWithDifferentCurrency() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/trans").build();
        BigDecimal amount = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        FundTransferTransaction transaction = new FundTransferTransaction("USD", amount, 3L, 4L);
        String jsonInString = objMapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("FUND TRANSFER WITH DIFFERENT CURRENCY failed",statusCode == 500);
    }
}
