package kr.sgen.fitnessclub;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import kr.sgen.fitnessclub.adapter.BluetoothListAdapter;
import kr.sgen.fitnessclub.bluetooth.ConnectThread;

public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private BluetoothAdapter mBluetoothAdapter;

    @InjectView(R.id.bluetoothList) ListView bluetoothListView;

    private List<BluetoothDevice> bluetoothDeviceData = new ArrayList<>();
    private BluetoothListAdapter adapter;
    private ProgressBar bluetoothProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        adapter = new BluetoothListAdapter(this);
        adapter.setData(bluetoothDeviceData);
        bluetoothListView.setAdapter(adapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        bluetoothProgressBar = (ProgressBar) LayoutInflater.from(MainActivity.this).inflate(R.layout.progressbar, null);
        bluetoothListView.addFooterView(bluetoothProgressBar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "이 기기는 블루투스를 지원하지 않습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            setBluetoothList();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                setBluetoothList();
                break;
        }
    }

    private void setBluetoothList() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            bluetoothDeviceData.add(device);
        }
        adapter.notifyDataSetChanged();
        mBluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("action",action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDeviceData.add(device);
                adapter.notifyDataSetChanged();
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                bluetoothListView.removeFooterView(bluetoothProgressBar);
            }
        }
    };

    @OnItemClick(R.id.bluetoothList)
    void onItemClicked(int position) {
        if(bluetoothDeviceData.size() == position){
            return;
        }
        BluetoothDevice selectedDevice = bluetoothDeviceData.get(position);
        startConnect(selectedDevice);
    }

    private void startConnect(BluetoothDevice selectedDevice) {
        ConnectThread connectThread = new ConnectThread(selectedDevice);
        connectThread.start();
    }
}