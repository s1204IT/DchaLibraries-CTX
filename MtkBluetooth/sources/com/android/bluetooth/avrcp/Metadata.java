package com.android.bluetooth.avrcp;

import java.util.Objects;

class Metadata implements Cloneable {
    public String album;
    public String artist;
    public String duration;
    public String genre;
    public String mediaId;
    public String numTracks;
    public String title;
    public String trackNum;

    Metadata() {
    }

    public Metadata m8clone() {
        Metadata metadata = new Metadata();
        metadata.mediaId = this.mediaId;
        metadata.title = this.title;
        metadata.artist = this.artist;
        metadata.album = this.album;
        metadata.trackNum = this.trackNum;
        metadata.numTracks = this.numTracks;
        metadata.genre = this.genre;
        metadata.duration = this.duration;
        return metadata;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Metadata)) {
            return false;
        }
        Metadata metadata = (Metadata) obj;
        if (!Objects.equals(this.title, metadata.title) || !Objects.equals(this.artist, metadata.artist) || !Objects.equals(this.album, metadata.album)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "{ mediaId=\"" + this.mediaId + "\" title=\"" + this.title + "\" artist=\"" + this.artist + "\" album=\"" + this.album + "\" duration=" + this.duration + " trackPosition=" + this.trackNum + "/" + this.numTracks + " }";
    }
}
