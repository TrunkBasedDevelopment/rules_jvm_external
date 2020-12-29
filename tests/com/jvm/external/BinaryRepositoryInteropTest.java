package com.jvm.external;

import org.http4k.client.ApacheClient;
import org.http4k.core.Uri;
import org.http4k.server.SunHttp;
import org.http4k.servirtium.ServirtiumServer;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import rules.jvm.external.maven.MavenPublisher;
import servirtium.http4k.ClimateApi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.http4k.servirtium.InteractionStorage.Disk;
import static servirtium.http4k.java.JUnitUtil.getMarkdownNameFrom;

public class BinaryRepositoryInteropTest {

    public Uri uri() {
        return Uri.of("http://localhost:" + servirtium.port());
    }

    private ServirtiumServer servirtium;

    @BeforeEach
    public void start(TestInfo info) {
        servirtium = ServirtiumServer.Recording(
                getMarkdownNameFrom(info),
                "https://oss.sonatype.org/content/repositories/releases/",
                Disk(new File("tests/resources")),
                new ClimateInteractionOptions(), 0, SunHttp::new,
                ApacheClient.create()
        );
        servirtium.start();
    }

    @AfterEach
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
