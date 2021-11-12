package org.schambon.loadsimrunner.http;

import java.io.IOException;

import org.bson.Document;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.schambon.loadsimrunner.Reporter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DownloadHandler extends AbstractHandler {

    private Reporter reporter;

    public DownloadHandler(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        var pathInfo = request.getPathInfo();
        if (pathInfo.startsWith("/download")) {
            baseRequest.setHandled(true);
        
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"performance.csv\"");
            response.setStatus(200);

            var out = response.getWriter();

            out.println("\"timestamp\",\"name\",\"ops\",\"records\",\"duration\",\"95th percentile\"");

            for (var report: reporter.getAllReports()) {
                for (var task: report.getReport().keySet()) {
                    var doc = (Document)report.getReport().get(task);
                    out.println(
                        String.format("\"%s\",\"%s\",%d,%d,%f,%f",
                            report.getTime().toString(),
                            task,
                            doc.getLong("ops"),
                            doc.getLong("records"),
                            doc.getDouble("mean duration"),
                            doc.getDouble("95th percentile")
                        )
                    );
                }
            }

            out.flush();
        }
    }
    
}
