package ulm.university.news.app.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;
import ulm.university.news.app.R;
import ulm.university.news.app.api.GroupAPI;
import ulm.university.news.app.api.ServerError;
import ulm.university.news.app.data.Option;
import ulm.university.news.app.manager.database.GroupDatabaseManager;
import ulm.university.news.app.util.Constants;
import ulm.university.news.app.util.TextInputLabels;
import ulm.university.news.app.util.Util;

import static ulm.university.news.app.util.Constants.BALLOT_NOT_FOUND;
import static ulm.university.news.app.util.Constants.CONNECTION_FAILURE;
import static ulm.university.news.app.util.Constants.GROUP_NOT_FOUND;
import static ulm.university.news.app.util.Constants.GROUP_PARTICIPANT_NOT_FOUND;

public class OptionAddActivity extends AppCompatActivity implements DialogListener {
    /** This classes tag for logging. */
    private static final String TAG = "OptionAddActivity";

    private LinearLayout llOptions;
    private List<TextInputLabels> tilOptions;
    private ProgressBar pgrAdding;
    private TextView tvError;
    private Button btnCreate;
    private Toast toast;
    private ImageButton ibAddOption;
    private ImageButton ibRemoveOption;

    private GroupDatabaseManager groupDBM;
    private int groupId;
    private int ballotId;
    private int initialNumberOfOptions;
    private int createdOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set color scheme to ballot green.
        setTheme(R.style.UlmUniversity_Ballot);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_option_add);
        groupId = getIntent().getIntExtra("groupId", 0);
        ballotId = getIntent().getIntExtra("ballotId", 0);
        initialNumberOfOptions = getIntent().getIntExtra("numberOfOptions", 1);
        groupDBM = new GroupDatabaseManager(this);

        tilOptions = new LinkedList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_option_add_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initView();
        updateRemoveButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (warnOnLeave()) {
                    YesNoDialogFragment dialog = new YesNoDialogFragment();
                    Bundle args = new Bundle();
                    args.putString(YesNoDialogFragment.DIALOG_TITLE, getString(R.string.general_leave_page_title));
                    args.putString(YesNoDialogFragment.DIALOG_TEXT, getString(R.string.general_leave_page));
                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), YesNoDialogFragment.DIALOG_LEAVE_PAGE_UP);
                    return true;
                } else {
                    navigateUp();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (warnOnLeave()) {
            YesNoDialogFragment dialog = new YesNoDialogFragment();
            Bundle args = new Bundle();
            args.putString(YesNoDialogFragment.DIALOG_TITLE, getString(R.string.general_leave_page_title));
            args.putString(YesNoDialogFragment.DIALOG_TEXT, getString(R.string.general_leave_page));
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), YesNoDialogFragment.DIALOG_LEAVE_PAGE_BACK);
        } else {
            super.onBackPressed();
        }
    }

    private boolean warnOnLeave() {
        for (TextInputLabels tilOption : tilOptions) {
            if (!tilOption.getText().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void navigateUp() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NavUtils.navigateUpTo(this, intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void initView() {
        llOptions = (LinearLayout) findViewById(R.id.activity_option_add_ll_options);
        tvError = (TextView) findViewById(R.id.activity_option_add_tv_error);
        btnCreate = (Button) findViewById(R.id.activity_option_add_btn_create);
        pgrAdding = (ProgressBar) findViewById(R.id.activity_option_add_pgr_adding);
        ibAddOption = (ImageButton) findViewById(R.id.activity_option_add_ib_add_option);
        ibRemoveOption = (ImageButton) findViewById(R.id.activity_option_add_ib_remove_option);

        // Add initial number of option fields.
        for (int i = 0; i < initialNumberOfOptions; i++) {
            addOptionField();
        }

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAddOptionsRequests();
            }
        });

        ibAddOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOptionField();
                updateRemoveButton();
            }
        });

        ibRemoveOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOptionField();
                updateRemoveButton();
            }
        });

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
    }

    private void addOptionField() {
        TextInputLabels tilOption = new TextInputLabels(this);
        tilOption.setNameAndHint(getString(R.string.general_text));
        tilOption.setLength(1, Constants.OPTION_TEXT_MAX_LENGTH);
        tilOptions.add(tilOption);

        // Add field to the view.
        llOptions.addView(tilOption);
    }

    /**
     * Removes the latest options field.
     */
    private void removeOptionField() {
        tilOptions.remove(tilOptions.size() - 1);
        llOptions.removeViewAt(tilOptions.size());
    }

    private void updateRemoveButton() {
        if (tilOptions.size() > initialNumberOfOptions) {
            ibRemoveOption.setVisibility(View.VISIBLE);
        } else {
            ibRemoveOption.setVisibility(View.GONE);
        }
    }

    private void sendAddOptionsRequests() {
        createdOptions = 0;
        boolean valid = true;
        for (TextInputLabels tilOption : tilOptions) {
            if (!tilOption.isValid()) {
                valid = false;
            }
        }
        if (!Util.getInstance(this).isOnline()) {
            String message = getString(R.string.general_error_no_connection) + getString(R.string.general_error_create);
            toast.setText(message);
            toast.show();
            valid = false;
        }

        if (valid) {
            // All checks passed. Create new option(s).
            tvError.setVisibility(View.GONE);
            btnCreate.setVisibility(View.GONE);
            pgrAdding.setVisibility(View.VISIBLE);

            // Send all option data to the server.
            Option option;
            for (TextInputLabels tilOption : tilOptions) {
                option = new Option();
                option.setText(tilOption.getText());
                GroupAPI.getInstance(this).createOption(groupId, ballotId, option);
            }
        }
    }

    /**
     * This method will be called when an option is posted to the EventBus.
     *
     * @param option The option object.
     */
    public void onEventMainThread(Option option) {
        Log.d(TAG, "EventBus: Option");
        Log.d(TAG, option.toString());

        // Store option in database and show created message.
        groupDBM.storeOption(ballotId, option);
        createdOptions++;

        if (createdOptions == tilOptions.size()) {
            if (createdOptions > 1) {
                toast.setText(getString(R.string.options_created));
            } else {
                toast.setText(getString(R.string.option_created));
            }
            toast.show();
            // Go back to ballot view.
            navigateUp();
        }
    }

    /**
     * This method will be called when a server error is posted to the EventBus.
     *
     * @param serverError The error which occurred on the server.
     */
    public void onEventMainThread(ServerError serverError) {
        Log.d(TAG, "EventBus: ServerError");
        handleServerError(serverError);
    }

    /**
     * Handles the server error and shows appropriate error message.
     *
     * @param serverError The error which occurred on the server.
     */
    public void handleServerError(ServerError serverError) {
        Log.d(TAG, serverError.toString());
        // Show appropriate error message.
        pgrAdding.setVisibility(View.GONE);
        btnCreate.setVisibility(View.VISIBLE);
        Intent intent;
        switch (serverError.getErrorCode()) {
            case CONNECTION_FAILURE:
                tvError.setText(R.string.general_error_connection_failed);
                break;
            case GROUP_NOT_FOUND:
                new GroupDatabaseManager(this).setGroupToDeleted(groupId);
                toast.setText(getString(R.string.group_deleted));
                toast.show();
                // Close activity and go to the main screen to show deleted dialog on restart activity.
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case BALLOT_NOT_FOUND:
                groupDBM.deleteBallot(ballotId);
                toast.setText(getString(R.string.ballot_delete_server));
                toast.show();
                intent = new Intent(this, GroupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
                finish();
            case GROUP_PARTICIPANT_NOT_FOUND:
                // showRemovedFromGroupDialog();
                showRemovedFromGroupMessage();
                break;
        }
    }

    private void showRemovedFromGroupMessage() {
        toast.setText(R.string.group_member_removed_dialog_text);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        removeLocalUserAsGroupMember();
    }

    private void showRemovedFromGroupDialog() {
        // Show group deleted dialog if it wasn't shown before.
        InfoDialogFragment dialog = new InfoDialogFragment();
        Bundle args = new Bundle();
        args.putString(InfoDialogFragment.DIALOG_TITLE, getString(R.string.group_member_removed_dialog_title));
        args.putString(InfoDialogFragment.DIALOG_TEXT, getString(R.string.group_member_removed_dialog_text));
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "removedFromGroup");

        dialog.onDismiss(new DialogInterface() {
            @Override
            public void cancel() {
                removeLocalUserAsGroupMember();
            }

            @Override
            public void dismiss() {
                removeLocalUserAsGroupMember();
            }
        });
    }

    private void removeLocalUserAsGroupMember(){
        // Remove local user as group member.
        new GroupDatabaseManager(this).removeUserFromGroup(groupId,
                Util.getInstance(this).getLocalUser().getId());
        // Close activity and go to the main screen to show deleted dialog on restart activity.
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(YesNoDialogFragment.DIALOG_LEAVE_PAGE_UP)) {
            navigateUp();
        } else if (tag.equals(YesNoDialogFragment.DIALOG_LEAVE_PAGE_BACK)) {
            super.onBackPressed();
        }
    }
}
