package com.tg.tgt.http;

/**
 *
 * @author yiyang
 */
public enum  ResponseCode {
    //系统级异常信息
    NONE(-100, "NONE"),
    SUCCESS(0, "SUCCESS"),
    //环信api错误
    EASEMOB_API_ERROR(-101, "EASEMOB_API_ERROR"),
    //图片类型错误
    IMAGE_TYPE_UNKNOWN(-102, "IMAGE_TYPE_UNKNOWN"),
    //上传图片错误
    IMAGE_UPLAOD_FAIL(-103, "IMAGE_UPLAOD_FAIL"),
    //邮件发送错误
    EMAIL_SEND_FAIL(-104, "EMAIL_SEND_FAIL"),

    //生成token, 发送验证码异常信息
    //类型错误
    TYPE_IS_ERROR(-105, "TYPE_IS_ERROR"),

    //获取验证码异常信息
    //验证码保存异常
    CODE_SEND_IS_ERROR(-106, "CODE_SEND_IS_ERROR"),

    //请求参数异常信息
    //用户名为空
    USERNAME_IS_EMPTY(-10000, "USERNAME_IS_EMPTY"),
    //密码为空
    PASSWORD_IS_EMPTY(-10001, "PASSWORD_IS_EMPTY"),
    //验证码为空
    CODE_IS_EMPTY(-10002, "CODE_IS_EMPTY"),
    //邮箱后缀为空
    EMAIL_SUFFIX_IS_EMPTY(-10003, "EMAIL_SUFFIX_IS_EMPTY"),
    //设备唯一标示为空
    UUID_IS_EMPTY(-10004, "UUID_IS_EMPTY"),
    //token为空
    TOKEN_IS_EMPTY(-10005, "TOKEN_IS_EMPTY"),
    //id为空
    ID_IS_EMPTY(-10006, "ID_IS_EMPTY"),
    //类型为空
    TYPE_IS_EMPTY(-10007, "TYPE_IS_EMPTY"),
    //个性签名为空
    SIGNATURE_IS_EMPTY(-10008, "SIGNATURE_IS_EMPTY"),
    //查询关键词为空
    SEARCH_IS_EMPTY(-10009, "SEARCH_IS_EMPTY"),
    //查询关键词为空
    CONTENT_IS_EMPTY(-10010, "CONTENT_IS_EMPTY"),
    //查询关键词为空
    GROUP_NAME_IS_EMPTY(-10011, "GROUP_NAME_IS_EMPTY"),
    //查询关键词为空
    GROUP_DESCRIPTION_IS_EMPTY(-10012, "GROUP_DESCRIPTION_IS_EMPTY"),

    //用户登陆, 注册, 安全异常信息 10000+
    //密码错误
    PASSWORD_IS_ERROR(10000, "PASSWORD_IS_ERROR"),
    //登陆生成token错误
    ADD_TOKEN_IS_FAIL(10001, "ADD_TOKEN_IS_FAIL"),
    //获取新token错误
    UPDATE_TOKEN_IS_FAIL(10002, "UPDATE_TOKEN_IS_FAIL"),
    //token过期或失效
    TOKEN_IS_EXPIRED_OR_INVALID(10003, "TOKEN_IS_EXPIRED_OR_INVALID"),
    THE_SAME_ACCOUNT_WAS_LOGGEDIN_IN_OTHER_DEVICE(20002, "用户在其他设备登陆 需回答登陆页面"),
    //清除token错误
    DELETE_TOKEN_IS_FAIL(10004, "DELETE_TOKEN_IS_FAIL"),
    //用户已存在
    USER_IS_EXIST(10005, "USER_IS_EXIST"),
    //注册错误
    REGIST_IS_ERROR(10006, "REGIST_IS_ERROR"),
    //验证码错误
    CODE_IS_ERROR(10007, "CODE_IS_ERROR"),

    //接口通用异常信息 20000+
    //用户不存在
    USER_NOT_EXIST(20000, "USER_NOT_EXIST"),
    //用户未登陆
    USER_NOT_LOGIN(20001, "USER_NOT_LOGIN"),

    //用户个人信息, 个性签名, 好友, 朋友圈动态 30000+
    //信息不属于你
    IT_IS_NOT_YOURS(30000, "IT_IS_NOT_YOURS"),
    //不能添加自己
    DONOT_ADD_YOURSELF(30001, "DONOT_ADD_YOURSELF"),
    //二维码格式错误
    QRCODE_IS_ERROR(30002, "QRCODE_IS_ERROR"),
    //关系已存在
    RELATION_IS_EXIST(30003, "RELATION_IS_EXIST"),
    //关系已存在
    RELATION_REJECT(30004, "RELATION_REJECT"),
    //关系添加错误
    RELATION_ADD_IS_ERROR(30005, "RELATION_ADD_IS_ERROR"),
    //关系不存在
    RELATION_NOT_EXIST(30006, "RELATION_NOT_EXIST"),
    //关系删除错误
    RELATION_DELETE_IS_ERROR(30007, "RELATION_DELETE_IS_ERROR"),
    //关系修改备注错误
    RELATION_MODIFY_REMARK_IS_ERROR(30008, "RELATION_MODIFY_REMARK_IS_ERROR"),
    //关系已存在
    SIGNATURE_IS_ERROR(30009, "SIGNATURE_IS_ERROR"),
    //关系已存在
    SIGNATURE_NOT_EXIST(30010, "SIGNATURE_NOT_EXIST"),
    //关系已存在
    MOMENTS_NOT_EXIST(300011, "MOMENTS_NOT_EXIST"),
    //关系已存在
    MOMENTS_LIKE_IS_ERROR(300012, "MOMENTS_LIKE_IS_ERROR"),
    //关系已存在
    MOMENTS_COMMENT_IS_ERROR(300013, "MOMENTS_COMMENT_IS_ERROR"),

    //消息
    //消息提交错误
    MESSAGE_ADD_IS_ERROR(50000, "MESSAGE_ADD_IS_ERROR"),
    //消息不存在
    MESSAGE_NOT_EXIST(50001, "MESSAGE_NOT_EXIST"),
    //消息过期
    MESSAGE_IS_EXPIRED(50002, "MESSAGE_IS_EXPIRED"),
    //消息通过错误
    MESSAGE_PASS_IS_ERROR(50003, "MESSAGE_PASS_IS_ERROR"),
    //消息状态错误
    MESSAGE_STATUS_IS_ERROR(50004, "MESSAGE_STATUS_IS_ERROR"),
    //消息不通过错误
    MESSAGE_NOT_PASS_IS_ERROR(50005, "RELATION_REJECT_IS_ERROR"),

    //群组
    //新建群组错误
    GROUP_ADD_IS_ERROR(40001, "GROUP_ADD_IS_ERROR"),
    //群组不存在
    GROUP_NOT_EXIST(40001, "GROUP_NOT_EXIST"),
    //无群组权限
    NOT_AUTHORIZATION(40002, "NOT_AUTHORIZATION"),
    //群组修改信息错误
    GROUP_MODIFY_IS_ERROR(40003, "GROUP_MODIFY_IS_ERROR"),
    //群组成员不存在
    GROUP_USER_NOT_EXIST(40004, "GROUP_USER_NOT_EXIST"),
    //群组成员添加错误
    GROUP_USER_ADD_ERROR(40005, "GROUP_USER_ADD_ERROR"),
    //群组管理员关系存在
    GROUP_ADMIN_IS_EXIST(40006, "GROUP_ADMIN_IS_EXIST"),
    //群组管理员关系添加错误
    GROUP_ADMIN_ADD_ERROR(40007, "GROUP_ADMIN_ADD_ERROR"),
    //群组管理员关系不存在
    GROUP_ADMIN_NOT_EXIST(40008, "GROUP_ADMIN_NOT_EXIST"),
    //群组管理员关系删除错误
    GROUP_ADMIN_DELETE_ERROR(40009, "GROUP_ADMIN_DELETE_ERROR"),
    //群组转让错误
    GROUP_TRANSFER_ERROR(40010, "GROUP_TRANSFER_ERROR"),
    //群组删除错误
    GROUP_DELETE_ERROR(40011, "GROUP_DELETE_ERROR"),
    //拒绝接收群组信息修改错误
    GROUP_ADD_BLOCKS_ERROR(40012, "GROUP_ADD_BLOCKS_ERROR"),
    //接收群组信息修改错误
    GROUP_DELETE_BLOCKS_ERROR(40013, "GROUP_DELETE_BLOCKS_ERROR"),
    //群组成员已存在
    GROUP_USER_IS_EXIST(40014, "GROUP_USER_IS_EXIST"),
    //拒绝接收群组信息已开启
    GROUP_BLOCKS_IS_EXIST(40015, "GROUP_USER_IS_EXIST"),
    //接收群组信息已开启
    GROUP_BLOCKS_NOT_EXIST(40016, "GROUP_BLOCKS_NOT_EXIST"),
    //群组禁言状态不存在
    GROUP_MUTE_NOT_EXIST(40017, "GROUP_MUTE_NOT_EXIST"),
    //群组禁言状态添加错误
    GROUP_MUTE_ADD_ERROR(40018, "GROUP_MUTE_ADD_ERROR"),
    //群组禁言状态存在
    GROUP_MUTE_IS_EXIST(40019, "GROUP_MUTE_IS_EXIST"),
    //群组禁言状态删除错误
    GROUP_MUTE_DELETE_ERROR(40020, "GROUP_MUTE_DELETE_ERROR"),
    //群组无权限可删除用户
    GROUP_NOT_USER_DELETE(40021, "GROUP_NOT_USER_DELETE"),
    //群组删除用户错误
    GROUP_USER_DELETE_ERROR(40022, "GROUP_USER_DELETE_ERROR");

    private int code;
    private String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
