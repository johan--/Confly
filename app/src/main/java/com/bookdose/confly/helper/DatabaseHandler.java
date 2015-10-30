package com.bookdose.confly.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bookdose.confly.object.Bookmark;
import com.bookdose.confly.object.Issue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Teebio on 9/10/15 AD.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_PATH = "";
    private SQLiteDatabase myDataBase;

    // Database Name
    private static final String DATABASE_NAME = "bd.sqlite";

    // Contacts table name
    private static final String TABLE_ISSUE = "issue";

    // Contacts Table Columns names
    private static final String CONTENT_ID = "content_aid";
    private static final String CONTENT_NAME = "content_name";
    private static final String CONTENT_TYPE = "content_type";
    private static final String CATEGORY_ID = "category_aid";
    private static final String CATEGORY_NAME = "category_name";
    private static final String AUTHOR = "author";
    private static final String PUBLISH_MONTH = "publish_month";
    private static final String ISSUE_ID = "issue_aid";
    private static final String VOL = "vol";
    private static final String ISSUE = "issue";
    private static final String ISSUE_ELSE = "issue_else";
    private static final String DESCRIPTION = "description";
    private static final String PUBLISH_DATE = "publish_date";
    private static final String IS_GENERATE_CONTENT = "is_generate_content";
    private static final String HAVE_FILE = "have_file";
    private static final String PATH = "path";
    private static final String FULL_PATH = "full_path";
    private static final String COVER_IMAGE = "cover_image";
    private static final String STATUS = "status";

    public static Context context;

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        //DATABASE_PATH  =  "/data/data/"+context.getPackageName()+"/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //this.getReadableDatabase();
        try {
            createDatabase();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ISSUE);
        //onCreate(db);

        if (newVersion > oldVersion) {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }
    }

    // Create a empty database on the system
    public void createDatabase() throws IOException {

        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getReadableDatabase();
            try {
                this.close();
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }
        else{
            Log.v("DB Exists", "db exists");
        }

    }

    // Check database already exist or not
    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
//			String myPath = DATABASE_PATH + DATABASE_NAME;
//			File dbfile = new File(myPath);
//			checkDB = dbfile.exists();
            File database=context.getDatabasePath(DATABASE_NAME);
            checkDB = database.exists();

            if (!database.exists()) {
                // Database does not exist so copy it from assets here
                Log.i("Database", "Not Found");
            } else {
                Log.i("Database", "Found");
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return checkDB;
    }

    // Copies your database from your local assets-folder to the just created
    // empty database in the system folder
    private void copyDataBase() throws IOException {

        String outFileName = DATABASE_PATH + DATABASE_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput =context.getAssets().open(DATABASE_NAME);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    // delete database
    public void db_delete() {
        File file = new File(DATABASE_PATH + DATABASE_NAME);
        if (file.exists()) {
            file.delete();
            System.out.println("delete database file.");
        }
    }

    // Open database
    public void openDatabase() throws SQLException {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase() throws SQLException {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public long addIssue(Issue issue) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTENT_ID, issue.content_aid);
        values.put(CONTENT_NAME, issue.content_name);
        values.put(CONTENT_TYPE, issue.content_type);
        values.put(CATEGORY_ID, issue.category_aid);
        values.put(CATEGORY_NAME, issue.category_name);
        values.put(AUTHOR, issue.author);
        values.put(PUBLISH_MONTH, issue.publish_month);
        values.put(ISSUE_ID, issue.issue_aid);
        values.put(VOL, issue.vol);
        values.put(ISSUE, issue.issue);
        values.put(ISSUE_ELSE, issue.issue_else);
        values.put(DESCRIPTION, issue.description);
        values.put(PUBLISH_DATE, issue.publish_date);
        values.put(IS_GENERATE_CONTENT, issue.is_generate_content);
        values.put(HAVE_FILE, issue.have_file);
        values.put(PATH, issue.path);
        values.put(FULL_PATH, issue.full_path);
        values.put(COVER_IMAGE, issue.cover_image);
        values.put(STATUS, issue.status);

        // Inserting Row
        long ret = db.insert(TABLE_ISSUE, null, values);
        db.close(); // Closing database connection
        return ret;
    }

    // Getting single contact
    public Issue getIssue(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ISSUE, new String[]{CONTENT_ID, CONTENT_NAME, CONTENT_TYPE, CATEGORY_ID, CATEGORY_NAME,
                        AUTHOR, PUBLISH_MONTH, ISSUE_ID, VOL, ISSUE, ISSUE_ELSE, DESCRIPTION, PUBLISH_DATE, IS_GENERATE_CONTENT,
                        HAVE_FILE, PATH, FULL_PATH, COVER_IMAGE, STATUS}, CONTENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
//        Cursor cursor = db.query(TABLE_ISSUE, new String[] { CONTENT_ID, CONTENT_NAME }, CONTENT_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor==null || cursor.getCount() < 1)
            return null;
        if (cursor != null)
            cursor.moveToFirst();

        Issue issue = new Issue();
        issue.content_aid = cursor.getString(cursor.getColumnIndex(CONTENT_ID));
        issue.content_name = cursor.getString(cursor.getColumnIndex(CONTENT_NAME));
        issue.content_type = cursor.getString(cursor.getColumnIndex(CONTENT_TYPE));
        issue.category_aid = cursor.getString(cursor.getColumnIndex(CATEGORY_ID));
        issue.category_name = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
        issue.author = cursor.getString(cursor.getColumnIndex(AUTHOR));
        issue.publish_month = cursor.getString(cursor.getColumnIndex(PUBLISH_MONTH));
        issue.issue_aid = cursor.getString(cursor.getColumnIndex(ISSUE_ID));
        issue.vol = cursor.getString(cursor.getColumnIndex(VOL));
        issue.issue = cursor.getString(cursor.getColumnIndex(ISSUE));
        issue.issue_else = cursor.getString(cursor.getColumnIndex(ISSUE_ELSE));
        issue.description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
        issue.publish_date = cursor.getString(cursor.getColumnIndex(PUBLISH_DATE));
        issue.is_generate_content = cursor.getString(cursor.getColumnIndex(IS_GENERATE_CONTENT));
        issue.have_file = cursor.getString(cursor.getColumnIndex(HAVE_FILE));
        issue.path = cursor.getString(cursor.getColumnIndex(PATH));
        issue.full_path = cursor.getString(cursor.getColumnIndex(FULL_PATH));
        issue.cover_image = cursor.getString(cursor.getColumnIndex(COVER_IMAGE));
        issue.status = cursor.getString(cursor.getColumnIndex(STATUS));

        close();
        return issue;
    }

    // Getting All Contacts
    public ArrayList<Issue> getAllIssue() {
        ArrayList<Issue> issues = new ArrayList<Issue>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ISSUE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Issue issue = new Issue();
                issue.content_aid = cursor.getString(cursor.getColumnIndex(CONTENT_ID));
                issue.content_name = cursor.getString(cursor.getColumnIndex(CONTENT_NAME));
                issue.content_type = cursor.getString(cursor.getColumnIndex(CONTENT_TYPE));
                issue.category_aid = cursor.getString(cursor.getColumnIndex(CATEGORY_ID));
                issue.category_name = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
                issue.author = cursor.getString(cursor.getColumnIndex(AUTHOR));
                issue.publish_month = cursor.getString(cursor.getColumnIndex(PUBLISH_MONTH));
                issue.issue_aid = cursor.getString(cursor.getColumnIndex(ISSUE_ID));
                issue.vol = cursor.getString(cursor.getColumnIndex(VOL));
                issue.issue = cursor.getString(cursor.getColumnIndex(ISSUE));
                issue.issue_else = cursor.getString(cursor.getColumnIndex(ISSUE_ELSE));
                issue.description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
                issue.publish_date = cursor.getString(cursor.getColumnIndex(PUBLISH_DATE));
                issue.is_generate_content = cursor.getString(cursor.getColumnIndex(IS_GENERATE_CONTENT));
                issue.have_file = cursor.getString(cursor.getColumnIndex(HAVE_FILE));
                issue.path = cursor.getString(cursor.getColumnIndex(PATH));
                issue.full_path = cursor.getString(cursor.getColumnIndex(FULL_PATH));
                issue.cover_image = cursor.getString(cursor.getColumnIndex(COVER_IMAGE));
                issue.status = cursor.getString(cursor.getColumnIndex(STATUS));
                // Adding contact to list
                issues.add(issue);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return issues;
    }

    // Getting All Contacts
    public ArrayList<Issue> getIssueFromCategory(String catID) {
        ArrayList<Issue> issues = new ArrayList<Issue>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ISSUE+" WHERE "+CATEGORY_ID+"="+catID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Issue issue = new Issue();
                issue.content_aid = cursor.getString(cursor.getColumnIndex(CONTENT_ID));
                issue.content_name = cursor.getString(cursor.getColumnIndex(CONTENT_NAME));
                issue.content_type = cursor.getString(cursor.getColumnIndex(CONTENT_TYPE));
                issue.category_aid = cursor.getString(cursor.getColumnIndex(CATEGORY_ID));
                issue.category_name = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
                issue.author = cursor.getString(cursor.getColumnIndex(AUTHOR));
                issue.publish_month = cursor.getString(cursor.getColumnIndex(PUBLISH_MONTH));
                issue.issue_aid = cursor.getString(cursor.getColumnIndex(ISSUE_ID));
                issue.vol = cursor.getString(cursor.getColumnIndex(VOL));
                issue.issue = cursor.getString(cursor.getColumnIndex(ISSUE));
                issue.issue_else = cursor.getString(cursor.getColumnIndex(ISSUE_ELSE));
                issue.description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
                issue.publish_date = cursor.getString(cursor.getColumnIndex(PUBLISH_DATE));
                issue.is_generate_content = cursor.getString(cursor.getColumnIndex(IS_GENERATE_CONTENT));
                issue.have_file = cursor.getString(cursor.getColumnIndex(HAVE_FILE));
                issue.path = cursor.getString(cursor.getColumnIndex(PATH));
                issue.full_path = cursor.getString(cursor.getColumnIndex(FULL_PATH));
                issue.cover_image = cursor.getString(cursor.getColumnIndex(COVER_IMAGE));
                issue.status = cursor.getString(cursor.getColumnIndex(STATUS));
                // Adding contact to list
                issues.add(issue);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return issues;
    }

    public String getIssueStatus(String path){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT "+STATUS+" FROM " + TABLE_ISSUE+" WHERE "+PATH+"="+path;
        Cursor cursor = db.rawQuery(selectQuery, null);
        String status = null;
        if (cursor.moveToFirst()) {
            do {
                status = cursor.getString(cursor.getColumnIndex(STATUS));
            }while (cursor.moveToNext());
        }
        return status;
    }

    public int updateIssueStatus(String status, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS, status);
        return db.update(TABLE_ISSUE, values, CONTENT_ID+" = ?", new String[]{String.valueOf(id)});
    }

    public void deleteIssue(Issue issue) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ISSUE, CONTENT_ID + " = ?",
                new String[] { String.valueOf(issue.content_aid) });
        db.close();
    }

    // Bookmark

    public long insertBookmark(String contentID,int page, String contentName,String imageName){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("page",page);
        values.put("content_id",contentID);
        values.put("content_name",contentName);
        values.put("image_name",imageName);
        // Inserting Row
        long ret = db.insert("bookmark", null, values);
        db.close(); // Closing database connection
        return ret;
    }

    public ArrayList<Bookmark> getBookmark(String contentId){
        ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
        // Select All Query
        String selectQuery = "SELECT  * FROM bookmark WHERE content_id=" + contentId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Bookmark bookmark = new Bookmark();
                bookmark.page = cursor.getInt(cursor.getColumnIndex("page"));
                bookmark.contentId = cursor.getString(cursor.getColumnIndex("content_id"));
                bookmark.contentName = cursor.getString(cursor.getColumnIndex("content_name"));
                bookmark.imageName = cursor.getString(cursor.getColumnIndex("image_name"));
                //bookmark.page = cursor.getInt(cursor.getColumnIndex(CONTENT_ID));
                bookmarks.add(bookmark);
            } while (cursor.moveToNext());
        }
        db.close();
        return bookmarks;
    }

    public Bookmark getBookmark(String contentId, int page){
        String selectQuery = "SELECT  * FROM bookmark WHERE content_id=" + contentId+" AND page="+page;
        Bookmark bookmark = new Bookmark();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                bookmark.page = cursor.getInt(cursor.getColumnIndex("page"));
                bookmark.contentId = cursor.getString(cursor.getColumnIndex("content_id"));
                bookmark.contentName = cursor.getString(cursor.getColumnIndex("content_name"));
                bookmark.imageName = cursor.getString(cursor.getColumnIndex("image_name"));
                //bookmark.page = cursor.getInt(cursor.getColumnIndex(CONTENT_ID));

            } while (cursor.moveToNext());
        }
        db.close();
        return bookmark;
    }

    public void deleteBookmark(Bookmark bookmark) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("bookmark", "content_id" + " = ? AND page="+bookmark.page,
                new String[] { String.valueOf(bookmark.contentId) });
        db.close();
    }
}
