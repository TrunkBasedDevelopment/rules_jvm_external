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
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class BinaryRepositoryInteropTest {

    public Uri uri() {
        return Uri.of("http://localhost:61417");
    }

    private ServirtiumServer servirtium;

    @Before
    public void start() {
        servirtium = ServirtiumServer.Recording(
            "fred.md",
            Uri.of("https://oss.sonatype.org"),
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
        Path tempFile = Files.createTempFile(null, null);
        Files.write(tempFile, "one".getBytes(StandardCharsets.UTF_8));
        String pomPath = tempFile.toFile().getAbsolutePath();

        tempFile = Files.createTempFile(null, null);
        Files.write(tempFile, "two".getBytes(StandardCharsets.UTF_8));
        String binPath = tempFile.toFile().getAbsolutePath();

        tempFile = Files.createTempFile(null, null);
        Files.write(tempFile, "three".getBytes(StandardCharsets.UTF_8));
        String srcPath = tempFile.toFile().getAbsolutePath();;

        tempFile = Files.createTempFile(null, null);
        Files.write(tempFile, "four".getBytes(StandardCharsets.UTF_8));
        String docPath = tempFile.toFile().getAbsolutePath();;

        MavenPublisher.main(new String[]{uri() + "/content/repositories/releases/", "false", "fred",
                "fredPW", "aaGroup:bbArtifact:111.2.3",
                pomPath, binPath, srcPath, docPath
        });

    }

}
