package at.stefanhuber.batterymeter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BatteryMeteringDatabaseHelper extends SQLiteOpenHelper {

    public BatteryMeteringDatabaseHelper(Context context) {
        super(context, "battery_metering_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String meteringSession = "CREATE TABLE metering_session (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "started DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "stopped DATETIME DEFAULT 0, " +
                "name TEXT" +
                ")";

        String meteringEvent = "CREATE TABLE metering_event (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "session_id INTEGER, " +
                "temperature INTEGER, " +
                "voltage INTEGER, " +
                "scale INTEGER, " +
                "level INTEGER," +
                "capacity INTEGER," +
                "charge_counter INTEGER," +
                "current_average INTEGER," +
                "current_now INTEGER," +
                "energy_counter INTEGER" +
                ")";

        db.execSQL(meteringSession);
        db.execSQL(meteringEvent);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { /*
        db.execSQL("DROP TABLE metering_session");
        db.execSQL("DROP TABLE metering_event");

        String meteringSession = "CREATE TABLE metering_session (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "started TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "stopped TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "name TEXT" +
                ")";

        String meteringEvent = "CREATE TABLE metering_event (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "session_id INTEGER, " +
                "temperature INTEGER, " +
                "voltage INTEGER, " +
                "scale INTEGER, " +
                "level INTEGER" +
                ")";

        db.execSQL(meteringSession);
        db.execSQL(meteringEvent); */
    }
}
