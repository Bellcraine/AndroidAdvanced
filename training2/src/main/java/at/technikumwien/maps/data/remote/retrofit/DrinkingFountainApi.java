package at.technikumwien.maps.data.remote.retrofit;

import at.technikumwien.maps.data.remote.DrinkingFountainRepo;
import at.technikumwien.maps.data.remote.retrofit.response.DrinkingFountainResponse;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Bellacraine on 04.11.2017.
 */

public interface DrinkingFountainApi {

    @GET(DrinkingFountainRepo.GET_DRINKING_FOUNTAINS_PATH)
    Call<DrinkingFountainResponse> getDrinkingFountains();
}
