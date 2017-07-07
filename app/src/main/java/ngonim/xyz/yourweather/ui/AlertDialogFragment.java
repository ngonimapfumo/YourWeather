package ngonim.xyz.yourweather.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by ngoni on 4/22/17.
 */

public class AlertDialogFragment extends DialogFragment {
    private Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Oops Sorry")
                .setMessage("There was an error")
                .setPositiveButton("OK",null);

        return builder.create();

    }


}
