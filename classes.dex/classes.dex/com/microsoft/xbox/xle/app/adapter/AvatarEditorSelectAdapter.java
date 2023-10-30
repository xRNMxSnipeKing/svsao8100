package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.view.AvatarEditorOption;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionAsset;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionCategory;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionColor;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.XLEGridView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithGridAndFloatingAvatarView;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorSelectActivityViewModel;

public class AvatarEditorSelectAdapter extends AdapterBaseWithGridAndFloatingAvatarView {
    private TextView gridDescription;
    private TextView gridTitle;
    private AvatarEditorSelectListAdapter listAdapter;
    private AvatarEditorOption[] selectList;
    private AvatarEditorSelectActivityViewModel viewModel;

    public AvatarEditorSelectAdapter(AvatarEditorSelectActivityViewModel vm) {
        this.screenBody = findViewById(R.id.avatar_select_body);
        this.content = findViewById(R.id.select_grid);
        this.viewModel = vm;
        this.gridView = (XLEGridView) findViewById(R.id.select_grid);
        this.gridTitle = (TextView) findViewById(R.id.select_grid_title);
        this.gridDescription = (TextView) findViewById(R.id.select_grid_description);
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AvatarEditorOption option = ((AvatarEditorOptionViewHolder) view.getTag()).getOption();
                if (option instanceof AvatarEditorOptionCategory) {
                    AvatarEditorSelectAdapter.this.viewModel.navigateToOptionCategory((AvatarEditorOptionCategory) option);
                    return;
                }
                if (option instanceof AvatarEditorOptionAsset) {
                    XboxMobileOmnitureTracking.TrackAvatarEditItem(((AvatarEditorOptionAsset) option).getAssetTitle());
                }
                AvatarEditorSelectAdapter.this.viewModel.navigateToOption(option);
            }
        });
    }

    public void updateViewOverride() {
        if (this.viewModel.getOptions() != null) {
            if (this.selectList != this.viewModel.getOptions()) {
                this.selectList = this.viewModel.getOptions();
                this.listAdapter = new AvatarEditorSelectListAdapter(XLEApplication.getMainActivity(), R.layout.achievements_list_row, this.viewModel);
                if (this.selectList != null && this.selectList.length > 0 && XboxApplication.Instance.getIsTablet() && ((this.selectList[0] instanceof AvatarEditorOptionAsset) || (this.selectList[0] instanceof AvatarEditorOptionColor))) {
                    this.gridView.setNumColumns(5);
                }
                this.gridView.setAdapter(this.listAdapter);
            } else {
                this.listAdapter.notifyDataSetChanged();
            }
            this.gridView.setSelection(this.viewModel.getFirstSelectedOptionIndex());
            XLEUtil.updateTextIfNotNull(this.gridTitle, this.viewModel.getScreenTitle());
            this.gridDescription.setText(this.viewModel.getScreenDescription());
        }
        this.avatarView.setAvatarViewVM(this.viewModel.getAvatarViewVM());
        this.avatarActor.setActorVM(this.viewModel.getAvatarActorVM());
    }
}
