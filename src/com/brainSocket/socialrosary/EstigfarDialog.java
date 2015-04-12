package com.brainSocket.socialrosary;

import com.brainSocket.socialrosary.model.AppEvent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class EstigfarDialog extends DialogFragment {

	private static final String TAG = "EstigfarDialog";

	DialogEstigfarInterface estigfar_interface;

	TextView tvfromuser;
	TextView tvdone_times;
	TextView tvtotal_done;
	TextView tvthe_dowaa;

	AppEvent event ;
	
	public EstigfarDialog(AppEvent event, DialogEstigfarInterface callBack) {
		estigfar_interface = callBack;
		this.event =event ;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_estigfar_receive, null);

		// TODO Add views here!!!
		tvdone_times = (TextView) view.findViewById(R.id.tvfromuser);
		tvdone_times = (TextView) view.findViewById(R.id.tvdone_times);
		tvtotal_done = (TextView) view.findViewById(R.id.tvtotal_done);
		tvthe_dowaa = (TextView) view.findViewById(R.id.tvthe_dowaa);

		builder.setPositiveButton("Done!!",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getActivity().getBaseContext(),
								"Ã“«ﬂ «··Â «·ŒÌ—", Toast.LENGTH_SHORT).show();
					}
				});
		builder.setView(view);
		builder.setTitle("Estigfar..");
		return builder.create();
	}

	public interface DialogEstigfarInterface {
		public void dialogEstigfarTheDowaaButtonClicked();

	}

}
