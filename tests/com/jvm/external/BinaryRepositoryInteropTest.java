package com.jvm.external;

import static org.http4k.servirtium.InteractionStorage.Disk;

import org.http4k.client.ApacheClient;
import org.http4k.core.Uri;
import org.http4k.server.SunHttp;
import org.http4k.servirtium.InteractionOptions;
import org.http4k.servirtium.ServirtiumServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rules.jvm.external.maven.MavenPublisher;

import java.io.File;

public class BinaryRepositoryInteropTest {

    public Uri uri() {
        return Uri.of("http://localhost:61417");
    }

    private ServirtiumServer servirtium;

    @Before
    public void start() {
        servirtium = ServirtiumServer.Recording(
            "fred.md",
            Uri.of("https://oss.sonatype.org/content/repositories/releases/"),
            Disk(new File("tests/resources")),
            InteractionOptions.Defaults,
            61417,
            SunHttp::new,
            ApacheClient.create()
        );
        servirtium.start();
    }

    @After
    public void stop() {
        servirtium.stop();
    }

    @Test
    public void test_cannotPushToSonatype() throws Exception {
        String pomPath = "/tmp/something";
        String binPath = "/tmp/something2";
        String srcPath = "/tmp/something3";
        String docPath = "/tmp/something4";

        MavenPublisher.main(new String[]{uri() + "/repoRoot", "false", "fred",
                "fredPW", "aaGroup:bbArtifact:111.2.3",
                pomPath, binPath, srcPath, docPath
        });

    }

}
