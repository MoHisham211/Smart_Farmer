package mo.zain.smartfarmer.ui.control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Set;

import mo.zain.smartfarmer.R;


public class ControlFragment extends Fragment {

    private Button btnPaired;
    private ListView devicelist;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_control, container, false);
        btnPaired=view.findViewById(R.id.paired_dev_btn);
        devicelist=view.findViewById(R.id.paired_dev_listview);

        myBluetooth=BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth==null){
            FancyToast.makeText(getContext(),"Bluetooth Adapter Not Available",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
            //getActivity().finish();
        }else if (!myBluetooth.isEnabled())
        {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1); // Intent to turn on bluetooth adapter
        }else
        {
            pairedDevicesList();
        }
        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });
        return view;
    }

    private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();

        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) // ArrayList with name and MAC address of paired devices
            for(BluetoothDevice bt : pairedDevices)
                list.add(bt.getName() + "\n" + bt.getAddress());
        else
        FancyToast.makeText(getContext(),"No Paired Bluetooth Devices Found.",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();

        // Display paired devices in the listview
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener);
    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3) {
            // MAC address are last 17 characters of the textview clicked
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_ADDRESS,address);
            Navigation.findNavController(v).navigate(R.id.actionControlFragment,bundle);

        }
    };


}