package org.schambon.loadsimrunner.inbound;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InboundReportEnvelope {
    
    @JsonProperty("time")
    private Instant time;
    @JsonProperty("report")
    private Map<String,InboundReportData> data;

    public void setTime(Instant time){
        this.time = time;
    }

    public Instant getTime() {
        return time;
    }

    public void setData(Map<String,InboundReportData> data){
        this.data = data;
    }

    public Map<String,InboundReportData> getData() {
        return data;
    }

}
