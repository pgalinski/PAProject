package pl.pg.kaboom.activity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationSet;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import pl.pg.kaboom.KaBoomApplication;
import pl.pg.kaboom.R;
import pl.pg.kaboom.task.RequestBackgroundTask;

/**
 * Created by pgalinsk 2015-06-02
 */

@EActivity(R.layout.main_menu_activity)
public class MainMenuActivity extends FragmentActivity {

    private static final String TAG = "MainMenuActivity";


    private boolean disconnectClicked = false;


    private AnimationSet slide;
    Context ctx;

    private boolean connected;


    @Bean
    RequestBackgroundTask requestBackgroundTask;


    @App
    protected KaBoomApplication app;



    @AfterViews
    protected void initUI(){

        ctx = this;

        if(!app.getBluetoothSPP().isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_bluetooth), Toast.LENGTH_SHORT).show();
        }

        app.getBluetoothSPP().setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                sendBroadcast(data);
            }
        });

        app.getBluetoothSPP().setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                Toast.makeText(ctx, getString(R.string.bluetooth_disconnected), Toast.LENGTH_LONG).show();
                setConnected(false);
                requestBackgroundTask.stop();

                if (!isDisconnectClicked()) {
                    startAutoConnect();
                }
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(ctx, getString(R.string.couldnt_connect_bluetooth), Toast.LENGTH_LONG).show();
                setConnected(false);
                requestBackgroundTask.stop();
            }

            public void onDeviceConnected(String name, String address) {
                Toast.makeText(ctx, getString(R.string.connected), Toast.LENGTH_LONG).show();
                setConnected(true);
                requestBackgroundTask.start();
                app.setLastBtName(name);
                app.setLastBtAddress(address);
            }
        });
        checkForBt();
    }

    protected void startAutoConnect(){
        Toast.makeText(ctx,"Próbuję połączyć z ostatnio używanym urządzeniem",Toast.LENGTH_SHORT).show();
        app.getBluetoothSPP().connect(app.getLastBtAddress());
    }

    protected void stopAutoConnect(){
        app.getBluetoothSPP().stopAutoConnect();
    }



    public void sendRequestIfConnected(){
        if (isConnected()) {
            app.getBluetoothSPP().send(new byte[]{0x66, 0x2E}, false);
            Log.d(TAG,"Request has been sent");
        }
    }


    /*private void sendRandomBroadcast() {

        sendBroadcast(TestDataCreator.getTestData());
    }*/

    private void sendBroadcast(byte[] buf) {
        Intent intent = new Intent();
        intent.setAction("pl.net.amg.solarteam.DATA_RECEIVED");
        intent.putExtra("Data", buf);
        sendBroadcast(intent);
    }

    private void disconnect() {
        if(app.getBluetoothSPP().getServiceState() == BluetoothState.STATE_CONNECTED) {
            app.getBluetoothSPP().disconnect();
        }
    }

    private void connectWithOther() {
        setDisconnectClicked(false);
        app.getBluetoothSPP().setDeviceTarget(BluetoothState.DEVICE_OTHER);
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    private void connectWithAndroid() {
        setDisconnectClicked(false);
        app.getBluetoothSPP().setDeviceTarget(BluetoothState.DEVICE_ANDROID);
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }


    private void checkForBt() {
        if (!app.getBluetoothSPP().isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!app.getBluetoothSPP().isServiceAvailable()) {
                app.getBluetoothSPP().setupService();
                app.getBluetoothSPP().startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                app.getBluetoothSPP().connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                app.getBluetoothSPP().setupService();
                app.getBluetoothSPP().startService(BluetoothState.DEVICE_ANDROID);
            } else {
                Toast.makeText(getApplicationContext()
                        , getString(R.string.bluetooth_wasnt_enabled)
                        , Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        }
    }

    public boolean isDisconnectClicked() {
        return disconnectClicked;
    }

    public void setDisconnectClicked(boolean disconnectClicked) {
        this.disconnectClicked = disconnectClicked;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.getBluetoothSPP().stopService();
        requestBackgroundTask.stop();
    }




    public static class MenuDialogFragment extends DialogFragment{

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.menu_dialog_fragment, null, false);

            return view;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }
    }


}