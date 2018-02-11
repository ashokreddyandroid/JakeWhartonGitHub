package payconiq.com.jakewhartongitgub.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import payconiq.com.jakewhartongitgub.model.JakeWhartonPage;
import payconiq.com.jakewhartongitgub.network.RetrofitApi;
import payconiq.com.jakewhartongitgub.network.RetrofitApiManager;
import retrofit2.Call;

/**
 * Created by Ashok on 10/02/18.
 */

public class Utils {

    /**
     * Check is internet available or not
     * @param context
     * @return true if it exist, otherwise return false
     */
    public static boolean isInterNetAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Save data to Realm database
     * @param results
     * @param realm
     */
    public static void saveData(final List<JakeWhartonPage> results, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmList<JakeWhartonPage> repoList = new RealmList<>();
                for (JakeWhartonPage rel : results) {
                    // TODO: for now storing limited fields in DB.
                    repoList.add(new JakeWhartonPage(rel.getId(), rel.getFullName(), rel.getName(), rel.getForks(), rel.getDescription(), rel.getUrl()));
                }
                realm.insertOrUpdate(repoList);
            }
        });
    }

    /**
     * Save data to Realm database
     * @param results
     * @param realm
     */
    public static void saveData(final JakeWhartonPage[] results, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmList<JakeWhartonPage> repoList = new RealmList<>();
                for (JakeWhartonPage rel : results) {
                    // TODO: for now storing limited fields in DB.
                    repoList.add(new JakeWhartonPage(rel.getId(), rel.getFullName(), rel.getName(), rel.getForks(), rel.getDescription(), rel.getUrl()));
                }
                realm.insertOrUpdate(repoList);
            }
        });
    }


    /**
     * Get github repositories
     * @param currentPage
     * @return
     */
    public static Call<List<JakeWhartonPage>> callJakeWhartonRepoApi(int currentPage) {
        RetrofitApiManager repositoryService = RetrofitApi.getClient().create(RetrofitApiManager.class);
        return repositoryService.getRepositoryAPI(
                currentPage,
                Constants.PER_PAGE_ITEM
        );
    }

    /**
     * Delete old data
     * @param realm
     * @param toDoItems
     */
    public static void deleteOldData(Realm realm, RealmResults<JakeWhartonPage> toDoItems) {
        realm.beginTransaction();
        toDoItems.deleteAllFromRealm();
        realm.commitTransaction();
    }

    /**
     * Parse and save local json file data to Realm database
     * @param context
     * @param realm
     */
    public static void parseGitgubData(Context context, Realm realm) {
        try {
            JakeWhartonPage[] jakeWhartonPages = new Gson().fromJson(readFromAssets("git_repo.json", context), JakeWhartonPage[].class);
            saveData(jakeWhartonPages, realm);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    /**
     * Read file from assets
     * @param fileName file to read
     * @param context context
     * @return content of the file
     */
    private static String readFromAssets(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }
}
