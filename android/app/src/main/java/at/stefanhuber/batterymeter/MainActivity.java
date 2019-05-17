package at.stefanhuber.batterymeter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ShareCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
    public static final String CHANNEL = "METERING";

    protected Metering metering;
    protected CsvExport csvExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

        metering = new Metering(this);
        csvExport = new CsvExport(this);

        new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
            new MethodChannel.MethodCallHandler() {
                @Override
                public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                    if (methodCall.method.equals("startMeteringSession")) {
                        String name = methodCall.argument("name");
                        int interval = methodCall.argument("interval");

                        Intent intent = new Intent(MainActivity.this, BatteryForegroundService.class);
                        intent.putExtra("name", name);
                        intent.putExtra("interval", interval * 1000);
                        MainActivity.this.startService(intent);

                        result.success(true);
                    } else if (methodCall.method.equals("getMeteringSessions")) {
                        List<Map<String, Object>> sessions = new ArrayList<>();

                        for (MeteringSession session : metering.getMeteringSessions()) {
                            sessions.add(session.asMap());
                        }

                        result.success(sessions);
                    } else if (methodCall.method.equals("shareCsv")) {
                        if (exportCsv(methodCall)) {
                            result.success(true);
                        } else {
                            result.success(false);
                        }
                    } else if (methodCall.method.equals("removeMeteringSession")) {
                        int sessionId = methodCall.argument("session");
                        metering.removeMeteringSession(sessionId);
                        result.success(true);
                    } else {
                        result.notImplemented();
                    }
                }
            }
        );
    }

    protected boolean exportCsv(MethodCall methodCall) {
        try {
            int sessionId = methodCall.argument("session");
            MeteringSession meteringSession = metering.getMeteringSession(sessionId);
            List<MeteringEvent> data = metering.getMeteringEvents(sessionId);
            Log.i("BATTERY_METER", "Try to export " + data.size() + " event entries");

            if (csvExport.export(meteringSession.getName(), data)) {
                Intent intent = ShareCompat.IntentBuilder
                    .from(this)
                    .setStream(csvExport.getUriForFile(meteringSession.getName()))
                    .setType("text/csv")
                    .getIntent()
                    .setAction(Intent.ACTION_SEND);

                // check if there is an activity to handle the intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e("BATTERY_METER", e.getMessage());
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        this.metering.close();
        super.onDestroy();
    }
}
