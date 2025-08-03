package com.ly.cloud.backup.common.constant;

/**
 * 通用正则表达式
 * @author jiangzhongxin
 */
public class RegexpConstant {

    /**
     * 可空标记
     */
    public static final String NULLFLAG = "^$|";

    /**
     * 手机正则表达式
     */
    public static final String MOBIL_EREGEXP = "^(13|14|15|16|17|18|19)\\d{9}$";

    /**
     * 邮箱正则表达式
     */
    public static final String EMAIL_EREGEXP = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

    /**
     * 身份证正则表达式
     */
    public static final String ID_CARD_EREGEXP = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";

    /**
     * 固话正则表达式
     */
    public static final String TELEPHONE_EREGEXP = "^(0\\d{2,3}-)?\\d{7,8}(-\\d{3,4})?$";

    /**
     * 网站正则表达式
     */
    public static final String IPV4_EREGEXP = "(?:(?:1[0-9][0-9]\\.)|(?:2[0-4][0-9]\\.)|(?:25[0-5]\\.)|(?:[1-9][0-9]\\.)|(?:[0-9]\\.)){3}(?:(?:1[0-9][0-9])|(?:2[0-4][0-9])|(?:25[0-5])|(?:[1-9][0-9])|(?:[0-9]))";

    /**
     * 网站正则表达式
     */
    public static final String URL_EREGEXP = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

    /**
     * 6位短信验证码
     */
    public static final String SIXNUMBER_EREGEXP = "^\\d{6}$";

    /**
     * 4位短信验证码
     */
    public static final String FOURNUMBER_EREGEXP = "^\\d{4}$";

    /**
     * 验证数字
     */
    public static final String NUMBER_EREGEXP = "^\\d{1,}$";

    /**
     * 年龄
     */
    public static final String AGE_EREGEXP = "^[0-9]{1,2}$";

    /**
     * 密码(6-12位字母或数字)正则表达式
     */
    public static final String PASSWORD_OR_EREGEXP = "^[0-9A-Za-z]{6,12}$";

    /**
     * 密码(6-12位字母和数字)正则表达式
     */
    public static final String PASSWORD_AND_EREGEXP = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$";

    /**
     * 中文姓名正则表达式
     */
    public static final String CHINESE_NAME_EREGEXP = "^[\u4e00-\u9fa5]+(\\·[\u4e00-\u9fa5]+)*$";

    /**
     * 金额正则表达式 正整数，不能为小数或者负数
     */
    public static final String MONEY_EREGEXP = "^([1-9]\\d*)*$";

    /**
     * 只能输入数字 0 或 1
     **/
    public static final String ZERO_OR_ONE_EREGEXP = "^[0-1]{1}$";

    /**
     * 正整数
     */
    public static final String POSITIVE_NUMBER = "^[0-9]*[1-9][0-9]*$";

    /**
     * 年月日日期模式
     */
    public static final String SIMPLE_DATE_PATTERN = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$";

    /**
     * 年月日日期模式无-
     */
    public static final String SIMPLE_DATE_PATTERN_SIMPLE = "^[1-9]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])$";

    /**
     * 日期格式验证
     */
    public static final String DATE_FORMATE_TEMPLATE1_EREGEXP = "^[1-2][0-9][0-9][0-9]-([1][0-2]|0?[1-9])-([12][0-9]|3[01]|0?[1-9]) ([01][0-9]|[2][0-3]):[0-5][0-9]$";

    /**
     * 邮编格式验证
     */
    public static final String POSTCODE_EREGEXP = "^[0-9]{6}$";

    /**
     * nginx默认格式 [Grok]
     */
    public static final String NGINX_DEFAULT_FORMAT_EREGEXP = "%{IPV4:remote_addr} - (%{USERNAME:remote_user}|-) \\[%{HTTPDATE:time_local}\\] \\\"%{WORD:request_method} %{URIPATHPARAM:request_uri} HTTP/%{NUMBER:http_protocol}\\\" %{NUMBER:http_status} %{NUMBER:body_bytes_sent} \\\"%{GREEDYDATA:http_referer}\\\" \\\"%{GREEDYDATA:http_user_agent}\\\"";

}
