package org.schambon.loadsimrunner;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.bson.Document;
import org.schambon.loadsimrunner.inbound.InboundReportEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Poller {

    private static final Logger LOGGER = LoggerFactory.getLogger(Poller.class);

    private List<String> hosts;
    private Reporter reporter;
    private TreeMap<String, Instant> lastpolls;
    private HttpClient client;
    private ObjectMapper objectMapper;

    private long interval;

    public Poller(Document pollerConfig, Reporter reporter) {
        this.hosts = pollerConfig.getList("hosts", String.class)
            .stream()
            .map( h -> h.startsWith("http") ? h : "http://" + h)
            .map( h -> String.format("%s/report", h))
            .collect(Collectors.toList());
        var _interval = pollerConfig.getInteger("interval");
        if (_interval == null) {
            _interval = 1000; // 1s default polling interval
        }
        this.interval = (long) _interval;
        this.reporter = reporter;

        this.lastpolls = new TreeMap<>();
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public void start() {

        new Thread(
            () -> {
                while(true) {
                    var start = System.currentTimeMillis();

                    for (var host : hosts) {
                        HttpRequest request;
                        if (lastpolls.get(host) == null) {
                            request = HttpRequest.newBuilder()
                                .uri(URI.create(host))
                                .GET()
                                .build();
                            
                            LOGGER.debug("Polling {} since beginning", host);
                        } else {
                            request = HttpRequest.newBuilder()
                                .uri(URI.create(String.format("%s?since=%s", host, lastpolls.get(host).toString())))
                                .GET()
                                .build();
                            LOGGER.debug("Polling {} since {}", host, lastpolls.get(host).toString());
                        }
                        
                        try {
                            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                            List<InboundReportEnvelope> reports = objectMapper.readValue(response.body(), new TypeReference<List<InboundReportEnvelope>>() {});
        
                            LOGGER.debug("Got {} reports", reports.size());
                            
                            Instant time = null;
                            for (var report: reports) {
                                time = report.getTime();
        
                                reporter.report(report);
                            }
        
                            if (time != null) {
                                lastpolls.put(host, time);
                            }
                        } catch (IOException | InterruptedException e) {
                            LOGGER.info(String.format("Error while polling host %s: %s", host, e.getMessage()));
                            LOGGER.debug("Full error", e);
                        }
                    }

                    var duration = System.currentTimeMillis() - start;
                    try {
                        Thread.sleep(Math.max(interval-duration, 0));
                    } catch (InterruptedException e) {
                        LOGGER.warn("Interrupted", e);
                    }
                }
            }
        ).start();

    }
    
}
