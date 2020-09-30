package io.flutter.plugins.firebasemessaging.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class ApiManager {

    private static ApiManager singleton;


    public static final String BASE_URL = "https://new.nexel.com/api/";
    private ApiInterface service;




    private ApiManager() {

        OkHttpClient.Builder builderForHttp = new OkHttpClient.Builder();


        builderForHttp.readTimeout(30, TimeUnit.SECONDS);
        builderForHttp.writeTimeout(30, TimeUnit.SECONDS);
        builderForHttp.connectTimeout(30, TimeUnit.SECONDS);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)

                .build();
        service = retrofit.create(ApiInterface.class);
    }

    public synchronized static ApiManager getInstance() {
        if (null == singleton) {
            singleton = new ApiManager();

        }
        return singleton;
    }



   public Call<ResponseBody>  getCallForId(String token, String id){
       return service.endCall(token, id);
    }


  public interface ApiInterface {

    @GET("phone-call/cancel-phone-call")
    @Headers({
      "Content-Type: application/json", "Accept-Encoding: gzip, deflate, br",
      "Accept-Language:  ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7",
      "Referer:  https://new.nexel.com/login?redirect=%2Fdashboard",
      "Sec-Fetch-Dest: empty",
      "Sec-Fetch-Mode:cors",
      "Sec-Fetch-Site:same-origin",
      "Pragma: no-cache",
      "Connection: keep-alive",
      "User-Agent: PostmanRuntime/7.26.1",
      "Accept:  application/json, text/plain, */*",
      "Origin:  https://new.nexel.com",})
    Call<ResponseBody> endCall(@Header("Authorization") String token, @Query("id") String callId);
  }
}



