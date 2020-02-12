package ngonim.xyz.yourweather.models;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by ngoni on 4/22/17.
 */

public class Ut {

    public static Dialog alert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",null);

        return builder.create();

    }


}
