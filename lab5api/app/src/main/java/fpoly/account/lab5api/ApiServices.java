package fpoly.account.lab5api;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiServices {
    @GET("api/items")
    Call<Response<ArrayList<Distributor>>> getListDistributor();

    @GET("search-distributor")
    Call<Response<ArrayList<Distributor>>> searchDistributor(@QueryMap Map<String, String> query);

    @POST("add-distributor")
    Call<Response<String>> addDistributor(@Body Distributor distributor);

    @DELETE("delete-distributor-by-id/{id}")
    Call<Response<Void>> deleteDistributorById(@Path("id") String id);

    @PUT("update-distributor-by-id/{id}")
    Call<Response<Void>> updateDistributorById(@Path("id") String id, @Body Distributor distributor);
}