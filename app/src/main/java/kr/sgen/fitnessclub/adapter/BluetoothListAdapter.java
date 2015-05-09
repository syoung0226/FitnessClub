package kr.sgen.fitnessclub.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import kr.sgen.fitnessclub.R;

/**
 * Created by 선영 on 2015-05-09.
 */
public class BluetoothListAdapter extends BaseAdapter {

    private List<BluetoothDevice> data;
    private Context context;

    public BluetoothListAdapter(Context context){
        this.context = context;
    }

    public void setData(List<BluetoothDevice> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bluetooth_list, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        BluetoothDevice device = data.get(position);
        holder.setData(device);

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
