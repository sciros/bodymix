package net.sciros.bodymix.io;

import net.sciros.bodymix.domain.InternalConstants;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabase extends SQLiteOpenHelper {
    private static final String DEBUG_TAG = "AppDatabase";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = InternalConstants.DB_NAME;
    
    public AppDatabase (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    @Override
    public void onCreate (SQLiteDatabase db) {
        
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
