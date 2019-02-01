package com.bbk.bfcupload.bfcuploadtestdemo.basic.ui;

import android.view.View;
import android.widget.Button;
import com.bbk.bfcupload.bfcuploadtestdemo.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OperationPanelUIHelper {
    @BindView(R.id.start_btn)
    Button startBtn;
    @BindView(R.id.delete_btn)
    Button deleteBtn;

    private final IOperationPanel mOperationPanel;

    public OperationPanelUIHelper(IOperationPanel operationPanel) {
        mOperationPanel = operationPanel;
    }

    public void bindView(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    @OnClick({R.id.start_btn, R.id.delete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_btn:
                mOperationPanel.onStartBtnClick();
                break;
            case R.id.delete_btn:
                mOperationPanel.onDeleteBtnClick();
                break;
        }
    }
}
