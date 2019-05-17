package at.stefanhuber.batterymeter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class CsvExport {

    protected Context context;

    CsvExport(Context context) {
        this.context = context;
    }

    public String convertToCSV(String[] data) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            if (i > 0) {
                builder.append(";");
            }
            builder.append(escapeSpecialCharacters(data[i]));
        }

        return builder.toString();
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public String escapeFilename(String name) {
        return name.replaceAll("[^A-Za-z0-9_]", "-");
    }

    public Uri getUriForFile(String name) {
        return FileProvider.getUriForFile(context,
                "at.stefanhuber.batterymeter.fileprovider",
                new File(context.getFilesDir(), "csv/" + escapeFilename(name) + ".csv"));
    }

    public boolean export(String name, List<MeteringEvent> session) {
        try {
            File dir = new File(context.getFilesDir(), "csv");
            dir.mkdirs();

            File out = new File(context.getFilesDir(), "csv/" + escapeFilename(name) + ".csv");

            PrintWriter pw = new PrintWriter(out);
            pw.println(convertToCSV(MeteringEvent.getHeader()));
            for (MeteringEvent event : session) {
                pw.println(convertToCSV(event.getData()));
            }
            pw.close();

            assert out.exists();
            return true;
        } catch (Exception e) {
            Log.e("BATTERY_METER", e.getMessage());
        }

        return false;
    }

}
