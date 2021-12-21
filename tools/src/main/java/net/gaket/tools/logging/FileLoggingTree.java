package net.gaket.tools.logging;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;


/**
 * Tree that is responsible for putting logs into a file
 */
public class FileLoggingTree extends Timber.DebugTree {

  private static final String LOG_TAG = FileLoggingTree.class.getSimpleName();

  private Context context;

  public FileLoggingTree(Context context) {
    this.context = context;
  }

  @Override
  protected void log(int priority, String tag, String message, Throwable t) {
    try {
      String path = "Log";
      String fileNameTimeStamp = new SimpleDateFormat("dd-MM-yyyy",
        Locale.getDefault()).format(new Date());
      String logTimeStamp = new SimpleDateFormat("E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
        Locale.getDefault()).format(new Date());
      String fileName = fileNameTimeStamp + ".html";

      // Create file
      File file = generateFile(path, fileName);

      // If file created or exists save logs
      if (file != null) {
        FileWriter writer = new FileWriter(file, true);
        writer.append("<p style=\"background:lightgray;\"><strong "
          + "style=\"background:lightblue;\">&nbsp&nbsp")
          .append(logTimeStamp)
          .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
          .append(tag)
          .append("</strong> - ")
          .append(message)
          .append("</p>");
        writer.flush();
        writer.close();
      }
    } catch (Exception e) {
      Log.e(LOG_TAG, "Error while logging into file : " + e);
    }
  }

  @Override
  protected String createStackElementTag(StackTraceElement element) {
    // Add log statements line number to the log
    return super.createStackElementTag(element) + " - " + element.getLineNumber();
  }

  /*  Helper method to create file*/
  @Nullable
  private File generateFile(@NonNull String path, @NonNull String fileName) {
    File file = null;
    if (isExternalStorageAvailable()) {
      File dir = new File(context.getFilesDir(), "logs");
      if (!dir.exists()) {
        dir.mkdirs();
      }
      file = new File(dir, fileName);
    }
    return file;
  }

  /* Helper method to determine if external storage is available*/
  private static boolean isExternalStorageAvailable() {
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
  }
}
