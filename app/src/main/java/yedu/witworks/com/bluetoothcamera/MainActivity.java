package yedu.witworks.com.bluetoothcamera;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import yedu.witworks.com.bluetoothcamera.activities.ReceiverActivity;
import yedu.witworks.com.bluetoothcamera.manage.Info;
import yedu.witworks.com.bluetoothcamera.transmitter.DeviceSelectActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonTxr;
    private Button mButtonRxr;

    private BluetoothAdapter mAdapter;
    private ProgressDialog waitDialog;

    private IntentFilter mFilter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonTxr=(Button)findViewById(R.id.button2);
        mButtonRxr=(Button)findViewById(R.id.button);

        mButtonTxr.setOnClickListener(this);
        mButtonRxr.setOnClickListener(this);
        mFilter = new IntentFilter();
        mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, mFilter);
        setUpBlue();
        
    }


    private void setUpBlue() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }else {
            cheackPerInfo();
        }
    }

    private void setEnableBluetooth() {
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("Bluetooth starting");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setCancelable(false);
        waitDialog.show();

        mAdapter.enable();
    }

    private Boolean cheackPerInfo(){
        if (mAdapter.getBondedDevices().size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "There is no pairing information . Please go to the terminal and the pairing to be connected ",
                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivityForResult(intent, Info.REQUEST_BLUE_SETTING);

            return false;
        }
        return true;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (mAdapter.isEnabled()) {
//                    waitDialog.dismiss();
                    waitDialog = null;
                    cheackPerInfo();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Info.REQUEST_BLUE_SETTING:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId())
        {
            case R.id.button:
                intent=new Intent(MainActivity.this, ReceiverActivity.class);
                startActivity(intent);
                break;
            case R.id.button2:
                intent=new Intent(MainActivity.this, DeviceSelectActivity.class);
                startActivity(intent);
                break;
        }
    }
}
