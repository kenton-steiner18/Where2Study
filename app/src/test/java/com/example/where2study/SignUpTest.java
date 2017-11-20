package com.example.where2study;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SignUpTest extends ActivityInstrumentationTestCase2<SignUp> {
    private SignUp signUpActivity;
    private Instrumentation signUpActivityInstrumentation = null;

    public SignUpTest() {
        super("com.example.where2study.SignUp", SignUp.class);
    }


    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        signUpActivityInstrumentation = getInstrumentation();
        signUpActivity = getActivity();
    }

    @Test
    public void testPreconditions() {
        assertNotNull(signUpActivity);
    }

    @Test
    public void testEmailValid() {
        assertThat(SignUp.isEmailValid(""), is(false));
        assertThat(SignUp.isEmailValid("kentonsteiner.com"), is(false));
        assertThat(SignUp.isEmailValid("kenton@steinercom"), is(true));
        assertThat(SignUp.isEmailValid("kenton@steiner.com"), is(true));
    }

    @Test
    public void testPasswordValid() {
        assertThat(SignUp.isPasswordValid(""), is(false));
        assertThat(SignUp.isPasswordValid("asdfg"), is(false));
        assertThat(SignUp.isPasswordValid("helloworld"), is(false));
        assertThat(SignUp.isPasswordValid("Reallyclose"), is(false));
        assertThat(SignUp.isPasswordValid("qwertyuiopasdfghjkl"), is(false));
        assertThat(SignUp.isPasswordValid("Thisisgood1"), is(true));
    }


    protected void tearDown() throws Exception {

        super.tearDown();
    }
}