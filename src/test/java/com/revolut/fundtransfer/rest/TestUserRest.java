package com.revolut.fundtransfer.rest;

import com.revolut.fundtransfer.model.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertTrue;

public class TestUserRest extends TestRest {

    @Test
    public void testGetUser() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/usr/LUCY").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("GET USER failed", statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        User user = objMapper.readValue(jsonString, User.class);
        assertTrue("GET USER failed", user.getName().equals("LUCY"));
        assertTrue("GET USER failed", user.getEmailId().equals("lucy369@gmail.com"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/usr/all").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("GET ALL USER failed", statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        User[] users = objMapper.readValue(jsonString, User[].class);
        assertTrue("GET ALL USER failed", users.length > 3);
    }

    @Test
    public void testCreateUser() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/usr/create").build();
        User user = new User("TEST_USER", "testuser123@gmail.com");
        String jsonInString = objMapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("CREATE USER failed", statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        User uAfterCreation = objMapper.readValue(jsonString, User.class);
        assertTrue("CREATE USER failed", uAfterCreation.getName().equals("TEST_USER"));
        assertTrue("CREATE USER failed", uAfterCreation.getEmailId().equals("testuser123@gmail.com"));
    }

    @Test
    public void testCreateExistingUser() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/usr/create").build();
        User user = new User("MERRY", "MERRY@gmail.com");
        String jsonInString = objMapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("CREATE EXISTING USER failed", statusCode == 400);
    }

    @Test
    public void testUpdateUser() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/usr/2").build();
        User user = new User(2L, "MERRY", "MERRY123@gmail.com");
        String jsonInString = objMapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("UPDATE USER failed", statusCode == 200);
    }

    @Test
    public void testUpdateNonExistingUser() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/usr/100").build();
        User user = new User(2L, "MERRY", "MERRY123@gmail.com");
        String jsonInString = objMapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("UPDATE USER failed", statusCode == 404);
    }

    @Test
    public void testDeleteUser() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/usr/3").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("DELETE USER failed", statusCode == 200);
    }

    @Test
    public void testDeleteNonExistingUser() throws Exception {
        URI uri = builder.setPath(BASE_URI + "/usr/300").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue("DELETE NON EXISTING USER failed", statusCode == 404);
    }
}
