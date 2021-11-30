# SimRunner-Collector
Stats collector &amp; front end for SimRunner

Build: mvn package (needs Java 11)
Run: java -jar Collector.jar config.json

In config.json, configure the host/port for the http interface, and add the host:port for the http interfaces of each SimRunner instance you want to poll.

Start SimRunners first, then start Collector. Point your browser at http://<host>:<port> to view graphs.

This aggregates a few metrics from SimRunner:
- number of operations per second
- number of documents per second
- average latency (average of averages)
- 95th percentile latency (max of 95th percentiles from all SimRunners)

Also provides a link to download the metrics in CSV format.
