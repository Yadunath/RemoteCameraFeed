package yedu.witworks.com.bluetoothcamera.transmitter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import yedu.witworks.com.bluetoothcamera.R;
import yedu.witworks.com.bluetoothcamera.activities.TransmitterActivity;
import yedu.witworks.com.bluetoothcamera.bluetooth.BluetoothActivity;

public class DeviceSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener {

    private BluetoothAdapter mAdapter = null;
    
    private ListView lv_list = null;
    private Button pairButton =null;
    private ArrayAdapter<String> adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_select);
        lv_list = (ListView) findViewById(R.id.lv_list);
        pairButton=(Button)findViewById(R.id.button3);
        lv_list.setOnItemClickListener(this);

        pairButton.setOnClickListener(this);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        lv_list.setAdapter(adapter);

        /**
         * Set Action Bar Color
         */
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.receiver_bar)));
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        setMyListData();
    }
    private void setMyListData() {
        adapter.clear();
        for (BluetoothDevice devices : mAdapter.getBondedDevices()) {
            adapter.add(devices.getName());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Log.d("OUT",
                "point ->" + position + " name -> " + adapter.getItem(position));

        Intent intent = new Intent(this, TransmitterActivity.class);
        intent.putExtra("name", adapter.getItem(position));
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button3:
                Intent intent=new Intent(DeviceSelectActivity.this, BluetoothActivity.class);
                startActivity(intent);
                break;
        }
    }
}
