package com.nigel.wenreader.widget.page;

import com.nigel.wenreader.db.entity.CollectBookEntity;

import java.io.BufferedReader;

public class NetPageLoader extends PageLoader {


    public NetPageLoader(PageView pageView, CollectBookEntity collectBookEntity) {
        super(pageView, collectBookEntity);
    }

    @Override
    public void refreshChapterList() {

    }

    @Override
    protected BufferedReader getChapterReader(TxtChapter chapter) throws Exception {
        return null;
    }

    @Override
    protected boolean hasChapterData(TxtChapter chapter) {
        return false;
    }
}
