//package com.tg.tgt.moment.model;
//
//import com.tg.tgt.http.ApiManger2;
//import com.tg.tgt.http.EmptyData;
//import com.tg.tgt.http.HttpResult;
//import com.tg.tgt.moment.bean.CircleItem;
//import com.tg.tgt.moment.contract.MomentContract;
//
//import java.util.List;
//
//import io.reactivex.Observable;
//import retrofit2.http.Field;
//
///**
// *
// * @author yiyang
// */
//public class MomentModel implements MomentContract.Model {
//
//    @Override
//    public Observable<HttpResult<List<CircleItem>>> loadCircleData(String id, int limit) {
//        return ApiManger2.getApiService().showFriendMoments(id, limit);
//        /*List<CircleItem> datas = DatasUtil.createCircleDatas();
//        return Observable.just(datas)
//                .compose(RxUtils.<List<CircleItem>>applySchedulers());*/
//    }
//
//    @Override
//    public Observable<HttpResult<List<CircleItem>>> loadUserMoment(String userId, String id, int limit) {
//        return ApiManger2.getApiService().showUserMoments(userId, id,limit);
//    }
//
//    @Override
//    public Observable<HttpResult<EmptyData>> likeMoments(String id, boolean isLike) {
//        return ApiManger2.getApiService().likeMoments(id,, true);
//    }
//
//    @Override
//    public Observable<HttpResult<EmptyData>> momentsComment(String id, String content) {
//        return null;
//    }
//}
