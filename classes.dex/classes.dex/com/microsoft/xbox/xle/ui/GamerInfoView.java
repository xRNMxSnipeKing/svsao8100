package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.net.URI;

public class GamerInfoView extends RelativeLayout {
    private CustomTypefaceTextView gamerAchievementsPercentView;
    private CustomTypefaceTextView gamerGamerTagView;
    private CustomTypefaceTextView gamerGamerscoreView;
    private PieChartView gamerPieChartView;
    private XLEImageViewFast gamerTile;
    private CustomTypefaceTextView gamerTotalAchievementsView;

    public GamerInfoView(Context context) {
        super(context);
        init(context);
    }

    public GamerInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GamerInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.gamer_info, this, true);
        this.gamerGamerTagView = (CustomTypefaceTextView) findViewById(R.id.gamer_gametag);
        this.gamerTile = (XLEImageViewFast) findViewById(R.id.gamer_gamerpic);
        this.gamerPieChartView = (PieChartView) findViewById(R.id.pie_chart_view);
        this.gamerAchievementsPercentView = (CustomTypefaceTextView) findViewById(R.id.gamer_achievements_percent);
        this.gamerTotalAchievementsView = (CustomTypefaceTextView) findViewById(R.id.gamer_total_achievements);
        this.gamerGamerscoreView = (CustomTypefaceTextView) findViewById(R.id.gamer_gamerscore);
    }

    public void updateGamerInfo(String gamerTag, URI gamerTileURI, int achievementPercent, String achievemtPercentStr, String totalAchievement, String gamerScore) {
        XLEUtil.updateTextIfNotNull(this.gamerGamerTagView, gamerTag);
        if (this.gamerTile != null) {
            this.gamerTile.setImageURI2(gamerTileURI);
        }
        if (this.gamerPieChartView != null) {
            this.gamerPieChartView.setPercentage(achievementPercent);
        }
        XLEUtil.updateTextIfNotNull(this.gamerAchievementsPercentView, achievemtPercentStr);
        XLEUtil.updateTextIfNotNull(this.gamerTotalAchievementsView, totalAchievement);
        XLEUtil.updateTextIfNotNull(this.gamerGamerscoreView, gamerScore);
    }
}
