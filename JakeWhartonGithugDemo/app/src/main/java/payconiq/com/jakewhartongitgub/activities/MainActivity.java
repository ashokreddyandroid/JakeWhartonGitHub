package payconiq.com.jakewhartongitgub.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import payconiq.com.jakewhartongitgub.R;
import payconiq.com.jakewhartongitgub.adapters.PaginationAdapter;
import payconiq.com.jakewhartongitgub.listeners.PaginationScrollListener;
import payconiq.com.jakewhartongitgub.model.JakeWhartonPage;
import payconiq.com.jakewhartongitgub.util.Constants;
import payconiq.com.jakewhartongitgub.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ashok on 10/02/18.
 */

public class MainActivity extends BaseActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private PaginationAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView githubRecycleView;
    private ProgressBar loadingProgressBar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = Constants.PAGE_START;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        // If internet is available add pagination support
        if (Utils.isInterNetAvailable(this)) {
            addPagination();
        }
        initLoadingData();
    }

    /**
     * Initialize views
     */
    private void initViews() {
        githubRecycleView = (RecyclerView) findViewById(R.id.github_recycler);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading_progress);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        githubRecycleView.setLayoutManager(linearLayoutManager);
        githubRecycleView.setItemAnimator(new DefaultItemAnimator());
        Realm.init(getApplicationContext());
        realm = Realm.getInstance(getRealmConfig());
        adapter = new PaginationAdapter(this);
        githubRecycleView.setAdapter(adapter);
    }


    /**
     * Add paygination support
     */
    private void addPagination() {
        githubRecycleView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                if (Utils.isInterNetAvailable(MainActivity.this)) {
                    isLoading = true;
                    currentPage += 1;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addLoadingFooter();
                            loadNextPage();
                        }
                    }, Constants.DELAY_IN_MILLIES);
                } else {
                    adapter.stopLoading();
                    showToast(getString(R.string.internet_unavailable));
                }
            }

            @Override
            public int getTotalPageCount() {
                return currentPage;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

        });
    }

    private void initLoadingData() {
        RealmResults<JakeWhartonPage> realmResults = realm.where(JakeWhartonPage.class).findAll();
        if (Utils.isInterNetAvailable(this)) {
            if (currentPage == 1) {
                // Delete old data and download freshly
                Utils.deleteOldData(realm, realmResults);
            }
            loadFirstPage();

        } else if (realmResults.size() > 0) {
            loadingProgressBar.setVisibility(View.GONE);
            showToast(getString(R.string.showing_local_realm_data));
            adapter.addAll(realmResults);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            Utils.parseGitgubData(this, realm);
            realmResults = realm.where(JakeWhartonPage.class).findAll();
            adapter.addAll(realmResults);
            showToast(getString(R.string.showing_local_json_data));
        }

    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        Utils.callJakeWhartonRepoApi(currentPage).enqueue(new Callback<List<JakeWhartonPage>>() {
            @Override
            public void onResponse(Call<List<JakeWhartonPage>> call, Response<List<JakeWhartonPage>> response) {

                if (response.body().size() > 0) {
                    final List<JakeWhartonPage> results = response.body();
                    loadingProgressBar.setVisibility(View.GONE);
                    adapter.addAll(results);
                    adapter.addLoadingFooter();
                    Utils.saveData(results, realm);
                } else {
                    isLoading = false;
                    showToast(getString(R.string.fail_to_get_data));
                }
            }

            @Override
            public void onFailure(Call<List<JakeWhartonPage>> call, Throwable t) {
                showToast(getString(R.string.fail_to_get_data));
            }
        });

    }

    /**
     * Load next set of data
     */
    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        Utils.callJakeWhartonRepoApi(currentPage).enqueue(new Callback<List<JakeWhartonPage>>() {
            @Override
            public void onResponse(Call<List<JakeWhartonPage>> call, Response<List<JakeWhartonPage>> response) {
                isLoading = false;
                if (response.body().size() > 0) {
                    final List<JakeWhartonPage> results = response.body();
                    loadingProgressBar.setVisibility(View.GONE);
                    adapter.addAll(results);
                    adapter.addLoadingFooter();
                    Utils.saveData(results, realm);
                } else {
                    adapter.removeLoadingFooter();
                    isLastPage = true;
                    showToast(getString(R.string.no_more_data));
                }

            }

            @Override
            public void onFailure(Call<List<JakeWhartonPage>> call, Throwable t) {
                t.printStackTrace();
                showToast(getString(R.string.fail_to_get_data));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }
}
