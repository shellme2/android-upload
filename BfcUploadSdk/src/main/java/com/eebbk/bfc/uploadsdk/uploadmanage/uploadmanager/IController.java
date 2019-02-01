package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.support.annotation.NonNull;
import java.util.ArrayList;

public interface IController {
	int addTask(UploadListener listtener, ITask pITask) ;
	ITask getTaskById(long id);
	ArrayList<ITask> getTask(Query query);
	ArrayList<ITask> getTaskByExtras(Query query, String[] keys, String[] values);
	int deleteTask(long... ids);
	int deleteTask(ITask... pITask);
	int deleteTask(ArrayList<ITask> pTasks);
	int reloadTask(UploadListener listtener, ITask... pITask);
	int reloadTask(UploadListener listtener, ArrayList<ITask> pTasks);
	int refreshData(ITask... pTasks);
	void registerListener(long taskId, @NonNull UploadListener listener);
}
