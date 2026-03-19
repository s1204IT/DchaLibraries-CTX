package com.android.bluetooth.avrcp;

import android.media.session.PlaybackState;
import java.util.List;
import java.util.Objects;

class MediaData {
    public Metadata metadata;
    public List<Metadata> queue;
    public PlaybackState state;

    MediaData(Metadata metadata, PlaybackState playbackState, List<Metadata> list) {
        this.metadata = metadata;
        this.state = playbackState;
        this.queue = list;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MediaData)) {
            return false;
        }
        MediaData mediaData = (MediaData) obj;
        if (!MediaPlayerWrapper.playstateEquals(this.state, mediaData.state) || !Objects.equals(this.metadata, mediaData.metadata) || !Objects.equals(this.queue, mediaData.queue)) {
            return false;
        }
        return true;
    }
}
