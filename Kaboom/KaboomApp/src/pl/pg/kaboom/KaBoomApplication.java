package pl.pg.kaboom;

import android.app.Application;

import org.androidannotations.annotations.EApplication;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

/**
 * Created by pgalinsk 2015-06-02
 */
@EApplication
public class KaBoomApplication extends Application {

    protected BluetoothSPP bluetoothSPP;

    private String lastBtName;
    private String lastBtAddress;

    public BluetoothSPP getBluetoothSPP() {
        if (bluetoothSPP==null) {
            bluetoothSPP = new BluetoothSPP(this);
        }
        return bluetoothSPP;
    }

    public String getLastBtName() {
        return lastBtName;
    }

    public void setLastBtName(String lastBtName) {
        this.lastBtName = lastBtName;
    }

    public String getLastBtAddress() {
        return lastBtAddress;
    }

    public void setLastBtAddress(String lastBtAddress) {
        this.lastBtAddress = lastBtAddress;
    }
}
