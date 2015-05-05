package kr.sgen.fitnessclub;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;


/*
TODO:
1. footerView를 List에 추가.
Progres를 추가하고
블루투스 찾는게 끝날 때 footerView를 제거

2. 연결
* */
public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private BluetoothAdapter mBluetoothAdapter;

    @InjectView(R.id.bluetoothList) ListView bluetoothListView;

    private List<BluetoothDevice> bluetoothDeviceData = new ArrayList<>();
    private BluetoothListAdapter adapter = new BluetoothListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        bluetoothListView.setAdapter(adapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
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

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDeviceData.add(device);
                adapter.notifyDataSetChanged();
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            }
        }
    };

    @OnItemClick(R.id.bluetoothList)
    void onItemClicked(int position) {
        BluetoothDevice selectedDevice = bluetoothDeviceData.get(position);
        Toast.makeText(this, selectedDevice.getName() +" " + selectedDevice.getAddress(), Toast.LENGTH_SHORT).show();
    }

    private class BluetoothListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bluetoothDeviceData.size();
        }

        @Override
        public Object getItem(int position) {
            return bluetoothDeviceData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_bluetooth_list, null);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            BluetoothDevice data = bluetoothDeviceData.get(position);
            holder.setData(data);

            return convertView;
        }

        private class Holder {
            private TextView name;
            private TextView address;

            private Holder(View parent) {
                name = (TextView) parent.findViewById(R.id.item_bluetooth_list_name);
                address = (TextView) parent.findViewById(R.id.item_bluetooth_list_address);
            }

            private void setData(BluetoothDevice data) {
                name.setText(data.getName());
                address.setText(data.getAddress());
            }
        }
    }
}

