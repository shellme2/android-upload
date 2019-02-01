package com.bbk.bfcupload.bfcuploadtestdemo.basic.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bbk.bfcupload.bfcuploadtestdemo.R;
import com.bbk.bfcupload.bfcuploadtestdemo.util.NetworkParseUtil;
import com.eebbk.bfc.uploadsdk.upload.net.NetworkType;
import com.eebbk.bfc.uploadsdk.upload.share.UploadConstants;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.util.ui.fileselector.ExplorerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UploadConfigUIHelper implements IUploadTaskConfig {
    @BindView(R.id.select_file_btn)
    Button selectFileBtn;
    @BindView(R.id.show_file_path_tv)
    TextView showFilePathTv;
    @BindView(R.id.show_id_tv)
    TextView showTaskIdTv;
    @BindView(R.id.show_task_name_tv)
    EditText showTaskNameTv;
    @BindView(R.id.network_wifi_chx)
    CheckBox networkWifiChx;
    @BindView(R.id.network_mobile_chx)
    CheckBox networkMobileChx;
    @BindView(R.id.network_bluetooth_chx)
    CheckBox networkBluetoothChx;
    @BindView(R.id.show_file_url_tv)
    TextView showFileUrlTv;

    private final Activity mActivity;
    private List<File> mFileList = new ArrayList<>();

    public UploadConfigUIHelper(Activity activity) {
        mActivity = activity;
    }

    public void bindView(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    public void setConfigViewEnable(boolean enable) {
        selectFileBtn.setEnabled(enable);
        showTaskNameTv.setEnabled(enable);
        networkWifiChx.setEnabled(enable);
        networkMobileChx.setEnabled(enable);
        networkBluetoothChx.setEnabled(enable);
    }

    public void setTaskIdView(long taskId) {
        showTaskIdTv.setText(taskId + "");
    }

    @OnClick({R.id.select_file_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.select_file_btn:
                showExplorerDialog();
                break;
        }
    }

    private void showExplorerDialog() {
        final ExplorerDialog explorerDialog = new ExplorerDialog(mActivity, 2);
        explorerDialog.show();
        explorerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                mFileList = explorerDialog.getFileList();
                StringBuffer msg = new StringBuffer();
                for (File temp : mFileList) {
                    msg.append(temp.getAbsolutePath()).append("\n");
                }
                showFilePathTv.setText(msg);
                showFileUrlTv.setText("");
                showTaskIdTv.setText("");
            }
        });
    }


    @Override
    public List<File> getFileList() {
        return mFileList;
    }

    @Override
    public String getTaskName() {
        return showTaskNameTv.getText().toString();
    }

    @Override
    public int getNetworkTypes() {
        boolean wifiEnable = networkWifiChx.isChecked();
        boolean mobileEnable = networkMobileChx.isChecked();
        boolean bluetoothEnable = networkBluetoothChx.isChecked();
        int networkTypes = NetworkType.NETWORK_UNKNOWN;
        if (wifiEnable) {
            networkTypes |= NetworkType.NETWORK_WIFI;
        }
        if (mobileEnable) {
            networkTypes |= NetworkType.NETWORK_MOBILE;
        }
        if (bluetoothEnable) {
            networkTypes |= NetworkType.NETWORK_BLUETOOTH;
        }
        return networkTypes;
    }

    public void updateTaskConfigView(ITask task) {
        mFileList = initFileList(task);
        String filePathStr = getFilePathStr(task);
        String fileUrlStr = getFileUrlStr(task);

        showTaskIdTv.setText(task.getId() + "");
        showTaskNameTv.setText(task.getFileName());
        showFilePathTv.setText(filePathStr);
        showFileUrlTv.setText(fileUrlStr);

        int networkTypes = task.getNetworkTypes();
        boolean enableWifi = NetworkParseUtil.containsWifi(networkTypes);
        boolean enableMobile = NetworkParseUtil.containsMobile(networkTypes);
        boolean enableBlueTooth = NetworkParseUtil.containsBluetooth(networkTypes);
        networkWifiChx.setChecked(enableWifi);
        networkMobileChx.setChecked(enableMobile);
        networkBluetoothChx.setChecked(enableBlueTooth);
    }

    @NonNull
    private String getFileUrlStr(ITask task) {
        if (task.getState() != UploadConstants.STATUS_SUCCESSFUL) {
            return "";
        }

        StringBuilder fileUrlBuilder = new StringBuilder();
        for (Map.Entry entry : task.getFilesMap().entrySet()) {
            String fileUrl = (String) entry.getValue();
            if (!TextUtils.isEmpty(fileUrl)) {
                fileUrlBuilder.append(fileUrl).append("\n");
            }
        }
        return fileUrlBuilder.toString();
    }

    @NonNull
    private String getFilePathStr(ITask task) {
        StringBuilder filePathBuilder = new StringBuilder();
        for (Map.Entry entry : task.getFilesMap().entrySet()) {
            String filePath = (String) entry.getValue();
            if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                filePathBuilder.append(filePath).append("\n");
            }
        }
        return filePathBuilder.toString();
    }

    @NonNull
    private List<File> initFileList(ITask task) {
        List<File> fileList = new ArrayList<>();
        for (Map.Entry entry : task.getFilesMap().entrySet()) {
            String filePath = (String) entry.getValue();
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                if (file.exists()) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }
}
