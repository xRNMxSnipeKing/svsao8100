package com.microsoft.xbox.xle.app.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.SearchGameHistoryActivityViewModel;
import java.util.ArrayList;

public class SearchGameHistoryActivityAdapter extends AdapterBaseNormal {
    private EditText titleEditView;
    private ArrayList<Title> titleList;
    private CollectionTitleListAdapter titleListAdapter;
    private ListView titleListView;
    private SwitchPanel titleSwitchPanel;
    private SearchGameHistoryActivityViewModel viewModel;

    public SearchGameHistoryActivityAdapter(SearchGameHistoryActivityViewModel vm) {
        this.screenBody = findViewById(R.id.search_game_history_activity_body);
        this.viewModel = vm;
        this.titleEditView = (EditText) findViewById(R.id.search_game_history_title);
        this.titleListView = (ListView) findViewById(R.id.search_game_history_games_list);
        this.titleSwitchPanel = (SwitchPanel) findViewById(R.id.search_game_history_switch_panel);
        this.content = this.titleSwitchPanel;
        this.titleEditView.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                SearchGameHistoryActivityAdapter.this.viewModel.onGameTitleEntryChanged(s.toString());
            }
        });
        this.titleListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (view.getTag() != null) {
                    Title title = null;
                    if (view.getTag() instanceof GameItemViewHolder) {
                        title = ((GameItemViewHolder) view.getTag()).getGame();
                    } else if (view.getTag() instanceof AppItemViewHolder) {
                        title = (Title) ((AppItemViewHolder) view.getTag()).getKey();
                    }
                    if (title != null) {
                        SearchGameHistoryActivityAdapter.this.dismissKeyboard();
                        SearchGameHistoryActivityAdapter.this.viewModel.navigateToAchievementsOrTitleDetail(title);
                    }
                    XLELog.Error("SearchGameHistoryActivityAdapter", "Title is null");
                }
            }
        });
    }

    protected void onAppBarUpdated() {
        showKeyboard();
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.viewModel.isBusy());
        if (this.viewModel.getFilteredTitles() != null) {
            if (this.titleList != this.viewModel.getFilteredTitles()) {
                this.titleList = this.viewModel.getFilteredTitles();
                this.titleListAdapter = new CollectionTitleListAdapter(XLEApplication.getMainActivity(), R.layout.simple_list_row, this.viewModel.getFilteredTitles());
                this.titleListView.setAdapter(this.titleListAdapter);
            } else {
                this.titleListAdapter.notifyDataSetChanged();
            }
        }
        this.titleSwitchPanel.setState(this.viewModel.getIsEmpty() ? 0 : 1);
    }

    public void showKeyboard() {
        this.titleEditView.requestFocus();
        showKeyboard(this.titleEditView);
    }

    public void setTitleText(String text) {
        this.titleEditView.setText("");
        if (text != null && text.length() > 0) {
            this.titleEditView.append(text);
        }
    }
}
