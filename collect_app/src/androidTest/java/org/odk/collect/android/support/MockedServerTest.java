package org.odk.collect.android.support;

import org.junit.After;
import org.junit.Before;

import org.odk.collect.android.TestSettingsProvider;
import org.odk.collect.android.preferences.keys.GeneralKeys;
import org.odk.collect.android.preferences.source.SettingsProvider;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.odk.collect.android.support.TestUtils.backupPreferences;
import static org.odk.collect.android.support.TestUtils.restorePreferences;

public abstract class MockedServerTest {
    private Map<String, ?> prefsBackup;

    protected MockWebServer server;
    protected final SettingsProvider settingsProvider = TestSettingsProvider.getSettingsProvider();

    @Before
    public void http_setUp() throws Exception {
        prefsBackup = backupPreferences();
        server = mockWebServer();
    }

    @After
    public void http_tearDown() throws Exception {
        if (server != null) {
            server.shutdown();
        }

        if (prefsBackup != null) {
            restorePreferences(prefsBackup);
        }
    }

    protected void willRespondWith(String... rawResponses) {
        for (String rawResponse : rawResponses) {
            MockResponse response = new MockResponse();

            String[] parts = rawResponse.split("\r\n\r\n", 2);

            String[] headerLines = parts[0].split("\r\n");

            response.setStatus(headerLines[0]);

            for (int i = 1; i < headerLines.length; ++i) {
                String[] headerParts = headerLines[i].split(": ", 2);
                response.addHeader(headerParts[0], headerParts[1]);
            }

            response.setBody(parts[1]);

            server.enqueue(response);
        }
    }

    protected RecordedRequest nextRequest() throws Exception {
        return server.takeRequest(1, TimeUnit.MILLISECONDS);
    }

   protected static String join(String... strings) {
        StringBuilder bob = new StringBuilder();
        for (String s : strings) {
            bob.append(s).append('\n');
        }
        return bob.toString();
    }

    private MockWebServer mockWebServer() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();
        configAppFor(server);
        return server;
    }

    private void configAppFor(MockWebServer server) {
        settingsProvider.getGeneralSettings().save(GeneralKeys.KEY_SERVER_URL, server.url("/").toString());
    }
}
