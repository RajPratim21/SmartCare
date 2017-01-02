package com.example.raj.smartcare.alertdialog;

import android.app.AlertDialog;
import android.content.Context;

import com.example.raj.smartcare.R;


/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 14:45:57 10/03/2013
 */
public abstract class AlertDialogGeneric {

	protected Context context;
	protected AlertDialog.Builder alert;
	
	public AlertDialogGeneric(Context context) {
		this.context = context;
		
		alert = new AlertDialog.Builder(context);

	}
	
	public void showAlertDialog(){
		alert.create().show();
	}
	
	public void cancelAlertDialog(){
		alert.create().cancel();
	}

}