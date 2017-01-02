package com.example.raj.smartcare.Activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.raj.smartcare.Buisness.ChatBusinessLogic;
import com.example.raj.smartcare.GraphView.GraphView;
import com.example.raj.smartcare.R;
import com.example.raj.smartcare.util.ToastUtil;

public class MainActivity extends AppCompatActivity {
    private GraphView graphView;
    public static int MSG_TOAST = 1;
    public static int MSG_BLUETOOTH = 2;
    public static int BT_TIMER_VISIBLE = 30;

    private final int BT_ACTIVATE = 0;
    private final int BT_VISIBLE = 1;

    private Button buttonService;
    private Button buttonClient;
    private ImageButton buttonSend;
    private EditText editTextMessage;
    private ListView listVewHistoric;
    private ArrayAdapter<String> historic;

    private ToastUtil toastUtil;
    private ChatBusinessLogic chatBusinessLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphView = (GraphView) findViewById(R.id.graph_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActivityHelper.initialize(this);
        settingsAttributes();
        settingsView();

        initializeBluetooth();
        registerFilters();

    }

    //@Override
    public void settingsAttributes() {
        toastUtil = new ToastUtil(this);
        chatBusinessLogic = new ChatBusinessLogic(this, handler);
    }

    //@Override
    public void settingsView(){
       // editTextMessage = (EditText)findViewById(R.id.editTextMessage);


        buttonService = (Button)findViewById(R.id.buttonService);
        buttonService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BT_TIMER_VISIBLE);
                startActivityForResult(discoverableIntent, BT_VISIBLE);
            }
        });


    }

    public void initializeBluetooth() {
        if (chatBusinessLogic.getBluetoothManager().verifySuportedBluetooth()) {
            if (!chatBusinessLogic.getBluetoothManager().isEnabledBluetooth()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, BT_ACTIVATE);
            }
        } else {
            toastUtil.showToast(getString(R.string.no_support_bluetooth));
            finish();
        }
    }

    public void registerFilters(){
        chatBusinessLogic.registerFilter();
    }

    private Handler handler =  new Handler(){
        public void handleMessage(android.os.Message msg){
          synchronized (msg){
              switch (msg.what)
              {
                  case 1:
                      toastUtil.showToast((String)(msg.obj));
                        break;
                  case 2:
                      Log.e("MESSAGE RECIEVED", (String)(msg.obj));
                      publishGraph((String)msg.obj);
                      break;
              }
          }

        };
    };

    public void publishGraph(final String fstr){

        Log.v("Report", "reporting back from the Bluetooth Data");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Double data = Double.parseDouble(fstr);
                graphView.plotProcedure(data);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BT_ACTIVATE:
                if (resultCode != RESULT_OK) {
                    toastUtil.showToast(getString(R.string.activate_bluetooth_to_continue));
                    finish();
                }
                break;
            case BT_VISIBLE:
                if(resultCode == BT_TIMER_VISIBLE){
                    Log.e("OnActivity","OnActivityResult_Obtained");
                    chatBusinessLogic.stopComunication();
                     chatBusinessLogic.startServer();
                }else {
                    toastUtil.showToast(getString(R.string.device_must_visible));
                }
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        chatBusinessLogic.unregisterFilter();
        chatBusinessLogic.stopComunication();
    }
}
