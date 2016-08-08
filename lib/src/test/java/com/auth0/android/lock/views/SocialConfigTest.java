/*
 * SocialConfigTest.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock.views;

import android.os.Build;

import com.auth0.android.lock.BuildConfig;
import com.auth0.android.lock.R;
import com.auth0.android.lock.utils.json.Application;
import com.auth0.android.lock.utils.json.Connection;
import com.auth0.android.lock.utils.json.GsonBaseTest;
import com.auth0.android.lock.utils.json.Strategy;
import com.google.gson.stream.JsonReader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.FileReader;
import java.util.Collections;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class SocialConfigTest extends GsonBaseTest {

    private Application application;
    private SocialConfig socialConfig;

    @Before
    public void setUp() throws Exception {
        final FileReader fr = new FileReader("src/test/resources/appinfo.json");
        application = createGson().fromJson(new JsonReader(fr), Application.class);

        socialConfig = new SocialConfig(RuntimeEnvironment.application, application.getSocialStrategies().get(0));
        //sample appinfo.json has 'Facebook' Strategy at the first position
    }

    @Test
    public void shouldThrowExceptionIfStrategyIsEnterprise() throws Exception {
        try {
            new SocialConfig(RuntimeEnvironment.application, application.getEnterpriseStrategies().get(0));
            fail("Should throw Exception if strategy is Enterprise.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Only SOCIAL Strategies can have a SocialConfig"));
        }
    }

    @Test
    public void shouldThrowExceptionIfStrategyIsDatabase() throws Exception {
        try {
            new SocialConfig(RuntimeEnvironment.application, application.getDatabaseStrategy());
            fail("Should throw Exception if strategy is Database.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Only SOCIAL Strategies can have a SocialConfig"));
        }
    }

    @Test
    public void shouldGetUnknownName() throws Exception {
        Strategy unknownStrategy = new Strategy("MY-NEW SOCIAL-NETWORK *#!", Collections.<Connection>emptyList());
        SocialConfig unknownSocialConfig = new SocialConfig(RuntimeEnvironment.application, unknownStrategy);
        assertThat(unknownSocialConfig.getName(), is("MY-NEW SOCIAL-NETWORK *#!"));
    }

    @Test
    public void shouldGetName() throws Exception {
        String name = RuntimeEnvironment.application.getString(R.string.com_auth0_lock_social_facebook);
        assertThat(socialConfig.getName(), is(name));
    }

    @Test
    public void shouldGetIcon() throws Exception {
        assertThat(socialConfig.getIcon(), is(R.drawable.com_auth0_lock_ic_social_facebook));
    }

    @Test
    public void shouldGetBackgroundColor() throws Exception {
        android.app.Application context = RuntimeEnvironment.application;
        int expectedColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            expectedColor = context.getResources().getColor(R.color.com_auth0_lock_social_facebook, context.getTheme());
        } else {
            //noinspection deprecation
            expectedColor = context.getResources().getColor(R.color.com_auth0_lock_social_facebook);
        }
        assertThat(socialConfig.getBackgroundColor(), is(equalTo(expectedColor)));
    }

}