package com.andromou.ytv.player.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andromou.ytv.player.data.SavedLink;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SavedLinksSQLite extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "Saved_Links";
    public static final String DB_NAME = "SavedLinks.db";
    public static final int DB_VERSION = 1;
    private final SQLiteDatabase linksDataBase;

    public SavedLinksSQLite(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        linksDataBase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table with appropriate columns
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (title TEXT, link TEXT, newlink TEXT, time TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema changes if needed
    }

    public void addLinkToTable(SavedLink savedLink) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", savedLink.title);
        contentValues.put("link", savedLink.link);
        contentValues.put("newlink", savedLink.newlink);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        contentValues.put("time", dateFormat.format(new Date()));

        // Check if the link already exists in the database
        Cursor cursor = linksDataBase.query(TABLE_NAME, null, "link = ?", new String[]{savedLink.link}, null, null, null);
        if (cursor.moveToFirst()) {
            // Update existing entry
            linksDataBase.update(TABLE_NAME, contentValues, "link = ?", new String[]{savedLink.link});
        } else {
            // Insert new entry
            linksDataBase.insert(TABLE_NAME, null, contentValues);
        }
        cursor.close();
    }

    public void updateLink(SavedLink savedLink) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", savedLink.title);
        contentValues.put("newlink", savedLink.newlink); // Update newlink field
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        contentValues.put("time", dateFormat.format(new Date()));

        // Update the entry based on the link (assuming link is unique identifier)
        linksDataBase.update(TABLE_NAME, contentValues, "link = ?", new String[]{savedLink.link});
    }

    public void deleteLink(String link) {
        linksDataBase.delete(TABLE_NAME, "link = ?", new String[]{link});
    }

    public void clearTable() {
        linksDataBase.execSQL("DELETE FROM " + TABLE_NAME);
    }

    @SuppressLint("Range")
    public List<SavedLink> getAllSavedLinks() {
        List<SavedLink> savedLinks = new ArrayList<>();
        Cursor cursor = linksDataBase.query(TABLE_NAME, new String[]{"title", "link", "newlink"}, null, null, null, null, "time DESC");
        while (cursor.moveToNext()) {
            SavedLink savedLink = new SavedLink();
            savedLink.title = cursor.getString(cursor.getColumnIndex("title"));
            savedLink.link = cursor.getString(cursor.getColumnIndex("link"));
            savedLink.newlink = cursor.getString(cursor.getColumnIndex("newlink"));
            savedLinks.add(savedLink);
        }
        cursor.close();
        return savedLinks;
    }

    @SuppressLint("Range")
    public List<SavedLink> getSavedLinksByKeyword(String keyword) {
        List<SavedLink> savedLinks = new ArrayList<>();
        Cursor cursor = linksDataBase.query(TABLE_NAME, new String[]{"title", "link", "newlink"}, "title LIKE ?", new String[]{"%" + keyword + "%"}, null, null, "time DESC");
        while (cursor.moveToNext()) {
            SavedLink savedLink = new SavedLink();
            savedLink.title = cursor.getString(cursor.getColumnIndex("title"));
            savedLink.link = cursor.getString(cursor.getColumnIndex("link"));
            savedLink.newlink = cursor.getString(cursor.getColumnIndex("newlink"));
            savedLinks.add(savedLink);
        }
        cursor.close();
        return savedLinks;
    }
}

