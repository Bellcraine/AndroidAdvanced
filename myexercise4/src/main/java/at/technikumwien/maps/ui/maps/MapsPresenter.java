package at.technikumwien.maps.ui.maps;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.util.List;

import at.technikumwien.maps.AppDependencyManager;
import at.technikumwien.maps.data.OnDataLoadedCallback;
import at.technikumwien.maps.data.model.DrinkingFountain;
import at.technikumwien.maps.data.remote.DrinkingFountainApi;
import at.technikumwien.maps.data.remote.response.DrinkingFountainResponse;
import at.technikumwien.maps.util.managers.SyncManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class MapsPresenter extends MvpBasePresenter<MapsView> {

    private final SyncManager syncManager;

    private LiveData<List<DrinkingFountain>> liveData;
    private Observer<List<DrinkingFountain>> observer = new Observer<List<DrinkingFountain>>() {
        @Override
        public void onChanged(@Nullable List<DrinkingFountain> drinkingFountains) {
            if (isViewAttached()) {
                getView().showDrinkingFountains(drinkingFountains);
            }
        }
    };

    public MapsPresenter(AppDependencyManager manager) {
        liveData = manager.getDrinkingFountainRepo().loadAll();
        syncManager = manager.getSyncManager();
    }


    @Override
    public void attachView(MapsView view) {
        super.attachView(view);
        liveData.observeForever(observer);
    }


    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        liveData.removeObserver(observer);
    }


    public void loadDrinkingFountains() {
        syncManager.loadDrinkingFountains(new OnDataLoadedCallback<List<DrinkingFountain>>() {
            @Override
            public void onDataLoaded(List<DrinkingFountain> data) {
                if (isViewAttached()) {
                    getView().showDrinkingFountains(data);
                }
            }

            @Override
            public void onDataLoadError(Throwable throwable) {
                if (isViewAttached()) {
                    getView().showLoadingError(throwable);
                }
            }
        });
    }


}