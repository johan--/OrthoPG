package com.orthopg.snaphy.orthopg.Fragment.NewsFragment;

import com.androidsdk.snaphy.snaphyandroidsdk.callbacks.DataListCallback;
import com.androidsdk.snaphy.snaphyandroidsdk.list.DataList;
import com.androidsdk.snaphy.snaphyandroidsdk.models.News;
import com.androidsdk.snaphy.snaphyandroidsdk.presenter.Presenter;
import com.androidsdk.snaphy.snaphyandroidsdk.repository.NewsRepository;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.orthopg.snaphy.orthopg.Constants;
import com.orthopg.snaphy.orthopg.MainActivity;
import com.strongloop.android.loopback.RestAdapter;

import java.util.HashMap;

/**
 * Created by Ravi-Gupta on 10/6/2016.
 */

public class NewsPresenter {

    RestAdapter restAdapter;
    DataList<News> newsDataList;
    public int limit = 5;
    CircleProgressBar circleProgressBar;
    MainActivity mainActivity;

    public NewsPresenter(RestAdapter restAdapter, CircleProgressBar progressBar, MainActivity mainActivity){
        this.restAdapter = restAdapter;
        circleProgressBar = progressBar;
        this.mainActivity = mainActivity;
        //Only add if not initialized already..
        if(Presenter.getInstance().getList(News.class, Constants.NEWS_LIST_NEWS_FRAGMENT) == null){
            newsDataList = new DataList<>();
            Presenter.getInstance().addList(Constants.NEWS_LIST_NEWS_FRAGMENT, newsDataList);
        }else{
            newsDataList = Presenter.getInstance().getList(News.class, Constants.NEWS_LIST_NEWS_FRAGMENT);
        }
    }

    public void fetchNews() {
        HashMap<String, Object> filter = new HashMap<>();
        NewsRepository newsRepository = restAdapter.createRepository(NewsRepository.class);
        newsRepository.find(filter, new DataListCallback<News>() {
            @Override
            public void onBefore() {
                super.onBefore();
            }

            @Override
            public void onSuccess(DataList<News> objects) {
                super.onSuccess(objects);
            }

            @Override
            public void onError(Throwable t) {
                super.onError(t);
            }

            @Override
            public void onFinally() {
                super.onFinally();
            }
        });
    }
}