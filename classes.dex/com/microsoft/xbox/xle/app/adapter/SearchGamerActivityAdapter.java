package com.microsoft.xbox.xle.app.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.FriendItem;
import com.microsoft.xbox.xle.viewmodel.SearchGamerActivityViewModel;
import java.util.ArrayList;

public class SearchGamerActivityAdapter extends AdapterBaseNormal {
    private ArrayList<FriendItem> friendsList;
    private SearchGamerListAdapter friendsListAdapter;
    private ListView friendsListView;
    private SearchGamerActivityViewModel friendsViewModel;
    private EditText gamertagEditView;

    public SearchGamerActivityAdapter(SearchGamerActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.search_gamer_activity_body);
        this.friendsViewModel = viewModel;
        this.gamertagEditView = (EditText) findViewById(R.id.search_gamer_gamertag);
        this.friendsListView = (ListView) findViewById(R.id.search_gamer_friends_list);
        this.gamertagEditView.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                SearchGamerActivityAdapter.this.friendsViewModel.onGamertagEntryChanged(s.toString());
            }
        });
        this.gamertagEditView.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 3) {
                    SearchGamerActivityAdapter.this.searchGamer(v.getText().toString());
                }
                return true;
            }
        });
        this.friendsListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (view.getTag() != null && (view.getTag() instanceof SimpleListItemViewHolder)) {
                    SearchGamerActivityAdapter.this.searchGamer(((SimpleListItemViewHolder) view.getTag()).getKey().toString());
                }
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.search_gamer_app_bar_button, new OnClickListener() {
            public void onClick(View v) {
                SearchGamerActivityAdapter.this.searchGamer(SearchGamerActivityAdapter.this.gamertagEditView.getText().toString());
            }
        });
        showKeyboard();
    }

    public void updateViewOverride() {
        setBlocking(this.friendsViewModel.isBlockingBusy(), this.friendsViewModel.getBlockingStatusText());
        updateLoadingIndicator(this.friendsViewModel.isBusy());
        boolean z = this.gamertagEditView.getText() != null && this.gamertagEditView.getText().length() > 0;
        setAppBarButtonEnabled(R.id.search_gamer_app_bar_button, z);
        if (this.friendsViewModel.getFilteredFriends() == null) {
            return;
        }
        if (this.friendsList != this.friendsViewModel.getFilteredFriends()) {
            this.friendsList = this.friendsViewModel.getFilteredFriends();
            this.friendsListAdapter = new SearchGamerListAdapter(XLEApplication.getMainActivity(), R.layout.friends_list_row, this.friendsViewModel.getFilteredFriends());
            this.friendsListView.setAdapter(this.friendsListAdapter);
            return;
        }
        this.friendsListAdapter.notifyDataSetChanged();
    }

    public void showKeyboard() {
        this.gamertagEditView.requestFocus();
        showKeyboard(this.gamertagEditView);
    }

    public void setGamertagText(String text) {
        this.gamertagEditView.setText("");
        if (text != null && text.length() > 0) {
            this.gamertagEditView.append(text);
        }
    }

    private void searchGamer(String gamertag) {
        if (this.friendsViewModel.beginSearchGamer(gamertag.trim())) {
            dismissKeyboard();
        }
    }
}
