package com.example.raj.smartcare.manager;

import android.bluetooth.BluetoothAdapter;

 public class BluetoothMan {
	
	private BluetoothAdapter bluetoothAdapter;
	
	public BluetoothMan(){
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
	}
	
	public BluetoothAdapter getBluetoothAdapter(){
		return bluetoothAdapter;
	}

	public boolean verifySuportedBluetooth(){
		return (bluetoothAdapter != null) ? true : false;
	}
	
	public boolean isEnabledBluetooth(){
		return bluetoothAdapter.isEnabled();
	}
	
}