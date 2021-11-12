package org.schambon.loadsimrunner.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InboundReportData {
    
    @JsonProperty("ops")
    private long ops;
    @JsonProperty("records")
    private long records;

    @JsonProperty("total ops")
    private long totalOps;

    public long getTotalOps() {
        return this.totalOps;
    }

    public void setTotalOps(long totalOps) {
        this.totalOps = totalOps;
    }

    public long getTotalRecords() {
        return this.totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    @JsonProperty("total records")
    private long totalRecords;

    @JsonProperty("mean duration")
    private double meanDuration;
    @JsonProperty("median duration")
    private double medianDuration;
    @JsonProperty("95th percentile")
    private double ninetyfifthPercentileDuration;
    @JsonProperty("min batch size")
    private double minBatchSize;
    @JsonProperty("mean batch size")
    private double meanBatchSize;
    @JsonProperty("max batch size")
    private double maxBatchSize;
    @JsonProperty("client util")
    private double clientUtil;


    public long getOps() {
        return this.ops;
    }

    public void setOps(long ops) {
        this.ops = ops;
    }

    public long getRecords() {
        return this.records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public double getMeanDuration() {
        return this.meanDuration;
    }

    public void setMeanDuration(double meanDuration) {
        this.meanDuration = meanDuration;
    }

    public double getMedianDuration() {
        return this.medianDuration;
    }

    public void setMedianDuration(double medianDuration) {
        this.medianDuration = medianDuration;
    }

    public double getNinetyfifthPercentileDuration() {
        return this.ninetyfifthPercentileDuration;
    }

    public void setNinetyfifthPercentileDuration(double ninetyfifthPercentileDuration) {
        this.ninetyfifthPercentileDuration = ninetyfifthPercentileDuration;
    }

    public double getMinBatchSize() {
        return this.minBatchSize;
    }

    public void setMinBatchSize(double minBatchSize) {
        this.minBatchSize = minBatchSize;
    }

    public double getMeanBatchSize() {
        return this.meanBatchSize;
    }

    public void setMeanBatchSize(double meanBatchSize) {
        this.meanBatchSize = meanBatchSize;
    }

    public double getMaxBatchSize() {
        return this.maxBatchSize;
    }

    public void setMaxBatchSize(double maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public double getClientUtil() {
        return this.clientUtil;
    }

    public void setClientUtil(double clientUtil) {
        this.clientUtil = clientUtil;
    }


    @Override
    public String toString() {
        return "{" +
            " ops='" + getOps() + "'" +
            ", records='" + getRecords() + "'" +
            ", meanDuration='" + getMeanDuration() + "'" +
            ", medianDuration='" + getMedianDuration() + "'" +
            ", ninetyfifthPercentileDuration='" + getNinetyfifthPercentileDuration() + "'" +
            ", minBatchSize='" + getMinBatchSize() + "'" +
            ", meanBatchSize='" + getMeanBatchSize() + "'" +
            ", maxBatchSize='" + getMaxBatchSize() + "'" +
            ", clientUtil='" + getClientUtil() + "'" +
            "}";
    }

}
