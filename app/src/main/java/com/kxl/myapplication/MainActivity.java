package com.kxl.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dothantech.lpapi.LPAPI;
import com.dothantech.printer.IDzPrinter;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesAdapter;
    private ArrayAdapter<String> mNewDevicesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 调用LPAPI对象的init方法初始化对象
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
//        this.api = LPAPI.Factory.createInstance(mCallback);

        setResult(Activity.RESULT_CANCELED);
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                discoverDevice();
            }
        });
        findViewById(R.id.button_pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.button_stick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,StickActivity.class);
                startActivity(intent);
            }
        });
        mPairedDevicesAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);
        mNewDevicesAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);
// 已经绑定的设备
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

// 搜索到的可用设备
        ListView newListView = (ListView) findViewById(R.id.new_devices);
        newListView.setAdapter(mNewDevicesAdapter);
//        newListView.setOnItemClickListener(mDeviceClickListener);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        this.registerReceiver(mReceiver, filter);

// 获取默认的蓝牙adapter
//        BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
// 获取当前可用的蓝牙设置
//        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        /*if (pairedDevices.size() > 0) {
//            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesAdapter.add(device.getName() + "\n"
                        + device.getAddress());
            }
        } else {
//            String noDevices = getResources().getText(R.string.none_paired)
//                    .toString();
//            mPairedDevicesAdapter.add(noDevices);
        }*/
    }

    /**
     * 扫描本地可用的设备
     */
    private void discoverDevice() {
        if (!mBtAdapter.isEnabled()){
            mBtAdapter.enable();
            return;
        }
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
//        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
//如果正在扫描，先停止扫描，再重新扫描
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
//        this.unregisterReceiver(mReceiver);
    }


    /**
     * 监听搜索到的设备
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.e("======", "----ACTION_FOUND---");
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if(!TextUtils.isEmpty(device.getName())){
                        if(device.getName().equals("小米手机")){
                            mNewDevicesAdapter.add(device.getName() + "\n"
                                    + device.getAddress());
                        }
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                Log.e("===========" ,"----ACTION_DISCOVERY_FINISHED----");
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(
                            R.string.none_found_device).toString();
                    mNewDevicesAdapter.add(noDevices);
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mBtAdapter.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.ACCESS_FINE_LOCATION
                ,Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // LPAPI 打印机操作相关的回调函数。
    private final LPAPI.Callback mCallback = new LPAPI.Callback() {

        /****************************************************************************************************************************************/
        // 所有回调函数都是在打印线程中被调用，因此如果需要刷新界面，需要发送消息给界面主线程，以避免互斥等繁琐操作。
        /****************************************************************************************************************************************/

        // 打印机连接状态发生变化时被调用
        @Override
        public void onStateChange(IDzPrinter.PrinterAddress arg0, IDzPrinter.PrinterState arg1) {
            final IDzPrinter.PrinterAddress printer = arg0;
            switch (arg1) {
                case Connected:
                case Connected2:
                    // 打印机连接成功，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrinterConnected(printer);
                        }
                    });
                    break;

                case Disconnected:
                    // 打印机连接失败、断开连接，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrinterDisconnected();
                        }
                    });
                    break;

                default:
                    break;
            }
        }

        // 蓝牙适配器状态发生变化时被调用
        @Override
        public void onProgressInfo(IDzPrinter.ProgressInfo arg0, Object arg1) {
        }

        @Override
        public void onPrinterDiscovery(IDzPrinter.PrinterAddress arg0, IDzPrinter.PrinterInfo arg1) {
        }

        // 打印标签的进度发生变化是被调用
        @Override
        public void onPrintProgress(IDzPrinter.PrinterAddress address, Object bitmapData, IDzPrinter.PrintProgress progress, Object addiInfo) {
            switch (progress) {
                case Success:
                    // 打印标签成功，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            onPrintSuccess();
                        }
                    });
                    break;

                case Failed:
                    // 打印标签失败，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            onPrintFailed();
                        }
                    });
                    break;

                default:
                    break;
            }
        }
    };

    private void onPrinterDisconnected() {
        Toast.makeText(this,"连接失败",Toast.LENGTH_SHORT).show();
    }

    private void onPrinterConnected(IDzPrinter.PrinterAddress printer) {
        Toast.makeText(this,"连接成功",Toast.LENGTH_SHORT).show();
    }
    // 上次连接成功的设备对象
    private IDzPrinter.PrinterAddress mPrinterAddress = null;
    private LPAPI api;
    // 用于处理各种通知消息，刷新界面的handler
    private final Handler mHandler = new Handler();
}
