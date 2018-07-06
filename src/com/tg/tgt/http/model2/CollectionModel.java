package com.tg.tgt.http.model2;

import java.util.List;

/**
 * Created by DELL on 2018/7/5.
 */

public class CollectionModel {
//    private int endRow;// ": 0,
//    private int firstPage;//": 0,
//    private boolean hasNextPage;//": true,
//    private boolean hasPreviousPage;//": true,
//    private boolean isFirstPage;//": true,
//    private boolean isLastPage;//": true,
//    private int lastPage;//": 0,
    private List<CollectionItemModel> list;//": [
    private int nextPage;//": 0,
    private int pageNum;//": 0,
    private int total;//": 0

    public List<CollectionItemModel> getList() {
        return list;
    }

    public void setList(List<CollectionItemModel> list) {
        this.list = list;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
