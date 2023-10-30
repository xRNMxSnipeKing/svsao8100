package com.microsoft.xbox.xle.app.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.EditTextContainer;
import com.microsoft.xbox.toolkit.ui.EditViewFixedLength;
import com.microsoft.xbox.toolkit.ui.XLEClickableLayout;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.EditProfileActivityViewModel;

public class EditProfileActivityAdapter extends AdapterBaseNormal {
    private EditViewFixedLength bio;
    private EditProfileActivityViewModel editViewModel;
    private TextView gamertagText;
    private EditViewFixedLength location;
    private EditViewFixedLength motto;
    private EditViewFixedLength name;
    private XLEClickableLayout privacySettingsButton;
    private TextView privacySettingsDescription;

    private class EditProfileActivityAdapterTextWatcher implements TextWatcher {
        private EditProfileActivityAdapterTextWatcher() {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
            boolean same;
            boolean z;
            boolean z2 = true;
            if (EditProfileActivityAdapter.this.motto.getText().equals(EditProfileActivityAdapter.this.editViewModel.getMotto()) && EditProfileActivityAdapter.this.name.getText().equals(EditProfileActivityAdapter.this.editViewModel.getName()) && EditProfileActivityAdapter.this.location.getText().equals(EditProfileActivityAdapter.this.editViewModel.getLocation()) && EditProfileActivityAdapter.this.bio.getText().equals(EditProfileActivityAdapter.this.editViewModel.getBio())) {
                same = true;
            } else {
                same = false;
            }
            EditProfileActivityAdapter editProfileActivityAdapter = EditProfileActivityAdapter.this;
            if (same) {
                z = false;
            } else {
                z = true;
            }
            editProfileActivityAdapter.setAppBarButtonEnabled(R.id.appbar_save, z);
            EditProfileActivityViewModel access$000 = EditProfileActivityAdapter.this.editViewModel;
            if (same) {
                z2 = false;
            }
            access$000.setIsDirty(z2);
        }
    }

    public EditProfileActivityAdapter(EditProfileActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.edit_profile_activity_body);
        this.editViewModel = viewModel;
        this.gamertagText = (TextView) findViewById(R.id.edit_profile_gamertag);
        this.motto = (EditViewFixedLength) findViewById(R.id.motto_edit);
        this.name = (EditViewFixedLength) findViewById(R.id.name_edit);
        this.location = (EditViewFixedLength) findViewById(R.id.location_edit);
        this.bio = (EditViewFixedLength) findViewById(R.id.bio_edit);
        this.privacySettingsButton = (XLEClickableLayout) findViewById(R.id.edit_profile_privacy_settings);
        this.privacySettingsDescription = (TextView) findViewById(R.id.edit_profile_privacy_settings_description);
        EditTextContainer parent = (EditTextContainer) findViewById(R.id.edit_profile_activity_body);
        this.motto.setContainer(parent);
        this.name.setContainer(parent);
        this.location.setContainer(parent);
        this.bio.setContainer(parent);
        if (this.privacySettingsButton != null) {
            this.privacySettingsButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    EditProfileActivityAdapter.this.editViewModel.navigateToPrivacySettings();
                }
            });
        }
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_save, new OnClickListener() {
            public void onClick(View v) {
                EditProfileActivityAdapter.this.dismissKeyboard();
                EditProfileActivityAdapter.this.saveEditTextToVM();
                EditProfileActivityAdapter.this.editViewModel.save();
            }
        });
        setAppBarButtonEnabled(R.id.appbar_save, this.editViewModel.getIsDirty());
        setAppBarButtonClickListener(R.id.appbar_cancel, new OnClickListener() {
            public void onClick(View v) {
                EditProfileActivityAdapter.this.dismissKeyboard();
                EditProfileActivityAdapter.this.editViewModel.cancel();
            }
        });
    }

    public void updateViewOverride() {
        setBlocking(this.editViewModel.isBlockingBusy(), this.editViewModel.getBlockingStatusText());
        if (this.privacySettingsDescription != null) {
            this.privacySettingsDescription.setText(this.editViewModel.getPrivacySettingsDescription());
        }
        setAppBarButtonEnabled(R.id.appbar_save, this.editViewModel.getIsDirty());
    }

    public void loadInitialDataFromVM() {
        if (this.gamertagText != null) {
            this.gamertagText.setText(this.editViewModel.getGamertag());
        }
        this.motto.setText(this.editViewModel.getMotto());
        this.name.setText(this.editViewModel.getName());
        this.location.setText(this.editViewModel.getLocation());
        this.bio.setText(this.editViewModel.getBio());
        this.motto.addTextChangedListener(new EditProfileActivityAdapterTextWatcher());
        this.name.addTextChangedListener(new EditProfileActivityAdapterTextWatcher());
        this.location.addTextChangedListener(new EditProfileActivityAdapterTextWatcher());
        this.bio.addTextChangedListener(new EditProfileActivityAdapterTextWatcher());
    }

    public void saveEditTextToVM() {
        this.editViewModel.setMotto(this.motto.getText());
        this.editViewModel.setName(this.name.getText());
        this.editViewModel.setLocation(this.location.getText());
        this.editViewModel.setBio(this.bio.getText());
    }
}
