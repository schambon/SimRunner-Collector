package org.schambon.loadsimrunner;

import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
import static java.time.Instant.now;

import java.nio.channels.Pipe.SinkChannel;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.common.math.Stats;

import org.bson.Document;
import org.schambon.loadsimrunner.inbound.InboundReportData;
import org.schambon.loadsimrunner.inbound.InboundReportEnvelope;
import org.schambon.loadsimrunner.report.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reporter.class);

    private Map<String, List<InboundReportData>> statsHolder = new TreeMap<>();
    private long interval;
    private TreeMap<Instant, Report> reports = new TreeMap<>();

    private long lastCompute = 0l;

    public Reporter(Document config) {
        interval = ((Number) config.get("interval")).longValue();
    }

    public void start() {

        new Thread( () -> {
            while(true) {
                long start = currentTimeMillis();

                computeReport();

                long duration = currentTimeMillis() - start;
                try {
                    Thread.sleep(max(interval - duration, 0));
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted!", e);
                }
            }
        }).start();

    }


    public Collection<Report> getAllReports() {
        return reports.values();
    }

    public Collection<Report> getReportsSince(Instant start) {
        return reports.tailMap(start, false).values();
    }
    

    private void computeReport() {
        if (statsHolder.size() == 0) {
            LOGGER.info("No data");
            return;
        }
        var temp = statsHolder;
        synchronized(this) {
            statsHolder = new TreeMap<>();
            for (var job: temp.keySet()) {
                statsHolder.put(job, new LinkedList<>());
            }
        }
        
        long now = currentTimeMillis();
        if (lastCompute == 0l) {
            lastCompute = now;
            LOGGER.info("Skipping first report in order to compute throughput");
            return;
        }

        long sinceLastCompute = now - lastCompute;
        lastCompute = now;

        Document doc = new Document();
        for (var job: temp.keySet()) {
            doc.append(job, compute(temp.get(job), sinceLastCompute));
        }

        Instant instant = Instant.ofEpochMilli(now);
        Report report = new Report(instant, doc);
        reports.put(instant, report);

        LOGGER.info("Periodic report:\n{}", report.toString());
    }

    private Document compute(List<InboundReportData> list, long sinceLastCompute) {
        Document doc = new Document();

        // total ops is sum of ops
        // total records is sum of records
        // mean (avg) duration is mean of means
        // 95th percentile is max of 95th percentiles (sortof)
        var ops = (Stats.of(list.stream().map(x -> x.getTotalOps()).collect(Collectors.toList())).sum() / (double) sinceLastCompute) * 1000d;
        var records = (Stats.of(list.stream().map(x -> x.getTotalRecords()).collect(Collectors.toList())).sum() / (double) sinceLastCompute) * 1000d;
        var meanDuration = list.size() > 0 ? Stats.of(list.stream().map(x -> x.getMeanDuration()).collect(Collectors.toList())).mean() : 0;
        var ninetyFifth = list.size()  > 0 ? Stats.of(list.stream().map(x -> x.getNinetyfifthPercentileDuration()).collect(Collectors.toList())).max() : 0;

        doc.append("ops", (long)ops)
           .append("records", (long)records)
           .append("mean duration", meanDuration)
           .append("95th percentile", ninetyFifth);
        
        return doc;
    }

    public synchronized void report(InboundReportEnvelope report) {
        LOGGER.debug("Got ping with {}", report.getData().keySet());

        for (var job: report.getData().keySet()) {
            List<InboundReportData> list = statsHolder.get(job);
            if (list == null) {
                list = new LinkedList<>();
                statsHolder.put(job, list);
            }
            list.add(report.getData().get(job));
        }
    }
    
}
