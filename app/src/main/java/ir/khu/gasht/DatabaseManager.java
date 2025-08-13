package ir.khu.gasht;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ir.khu.gasht.models.Location;
import ir.khu.gasht.models.Task;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATA_BASE_NAME = "gasht_data.db";

    private static final int VERSION = 1;

    private static final String TASKS_TABLE_NAME = "tasks";
    private static final String LOCATIONS_TABLE_NAME = "locations";

    private static final String ID = "id";
    private static final String TITLE = "title";

    private static final String DESCRIPTION = "description";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private static final String IS_CHECKED = "isChecked";
    private static final String ALERT_DATE = "alertDate";
    private static final String TYPE = "type";


    public DatabaseManager(@Nullable Context context) {
        super(context, DATA_BASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TASKS_TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " TEXT, "
                + IS_CHECKED + " INTEGER, "
                + ALERT_DATE + " INTEGER, "
                + TYPE + " TEXT"
                + ");");

        sqLiteDatabase.execSQL("CREATE TABLE " + LOCATIONS_TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " TEXT, "
                + LATITUDE + " REAL, "
                + LONGITUDE + " REAL "
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_TABLE_NAME);
        onCreate(db);
    }

    public int insertTask(Task item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, item.getTitle());
        values.put(IS_CHECKED, item.getIsChecked());
        values.put(ALERT_DATE, item.getAlertDate());
        values.put(TYPE, item.getType());

        long insertedId = db.insert(TASKS_TABLE_NAME, null, values);
        db.close();

        item.setId((int) insertedId);

        return (int) insertedId;
    }

    public long insertLocation(Location item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TITLE, item.getTitle());
        values.put(LATITUDE, item.getLatitude());
        values.put(LONGITUDE, item.getLongitude());

        long result = db.insert("locations", null, values);
        db.close();

        return result;
    }

    public Task getTask(String id) {
        Task item = new Task();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TASKS_TABLE_NAME + " WHERE " + ID + " = ?", new String[]{id});
            if (cursor.moveToFirst()) {
                item.setId(cursor.getInt(0));
                item.setTitle(cursor.getString(1));
                item.setIsChecked(cursor.getInt(2));
                item.setAlertDate(cursor.getLong(3));
                item.setType(cursor.getString(4));
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return item;
    }

    public Location getLocation(String id) {
        Location item = new Location();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + LOCATIONS_TABLE_NAME + " WHERE " + ID + " = ?", new String[]{id});
            if (cursor.moveToFirst()) {
                item.setId(cursor.getInt(0));
                item.setTitle(cursor.getString(1));
                item.setLatitude(cursor.getDouble(2));
                item.setLongitude(cursor.getDouble(3));
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return item;
    }

    public void updateTask(Task item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, item.getTitle());
        values.put(IS_CHECKED, item.getIsChecked());
        values.put(ALERT_DATE, item.getAlertDate());
        values.put(TYPE, item.getType());

        db.update(TASKS_TABLE_NAME, values, ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
    }

    public void updateLocation(Location item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, item.getTitle());
        values.put(LATITUDE, item.getLatitude());
        values.put(LONGITUDE, item.getLongitude());

        db.update(LOCATIONS_TABLE_NAME, values, ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
    }

    public boolean deleteTask(Task item) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TASKS_TABLE_NAME, ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
        return result > 0;
    }

    public boolean deleteTaskById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TASKS_TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean deleteLocation(Location item) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(LOCATIONS_TABLE_NAME, ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
        return result > 0;
    }

    public List<Task> getAllTasks() {
        List<Task> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TASKS_TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                do {
                    Task item = new Task();
                    item.setId(cursor.getInt(0));
                    item.setTitle(cursor.getString(1));
                    item.setIsChecked(cursor.getInt(2));
                    item.setAlertDate(cursor.getLong(3));
                    item.setType(cursor.getString(4));
                    itemList.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return itemList;
    }

    public List<Location> getAllLocations() {
        List<Location> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + LOCATIONS_TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                do {
                    Location location = new Location();
                    location.setId(cursor.getInt(0));
                    location.setTitle(cursor.getString(1));
                    location.setLatitude(cursor.getDouble(2));
                    location.setLongitude(cursor.getDouble(3));
                    itemList.add(location);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return itemList;
    }

    public List<Task> searchTasks(String type) {
        List<Task> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT * FROM " + TASKS_TABLE_NAME + " WHERE " + TYPE + " = ?",
                    new String[]{type}
            );

            if (cursor.moveToFirst()) {
                do {
                    Task task = new Task();
                    task.setId(cursor.getInt(0));
                    task.setTitle(cursor.getString(1));
                    task.setIsChecked(cursor.getInt(2));
                    task.setAlertDate(cursor.getLong(3));
                    task.setType(cursor.getString(4));
                    itemList.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return itemList;
    }

    public List<Task> searchTasksByDate(long startOfDay, long endOfDay) {
        List<Task> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] columns = {ID, TITLE, ALERT_DATE, IS_CHECKED, TYPE};
            String selection = "alertDate BETWEEN ? AND ?";
            String[] selectionArgs = {String.valueOf(startOfDay), String.valueOf(endOfDay)};

            cursor = db.query(TASKS_TABLE_NAME, columns, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    Task item = new Task();
                    item.setId(cursor.getInt(0));
                    item.setTitle(cursor.getString(1));
                    item.setAlertDate(cursor.getLong(2));
                    item.setIsChecked(cursor.getInt(3));
                    item.setType(cursor.getString(4));
                    itemList.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return itemList;
    }

    public int countTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TASKS_TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return count;
    }

    public int countLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + LOCATIONS_TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return count;
    }
}