package com.example.guilherme.uottawa;

import android.os.SystemClock;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import nigelhenshaw.com.cameraintenttutorial.FaceAPI;
import nigelhenshaw.com.cameraintenttutorial.Message;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testMessage(){
        Message msg = new Message(true,"xxx");
        assertEquals("xxx",msg.msg);
    }
}