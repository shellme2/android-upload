package com.bbk.bfcupload.bfcuploadtestdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.bbk.bfcupload.bfcuploadtestdemo.R;
import com.bbk.bfcupload.bfcuploadtestdemo.basic.presenter.CloudUploadPresenter;
import com.bbk.bfcupload.bfcuploadtestdemo.basic.ui.ICloudUploadView;
import com.bbk.bfcupload.bfcuploadtestdemo.basic.ui.IOperationPanel;
import com.bbk.bfcupload.bfcuploadtestdemo.basic.ui.OperationPanelUIHelper;
import com.bbk.bfcupload.bfcuploadtestdemo.basic.ui.UploadConfigUIHelper;
import com.bbk.bfcupload.bfcuploadtestdemo.basic.ui.UploadStatusUIHelper;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.MultiCloudTask;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CloudUploadActivity extends Activity implements IOperationPanel, ICloudUploadView {
    @BindView(R.id.show_config_panel_btn)
    Button showConfigPanelBtn;
    @BindView(R.id.task_config_panel_sv)
    ScrollView taskConfigPanelSv;
    @BindView(R.id.task_status_layout)
    LinearLayout taskStatusLayout;
    @BindView(R.id.handler_panel_sv)
    ScrollView handlePanelSv;
    @BindView(R.id.show_handler_panel_btn)
    Button showHandlerPanelBtn;
    @BindView(R.id.title_tv)
    TextView mTitle;
    @BindView(R.id.back_iv)
    ImageView mBack;

    private UploadConfigUIHelper mUploadConfigUIHelper;
    private UploadStatusUIHelper mUploadStatusUIHelper;
    private CloudUploadPresenter mPresenter;
    private MultiCloudTask mTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_upload);
        initView();
        initPresenter();
    }

    private void initPresenter() {
        mPresenter = new CloudUploadPresenter(this);
        mPresenter.bindTaskConfig(mUploadConfigUIHelper);
    }

    private void initView() {
        ButterKnife.bind(this);
        mUploadConfigUIHelper = new UploadConfigUIHelper(this);
        mUploadConfigUIHelper.bindView(taskConfigPanelSv);
        mUploadStatusUIHelper = new UploadStatusUIHelper();
        mUploadStatusUIHelper.bindView(taskStatusLayout);
        OperationPanelUIHelper operationPanelUIHelper = new OperationPanelUIHelper(this);
        operationPanelUIHelper.bindView(handlePanelSv);
        mTitle.setText("云上传");
    }

    @Override
    public void onStartBtnClick() {
        if(mTask != null && mTask.getState() == 0) {
            Toast.makeText(getApplicationContext(), "当前任务正在上传呢，要不您再等等？", Toast.LENGTH_SHORT).show();
            return;
        }

        mTask = mPresenter.createTask();
        if(mTask == null) {
            Toast.makeText(getApplicationContext(), "参数不全", Toast.LENGTH_SHORT).show();
            return;
        }
        mPresenter.startTask(mTask);
        mUploadConfigUIHelper.setConfigViewEnable(false);
    }

    @Override
    public void onDeleteBtnClick() {
        mPresenter.deleteTask(mTask);
        mTask = null;
        mUploadConfigUIHelper.setConfigViewEnable(true);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public ITask getTask() {
        return mTask;
    }

    @Override
    public void updateSuccessView(ITask task) {
        mTask = (MultiCloudTask) task;
        mUploadConfigUIHelper.updateTaskConfigView(mTask);
        mUploadStatusUIHelper.showSuccessView(mTask);
    }

    @Override
    public void updateUnfinishedView(ITask task) {
        mTask = (MultiCloudTask) task;
        mUploadConfigUIHelper.updateTaskConfigView(mTask);
    }

    @Override
    public void onWaiting() {
        mUploadConfigUIHelper.setTaskIdView(mTask.getId());
        mUploadStatusUIHelper.updateWaitingView(mTask);
    }

    @Override
    public void onLoading(long total, long current, long speed, int precent) {
        mUploadStatusUIHelper.updateUploadingView(total, current, speed, precent);
        mUploadConfigUIHelper.setConfigViewEnable(false);
    }

    @Override
    public void onSuccess(List<String> urlList) {
        mUploadStatusUIHelper.showSuccessView(mTask);
        mUploadConfigUIHelper.updateTaskConfigView(mTask);
        mUploadConfigUIHelper.setConfigViewEnable(true);
        LogUtils.i("upload success,urlList=" + urlList.toString());
    }

    @Override
    public void onFailure(String msg) {
        mUploadStatusUIHelper.showFailView(msg);
        mUploadConfigUIHelper.setConfigViewEnable(true);
    }

    @OnClick({R.id.show_config_panel_btn, R.id.show_handler_panel_btn,R.id.back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.show_config_panel_btn:
                if (taskConfigPanelSv.getVisibility() == View.VISIBLE) {
                    taskConfigPanelSv.setVisibility(View.GONE);
                    showConfigPanelBtn.setText("展开");
                } else {
                    taskConfigPanelSv.setVisibility(View.VISIBLE);
                    showConfigPanelBtn.setText("收起");
                    handlePanelSv.setVisibility(View.GONE);
                    showHandlerPanelBtn.setText("展开");
                }
                break;
            case R.id.show_handler_panel_btn:
                if (handlePanelSv.getVisibility() == View.VISIBLE) {
                    handlePanelSv.setVisibility(View.GONE);
                    showHandlerPanelBtn.setText("操作");
                } else {
                    handlePanelSv.setVisibility(View.VISIBLE);
                    showHandlerPanelBtn.setText("收起");
                    taskConfigPanelSv.setVisibility(View.GONE);
                    showConfigPanelBtn.setText("展开");
                }
                break;
            case R.id.back_iv:
                finish();
                break;
        }
    }
}