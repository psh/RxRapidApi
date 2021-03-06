package com.gatebuzz.rapidapi.rx.example.hackernews;

import com.gatebuzz.rapidapi.rx.ApiPackage;
import com.gatebuzz.rapidapi.rx.Application;
import com.gatebuzz.rapidapi.rx.Named;

import java.util.Map;

import rx.Observable;

@Application(project = "RxRapidApi_Demo", key = "e21f74d4-1cb0-4476-92fd-a81fb29d6fa0")
@ApiPackage("HackerNews")
public interface HackerNewsApi {
    Observable<Map<String, Object>> getNewStories();

    Observable<Map<String, Object>> getBestStories();

    Observable<Map<String, Object>> getAskStories();

    Observable<Map<String, Object>> getShowStories();

    Observable<Map<String, Object>> getJobStories();

    Observable<Map<String, Object>> getUpdates();

    Observable<Map<String, Object>> getUser(@Named("username") String username);

    Observable<Map<String, Object>> getItem(@Named("itemId") Long item);
}
