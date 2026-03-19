package com.android.gallery3d.filtershow.filters;

import android.util.JsonReader;
import android.util.JsonWriter;
import com.android.gallery3d.R;
import com.mediatek.gallery3d.util.Log;
import java.io.IOException;

public class FilterMirrorRepresentation extends FilterRepresentation {
    private static final String TAG = FilterMirrorRepresentation.class.getSimpleName();
    Mirror mMirror;

    public enum Mirror {
        NONE('N'),
        VERTICAL('V'),
        HORIZONTAL('H'),
        BOTH('B');

        char mValue;

        Mirror(char c) {
            this.mValue = c;
        }

        public char value() {
            return this.mValue;
        }

        public static Mirror fromValue(char c) {
            if (c == 'B') {
                return BOTH;
            }
            if (c == 'H') {
                return HORIZONTAL;
            }
            if (c == 'N') {
                return NONE;
            }
            if (c == 'V') {
                return VERTICAL;
            }
            return null;
        }
    }

    public FilterMirrorRepresentation(Mirror mirror) {
        super("MIRROR");
        setSerializationName("MIRROR");
        setShowParameterValue(false);
        setFilterClass(FilterMirrorRepresentation.class);
        setFilterType(7);
        setSupportsPartialRendering(true);
        setTextId(R.string.mirror);
        setEditorId(R.id.imageOnlyEditor);
        setMirror(mirror);
    }

    public FilterMirrorRepresentation(FilterMirrorRepresentation filterMirrorRepresentation) {
        this(filterMirrorRepresentation.getMirror());
        setName(filterMirrorRepresentation.getName());
    }

    public FilterMirrorRepresentation() {
        this(getNil());
    }

    @Override
    public boolean equals(FilterRepresentation filterRepresentation) {
        return (filterRepresentation instanceof FilterMirrorRepresentation) && this.mMirror == ((FilterMirrorRepresentation) filterRepresentation).mMirror;
    }

    public Mirror getMirror() {
        return this.mMirror;
    }

    public void setMirror(Mirror mirror) {
        if (mirror == null) {
            throw new IllegalArgumentException("Argument to setMirror is null");
        }
        this.mMirror = mirror;
    }

    public boolean isHorizontal() {
        if (this.mMirror == Mirror.BOTH || this.mMirror == Mirror.HORIZONTAL) {
            return true;
        }
        return false;
    }

    public boolean isVertical() {
        if (this.mMirror == Mirror.BOTH || this.mMirror == Mirror.VERTICAL) {
            return true;
        }
        return false;
    }

    public void cycle() {
        switch (this.mMirror) {
            case NONE:
                this.mMirror = Mirror.HORIZONTAL;
                break;
            case HORIZONTAL:
                this.mMirror = Mirror.BOTH;
                break;
            case BOTH:
                this.mMirror = Mirror.VERTICAL;
                break;
            case VERTICAL:
                this.mMirror = Mirror.NONE;
                break;
        }
    }

    @Override
    public boolean allowsSingleInstanceOnly() {
        return true;
    }

    @Override
    public FilterRepresentation copy() {
        return new FilterMirrorRepresentation(this);
    }

    @Override
    protected void copyAllParameters(FilterRepresentation filterRepresentation) {
        if (!(filterRepresentation instanceof FilterMirrorRepresentation)) {
            throw new IllegalArgumentException("calling copyAllParameters with incompatible types!");
        }
        super.copyAllParameters(filterRepresentation);
        filterRepresentation.useParametersFrom(this);
    }

    @Override
    public void useParametersFrom(FilterRepresentation filterRepresentation) {
        if (!(filterRepresentation instanceof FilterMirrorRepresentation)) {
            throw new IllegalArgumentException("calling useParametersFrom with incompatible types!");
        }
        setMirror(((FilterMirrorRepresentation) filterRepresentation).getMirror());
    }

    @Override
    public boolean isNil() {
        return this.mMirror == getNil();
    }

    public static Mirror getNil() {
        return Mirror.NONE;
    }

    @Override
    public void serializeRepresentation(JsonWriter jsonWriter) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("value").value(this.mMirror.value());
        jsonWriter.endObject();
    }

    @Override
    public void deSerializeRepresentation(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        boolean z = true;
        while (jsonReader.hasNext()) {
            if ("value".equals(jsonReader.nextName())) {
                Mirror mirrorFromValue = Mirror.fromValue((char) jsonReader.nextInt());
                if (mirrorFromValue != null) {
                    setMirror(mirrorFromValue);
                    z = false;
                }
            } else {
                jsonReader.skipValue();
            }
        }
        if (z) {
            Log.w(TAG, "WARNING: bad value when deserializing MIRROR");
        }
        jsonReader.endObject();
    }

    @Override
    public void resetRepresentation() {
        this.mMirror = Mirror.NONE;
    }
}
