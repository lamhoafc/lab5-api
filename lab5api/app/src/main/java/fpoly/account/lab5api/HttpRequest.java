package fpoly.account.lab5api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequest {
    private static final String BASE_URL = "http://192.168.1.8:3000/";
    private ApiServices requestInterface;

    public HttpRequest() {
        requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiServices.class);
    }

    public ApiServices callAPI() {
        return requestInterface;
    }
}