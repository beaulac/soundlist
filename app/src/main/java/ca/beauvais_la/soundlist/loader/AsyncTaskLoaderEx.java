package ca.beauvais_la.soundlist.loader;

import android.content.Context;
import android.content.AsyncTaskLoader;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AsyncTaskLoaderEx<T> extends AsyncTaskLoader<T> {
    private static final AtomicInteger sCurrentUniqueId = new AtomicInteger(0);
    private T mData;
    public boolean hasResult = false;

    public static int getNewUniqueLoaderId() {
        return sCurrentUniqueId.getAndIncrement();
    }

    public AsyncTaskLoaderEx(final Context context) {
        super(context);
        onContentChanged();
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged()) {
            forceLoad();
        } else if (hasResult) {
            deliverResult(mData);
        }
    }

    @Override
    public void deliverResult(final T data) {
        mData = data;
        hasResult = true;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (hasResult) {
            onReleaseResources(mData);
            mData = null;
            hasResult = false;
        }
    }

    @Override protected void onStopLoading() {
        cancelLoad();
    }

    protected void onReleaseResources(T data) {
        //nothing to do.
    }

    public T getResult() {
        return mData;
    }
}