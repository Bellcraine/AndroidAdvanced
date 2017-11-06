package at.technikumwien.maps.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

import at.technikumwien.maps.AppDependencyManager;
import at.technikumwien.maps.MyApplication;
import at.technikumwien.maps.util.managers.Cancelable;

/**
 * Created by Bellacraine on 05.11.2017.
 */

public class SyncDrinkingFountainsJobService extends JobService {

    private Cancelable cancelable = null;


    @Override
    public boolean onStartJob(JobParameters params) {
        AppDependencyManager manager = ((MyApplication) getApplicationContext()).getAppDependencyManager();
        cancelable = manager.getSyncManager().syncDrinkingFountains(new JobServiceOnOperationSuccessFulCallback(this, params));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if(cancelable != null && !cancelable.isCanceled()) {
            cancelable.cancel();
        }
        cancelable = null;
        return true;
    }
}
