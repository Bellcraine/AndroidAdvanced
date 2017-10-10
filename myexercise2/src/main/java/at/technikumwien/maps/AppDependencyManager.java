package at.technikumwien.maps;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;

import at.technikumwien.maps.data.remote.DrinkingFountainRepo;
import at.technikumwien.maps.data.remote.MockDrinkingFountainRepo;
import at.technikumwien.maps.data.remote.retrofit.DrinkingFountainApi;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AppDependencyManager {

    private final Context appContext;
    private DrinkingFountainRepo drinkingFountainRepo;
    private DrinkingFountainApi drinkingFountainApi;
    private Gson gson;

    public AppDependencyManager(Context appContext) {
        this.appContext = appContext;
    }


    public Context getAppContext() {
        return appContext;
    }

    public Resources getResources() {
        return appContext.getResources();
    }

    public DrinkingFountainRepo getDrinkingFountainRepo() {
        if(drinkingFountainRepo == null) { drinkingFountainRepo = new MockDrinkingFountainRepo(); }
        return drinkingFountainRepo;
    }


    public Gson getGson() {
        if(gson == null) { gson = new Gson(); }
        return gson;
    }

    public DrinkingFountainApi getDrinkingFountainApi() {
        if(drinkingFountainApi != null) {
            drinkingFountainApi = new Retrofit.Builder()
                    .baseUrl(DrinkingFountainRepo.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(new OkHttpClient())
                    .build()
                    .create(DrinkingFountainApi.class);

        }
        return drinkingFountainApi;
    }
}
