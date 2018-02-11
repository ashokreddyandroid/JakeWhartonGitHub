package payconiq.com.jakewhartongitgub.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private String TAG = PaginationScrollListener.class.getSimpleName();
    LinearLayoutManager layoutManager;

    /**
     * Supporting only LinearLayoutManager for now.
     *
     * @param layoutManager
     */
    public PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        Log.d(TAG, "visibleItemCount:" + visibleItemCount);
        Log.d(TAG, "totalItemCount:" + totalItemCount);
        Log.d(TAG, "firstVisibleItemPosition:" + firstVisibleItemPosition);

        Log.d(TAG, "isLoading:" + isLoading());
        Log.d(TAG, "isLastPage:" + isLastPage());

        Log.d(TAG, "getTotalPageCount:" + getTotalPageCount());

        if (!isLoading() && !isLastPage()) {
            // Load the data when scroll reaches 3rd item from bottom
            if (((visibleItemCount + firstVisibleItemPosition + 3)) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= getTotalPageCount()) {
                Log.d(TAG, "*****load more items calling****");
                loadMoreItems();
            }
        }

    }

    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

}
