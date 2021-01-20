package org.chen.constant;

import java.util.HashMap;

/**
 * 
 *  
 * @author YuChen
 * @date 2020/8/10 11:06
 **/
 
public class BusinessConstant {

    public static final String APP_ID = "2131";
    public static final String APP_SECRET = "1231";
    public static final String JSCODE2SESSION = "https://api.weixin.qq.com/sns/jscode2session";
    public static final String GET_ACCESSTOKEN = "https://api.weixin.qq.com/cgi-bin/token";
    public static final String GET_ACCESSTOKEN_WITH_PARAM
            = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APP_ID+"&secret="+APP_SECRET;
    public static final String SUBSCRIBE_MESSAGE_SEND = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";

    public static final String JOB_CODE_PREFIX = "J";
    public static final String JOB_CODE_PREFIX_CRON = "JC";
    public static final String JOB_CODE_PREFIX_EXTRA = "JE";

    public static class JOB_USER_STATE {
        public static final int TO_CHECK = 0;
        public static final int TO_DO = 1;
        public static final int EXPIRED = 2;
        public static final int REFUSED = 3;
        public static final int FINISHED = 4;
    }

    public static class INVITE_STATE {
        public static final int WAIT_TO_CHECK = 0;
        public static final int ACCEPTED = 1;
        public static final int REFUSED = 2;
        public static final int QUITE = 3;
    }


    public static class USER_ROLE {
        public static final int NO_FAMILY = 0;
        public static final int OWNER = 1;
        public static final int MEMBER = 2;
        public static final int IS_INVITING = 3;

    }

    public static class CREATE_TYPE {
        public static final int USER = 0;
        public static final int CRON = 1;
        public static final int EXTRA = 2;
        public static final HashMap<Integer,String> MAP = new HashMap<>();
        static{
            MAP.put(0,"指派");
            MAP.put(1,"定时");
            MAP.put(2,"额外");
        }
    }

    public static class CRON_STATE {
        public static final int UNABLE = 0;
        public static final int ENABLE = 1;
        public static final int DELETED = 2;
        public static final HashMap<Integer,String> MAP = new HashMap<>();
        static{
            MAP.put(0,"未启用");
            MAP.put(1,"启用中");
            MAP.put(2,"已删除");
        }
    }

    public static class CRON_TYPE {
        public static final int WEEK = 0;
        public static final int DAY = 1;
        public static final int MONTH = 2;
        public static final HashMap<Integer,String> MAP = new HashMap<>();
        static{
            MAP.put(0,"每周");
            MAP.put(1,"每日");
            MAP.put(2,"每月");
        }
    }

    public static class POINT_RECORD_TYPE {
        public static final int ZHICHU_ZHIPAI = 0;
        public static final int SHOURU_ZHIPAI = 1;
        public static final int SHOURU_EWAI = 2;
        public static final int SHOURU_DINGSHI = 3;
        public static final int SHOURU_DINGSHIHUIFU = 4;
    }

    public static class FAMILY_OWNER {
        public static final int NO_FAMILY = 0;
        public static final int OWNER = 1;
        public static final int MEMBER = 2;
        public static final int IS_INVITING = 3;
        public static final HashMap<Integer,String> MAP = new HashMap<>();
        static{
            MAP.put(0,"未加入");
            MAP.put(1,"家长");
            MAP.put(2,"成员");
            MAP.put(3,"邀请中");
        }
    }

    public static class WEIXIN_TEMPLATE {
        // 每日通知
        public static final String MEIRI_TONGZHI = "54353";
    }

    public static class STATISTICS_TIME {
        public static final int WEEK = 1;
        public static final int MONTH = 2;
        public static final int ALL = 3;
        public static final HashMap<Integer,String> MAP = new HashMap<>();
        static{
            MAP.put(1,"本周");
            MAP.put(2,"本月");
            MAP.put(3,"全部");
        }
    }

    public static class STATISTICS_TYPE {
        public static final int NUM = 1;
        public static final int POINT = 2;
        public static final int ITEM = 3;
        public static final HashMap<Integer,String> MAP = new HashMap<>();
        static{
            MAP.put(1,"数量");
            MAP.put(2,"家务点");
            MAP.put(3,"项目");
        }
    }

}
