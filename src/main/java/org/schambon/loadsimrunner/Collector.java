package org.schambon.loadsimrunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bson.Document;
import org.schambon.loadsimrunner.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Collector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Collector.class);
    private Reporter reporter;
    private HttpServer httpServer;
    private Poller poller;


    public Collector(Document config) {
        var reportConfig = (Document) config.get("report");
        var httpConfig = (Document) config.get("http");
        var pollerConfig = (Document) config.get("poller");

        reporter = new Reporter(reportConfig);
        httpServer = new HttpServer(httpConfig, reporter);
        poller = new Poller(pollerConfig, reporter);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            LOGGER.error("Usage: Collector config.json");
            System.exit(-1);
        }

        String configString = Files.readString(Path.of(args[0]));
        var config = Document.parse(configString);

        new Collector(config).start();
    }

    private void start() {
        reporter.start();
        poller.start();
        try {
            httpServer.start();
        } catch (Exception e) {
            LOGGER.error("Cannot start http server", e);
        }
    }
}
