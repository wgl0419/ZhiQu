package com.bamboolmc.zhiqu.ui.adapter;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bamboolmc.zhiqu.R;
import com.bamboolmc.zhiqu.base.BaseItemType;
import com.bamboolmc.zhiqu.model.bean.MtHotMovieListBean;
import com.bamboolmc.zhiqu.ui.activity.MtMovieDetailActivity;
import com.bamboolmc.zhiqu.ui.activity.MtMovieVideoActivity;
import com.bamboolmc.zhiqu.util.ImgResetUtil;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

/**
 * Created by limc on 17/5/11.
 */
public class MtHotMovieListAdapter extends BaseMultiItemQuickAdapter<MtHotMovieListBean.DataBean.HotBean, BaseViewHolder> {

    public MtHotMovieListAdapter() {
        super(null);
        addItemType(BaseItemType.TYPE_HOT_HEADLINE, R.layout.item_mthot_headline);
        addItemType(BaseItemType.TYPE_HOT_NORMAL, R.layout.item_mthot_normal);
    }

    @Override
    protected void convert(BaseViewHolder helper, final MtHotMovieListBean.DataBean.HotBean item) {
        helper.setText(R.id.tv_hot_movie_name, String.format("%s", item.getNm()))
                .setText(R.id.tv_hot_desc, String.format("%s", item.getDesc()))
                .setText(R.id.tv_hot_showInfo, String.format("%s", item.getShowInfo()));

        //预售 显示为（xxx人想看）在售显示评分
        if (item.getPreSale() == 0) {
            helper.setText(R.id.tv_hot_audience, String.format("观众评 %s", item.getSc()));
        } else if (item.getPreSale() == 1) {

            helper.setText(R.id.tv_hot_audience, String.format("%s人想看", item.getWish()));
        }

        if (item.getSc() == 0 && item.getPreSale() == 0) {
            helper.setText(R.id.tv_hot_audience, "暂无评分");
        }

        //显示图片
        String img = ImgResetUtil.resetPicUrl(item.getImg(), ".webp@171w_240h_1e_1c_1l");
        Picasso.with(mContext)
                .load(img)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into((ImageView) helper.getView(R.id.iv_hot_img));

        //一个TextView里显示不同的字体
        TextView tv = helper.getView(R.id.tv_hot_audience);
        String content = tv.getText().toString();
        Spannable span = new SpannableString(content);
        if (content.contains("人想看")) {
            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_yellow)), 0, content.indexOf("人"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(span);
        } else if (content.contains("观众评 ")) {
            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_yellow)), 3, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(span);
        }

        switch (helper.getItemViewType()) {
            case BaseItemType.TYPE_HOT_HEADLINE:
                if (item.getHeadLinesVO().size() > 1) {
                    helper.setText(R.id.tv_hot_type1, String.format("%s", item.getHeadLinesVO().get(0).getType()))
                            .setText(R.id.tv_hot_type2, String.format("%s", item.getHeadLinesVO().get(1).getType()))
                            .setText(R.id.tv_hot_headline_title1, String.format("%s", item.getHeadLinesVO().get(0).getTitle()))
                            .setText(R.id.tv_hot_headline_title2, String.format("%s", item.getHeadLinesVO().get(1).getTitle()));
                } else if (item.getHeadLinesVO().size() == 1) {
                    helper.getView(R.id.ll_hot_head2).setVisibility(View.GONE);
                    helper.setText(R.id.tv_hot_type1, String.format("%s", item.getHeadLinesVO().get(0).getType()))
                            .setText(R.id.tv_hot_headline_title1, String.format("%s", item.getHeadLinesVO().get(0).getTitle()));
                }
                helper.getView(R.id.tv_hot_headline_title1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        BaseWebViewActivity.start(mContext, item.getHeadLinesVO().get(0).getUrl());
                    }
                });
                helper.getView(R.id.tv_hot_headline_title2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        BaseWebViewActivity.start(mContext, item.getHeadLinesVO().get(1).getUrl());
                    }
                });
                break;
        }
        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MtMovieDetailActivity.startActivity(mContext, item.getId());
            }
        });

        helper.getView(R.id.fl_hot_movie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MtMovieVideoActivity.startActivity(mContext,item.getId(),item.getVideoId(),item.getNm()+" "
                        +item.getVideoName(),item.getVideourl());
            }
        });


    }

}
