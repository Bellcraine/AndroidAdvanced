package at.technikumwien.maps.util.managers;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.util.List;

import at.technikumwien.maps.AppDependencyManager;
import at.technikumwien.maps.data.NoOpOnOperationSuccessfulCallback;
import at.technikumwien.maps.data.OnDataLoadedCallback;
import at.technikumwien.maps.data.OnOperationSuccessfulCallback;
import at.technikumwien.maps.data.local.DrinkingFountainRepo;
import at.technikumwien.maps.data.model.DrinkingFountain;
import at.technikumwien.maps.data.remote.DrinkingFountainApi;
import at.technikumwien.maps.data.remote.response.DrinkingFountainResponse;
import at.technikumwien.maps.service.SyncDrinkingFountainsJobService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created by Bellacraine on 05.11.2017.
 */

public class SyncManager {

    private DrinkingFountainApi drinkingFountainApi;
    private DrinkingFountainRepo drinkingFountainRepo;
    private Context context;

    private static String TAG = SyncManager.class.getSimpleName();
    private static int SYNC_JOB_ID = 32561289;

    public SyncManager(AppDependencyManager manager) {
        this.context = manager.getAppContext();
        this.drinkingFountainApi = manager.getDrinkingFountainApi();
        this.drinkingFountainRepo = manager.getDrinkingFountainRepo();
    }

    public void schedulePeriodicSync() {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        JobInfo jobInfo = new JobInfo.Builder(SYNC_JOB_ID, new ComponentName(context, SyncDrinkingFountainsJobService.class))
                .setPeriodic(7L * 24L * 60L * 60L * 1000L) // Weekly sync
                .setPersisted(true) // Don't forget to add RECEIVE_BOOT_COMPLETED permission!
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(true)
                .setRequiresDeviceIdle(true)
                .build();

        jobScheduler.schedule(jobInfo);

        Log.i(TAG, "SyncJobService scheduled for periodic execution");
    }


    public Cancelable syncDrinkingFountains(final OnOperationSuccessfulCallback callback, final OnDataLoadedCallback<List<DrinkingFountain>> loadedCallback) {
        Call<DrinkingFountainResponse> c = drinkingFountainApi.getDrinkingFountains();

        c.enqueue(new Callback<DrinkingFountainResponse>() {
            @Override
            public void onResponse(Call<DrinkingFountainResponse> call, Response<DrinkingFountainResponse> response) {
                if (response.isSuccessful()) {
                    List<DrinkingFountain> drinkingFountainList = response.body().getDrinkingFountainList();
                    if (loadedCallback != null) {
                        loadedCallback.onDataLoaded(drinkingFountainList);
                    }
                    drinkingFountainRepo.refreshList(callback, drinkingFountainList);
                } else {
                    HttpException e = new HttpException(response);
                    callback.onOperationError(e);
                    if (loadedCallback != null) {
                        loadedCallback.onDataLoadError(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<DrinkingFountainResponse> call, Throwable throwable) {
                callback.onOperationError(throwable);
                if (loadedCallback != null) {
                    loadedCallback.onDataLoadError(throwable);
                }
            }
        });

        return new CallCancelable(c);
    }


    public Cancelable syncDrinkingFountains(OnOperationSuccessfulCallback callback) {
        return syncDrinkingFountains(callback, null);
    }


    public void loadDrinkingFountains(final OnDataLoadedCallback<List<DrinkingFountain>> callback) {
        drinkingFountainRepo.loadAll(new OnDataLoadedCallback<List<DrinkingFountain>>() {
            @Override
            public void onDataLoaded(List<DrinkingFountain> data) {
                if(data.isEmpty()) {
                    syncDrinkingFountains(new NoOpOnOperationSuccessfulCallback(), callback);
                } else {
                    callback.onDataLoaded(data);
                }
            }

            @Override
            public void onDataLoadError(Throwable throwable) {
                callback.onDataLoadError(throwable);
            }
        });
    }


    private static class CallCancelable implements Cancelable {
        private final Call<?> call;

        CallCancelable(Call<?> call) {
            this.call = call;
        }

        @Override
        public void cancel() {
            call.cancel();
        }

        @Override
        public boolean isCanceled() {
            return call.isCanceled();
        }
    }


}
