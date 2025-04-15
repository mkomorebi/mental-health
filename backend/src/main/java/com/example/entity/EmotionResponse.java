package com.example.entity;

import java.util.Map;

/**
 * 情绪分析API响应实体类
 */
public class EmotionResponse {
    private String emotion;
    private String status;
    private Map<String, Double> probabilities;
    private String text;
    private Double snr;
    private Double volume;
    private Double duration;

    // Getters and Setters
    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Double> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(Map<String, Double> probabilities) {
        this.probabilities = probabilities;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getSnr() {
        return snr;
    }

    public void setSnr(Double snr) {
        this.snr = snr;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "EmotionResponse{" +
                "emotion='" + emotion + '\'' +
                ", status='" + status + '\'' +
                ", probabilities=" + probabilities +
                ", text='" + text + '\'' +
                ", snr=" + snr +
                ", volume=" + volume +
                ", duration=" + duration +
                '}';
    }
}
