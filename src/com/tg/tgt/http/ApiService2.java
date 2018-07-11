package com.tg.tgt.http;

import com.tg.tgt.http.model2.CollectionItemModel;
import com.tg.tgt.http.model2.CollectionModel;
import com.tg.tgt.http.model2.GroupEntity;
import com.tg.tgt.http.model2.GroupModel;
import com.tg.tgt.http.model2.GroupUserModel;
import com.tg.tgt.http.model2.LoginModel;
import com.tg.tgt.http.model2.MomentsLogModel;
import com.tg.tgt.http.model2.NewsModel;
import com.tg.tgt.http.model2.NonceBean;
import com.tg.tgt.http.model2.TokenModel;
import com.tg.tgt.http.model2.UserFriendModel;
import com.tg.tgt.http.model2.UserRelationInfoModel;
import com.tg.tgt.http.model2.VerModel;
import com.tg.tgt.moment.bean.CircleItem;
import com.tg.tgt.moment.bean.CollectBean;
import com.tg.tgt.moment.bean.CollectItem;
import com.tg.tgt.moment.bean.CommentItem;
import com.tg.tgt.moment.bean.FavortItem;
import com.tg.tgt.moment.bean.PicBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 *
 * @author yiyang
 */
public interface ApiService2 {
//    Base URL：总是以/结尾
//    @GET/POST等：不要以/开头
    /**
     * 网络请求超时时间毫秒
     */
    int DEFAULT_TIMEOUT = 15000;
    /*String downUrl="https://fir.im/uf39";
    String BASE_URL = "http://timly2.live2017.biz/timely/";
    String ppk="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHLe0aLInF//BveiN22YAOLQ8EFdS+qHfuqb8FmO9lIzs/TwG2LNyd7X1KXsP14B6LgsatNzxGvwVwTe2WYHECGm4iWaTB6lUVHnc4MuVb+4gLgCmIH+TP2BfOfNtkjGuKygSaXUMKi/uzngeybqp0dgQ2YDpcExurRax/2+L0jwIDAQAB";*/
    String downUrl="https://www.pgyer.com/NTfV";
    String BASE_URL = "http://timly.live2017.biz/timly/";//http://192.168.2.82:8050/timely/   http://timly.live2017.biz/timly/
    String ppk = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQNVqLqr9rwM+flL+U3GWfkirttHWstY7iB2HStfLirVOG/Yg09ABMFtcvWK8+3yx2Z7UZFu16Z56YK+nI3aEzv0rni/3CIJ/ljO9o+j8KAc4Y+9Ql1WQAmDxGzE7GY60rALvzJgT6cdQFwwwI9AdiGdAyswD1R5y7Cu1M+aWUSwIDAQAB";

    //String BASE_URL = "http://192.168.2.47:8050/timely/";
    @FormUrlEncoded
    @POST("api/user/regist")
    Observable<HttpResult<EmptyData>> regist(@Field("nickname") String nickname, @Field("username") String username,
                                             @Field("password") String password, @Field("code") String code,
                                             @Field("mobilePrefix") String mobilePrefix, @Field("nonce") String nonce);

    @Multipart
    @POST("api/user/regist")
    Observable<HttpResult<EmptyData>> regist(@Part MultipartBody.Part avatar, @Part("nickname") RequestBody nickname,
                                             @Part("username") RequestBody username, @Part("password") RequestBody password,
                                             @Part("code") RequestBody code, @Part("mobilePrefix") RequestBody mobilePrefix,
                                             @Part("nonce") RequestBody nonce);


    @FormUrlEncoded
    @POST("api/login")
    Observable<HttpResult<LoginModel>> login(@Field("username") String username, @Field("password") String password, @Field("code") String code, @Field
                                                     ("nonce") String nonce);

    /**
     * 查看登陆用户信息
     */
    @POST("api/user/myInfo")
    Observable<HttpResult<EmptyData>> myInfo();

    /**
     * 查看好友信息
     */
    @FormUrlEncoded
    @POST("api/user/showInfo")
    Observable<HttpResult<UserFriendModel>> showFriendInfo(@Field("username") String username);

    /**
     * 查看所有好友信息
     */
    @POST("api/user/showFriends")
    Observable<HttpResult<List<UserFriendModel>>> showFriendsInfo();

    /**
     * @param sex   性别 1:男,2:女,3:其他
     */
    @Multipart
    @POST("api/user/modify")
    Observable<HttpResult<String>> modifyInfo(@Part MultipartBody.Part avatar, @Part("nickname") RequestBody nickname,
                                                 @Part("sex") RequestBody sex, @Part("age") RequestBody age,
                                                 @Part("address") RequestBody address);

    @Multipart
    @POST("api/collection/addCollectionMessage")
    Observable<HttpResult<CollectBean>>collection(@Part List<MultipartBody.Part> part);


    @FormUrlEncoded
    @POST("api/sendOfflineMesgToRec")
    Observable<HttpResult<CollectBean>>voice_sms(@Field("fromUid")String fromUid,@Field("type")String type);



    @FormUrlEncoded
    @POST("api/collection/queryLastFourPicture ")
    Observable<HttpResult<List<PicBean>>>getPic(@Field("fromUid")String fromUid);


    String CODE_REGIEST = "1";
    String CODE_LOGIN = "2";
    String CODE_RESET = "3";
    String CODE_RESET_SAFE = "4";
    /**
     * @param type  验证码类型 1:注册, 2:登陆, 3:找回密码, 4:重置安全密码
     */
 /*   @FormUrlEncoded
    @POST("api/code")
    Observable<HttpResult<EmptyData>> getCode(@Field("username") String username, @Field("emailSuffix") String emailSuffix, @Field("type") String type);*/

    /**
     *  发送登录短信验证码
     * */
    @FormUrlEncoded
    @POST("api/sendLoginSMS")
    Observable<HttpResult<EmptyData>>sendLoginSms(@Field("username") String username, @Field("password") String password,@Field("nonce") String nonce);

    /**
     *  发送注册短信验证码
     * */
    @FormUrlEncoded
    @POST("api/sendRegistSMS")
    Observable<HttpResult<EmptyData>>sendRegistSms(@Field("username") String username, @Field("mobilePrefix") String mobilePrefix);

    /**
     *  发送找回用户密码短信验证码
     * */
    @FormUrlEncoded
    @POST("api/sendRestPwdSMSCode")
    Observable<HttpResult<EmptyData>>sendRestPwdSms(@Field("username") String username);

    /**
     *  重置用户安全密码短信验证码
     * */
    @FormUrlEncoded
    @POST("api/sendRestSafePwdCode")
    Observable<HttpResult<EmptyData>>sendRestSafePwdSms(@Field("uuid") String uuid);

    /**
     *  检查重置用户密码短信验证码
     * */
    @FormUrlEncoded
    @POST("api/verifyRestPwdSmsCode")
    Observable<HttpResult<String>>verifyRestPwd(@Field("code") String code,@Field("username") String username);

    /**
     *  检查重置安全密码短信验证码
     * */
    @FormUrlEncoded
    @POST("api/verifyRestSafePwdSmsCode")
    Observable<HttpResult<String>>verifyRestSafePwd(@Field("code") String code);

    /**
     * 查找好友
     * @param search 关键词, email, sn
     */
    @FormUrlEncoded
    @POST("api/user/find")
    Observable<HttpResult<List<UserRelationInfoModel>>> find(@Field("search") String search);

    /**
     * 通过qrcode的值搜索用户
     */
    @POST()
    Observable<HttpResult<UserRelationInfoModel>> qrSearch(@Url String search);

    @FormUrlEncoded
    @POST("api/user/safe/modifyPassword")
    Observable<HttpResult<EmptyData>> modifyPassword(@Field("oldPassword") String oldPassword,@Field("password") String password,@Field("nonce") String nonce);

    @FormUrlEncoded
    @POST("api/user/safe/resetPassword")
    Observable<HttpResult<EmptyData>> resetPassword(@Field("username") String username, @Field("code") String code, @Field
            ("nonce") String nonce, @Field("password") String password);
   @FormUrlEncoded
    @POST("api/user/safe/resetSafePassword")
    Observable<HttpResult<EmptyData>> resetSafePassword(@Field("username") String username, @Field("code") String code, @Field
            ("nonce") String nonce, @Field("password") String password);

    /**
     * @param type 验证码类型 1:注册, 2:登陆, 3:找回密码
     */
/*    @FormUrlEncoded
    @POST("api/checkCode")
    Observable<HttpResult<String>> checkCode(@Field("username") String username, @Field("code") String code, @Field
            ("emailSuffix") String emailSuffix, @Field("type") String type);

    @FormUrlEncoded
    @POST("api/user/safe/modifySafePassword")
    Observable<HttpResult<EmptyData>> modifySafePassword(@Field("oldPassword") String oldPassword,@Field("password") String password);*/

    @FormUrlEncoded
    @POST("api/user/safe/safePassword")
    Observable<HttpResult<EmptyData>> safePassword(@Field("password") String password,@Field("nonce") String nonce);

    @FormUrlEncoded
    @POST("api/user/modifySafe")
    Observable<HttpResult<EmptyData>> modifySafe(@Field("safe") boolean isSafe, @Field("id") String id);

    String QR_CODE = "api/user/qrCode";
    /**
     * 获取二维码
     */
    @POST("api/user/qrCode")
    Observable<ResponseBody> qrCode();

    /**
     *前端-重新生成二维码图片
     * @param type 类型, 1: 带域名(默认),2: 不带域名
     */
    @FormUrlEncoded
    @POST("api/user/resetQrCode")
    Observable<HttpResult<String>> resetQrCode(@Field("type") String type);

    /**
     * 同意添加好友
     */
    @FormUrlEncoded
    @POST("api/user/addFriend")
    Observable<HttpResult<EmptyData>> addFriend(@Field("id") String id);

    /**
     * 好友申请
     */
    @FormUrlEncoded
    @POST("api/user/applyFriend")
    Observable<HttpResult<EmptyData>> applyFriend(@Field("id") String id, @Field("message") String message);
    /**
     * 前端-同意添加好友
     */
    @FormUrlEncoded
    @POST("api/message/user/add")
    Observable<HttpResult<UserFriendModel>> agreeAddFriend(@Field("id") String id);
    /**
     * 前端-拒绝添加好友
     */
    @FormUrlEncoded
    @POST("api/message/user/reject")
    Observable<HttpResult<EmptyData>> rejectAddFriend(@Field("id") String id, @Field("message") String message);

    @FormUrlEncoded
    @POST("api/user/deleteFriend")
    Observable<HttpResult<EmptyData>> deleteFriend(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/user/modifyRemark")
    Observable<HttpResult<EmptyData>> modifyRemark(@Field("id") String id, @Field("remark") String remark);

    @FormUrlEncoded
    @POST("api/user/signature/addSignature")
    Observable<HttpResult<EmptyData>> addSignature(@Field("signature") String signature);
    @FormUrlEncoded
    @POST("api/user/signature/deleteSignature")
    Observable<HttpResult<EmptyData>> deleteSignature(@Field("id") String id);

    /**
     * 添加朋友圈动态
     */
    @Multipart
    @POST("api/user/moments/applyMoments")
    Observable<HttpResult<EmptyData>> applyMoments(@PartMap Map<String, RequestBody> pics, @Part("content") RequestBody content,
                                                   @Part("width") RequestBody width, @Part("height") RequestBody height);

    /**
     * 动态点赞
     */
    @FormUrlEncoded
    @POST("api/user/moments/likeMoments")
    Observable<HttpResult<FavortItem>> likeMoments(@Field("id") String id, @Field("isLike") boolean isLike);

    /**
     * 动态评论
     */
    @FormUrlEncoded
    @POST("api/user/moments/momentsComment")
    Observable<HttpResult<CommentItem>> momentsComment(@Field("id") String id, @Field("content") String content, @Field("parentId") String parentId);
    @FormUrlEncoded
    @POST("api/user/moments/deleteMomentsComment")
    Observable<HttpResult<List<CommentItem>>> deleteMomentsComment(@Field("id") String id);

    /**
     * 查看朋友圈动态
     */
    @FormUrlEncoded
    @POST("api/user/moments/showFriendMoments")
    Observable<HttpResult<List<CircleItem>>> showFriendMoments(@Field("id") String id, @Field("limit") int limit);

    /**
     * 查看userId主页
     */
    @FormUrlEncoded
    @POST("api/user/moments/showMoments")
    Observable<HttpResult<List<CircleItem>>> showUserMoments(@Field("userId") String userId, @Field("id") String id, @Field("limit") int limit);

    /**
     * 查看单条动态
     */
    @POST("api/user/moments/{id}")
    Observable<HttpResult<List<CircleItem>>> showMoment(@Path("id") String id);

    /**
     * 查看来访记录
     */
    @FormUrlEncoded
    @POST("api/user/moments/showMomentsLog")
    Observable<HttpResult<List<MomentsLogModel>>> showMomentsLog(@Field("id") String id, @Field("limit") int limit);

    @FormUrlEncoded
    @POST("api/group/create")
    Observable<HttpResult<GroupEntity>> createGroup(@Field("groupName") String groupName, @Field("groupDescription") String groupDescription);

    @FormUrlEncoded
    @POST("api/group/createGroup")
    Observable<HttpResult<GroupModel>> createGroup(@Field("groupName") String groupName, @Field("groupDescription") String groupDescription, @Field("maxUsers") int maxUsers, @Field("allowInvites") boolean allowInvites, @Field("inviteNeedConffirm") boolean inviteNeedConffirm, @Field("userIds") String userIds);

    @FormUrlEncoded
    @POST("api/group/delete")
    Observable<HttpResult<EmptyData>> deleteGroup(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/group/backGroup")
    Observable<HttpResult<EmptyData>> backGroup(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/group/addBlocks")
    Observable<HttpResult<EmptyData>> addBlocks(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/group/deleteBlocks")
    Observable<HttpResult<EmptyData>> deleteBlocks(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/group/addUser")
    Observable<HttpResult<List<GroupUserModel>>> addUser(@Field("userIds") String userIds, @Field("id") String id);

    @FormUrlEncoded
    @POST("api/group/modify")
    Observable<HttpResult<EmptyData>> modifyGroupInfo(@Field("id") String id, @Field("groupName") String groupName, @Field("groupDescription") String groupDescription);

    @FormUrlEncoded
    @POST("api/group/modify")
    Observable<HttpResult<EmptyData>> modifyGroupInfo(@Field("id") String id, @Field("groupName") String groupName, @Field("groupDescription") String groupDescription, @Field("maxUsers") String maxUsers, @Field("allowInvites") String allowInvites, @Field("inviteNeedConffirm") String inviteNeedConffirm);

    @POST("api/group/showMyGroup")
    Observable<HttpResult<List<GroupModel>>> showMyGroup();

    @FormUrlEncoded
    @POST("api/group/groupInfo")
    Observable<HttpResult<GroupModel>> groupInfo(@Field("sn") String sn);

    @FormUrlEncoded
    @POST("api/group/addAdmin")
    Observable<HttpResult<EmptyData>> addAdmin(@Field("id") String id, @Field("userId") String userId);

    @FormUrlEncoded
    @POST("api/group/deleteAdmin")
    Observable<HttpResult<EmptyData>> deleteAdmin(@Field("id") String id, @Field("userId") String userId);

    @FormUrlEncoded
    @POST("api/group/deleteUser")
    Observable<HttpResult<List<GroupUserModel>>> deleteUser(@Field("id") String id, @Field("userIds") String userIds);


    String REFRESH_TOKEN_URL = "api/token/refreshToken";
    /**
     * @param type 1:密码登陆,2:手势登陆
     */
    @POST("api/token/refreshToken")
    Observable<HttpResult<TokenModel>> refreshToken(@Header("token") String token, @Query("type") int type);

    @FormUrlEncoded
    @POST("api/news/sharing/{id}")
    Observable<HttpResult<EmptyData>> shareNews(@Path("id") String id, @Field("url") String url, @Field("content") String content);

    @FormUrlEncoded
    @POST("api/news/show")
    Observable<HttpResult<List<NewsModel>>> showNews(@Field("id") String id, @Field("limit") int limit);

    @POST("api/news/show/{id}")
    Observable<HttpResult<NewsModel>> showNewsDetail(@Path("id") String id);

    @FormUrlEncoded
    @POST("api/version")
    Observable<HttpResult<VerModel>> ver(@Field("platform") String platform);

    /**
     *  获取服务器随机数
     * */
    @GET("api/servernonce")
    Observable<HttpResult<NonceBean>>servernonce(@Query("uuid") String uuid);


    @FormUrlEncoded
    @POST("api/collection/queryCollectionPageInfo")
    Observable<HttpResult<CollectionModel>> collectionList(@Field("pageNum")String pageNum, @Field("pageSize")int pageSize);

    @FormUrlEncoded
    @POST("api/collection/deleteCollectionById")
    Observable<HttpResult<EmptyData>> deleteCollection(@Field("ids")String id);

}
