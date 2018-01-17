package com.bamboolmc.modulevideo.module.videoDetail;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bamboolmc.library.utils.ToastUtil;
import com.bamboolmc.library.widget.MultiStateView;
import com.bamboolmc.modulevideo.R;
import com.bamboolmc.modulevideo.base.BaseActivity;
import com.bamboolmc.modulevideo.dagger.DaggerVideoComponent;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideoDetailActivity extends BaseActivity<VideoDetailPresenter> implements VideoDetailContract.View {
    private static final String VIDEO_ID = "video_id";

    @BindView(R.id.vp_video_detail)
    JCVideoPlayerStandard mVideoPlayer;
    @BindView(R.id.rv_video_detail)
    RecyclerView mRecyclerView;
    @BindView(R.id.multi_state_view)
    MultiStateView mMultiStateView;
    @BindView(R.id.title_vdetail_topic)
    TextView mTopicTitle;
    @BindView(R.id.name_vdetail_topic)
    TextView mTopicName;
    @BindView(R.id.img_vdetail_topic)
    ImageView mTopicImg;

    private String mVideoId;
    private VideoDetailAdapter mVideoDetailAdapter;

    public static void startActivity(Context context, String videoId) {
        Intent starter = new Intent(context, VideoDetailActivity.class);
        starter.putExtra(VIDEO_ID, videoId);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void attachView() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    protected void setComponentInject() {
        DaggerVideoComponent.builder()
                .build()
                .inject(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mVideoDetailAdapter = new VideoDetailAdapter();
        mRecyclerView.setAdapter(mVideoDetailAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }

    @Override
    protected void initData() {
        mVideoId = getIntent().getStringExtra(VIDEO_ID);
        mPresenter.getVideoDetail(mVideoId);
        RxBus.get().register(this);
    }

    @Override
    public void showVideo(VideoDetailBean videoDetailBean) {
        RxBus.get().post(new VideoPostBean(videoDetailBean.getTitle(), videoDetailBean.getVurl(),
                videoDetailBean.getCover(), videoDetailBean.getTopicName(), videoDetailBean.getTopicImg()));
    }

    @Subscribe
    public void ExchangeVideo(VideoPostBean mVideoPostBean) {
        mTopicTitle.setText(mVideoPostBean.getVideoName());
        mTopicName.setText(mVideoPostBean.getTopicName());
        Picasso.with(getBaseContext())
                .load(mVideoPostBean.getTopicImg())
                .error(R.drawable.ic_launcher)
                .placeholder(R.drawable.ic_launcher)
                .into(mTopicImg);
        mVideoPlayer.setUp(mVideoPostBean.getVideoUrl(), JCVideoPlayer.SCREEN_LAYOUT_NORMAL, mVideoPostBean.getVideoName());
        mVideoPlayer.startVideo();
        Picasso.with(this)
                .load(mVideoPostBean.getCoverUrl())
                .error(R.drawable.ic_launcher)
                .placeholder(R.drawable.ic_launcher)
                .into(mVideoPlayer.thumbImageView);
    }

    @Override
    public void showRelVideoList(List<VideoDetailBean.RecommendBean> recommendBeanList) {
        mVideoDetailAdapter.setNewData(recommendBeanList);
        mMultiStateView.setState(MultiStateView.STATE_CONTENT);
    }

    @Override
    public void onStartRequest() {
        if (mMultiStateView.getState() != MultiStateView.STATE_CONTENT) {
            mMultiStateView.setState(MultiStateView.STATE_LOADING);
        }
    }

    @Override
    public void showContent() {
        mMultiStateView.setState(MultiStateView.STATE_CONTENT);
    }

    @Override
    public void showError() {
        if (mMultiStateView.getState() != MultiStateView.STATE_CONTENT) {
            mMultiStateView.setState(MultiStateView.STATE_ERROR)
                    .setIcon(R.mipmap.ic_exception)
                    .setTitle(R.string.label_error_network_is_bad)
                    .setButton(R.string.label_click_button_to_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPresenter.getVideoDetail(mVideoId);
                        }
                    });
        } else {
            ToastUtil.showToast(R.string.label_error_network_is_bad);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}