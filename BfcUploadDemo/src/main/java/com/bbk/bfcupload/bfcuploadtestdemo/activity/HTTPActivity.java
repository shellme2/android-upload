package com.bbk.bfcupload.bfcuploadtestdemo.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bbk.bfcupload.bfcuploadtestdemo.R;
import com.bbk.bfcupload.bfcuploadtestdemo.util.UploadUtilds;
import com.eebbk.bfc.uploadsdk.upload.net.NetworkType;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.HttpTask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.IController;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadController;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadListener;
import com.eebbk.bfc.util.ui.fileselector.ExplorerDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HTTPActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG=HTTPActivity.class.getName();
    private static final String url = "http://da.eebbk.net:80/v3/receiveFile";
    private final int HANDER_SUCCESS = 0;
    private final int HANDER_FAIL = 1;
    private final int HANDER_ONLOADING = 2;

    @BindView(R.id.task_http_add)
    Button mAddBtn;
    @BindView(R.id.task_http_result)
    TextView mResult;
    @BindView(R.id.task_http_query)
    Button mQueryBtn;
    @BindView(R.id.task_http_reload)
    Button mReloadBtn;
    @BindView(R.id.task_http_delet)
    Button mDeleteBtn;
    @BindView(R.id.task_http_file_add)
    Button mAddFileBtn;
    @BindView(R.id.task_http_name)
    EditText mTaskName;
    @BindView(R.id.task_http_file_names)
    TextView mFileName;
    @BindView(R.id.task_http_progress)
    ProgressBar mProgress;
    @BindView(R.id.task_http_progress_number)
    TextView mProgressNum;
    @BindView(R.id.task_http_progress_speed)
    TextView mProgressSpeed;
    @BindView(R.id.task_http_progress_total)
    TextView mProgressTotal;
    @BindView(R.id.task_http_progress_current)
    TextView mProgressCurrent;
    @BindView(R.id.task_http_progress_remaintime)
    TextView mProgressRemainTime;
    @BindView(R.id.back_iv)
    ImageView mBack;
    @BindView(R.id.title_tv)
    TextView mTitle;

    private ITask task = null;
    private ITask queryTask = null;
    private ExplorerDialog mAddDialog;
    private UploadListener mUploadListener;
    private IController mUploadController = null;
    private ArrayList<File> mFileList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        initView();
    }

    @OnClick({R.id.task_http_add, R.id.task_http_reload,R.id.task_http_file_add,R.id.task_http_delet,R.id.task_http_query,R.id.back_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_http_add:
                handleAdd();
                break;
            case R.id.task_http_reload:
                handleReload();
                break;
            case R.id.task_http_file_add:
                handleFileAdd();
                break;
            case R.id.task_http_delet:
                handleDelete();
                break;
            case R.id.task_http_query:
                handleQuery();
                break;
            case R.id.back_iv:
                finish();
                break;
            default:
                break;
        }
    }

    private void handleQuery() {
        queryTask = mUploadController.getTaskById(task.getId());
        queryDataSuccess(queryTask);
    }

    private void queryDataSuccess(ITask query) {
        String type;
        if (query == null) {
            mResult.setText("没有任务或查询为空");
            return;
        }
        mResult.setText("");
        if (query.getTaskType() == 4) {
            type = "云上传";
        } else {
            type = String.valueOf(query.getTaskType());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.equals(type, "2")) {
                    type = "http上载";
                }
            }
        }
        String stateCode = getStateCode(query.getState());
        String temp = "\b\b" + "任务id:" + query.getId() + "   任务名：" + query.getFileName() + "    类型 ：" + type + "\n\n"
                + "\b\b任务状态：" + stateCode + "       上载大小 ：" + UploadUtilds.formatBytes(query.getUploadedSize()) + "\n\n";
        mResult.setText(temp);
    }

    private String getStateCode(int state) {
        if (state == 1) {
            return "正在等待";
        } else if (state == 2) {
            return "正在上载";
        } else if (state == 4) {
            return "上载暂停";
        } else if (state == 8) {
            return "上载成功";
        } else if (state == 16) {
            return "上载异常";
        } else {
            return "上载失败";
        }
    }

    private void handleAdd() {
        task = new HttpTask(url);
        task.setUrl(url);
        String taskName = mTaskName.getText().toString();
        if (mFileList.size() == 0) {
            Toast.makeText(this, "请添加文件或输入任务名", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < mFileList.size(); i++) {
            String pFilePath = mFileList.get(i).getAbsolutePath();
            task.addFileBody("a" + i, pFilePath);
            task.addStringBody("text", "123");
            task.putExtra("HTTP", "xx" + i);
        }
        task.setFileName(taskName);
        task.setNetworkTypes(NetworkType.NETWORK_MOBILE);
        int taskId = mUploadController.addTask(mUploadListener, task);
        LogUtils.i("mTaskId:"+ taskId);
        task.setId(taskId);
    }

    private void handleDelete() {
        int row = mUploadController.deleteTask(task.getId());
        mProgress.setProgress(0);
        mProgressNum.setText("0");
        mProgressRemainTime.setText("");
        mProgressCurrent.setText("");
        mProgressTotal.setText("");
        mProgressSpeed.setText("");
        if (row == -1) {
            mResult.setText("删除任务失败！");
        } else {
            mResult.setText("删除任务成功！");
        }
    }

    private void handleFileAdd() {
        mAddDialog.show();
        mAddDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                mFileList = mAddDialog.getFileList();
                StringBuffer msg = new StringBuffer();
                for (File temp : mFileList) {
                    msg.append(temp.getAbsolutePath()).append("\n");
                }
                mFileName.setText(msg);
            }
        });
    }

    private void handleReload() {
        mUploadController.reloadTask(mUploadListener, task);
    }

    private void initView() {
        task = new HttpTask(url);
        ButterKnife.bind(this);
        mTitle.setText("Http上传");
        mAddDialog = new ExplorerDialog(this, 10);
        mUploadController = new UploadController(HTTPActivity.this);
        mUploadListener = new UploadListener() {
            @Override
            public void onSuccess(int id, String taskName, long total, List<String> urlList) {
                LogUtils.i(TAG+"  onSuccess");
                onSuceesMessage(id,taskName,total);
            }

            @Override
            public void onFailure(int id, String taskName, String msg) {
                LogUtils.i(TAG+"  onFailure");
                onFailureMessage(id,taskName,msg);
            }

            @Override
            public void onLoading(int id, long total, long current, long remainingMillis, long speed, int precent) {
                LogUtils.i(TAG+"  onLoading");
                onLoadingMessage(id,total,current,remainingMillis,speed,precent);
            }
        };
    }

    private void onSuceesMessage(int id, String taskName, long total){
        Message msg = new Message();
        msg.what = HANDER_SUCCESS;
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("taskName", taskName);
        bundle.putLong("total", total);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private void onFailureMessage(int id, String taskName, String msg){
        Message msg1 = new Message();
        msg1.what = HANDER_FAIL;
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("taskName", taskName);
        bundle.putString("msg", msg);
        msg1.setData(bundle);
        mHandler.sendMessage(msg1);
    }

    private void onLoadingMessage(int id, long total, long current, long remainingMillis, long speed, int precent){
        Message msg = new Message();
        msg.what = HANDER_ONLOADING;
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putLong("total", total);
        bundle.putLong("current", current);
        bundle.putLong("remainingMillis", remainingMillis);
        bundle.putLong("speed", speed);
        bundle.putInt("precent", precent);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case HANDER_SUCCESS:
                    updateDataSuccess(
                            bundle.getString("taskName"),
                            bundle.getLong("total"));
                    break;
                case HANDER_FAIL:
                    updateDataFail(
                            bundle.getString("taskName"),
                            bundle.getString("msg"));
                    break;
                case HANDER_ONLOADING:
                    updateDataGoing(
                            bundle.getLong("total"),
                            bundle.getLong("current"),
                            bundle.getLong("remainingMillis"),
                            bundle.getLong("speed"),
                            bundle.getInt("precent")
                    );
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 更新进度条信息
     */
    private void updateDataGoing(long total, long current, long remainingMillis, long speed, int precent) {
        if (precent >= 100) {
            mProgress.setProgress(100);
            mProgressNum.setText(getResources().getText(R.string.hundred));
        } else {
            mProgress.setProgress(precent);
            mProgressNum.setText(String.valueOf(precent));
        }
        mProgressRemainTime.setText(UploadUtilds.formatDuration(remainingMillis));
        mProgressCurrent.setText(UploadUtilds.formatBytes(current));
        mProgressTotal.setText(UploadUtilds.formatBytes(total));
        mProgressSpeed.setText(UploadUtilds.getSpeedString(speed));
    }

    /**
     * 上传完成，更新信息
     */
    private void updateDataFail(String taskName, String msg) {
        String temp = "\b\b" + taskName + "：：上传失败!\n\n" + "\b\b错误信息：" + msg;
        mResult.setText(temp);
        mProgress.setProgress(0);
        mProgressNum.setText(getResources().getText(R.string.zero));
        mProgressRemainTime.setText("");
        mProgressCurrent.setText("");
        mProgressTotal.setText("");
        mProgressSpeed.setText("");
    }

    /**
     * 上传成功更新信息
     */
    private void updateDataSuccess(String taskName, long total) {
        String temp = "\b\b" + taskName + "：：上传成功!\n\n" + "\b\b文件大小为：" + UploadUtilds.formatBytes(total);
        mResult.setText(temp);
        mProgress.setProgress(100);
        mProgressNum.setText(getResources().getText(R.string.hundred));
        mProgressRemainTime.setText("");
        mProgressCurrent.setText("" + UploadUtilds.formatBytes(total));
        mProgressSpeed.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
