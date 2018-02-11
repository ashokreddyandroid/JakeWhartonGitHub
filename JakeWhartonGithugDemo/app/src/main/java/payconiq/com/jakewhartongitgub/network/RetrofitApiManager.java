package payconiq.com.jakewhartongitgub.network;


import java.util.List;

import payconiq.com.jakewhartongitgub.model.JakeWhartonPage;
import payconiq.com.jakewhartongitgub.util.Constants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface RetrofitApiManager {
    @GET(Constants.APPEND_URL)
    Call<List<JakeWhartonPage>> getRepositoryAPI(@Query(Constants.PAGE) int pageIndex, @Query(Constants.PER_PAGE) int perPage);
}
