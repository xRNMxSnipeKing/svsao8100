package com.microsoft.xbox.service.model.edsv2;

import java.util.ArrayList;

public class EDSV2MovieMediaItem extends EDSV2MediaItem {
    private ArrayList<String> actors;
    private ArrayList<String> directors;
    private float metaCriticReviewScore;
    private String studio;
    private ArrayList<String> writers;

    public EDSV2MovieMediaItem(EDSV2MediaItem source) {
        super(source);
        if (getMediaType() == 0) {
            setMediaType(EDSV2MediaType.MEDIATYPE_MOVIE);
        }
    }

    public ArrayList<String> getActors() {
        return this.actors;
    }

    public void setActors(ArrayList<String> actors) {
        this.actors = actors;
    }

    public ArrayList<String> getDirectors() {
        return this.directors;
    }

    public void setDirectors(ArrayList<String> directors) {
        this.directors = directors;
    }

    public ArrayList<String> getWriters() {
        return this.writers;
    }

    public void setWriters(ArrayList<String> writers) {
        this.writers = writers;
    }

    public String getStudio() {
        return this.studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public float getMetaCriticReviewScore() {
        return this.metaCriticReviewScore;
    }

    public void setMetaCriticReviewScore(float metaCriticReviewScore) {
        this.metaCriticReviewScore = metaCriticReviewScore;
    }
}
