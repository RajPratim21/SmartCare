package com.example.raj.smartcare.Buisness;

import com.example.raj.smartcare.Activity.MainActivity;
import com.example.raj.smartcare.alertdialog.AlertDialogDevicesFound;
import com.example.raj.smartcare.broadcast.EventsBluetoothReceiver;
import com.example.raj.smartcare.communication.BluetoothComunication;

/**
 * Created by raj on 22/12/16.
 */
import com.example.raj.smartcare.manager.BluetoothMan;
import com.example.raj.smartcare.task.BluetoothClientTask;
import com.example.raj.smartcare.task.BluetoothServiceTask;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.List;

public class ChatBusinessLogic implements IBusinessLogic.OnBluetoothDeviceSelectedListener,
                                          IBusinessLogic.OnConnectionBluetoothListener,
                                          IBusinessLogic.OnSearchBluetoothListener{

    private Context context;
    private Handler handler;

    private BluetoothMan bluetoothManager;
    private BluetoothComunication bluetoothComunication;
    private AlertDialogDevicesFound alertDialogDevicesFound;
    private EventsBluetoothReceiver eventsBluetoothReceiver;


    public ChatBusinessLogic(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;

        bluetoothManager = new BluetoothMan();
        alertDialogDevicesFound = new AlertDialogDevicesFound(context, this);
        eventsBluetoothReceiver = new EventsBluetoothReceiver(context, this);

    }
    public void startFoundDevices(){
        stopComunication();

        eventsBluetoothReceiver.showProgress();
        bluetoothManager.getBluetoothAdapter().startDiscovery();
    }

    public BluetoothMan getBluetoothManager() {
        return bluetoothManager;
    }

    public void registerFilter() {
        eventsBluetoothReceiver.registerFilters();
    }
    public void  unregisterFilter(){
        eventsBluetoothReceiver.unregisterFilters();
    }

    public void stopComunication() {
        if(bluetoothComunication!=null)
        {
            bluetoothComunication.stopComunication();
        }
    }

    public void startClient(BluetoothDevice bluetoothDevice){
        BluetoothClientTask bluetoothClientTask = new BluetoothClientTask(context, this);
        bluetoothClientTask.execute(bluetoothDevice);
    }

    public void startServer() {
        Log.e("ChatBuisnessLogic", "Starting Server");
        BluetoothServiceTask bluetoothServiceTask = new BluetoothServiceTask(context, this);
        bluetoothServiceTask.execute(bluetoothManager.getBluetoothAdapter());

    }


    public void starCommunication(BluetoothSocket bluetoothSocket){
        bluetoothComunication = new BluetoothComunication(context, handler);
        bluetoothComunication.setBluetoothSocket(bluetoothSocket);
        bluetoothComunication.start();
    }

    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice bluetoothDevice) {
        startClient(bluetoothDevice);
    }

    @Override
    public void onConnectionBluetooth(BluetoothSocket bluetoothSocket) {
        starCommunication(bluetoothSocket);
    }

    @Override
    public void onSearchBluetooth(List<BluetoothDevice> devicesFound) {
        alertDialogDevicesFound.settingsAlertDialog(devicesFound);
    }
}
