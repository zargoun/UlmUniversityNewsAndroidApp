package ulm.university.news.app.controller;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.DatePicker;

/**
 * TODO
 *
 * @author Matthias Mak
 */
public class DatePickerDialogFragment extends AppCompatDialogFragment implements android.app.DatePickerDialog
        .OnDateSetListener {

    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";

    // Use this instance of the interface to deliver action events.
    DatePickerListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use previous selected values if existing.
        // Use the current date as the default date in the picker.
        int year = getArguments().getInt(YEAR);
        int month = getArguments().getInt(MONTH);
        int day = getArguments().getInt(DAY);

        // Create a new instance of DatePickerDialogFragment and return it.
        return new android.app.DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Notify caller that date has been set.
        listener.onDateSet(getTag(), view, year, month, day);
    }

    @Override
    public void onAttach(Activity activity) {
        // Override the Fragment.onAttach() method to instantiate the DialogListener.
        super.onAttach(activity);
        // Verify that the host fragment implements the callback interface.
        try {
            // Instantiate the DialogListener so we can send events to the host.
            listener = (DatePickerListener) getActivity();
        } catch (ClassCastException e) {
            // The target fragment doesn't implement the interface, throw exception.
            throw new ClassCastException(getTargetFragment().toString() + " must implement DatePickerListener.");
        }
    }
}
