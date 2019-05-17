package at.stefanhuber.batterymeter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MeteringEvent {
    protected int id;
    protected Date timestamp;
    protected int temperature;
    protected int voltage;
    protected int scale;
    protected int level;
    protected int capacity;
    protected int charge_counter;
    protected int current_average;
    protected int current_now;
    protected long energy_counter;

    public int getId() {
        return id;
    }

    public MeteringEvent setId(int id) {
        this.id = id;
        return this;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getTemperature() {
        return temperature;
    }

    public MeteringEvent setTemperature(int temperature) {
        this.temperature = temperature;
        return this;
    }

    public int getVoltage() {
        return voltage;
    }

    public MeteringEvent setVoltage(int voltage) {
        this.voltage = voltage;
        return this;
    }

    public int getScale() {
        return scale;
    }

    public MeteringEvent setScale(int scale) {
        this.scale = scale;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public MeteringEvent setLevel(int level) {
        this.level = level;
        return this;
    }

    public MeteringEvent setTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        this.timestamp = cal.getTime();
        return this;
    }

    public int getCapacity() {
        return capacity;
    }

    public MeteringEvent setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public int getChargeCounter() {
        return charge_counter;
    }

    public MeteringEvent setChargeCounter(int charge_counter) {
        this.charge_counter = charge_counter;
        return this;
    }

    public int getCurrentNow() {
        return current_now;
    }

    public MeteringEvent setCurrentNow(int current_now) {
        this.current_now = current_now;
        return this;
    }

    public long getEnergyCounter() {
        return energy_counter;
    }

    public MeteringEvent setEnergyCounter(long energy_counter) {
        this.energy_counter = energy_counter;
        return this;
    }

    public int getCurrentAverage() {
        return current_average;
    }

    public MeteringEvent setCurrentAverage(int current_average) {
        this.current_average = current_average;
        return this;
    }

    public String[] getData() {
        ArrayList<String> data = new ArrayList<>();

        data.add(String.valueOf(id));
        data.add(String.valueOf(timestamp.getTime()));
        data.add(String.valueOf(temperature));
        data.add(String.valueOf(voltage));
        data.add(String.valueOf(scale));
        data.add(String.valueOf(level));
        data.add(String.valueOf(capacity));
        data.add(String.valueOf(charge_counter));
        data.add(String.valueOf(current_average));
        data.add(String.valueOf(current_now));
        data.add(String.valueOf(energy_counter));

        return data.toArray(new String[data.size()]);
    }

    public static String[] getHeader() {
        return new String[] {
            "id",
            "timestamp",
            "temperature",
            "voltage",
            "scale",
            "level",
            "capacity",
            "charge_counter",
            "current_average",
            "current_now",
            "energy_counter"
        };
    }

}
