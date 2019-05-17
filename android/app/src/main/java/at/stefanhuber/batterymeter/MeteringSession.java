package at.stefanhuber.batterymeter;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MeteringSession {

    protected int id;
    protected String name;
    protected Date started;
    protected Date stopped;

    public int getId() {
        return id;
    }

    public MeteringSession setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MeteringSession setName(String name) {
        this.name = name;
        return this;
    }

    public Date getStarted() {
        return started;
    }

    public Date getStopped() { return stopped; }

    public Long getStartedTimestamp() { return started.getTime(); }

    public Long getStoppedTimestamp() { return stopped.getTime(); }

    public MeteringSession setStarted(Date started) {
        this.started = started;
        return this;
    }

    public MeteringSession setStopped(Date stopped) {
        this.stopped = stopped;
        return this;
    }

    public MeteringSession setStartedFromTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        this.setStarted(cal.getTime());
        return this;
    }

    public MeteringSession setStoppedFromTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        this.setStopped(cal.getTime());
        return this;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> data = new HashMap<>();

        data.put("id", this.id);
        data.put("name", this.name);
        data.put("started", this.getStartedTimestamp());
        data.put("stopped", this.getStoppedTimestamp());

        return data;
    }
}
