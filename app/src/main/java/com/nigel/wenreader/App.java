package com.nigel.wenreader;

import android.app.Application;
import android.content.Context;

import com.nigel.wenreader.db.database.BookDatabase;
import com.nigel.wenreader.model.repository.BookRepository;

public class App extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static BookDatabase getDatabase(){
        return BookDatabase.getInstance();
    }

//    public static BookRepository getRepository() {
//        return BookRepository.getInstance();
//    }

    public static Context getContext() {
        return sContext;
    }
}
