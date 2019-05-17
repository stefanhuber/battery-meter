package at.stefanhuber.batterymeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Metering {

    protected Context context;
    protected SQLiteDatabase writeableDb;
    protected SQLiteDatabase readableDb;
    protected BatteryMeteringDatabaseHelper helper;
    protected int sessionId;


    public Metering(Context context) {
        this.context = context;
        this.sessionId = 0;
    }

    public int getCurrentSessionId() {
        return this.sessionId;
    }

    public SQLiteDatabase getDatabase(boolean writeable) {
        if (this.helper == null) {
            this.helper = new BatteryMeteringDatabaseHelper(this.context);
        }
        if (writeable) {
            if (this.writeableDb == null) {
                this.writeableDb = this.helper.getWritableDatabase();
            }
            return this.writeableDb;
        } else {
            if (this.readableDb == null) {
                this.readableDb = this.helper.getReadableDatabase();
            }
            return this.readableDb;
        }
    }

    public int startSession(String name) {
        this.stopSession();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("started", (new Date()).getTime());

        this.sessionId = (int) this.getDatabase(true).insert("metering_session", null, cv);
        return this.sessionId;
    }

    public void stopSession() {
        if (this.sessionId > 0) {
            ContentValues cv = new ContentValues();
            cv.put("stopped", (new Date()).getTime());
            this.getDatabase(true).update("metering_session", cv, "stopped < started", null);
            this.sessionId = 0;
        }
    }

    public void close() {
        if (this.helper != null) {
            this.helper.close();
        }
    }

    public boolean addMeteringEvent(
            int temperature,
            int voltage,
            int scale,
            int level,
            int capacity,
            int charge_counter,
            int current_average,
            int current_now,
            long energy_counter) {
        if (this.sessionId > 0) {
            ContentValues cv = new ContentValues();
            cv.put("session_id", this.sessionId);
            cv.put("timestamp", (new Date()).getTime());
            cv.put("temperature", temperature);
            cv.put("voltage", voltage);
            cv.put("scale", scale);
            cv.put("level", level);
            cv.put("capacity", capacity);
            cv.put("charge_counter", charge_counter);
            cv.put("current_average", current_average);
            cv.put("current_now", current_now);
            cv.put("energy_counter", energy_counter);

            this.getDatabase(true).insert("metering_event", null, cv);
            return true;
        }
        return false;
    }

    public MeteringSession getMeteringSession(int id) {
        Cursor c = getDatabase(false).rawQuery("SELECT * FROM metering_session WHERE id = ?", new String[] { String.valueOf(id) });
        if (c.moveToFirst()) {
            return getMeteringSession(c);
        } else {
            return null;
        }
    }

    public List<MeteringSession> getMeteringSessions() {
        List<MeteringSession> sessions = new ArrayList<>();
        Cursor c = getDatabase(false).rawQuery("SELECT id, started, stopped, name FROM metering_session ORDER BY started DESC", null);
        while(c.moveToNext()) {
            sessions.add(getMeteringSession(c));
        }

        return sessions;
    }

    public List<MeteringEvent> getMeteringEvents(int sessionId) {
        List<MeteringEvent> events = new ArrayList<>();

        String[] columns = new String[] {
            "id",
            "timestamp",
            "temperature",
            "voltage",
            "scale",
            "level"
        };

        String[] whereArgs = new String[] { String.valueOf(sessionId) };

        Cursor c = getDatabase(false).query("metering_event", columns, "session_id = ?", whereArgs, null, null, "id");

        while(c.moveToNext()) {
            events.add(getMeteringEvent(c));
        }

        return events;
    }

    public void removeMeteringSession(int sessionId) {
        getDatabase(true).delete("metering_session", "id = ?", new String[] {String.valueOf(sessionId)});
        getDatabase(true).delete("metering_event", "session_id = ?", new String[] {String.valueOf(sessionId)});
    }

    public MeteringEvent getMeteringEvent(Cursor c) {
        return new MeteringEvent()
            .setId(c.getInt(c.getColumnIndex("id")))
            .setTimestamp(c.getLong(c.getColumnIndex("timestamp")))
            .setTemperature(c.getInt(c.getColumnIndex("temperature")))
            .setVoltage(c.getInt(c.getColumnIndex("voltage")))
            .setScale(c.getInt(c.getColumnIndex("scale")))
            .setLevel(c.getInt(c.getColumnIndex("level")));
    }

    public MeteringSession getMeteringSession(Cursor c) {
        return new MeteringSession()
            .setId(c.getInt(c.getColumnIndex("id")))
            .setStartedFromTimestamp(c.getLong(c.getColumnIndex("started")))
            .setStoppedFromTimestamp(c.getLong(c.getColumnIndex("stopped")))
            .setName(c.getString(c.getColumnIndex("name")));
    }

}
