package com.tg.tgt.http.model;

import com.tg.tgt.http.BaseHttpResult;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author yiyang
 */
public class NewsResult extends BaseHttpResult {


    private static final long serialVersionUID = 9079821123181184864L;
    /**
     * allp : 1
     * more : [{"id":"7","title":"麦田町游戏上线啦！","descripe":"2017年5月16日麦田圈系统正式上线啦！邀请你的朋友和家人一起来玩","pic":"http://live
     * .qevedkc.net/time/public/upload/dc24ff4211891088e35136017486d721.jpg","remark":" <p>     <span
     * style=\"font-family: 微软雅黑; line-height: 28px; text-indent: 30px; white-space: normal; color: rgb(0, 0, 0);
     * background-color: rgb(251, 251, 251);
     * \">有的人长时间不运动，突然去跑步可能会出现岔气的情况，这种情况是很危险的，身体会感到明显的不适，比如呼吸特别困难，为了预防和解决这种突发情况，大家要懂得下面这些措施。<\/span><\/p>         <p>
     *     <br/><\/p>             <p style=\"margin-top: 0px; margin-bottom: 20px; padding: 0px; font-family: 微软雅黑;
     *     line-height: 28px; text-indent: 30px; color: rgb(51, 51, 51); white-space: normal; background: rgb(251,
     *     251, 251);\">                 <span style=\"color: rgb(0, 0, 0);\">                     <strong
     *     style=\"font-size: 18px; color: rgb(42, 165, 59); line-height: 30px; padding-bottom: 20px;\">1、<\/strong>
     *         <span style=\"color: rgb(0, 0, 0); font-family: 微软雅黑; line-height: 28px; text-indent: 30px;
     *         white-space: normal; background-color: rgb(251, 251, 251);\">停下来慢慢的走五六百米<\/span><\/span><\/p>
     *             <p style=\"margin-top: 0px; margin-bottom: 20px; padding: 0px; font-family: 微软雅黑; line-height:
     *             28px; text-indent: 30px; color: rgb(51, 51, 51); white-space: normal; background: rgb(251, 251,
     *             251);\">                                 <span style=\"font-family: 微软雅黑; line-height: 28px;
     *             text-indent: 30px; white-space: normal; color: rgb(0, 0, 0); background-color: rgb(251, 251, 251);
     *             \">跑到中途发生岔气，可以停下来慢慢的走，这是最快也是最有效的缓解岔气症状的方式，一般走个五六百米岔气症状就能消失。<\/span><\/p>
     *                 <p>                                         <br/><\/p>
     *                     <p>                                             <br/><\/p>
     *                         <p>                                                 <br/><\/p>
     *                             <p>                                                     <br/><\/p>
     *                                 <p style=\"margin-top: 0px; margin-bottom: 20px; padding: 0px; font-family:
     *                                 微软雅黑; line-height: 28px; text-indent: 30px; color: rgb(51, 51, 51);
     *                                 white-space: normal; background: rgb(251, 251, 251);\">
     *                                 <span style=\"font-family: 微软雅黑; line-height: 28px; text-indent: 30px;
     *                                 white-space: normal; color: rgb(0, 0, 0); background-color: rgb(251, 251, 251)
     *                                 ;\">2、停下来并用手按揉岔气部位<\/span><\/p>
     *                                     <p style=\"margin-top: 0px; margin-bottom: 20px; padding: 0px;
     *                                     font-family: 微软雅黑; line-height: 28px; text-indent: 30px; color: rgb(51,
     *                                     51, 51); white-space: normal; background: rgb(251, 251, 251);\">
     *                                     <span style=\"font-family: 微软雅黑; line-height: 28px; text-indent: 30px;
     *                                     white-space: normal; color: rgb(0, 0, 0); background-color: rgb(251, 251,
     *                                     251);
     *                                     \">如果在跑步时岔气了，建议不要再跑了，应立即停止跑步并把手放在岔气部位，随着呼吸的频率揉搓。对岔气的部位进行按压按摩，并使身体尽量前倾使得横膈膜能够被尽量拉伸，这有助于缓解疼痛。<\/span><\/p>                                                                     <p>                                                                         <span style=\"font-family: 微软雅黑; line-height: 28px; text-indent: 30px; white-space: normal; color: rgb(255, 255, 255); background-color: rgb(251, 251, 251);\">                                                                             <br/><\/span><\/p>","addtime":"2017-05-14"}]
     * total : 1
     */

    private int allp;
    private String total;
    private List<MoreBean> more;

    public int getAllp() {
        return allp;
    }

    public void setAllp(int allp) {
        this.allp = allp;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<MoreBean> getMore() {
        return more;
    }

    public void setMore(List<MoreBean> more) {
        this.more = more;
    }

    public static class MoreBean implements Serializable{
        private static final long serialVersionUID = -3769150957494373447L;
        /**
         * id : 7
         * title : 麦田町游戏上线啦！
         * descripe : 2017年5月16日麦田圈系统正式上线啦！邀请你的朋友和家人一起来玩
         * pic : http://live.qevedkc.net/time/public/upload/dc24ff4211891088e35136017486d721.jpg
         * remark :  <p>     <span style="font-family: 微软雅黑; line-height: 28px; text-indent: 30px; white-space: normal;
         * color: rgb(0, 0, 0); background-color: rgb(251, 251, 251);
         * ">有的人长时间不运动，突然去跑步可能会出现岔气的情况，这种情况是很危险的，身体会感到明显的不适，比如呼吸特别困难，为了预防和解决这种突发情况，大家要懂得下面这些措施。</span></p>
         * <p>             <br/></p>             <p style="margin-top: 0px; margin-bottom: 20px; padding: 0px;
         * font-family: 微软雅黑; line-height: 28px; text-indent: 30px; color: rgb(51, 51, 51); white-space: normal;
         * background: rgb(251, 251, 251);">                 <span style="color: rgb(0, 0, 0);">
         *     <strong style="font-size: 18px; color: rgb(42, 165, 59); line-height: 30px; padding-bottom: 20px;
         *     ">1、</strong>                         <span style="color: rgb(0, 0, 0); font-family: 微软雅黑;
         *     line-height: 28px; text-indent: 30px; white-space: normal; background-color: rgb(251, 251, 251);
         *     ">停下来慢慢的走五六百米</span></span></p>                             <p style="margin-top: 0px; margin-bottom:
         *     20px; padding: 0px; font-family: 微软雅黑; line-height: 28px; text-indent: 30px; color: rgb(51, 51, 51);
         *     white-space: normal; background: rgb(251, 251, 251);">                                 <span
         *     style="font-family: 微软雅黑; line-height: 28px; text-indent: 30px; white-space: normal; color: rgb(0, 0,
         *     0); background-color: rgb(251, 251, 251);
         *     ">跑到中途发生岔气，可以停下来慢慢的走，这是最快也是最有效的缓解岔气症状的方式，一般走个五六百米岔气症状就能消失。</span></p>
         *     <p>                                         <br/></p>                                         <p>
         *         <br/>
         *     </p>                                             <p>
         *         <br/>
         *     </p>                                                 <p>
         *         <br/>
         *     </p>                                                     <p style="margin-top: 0px; margin-bottom:
         *     20px; padding: 0px; font-family: 微软雅黑; line-height: 28px; text-indent: 30px; color: rgb(51, 51, 51);
         *     white-space: normal; background: rgb(251, 251, 251);">
         *     <span style="font-family: 微软雅黑; line-height: 28px; text-indent: 30px; white-space: normal; color: rgb
         *     (0, 0, 0); background-color: rgb(251, 251, 251);">2、停下来并用手按揉岔气部位</span></p>
         *     <p style="margin-top: 0px; margin-bottom: 20px; padding: 0px; font-family: 微软雅黑; line-height: 28px;
         *     text-indent: 30px; color: rgb(51, 51, 51); white-space: normal; background: rgb(251, 251, 251);">
         *     <span style="font-family: 微软雅黑; line-height: 28px; text-indent: 30px; white-space: normal; color: rgb
         *     (0, 0, 0); background-color: rgb(251, 251, 251);
         *     ">如果在跑步时岔气了，建议不要再跑了，应立即停止跑步并把手放在岔气部位，随着呼吸的频率揉搓。对岔气的部位进行按压按摩，并使身体尽量前倾使得横膈膜能够被尽量拉伸，这有助于缓解疼痛。</span></p>
         *     <p>                                                                         <span style="font-family:
         *     微软雅黑; line-height: 28px; text-indent: 30px; white-space: normal; color: rgb(255, 255, 255);
         *     background-color: rgb(251, 251, 251);">
         *     <br/></span></p>
         * addtime : 2017-05-14
         */

        private String id;
        private String title;
        private String descripe;
        private String pic;
        private String con;
        private String addtime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescripe() {
            return descripe;
        }

        public void setDescripe(String descripe) {
            this.descripe = descripe;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getCon() {
            return con;
        }

        public void setCon(String con) {
            this.con = con;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }
    }
}
