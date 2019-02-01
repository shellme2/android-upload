package com.bbk.bfcupload.bfcuploadtestdemo.basic.ui;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bbk.bfcupload.bfcuploadtestdemo.R;
import com.bbk.bfcupload.bfcuploadtestdemo.util.UploadUtilds;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;


import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadStatusUIHelper {
    @BindView(R.id.task_progress_bar)
    ProgressBar taskProgressBar;
    @BindView(R.id.number_progress_tv)
    TextView numberProgressTv;
    @BindView(R.id.total_size_tv)
    TextView totalSizeTv;
    @BindView(R.id.status_tv)
    TextView statusTv;
    @BindView(R.id.finished_size_tv)
    TextView finishedSizeTv;
    @BindView(R.id.speed_tv)
    TextView speedTv;
    @BindView(R.id.exception_tv)
    TextView exceptionTv;

    public void bindView(View rootView){
        ButterKnife.bind(this, rootView);
    }

    public void updateWaitingView(ITask task){
        taskProgressBar.setProgress(0);
        numberProgressTv.setText("0%");
        statusTv.setText("状态：等待中");
        totalSizeTv.setText("总大小：" + UploadUtilds.formatBytes(task.getFileSize()));
        finishedSizeTv.setText("已完成：" + UploadUtilds.formatBytes(task.getUploadedSize()));
        speedTv.setText("速度：");
        exceptionTv.setText("异常信息：");
    }

    public void updateUploadingView(long total, long current, long speed, int precent){
        taskProgressBar.setProgress(precent);
        numberProgressTv.setText(String.format("%s%%", String.valueOf(precent)));
        statusTv.setText("状态：正在上传");
        totalSizeTv.setText("总大小：" + UploadUtilds.formatBytes(total));
        finishedSizeTv.setText("已完成：" + UploadUtilds.formatBytes(current));
        speedTv.setText(String.format("速度：%s", UploadUtilds.getSpeedString(speed)));
        exceptionTv.setText("异常信息：");
    }

    public void showSuccessView(ITask task) {
        taskProgressBar.setProgress(100);
        numberProgressTv.setText("100%");
        statusTv.setText("状态：上传成功");
        totalSizeTv.setText("总大小：" + UploadUtilds.formatBytes(task.getFileSize()));
        finishedSizeTv.setText("已完成：" + UploadUtilds.formatBytes(task.getUploadedSize()));
        speedTv.setText("速度：");
        exceptionTv.setText("异常信息：");
    }

    public void showFailView(String msg) {
        statusTv.setText("状态：上传失败");
        speedTv.setText("速度：");
        exceptionTv.setText("异常信息：" + msg);
    }

}
