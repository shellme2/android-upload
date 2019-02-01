package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQueryBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.eebbk.bfc.uploadsdk.repo.persistence.UploadSdkDatabase;
import com.eebbk.bfc.uploadsdk.upload.ExtrasConverter;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Query {
    /**
     * Constant for use with {@link #orderBy}
     *
     * @hide
     */
    private static final int ORDER_ASCENDING = 1;

    /**
     * Constant for use with {@link #orderBy}
     *
     * @hide
     */
    private static final int ORDER_DESCENDING = 2;

    private long[] mIds = null;

    private Integer mStatusFlags = null;
    private String mOrderByColumn = Impl.COLUMN_LAST_MODIFICATION;
    private int mOrderDirection = ORDER_DESCENDING;

    private static final int MIN_CLIENT_ERROR_CODE = 400;
    private static final int MAX_SERVER_ERRPR_CODE = 600;

    // add by xym 2014.7.16
    private final HashMap<String, String> mExtras = new HashMap<>();

    public Query addFilterByExtras(String[] keys, String[] values) {
        if (keys == null || values == null || keys.length != values.length) {
            try {
                throw new IllegalArgumentException("set filter by extras and the argument is illegal!");
            } catch (Exception e) {
                e.printStackTrace();
                return this;
            }
        }

        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null) {
                continue;
            }
            mExtras.put(keys[i], values[i]);
        }

        return this;
    }

    public Query addFilterByExtras(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            try {
                throw new IllegalArgumentException("set filter by extras add the key is null!");
            } catch (Exception e) {
                e.printStackTrace();
                return this;
            }
        }

        if (key != null) {
            mExtras.put(key, value);
        }
        return this;
    }

    /**
     * Include only the uploads with the given IDs.
     *
     * @return this object
     */
    public Query setFilterById(long... ids) {
        mIds = ids;
        return this;
    }

    /**
     * Include only uploads with status matching any the given status flags.
     *
     * @param flags any combination of the STATUS_* bit flags
     * @return this object
     */
    public Query setFilterByStatus(int flags) {
        mStatusFlags = flags;
        return this;
    }

    /**
     * Change the sort order of the returned Cursor.
     *
     * @param direction either {@link #ORDER_ASCENDING} or {@link #ORDER_DESCENDING}
     * @return this object
     * @hide
     */
    public Query orderBy(String column, int direction) {
        if (direction != ORDER_ASCENDING && direction != ORDER_DESCENDING) {
            throw new IllegalArgumentException("Invalid direction: " + direction);
        }

        switch (column) {
            case Impl.COLUMN_LAST_MODIFICATION:
                mOrderByColumn = Impl.COLUMN_LAST_MODIFICATION;
                break;
            case Impl.COLUMN_FILE_SIZE:
                mOrderByColumn = Impl.COLUMN_FILE_SIZE;
                break;
            default:
                throw new IllegalArgumentException("Cannot order by " + column);
        }
        mOrderDirection = direction;

        return this;
    }

    /**
     * Run this query using the given ContentResolver.
     *
     * @param projection the projection to pass to ContentResolver.query()
     * @return the Cursor returned by ContentResolver.query()
     */
    public Cursor runQuery(ContentResolver resolver, String[] projection, Uri baseUri) {
        List<String> selectionParts = new ArrayList<>();
        String[] selectionArgs = null;

        if (mIds != null) {
            selectionParts.add(getWhereClauseForIds(mIds));
            selectionArgs = getWhereArgsForIds(mIds);
        }

        if (mExtras != null && !mExtras.isEmpty()) {
            selectionParts.add(getWhereClauseForExtras(mExtras));

            String[] args = null;
            int argsLen = 0;
            try {
                args = getWhereArgsForExtras(mExtras);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (args != null) {
                argsLen = args.length;
            }

            String[] wherArgs = new String[(selectionArgs == null ? 0 : selectionArgs.length) + argsLen];
            int i = 0;
            for (i = 0; selectionArgs != null && i < selectionArgs.length; i++) {

                wherArgs[i] = selectionArgs[i];
            }
            for (int j = 0; i < wherArgs.length && j < args.length; i++, j++) {

                wherArgs[i] = args[j];
            }
            selectionArgs = null;
            selectionArgs = wherArgs;
        }

        if (mStatusFlags != null) {
            List<String> parts = new ArrayList<>();
            if ((mStatusFlags & UploadManager.STATUS_PENDING) != 0) {
                parts.add(statusClause("=", Impl.STATUS_PENDING));
                parts.add(statusClause("=", Impl.STATUS_WAITING_TO_RETRY));
                parts.add(statusClause("=", Impl.STATUS_WAITING_FOR_NETWORK));
                parts.add(statusClause("=", Impl.STATUS_QUEUED_FOR_WIFI));
            }
            if ((mStatusFlags & UploadManager.STATUS_RUNNING) != 0) {
                parts.add(statusClause("=", Impl.STATUS_RUNNING));
            }
            if ((mStatusFlags & UploadManager.STATUS_PAUSED) != 0) {
                parts.add(statusClause("=", Impl.STATUS_PAUSED_BY_APP));
            }
            if ((mStatusFlags & UploadManager.STATUS_SUCCESSFUL) != 0) {
                parts.add(statusClause("=", Impl.STATUS_SUCCESS));
            }

            if ((mStatusFlags & UploadManager.STATUS_FAILED) != 0) {
                parts.add("(" + statusClause(">=", MIN_CLIENT_ERROR_CODE) + " AND " + statusClause("<", MAX_SERVER_ERRPR_CODE) + ")");
            }

            if (!parts.isEmpty()) {
                selectionParts.add("(" + joinStrings(" OR ", parts) + ")");
            }
        }

        // only return rows which are not marked 'deleted = 1'
        selectionParts.add(Impl.COLUMN_DELETED + " != '1'");

        String selection = joinStrings(" AND ", selectionParts);

        String orderDirection = mOrderDirection == ORDER_ASCENDING ? "ASC" : "DESC";
        String orderBy = mOrderByColumn + " " + orderDirection;

//        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder("")
//        SQLiteQuery query = new SQLiteQueryBuilder().qu
        return resolver.query(baseUri, projection, selection, selectionArgs, orderBy);
    }

    private String joinStrings(String joiner, Iterable<String> parts) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String part : parts) {
            if (!first) {
                builder.append(joiner);
            }
            builder.append(part);
            first = false;
        }

        return builder.toString();
    }

    private String statusClause(String operator, int value) {
        return Impl.COLUMN_STATUS + operator + "'" + value + "'";
    }

    public Cursor runQuery(Context context,String[] projection) {
        List<String> selectionParts = new ArrayList<>();
        String[] selectionArgs = null;

        if (mIds != null) {
            selectionParts.add(getWhereClauseForIds(mIds));
            selectionArgs = getWhereArgsForIds(mIds);
        }

        if (mExtras != null && !mExtras.isEmpty()) {
            selectionParts.add(getWhereClauseForExtras(mExtras));

            String[] args = null;
            int argsLen = 0;
            try {
                args = getWhereArgsForExtras(mExtras);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (args != null) {
                argsLen = args.length;
            }

            String[] wherArgs = new String[(selectionArgs == null ? 0 : selectionArgs.length) + argsLen];
            int i = 0;
            for (i = 0; selectionArgs != null && i < selectionArgs.length; i++) {

                wherArgs[i] = selectionArgs[i];
            }
            for (int j = 0; i < wherArgs.length && j < args.length; i++, j++) {

                wherArgs[i] = args[j];
            }
            selectionArgs = null;
            selectionArgs = wherArgs;
        }

        if (mStatusFlags != null) {
            List<String> parts = new ArrayList<>();
            if ((mStatusFlags & UploadManager.STATUS_PENDING) != 0) {
                parts.add(statusClause("=", Impl.STATUS_PENDING));
                parts.add(statusClause("=", Impl.STATUS_WAITING_TO_RETRY));
                parts.add(statusClause("=", Impl.STATUS_WAITING_FOR_NETWORK));
                parts.add(statusClause("=", Impl.STATUS_QUEUED_FOR_WIFI));
            }
            if ((mStatusFlags & UploadManager.STATUS_RUNNING) != 0) {
                parts.add(statusClause("=", Impl.STATUS_RUNNING));
            }
            if ((mStatusFlags & UploadManager.STATUS_PAUSED) != 0) {
                parts.add(statusClause("=", Impl.STATUS_PAUSED_BY_APP));
            }
            if ((mStatusFlags & UploadManager.STATUS_SUCCESSFUL) != 0) {
                parts.add(statusClause("=", Impl.STATUS_SUCCESS));
            }

            if ((mStatusFlags & UploadManager.STATUS_FAILED) != 0) {
                parts.add("(" + statusClause(">=", MIN_CLIENT_ERROR_CODE) + " AND " + statusClause("<", MAX_SERVER_ERRPR_CODE) + ")");
            }

            if (!parts.isEmpty()) {
                selectionParts.add("(" + joinStrings(" OR ", parts) + ")");
            }
        }

        // only return rows which are not marked 'deleted = 1'
        selectionParts.add(Impl.COLUMN_DELETED + " != '1'");

        String selection = joinStrings(" AND ", selectionParts);

        String orderDirection = mOrderDirection == ORDER_ASCENDING ? "ASC" : "DESC";
        String orderBy = mOrderByColumn + " " + orderDirection;

        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder("newbfcuploads")
                .selection(selection,selectionArgs)
                .columns(projection)
                .orderBy(orderBy)
                .create();
        return UploadSdkDatabase.getInstance(context).query(query);
    }

    private String getWhereClauseForExtras(HashMap<String, String> map){
        StringBuilder whereClause = new StringBuilder();

        Iterator iter = map.entrySet().iterator();
        while(iter.hasNext()){
            iter.next();
            whereClause.append(Impl.COLUMN_EXTRAS);
            whereClause.append(" LIKE ? ");

            if(iter.hasNext()){
                whereClause.append(" AND ");
            }
        }

        return whereClause.toString();
    }

    /**
     * Get a parameterized SQL WHERE clause to select a bunch of IDs.
     */
    public String getWhereClauseForIds(long[] ids){
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("(");
        for(int i = 0; i < ids.length; i++){
            if(i > 0){
                whereClause.append("OR ");
            }
            whereClause.append(Impl._ID);
            whereClause.append(" = ? ");
        }
        whereClause.append(")");

        return whereClause.toString();
    }

    /**
     * Get the selection args for a clause returned by
     * {@link #getWhereClauseForIds(long[])}.
     *
     * @throws UnsupportedEncodingException
     */
    private String[] getWhereArgsForExtras(HashMap<String, String> map)
            throws UnsupportedEncodingException{
        String[] whereArgs = new String[map.size()];

        Iterator iter = map.entrySet().iterator();
        int i = 0;
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            String extra = ExtrasConverter.encodeFormat((String) entry.getKey(), (String) entry.getValue());

            whereArgs[i] = "%" + extra + "%";
            i++;
        }

        return whereArgs;
    }

    /**
     * Get the selection args for a clause returned by
     * {@link #getWhereClauseForIds(long[])}.
     */
    public String[] getWhereArgsForIds(long[] ids){
        String[] whereArgs = new String[ids.length];
        for(int i = 0; i < ids.length; i++){
            whereArgs[i] = Long.toString(ids[i]);
        }

        return whereArgs;
    }
}
