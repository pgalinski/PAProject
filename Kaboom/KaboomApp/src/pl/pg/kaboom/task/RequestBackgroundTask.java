package pl.pg.kaboom.task;

import android.os.SystemClock;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.api.BackgroundExecutor;
import pl.pg.kaboom.activity.MainMenuActivity;


/**
 * Created by pgalinsk 2015-06-18
 */

@EBean
public class RequestBackgroundTask {


    @RootContext
    MainMenuActivity activity;


    @Background(id = "request_task",serial = "REQUESTS")
    public void start(){
        int interval = 5 * 1000; //milis to seconds
        if(interval > 0) {
            activity.sendRequestIfConnected();
        }else{
            interval = 5000;
        }
        SystemClock.sleep(interval);
        start();
    }

    public void stop(){
        BackgroundExecutor.cancelAll("request_task",true);
    }


}
