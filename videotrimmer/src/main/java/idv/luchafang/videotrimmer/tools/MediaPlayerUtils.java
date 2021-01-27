package idv.luchafang.videotrimmer.tools;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;

public class MediaPlayerUtils {

  public static void setMediaPlayerDataSource(Context context,
                                              MediaMetadataRetriever mp, String fileInfo) throws Exception {

    if (fileInfo.startsWith("content://")) {
      try {
        Uri uri = Uri.parse(fileInfo);
        fileInfo = getRingtonePathFromContentUri(context, uri);
      } catch (Exception e) {
      }
    }

    try {
      setMediaPlayerDataSourcePostHoneyComb(context, mp, fileInfo);
    } catch (Exception e) {
      try {
        setMediaPlayerDataSourceUsingFileDescriptor(mp,
          fileInfo);
      } catch (Exception ee) {
        String uri = getRingtoneUriFromPath(context, fileInfo);
        mp.setDataSource(uri);
      }
    }
  }

  private static void setMediaPlayerDataSourcePostHoneyComb(Context context,
                                                            MediaMetadataRetriever mp, String fileInfo) throws Exception {
    mp.setDataSource(context, Uri.parse(Uri.encode(fileInfo)));
  }

  private static void setMediaPlayerDataSourceUsingFileDescriptor(
    MediaMetadataRetriever mp, String fileInfo) throws Exception {
    File file = new File(fileInfo);
    FileInputStream inputStream = new FileInputStream(file);
    mp.setDataSource(inputStream.getFD());
    inputStream.close();
  }

  private static String getRingtoneUriFromPath(Context context, String path) {
    Uri ringtonesUri = MediaStore.Audio.Media.getContentUriForPath(path);
    Cursor ringtoneCursor = context.getContentResolver().query(
      ringtonesUri, null,
      MediaStore.Audio.Media.DATA + "='" + path + "'", null, null);
    ringtoneCursor.moveToFirst();

    long id = ringtoneCursor.getLong(ringtoneCursor
      .getColumnIndex(MediaStore.Audio.Media._ID));
    ringtoneCursor.close();

    if (!ringtonesUri.toString().endsWith(String.valueOf(id))) {
      return ringtonesUri + "/" + id;
    }
    return ringtonesUri.toString();
  }

  public static String getRingtonePathFromContentUri(Context context,
                                                     Uri contentUri) {
    String[] proj = {MediaStore.Audio.Media.DATA};
    Cursor ringtoneCursor = context.getContentResolver().query(contentUri,
      proj, null, null, null);
    ringtoneCursor.moveToFirst();

    String path = ringtoneCursor.getString(ringtoneCursor
      .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

    ringtoneCursor.close();
    return path;
  }
}
