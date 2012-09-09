package pt.up.beta.mobile.sendtosamba;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jibble.simpleftp.SimpleFTP;
import org.jibble.simpleftp.SimpleFTP.WrongCredentials;

import pt.up.beta.mobile.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

public class UploaderTask extends AsyncTask<String, Integer, Boolean> {

    private final InputStreamManaged is;
    private final String filename;
    private Notification notification;
    private final Context context;
    private final FinishedTaskListener listener;

    private int error = 0;
    private final static int WRONG_CREDENTIAL = 1;
    private final static int WRONG_HOST = 2;
    private final static int GENERAL_ERROR = 3;

    private NotificationManager mNotificationManager;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private final int UNIQUE_ID;

    private final String folder;

    private final static String SERVER = "tom.fe.up.pt";

    public UploaderTask(final FinishedTaskListener lis, final Context con,
            final InputStreamManaged is, final String filename) {
        this.is = is;
        this.filename = filename;
        this.context = con;
        this.listener = lis;
        folder = "FEUPMobile";
        UNIQUE_ID = (int) (R.string.app_name + System.currentTimeMillis());
        mNotificationManager = (NotificationManager) con
                .getSystemService(Context.NOTIFICATION_SERVICE);

    }

	protected void onPreExecute() {
        RemoteViews contentView = new RemoteViews(context.getPackageName(),
                R.layout.notification_upload);
        if (is.getLength() == 0)
            contentView.setProgressBar(R.id.progressBar, 0, 0, true);
        else
            contentView.setProgressBar(R.id.progressBar, 100, 0, false);
        contentView.setTextViewText(R.id.text, context.getString(
                R.string.notification_uploader_content, filename));
        final PendingIntent contentIntent = PendingIntent.getActivity(
                context.getApplicationContext(), 0, new Intent(), // add this
                                                                  // pass null
                                                                  // to intent
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Build the notification
        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(
                context);
        notBuilder.setOngoing(true);
        notBuilder.setContentTitle(context
                .getString(R.string.notification_uploader_title));
        notBuilder.setSmallIcon(R.drawable.ic_launcher);
        notBuilder.setContent(contentView);
        notBuilder.setContentIntent(contentIntent);
        notification = notBuilder.build();
        mNotificationManager.notify(UNIQUE_ID, notification);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.i("UploadingService", "File " + filename);
        final String username = params[0];
        final String password = params[1];
        SimpleFTP ftp = null;
        try {
            ftp = new SimpleFTP();

            // Connect to an FTP server on port 21.
            ftp.connect(SERVER, 21, username, password);

            // Set binary mode.
            ftp.bin();
            // Change to a new working directory on the FTP server.
            ftp.mkd(folder);
            ftp.cwd(folder);
            is.setOnPercentageChangedListener(new ManagedOnPercentageChangedListener() {
                public void onChanged(int nperc) {
                    publishProgress(nperc);
                }
            });
            // You can also upload from an InputStream, e.g.
            ftp.stor(is, filename);
            is.close();

        } catch (UnknownHostException e) {
            error = WRONG_HOST;
            return false;
        } catch (WrongCredentials e) {
            error = WRONG_CREDENTIAL;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            error = GENERAL_ERROR;
            return false;
        } finally {
            if (ftp != null) {
                try {
                    // Quit from the FTP server.
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    protected void onProgressUpdate(Integer... values) {
        // update the notification object
        if (is.getLength() != 0)
            notification.contentView.setProgressBar(R.id.progressBar, 100,
                    values[0], false);
        // notify the notification manager on the update.
        mNotificationManager.notify(UNIQUE_ID, notification);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mNotificationManager.cancel(UNIQUE_ID);

        final PendingIntent contentIntent = PendingIntent.getActivity(
                context.getApplicationContext(), 0, new Intent(), // add this
                                                                  // pass null
                                                                  // to intent
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Build the notification
        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(
                context);
        notBuilder.setOngoing(false);
        notBuilder.setContentIntent(contentIntent);

        if (result) {
            notBuilder.setContentTitle(context
                    .getString(R.string.notification_uploader_title));
            notBuilder.setContentText(context.getString(
                    R.string.notification_uploader_finished, filename));
        } else {
            switch (error) {
            case WRONG_CREDENTIAL:
                notBuilder
                        .setContentTitle(context
                                .getString(R.string.notification_uploader_error_credential_title));
                notBuilder
                        .setContentText(context
                                .getString(R.string.notification_uploader_error_credential));
                break;
            case WRONG_HOST:
                notBuilder
                        .setContentTitle(context
                                .getString(R.string.notification_uploader_error_host_title));
                notBuilder.setContentText(context
                        .getString(R.string.notification_uploader_error_host));
                break;
            default:
                notBuilder.setContentTitle(context
                        .getString(R.string.notification_uploader_error_title));
                notBuilder.setContentText(context.getString(
                        R.string.notification_uploader_error, filename));
                break;
            }
        }
        notBuilder.setSmallIcon(R.drawable.ic_launcher);
        mNotificationManager.notify(UNIQUE_ID, notBuilder.build());

        listener.finishedTask();

    }

}