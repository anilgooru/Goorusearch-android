package org.ednovo.shutterbug;

import org.ednovo.shutterbug.ShutterbugManager.ShutterbugManagerListener;

public class DownloadRequest {
    private String mUrl;
    private ShutterbugManagerListener mListener;

    public DownloadRequest(String url, ShutterbugManagerListener listener) {
        mUrl = url;
        mListener = listener;
    }

    public String getUrl() {
        return mUrl;
    }

    public ShutterbugManagerListener getListener() {
        return mListener;
    }
}
