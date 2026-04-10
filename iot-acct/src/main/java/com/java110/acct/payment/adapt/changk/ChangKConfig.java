package com.java110.acct.payment.adapt.changk;

public class ChangKConfig {

    public static final String BASE_URL = "https://trans.changkuaikeji.cn";

    public static final String BARCODE_PAY_URL = BASE_URL + "/pay/110/barcodepay";

    public static final String QR_PAY_URL = BASE_URL + "/pay/110/qrpay";

    public static final String QUERY_URL = BASE_URL + "/pay/110/query";

    public static final String REFUND_URL = BASE_URL + "/pay/110/refund";

    public static final String REFUND_QUERY_URL = BASE_URL + "/pay/110/refundQuery";

    public static final String CANCEL_URL = BASE_URL + "/pay/110/cancel";

    public static final String JSP_PAY_URL = BASE_URL + "/pay/110/jspay";

    public static final String MINI_PAY_URL = BASE_URL + "/pay/110/minipay";

    public static final String CLOSE_URL = BASE_URL + "/pay/110/close";

    public static final String PAY_TYPE_WECHAT = "010";
    public static final String PAY_TYPE_ALIPAY = "020";
    public static final String PAY_TYPE_CASH = "040";
    public static final String PAY_TYPE_QQ = "060";
    public static final String PAY_TYPE_JD = "080";
    public static final String PAY_TYPE_KOUBEI = "090";
    public static final String PAY_TYPE_BEST = "100";
    public static final String PAY_TYPE_UNIONPAY = "110";
    public static final String PAY_TYPE_HEBAO = "140";
    public static final String PAY_TYPE_AUTO = "010";

    public static final String RESULT_CODE_SUCCESS = "01";
    public static final String RESULT_CODE_FAIL = "02";

    public static final String RETURN_CODE_SUCCESS = "01";
    public static final String RETURN_CODE_FAIL = "02";
}
