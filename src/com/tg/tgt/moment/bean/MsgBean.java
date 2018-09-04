package com.tg.tgt.moment.bean;

/**
 * Created by DELL on 2018/8/30.
 */
import java.util.List;
/**
 * Created by DELL on 2018/9/4.
 */

public class MsgBean {
    /**
     * pageNum : 1
     * pageSize : 5
     * size : 4
     * startRow : 1
     * endRow : 4
     * total : 4
     * pages : 1
     * list : [{"nickname":"小兔崽子","picture":"resources1/M00/21/37/wKhkFltFsJyAG8TkAAIAlphSb4c021.jpg","momentId":294,"momentImg":"resources1/M00/24/59/wKhkFluNA0qAJ0tsAAFqLzfodsA898_300x300.png","commentMsg":"和","noticeType":"1","createTime":"2018-09-04 10:01:34"},{"nickname":"小兔崽子","picture":"resources1/M00/21/37/wKhkFltFsJyAG8TkAAIAlphSb4c021.jpg","momentId":294,"momentImg":"resources1/M00/24/59/wKhkFluNA0qAJ0tsAAFqLzfodsA898_300x300.png","commentMsg":"米","noticeType":"1","createTime":"2018-09-04 09:48:50"},{"nickname":"小兔崽子","picture":"resources1/M00/21/37/wKhkFltFsJyAG8TkAAIAlphSb4c021.jpg","momentId":294,"momentImg":"resources1/M00/24/59/wKhkFluNA0qAJ0tsAAFqLzfodsA898_300x300.png","commentMsg":"后","noticeType":"1","createTime":"2018-09-04 09:43:40"},{"nickname":"小兔崽子","picture":"resources1/M00/21/37/wKhkFltFsJyAG8TkAAIAlphSb4c021.jpg","momentId":294,"momentImg":"resources1/M00/24/59/wKhkFluNA0qAJ0tsAAFqLzfodsA898_300x300.png","commentMsg":"你","noticeType":"1","createTime":"2018-09-04 09:38:28"}]
     * prePage : 0
     * nextPage : 0
     * isFirstPage : true
     * isLastPage : true
     * hasPreviousPage : false
     * hasNextPage : false
     * navigatePages : 8
     * navigatepageNums : [1]
     * navigateFirstPage : 1
     * navigateLastPage : 1
     * lastPage : 1
     * firstPage : 1
     */

    private int pageNum;
    private int pageSize;
    private int size;
    private int startRow;
    private int endRow;
    private int total;
    private int pages;
    private int prePage;
    private int nextPage;
    private boolean isFirstPage;
    private boolean isLastPage;
    private boolean hasPreviousPage;
    private boolean hasNextPage;
    private int navigatePages;
    private int navigateFirstPage;
    private int navigateLastPage;
    private int lastPage;
    private int firstPage;
    private List<ListBean> list;
    private List<Integer> navigatepageNums;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isIsFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public boolean isIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int getNavigateFirstPage() {
        return navigateFirstPage;
    }

    public void setNavigateFirstPage(int navigateFirstPage) {
        this.navigateFirstPage = navigateFirstPage;
    }

    public int getNavigateLastPage() {
        return navigateLastPage;
    }

    public void setNavigateLastPage(int navigateLastPage) {
        this.navigateLastPage = navigateLastPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public List<Integer> getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(List<Integer> navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }

    public static class ListBean {
        /**
         * nickname : 小兔崽子
         * picture : resources1/M00/21/37/wKhkFltFsJyAG8TkAAIAlphSb4c021.jpg
         * momentId : 294
         * momentImg : resources1/M00/24/59/wKhkFluNA0qAJ0tsAAFqLzfodsA898_300x300.png
         * commentMsg : 和
         * noticeType : 1
         * createTime : 2018-09-04 10:01:34
         */

        private String nickname;
        private String picture;
        private int momentId;
        private String momentImg;
        private String commentMsg;
        private String noticeType;
        private String createTime;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public int getMomentId() {
            return momentId;
        }

        public void setMomentId(int momentId) {
            this.momentId = momentId;
        }

        public String getMomentImg() {
            return momentImg;
        }

        public void setMomentImg(String momentImg) {
            this.momentImg = momentImg;
        }

        public String getCommentMsg() {
            return commentMsg;
        }

        public void setCommentMsg(String commentMsg) {
            this.commentMsg = commentMsg;
        }

        public String getNoticeType() {
            return noticeType;
        }

        public void setNoticeType(String noticeType) {
            this.noticeType = noticeType;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}


