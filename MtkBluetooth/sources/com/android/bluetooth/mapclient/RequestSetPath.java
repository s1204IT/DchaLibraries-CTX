package com.android.bluetooth.mapclient;

import java.io.IOException;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;

class RequestSetPath extends Request {
    SetPathDir mDir;
    String mName;

    enum SetPathDir {
        ROOT,
        UP,
        DOWN
    }

    RequestSetPath(String str) {
        this.mDir = SetPathDir.DOWN;
        this.mName = str;
        this.mHeaderSet.setHeader(1, str);
    }

    RequestSetPath(boolean z) {
        this.mHeaderSet.setEmptyNameHeader();
        if (z) {
            this.mDir = SetPathDir.ROOT;
        } else {
            this.mDir = SetPathDir.UP;
        }
    }

    @Override
    public void execute(ClientSession clientSession) {
        HeaderSet path;
        try {
            switch (this.mDir) {
                case ROOT:
                case DOWN:
                    path = clientSession.setPath(this.mHeaderSet, false, false);
                    break;
                case UP:
                    path = clientSession.setPath(this.mHeaderSet, true, false);
                    break;
                default:
                    path = null;
                    break;
            }
            this.mResponseCode = path.getResponseCode();
        } catch (IOException e) {
            this.mResponseCode = 208;
        }
    }
}
