package net.bjmsw.model;

import java.awt.image.BufferedImage;

public class TrackInfo {

    private String trackName, artist, album;

    private BufferedImage artwork;

    private final int length;

    // current time in seconds
    private final long startTime;

    private final boolean isEmpty;

    public TrackInfo(String trackName, String artist, String album, int length) {
        this.trackName = trackName;
        this.artist = artist;
        this.album = album;
        this.artwork = null;
        this.length = length;
        this.startTime = System.currentTimeMillis() / 1000L;

        if (trackName.isEmpty()) {
            isEmpty = true;
        }  else {
            isEmpty = false;
        }
    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public BufferedImage getArtwork() {
        return artwork;
    }

    public void setArtwork(BufferedImage artwork) {
        this.artwork = artwork;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public int getLength() {
        return length;
    }
}


