package com.java110.core.cmd;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.CmdListenerDto;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdExecuteException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DataFlowListenerOrderComparator;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 数据流 事件发布
 * Created by wuxw on 2018/4/17.
 */
public class ServiceCmdEventPublishing {
    private static Logger logger = LoggerFactory.getLogger(ServiceCmdEventPublishing.class);

    private static Executor taskExecutor;

    //默认 线程数 100
    private static final int DEFAULT_THREAD_NUM = 100;

    /**
     * 保存侦听实例信息，一般启动时加载
     */
    private static final List<CmdListenerDto> listeners = new ArrayList<CmdListenerDto>();

    /**
     * 根据 事件类型查询侦听
     */
    private static final Map<String, List<ServiceCmdListener>> cacheListenersMap = new HashMap<String, List<ServiceCmdListener>>();

    /**
     * 添加 侦听，这个只有启动时，单线程 处理，所以是线程安全的
     *
     * @param listener
     */
    public static void addListener(CmdListenerDto listener) {
        listeners.add(listener);
    }

    /**
     * 获取侦听（全部侦听）
     *
     * @return
     */
    public static List<CmdListenerDto> getListeners() {
        return listeners;
    }

    /**
     * 根据是否实现了某个接口，返回侦听
     *
     * @param serviceCode
     * @return
     * @since 1.8
     */
    public static List<ServiceCmdListener> getListeners(String serviceCode) {

        Assert.hasLength(serviceCode, "获取需要发布的事件处理侦听时，传递事件为空，请检查");

        String needCachedServiceCode = serviceCode;
        //先从缓存中获取，为了提升效率
        if (cacheListenersMap.containsKey(needCachedServiceCode)) {
            return cacheListenersMap.get(needCachedServiceCode);
        }

        List<ServiceCmdListener> cmdListeners = new ArrayList<ServiceCmdListener>();
        for (CmdListenerDto listenerBean : getListeners()) {
            //ServiceCmdListener listener = ApplicationContextFactory.getBean(listenerBean.getBeanName(), ServiceCmdListener.class);
            ServiceCmdListener listener = ApplicationContextFactory.getBean(listenerBean.getBeanName(), ServiceCmdListener.class);
            if (listenerBean.getServiceCode().equals(serviceCode)) {
                cmdListeners.add(listener);
            }
        }

        //这里排序
        DataFlowListenerOrderComparator.sort(cmdListeners);


        //将数据放入缓存中
        if (cmdListeners.size() > 0) {
            cacheListenersMap.put(needCachedServiceCode, cmdListeners);
        }
        return cmdListeners;
    }


    /**
     * 发布事件
     *
     * @param cmdDataFlowContext
     */
    public static void multicastEvent(ICmdDataFlowContext cmdDataFlowContext) throws Exception {
        Assert.notNull(cmdDataFlowContext.getServiceCode(), "当前没有可处理的业务信息！");
        multicastEvent(cmdDataFlowContext.getServiceCode(), cmdDataFlowContext, null);
    }


    /**
     * 发布事件
     *
     * @param serviceCode
     * @param dataFlowContext
     */
    public static void multicastEvent(String serviceCode, ICmdDataFlowContext dataFlowContext) throws Exception {
        multicastEvent(serviceCode, dataFlowContext, null);
    }

    /**
     * 发布事件
     *
     * @param serviceCode
     * @param dataFlowContext 这个订单信息，以便于 侦听那边需要用
     */
    public static void multicastEvent(String serviceCode, ICmdDataFlowContext dataFlowContext, String asyn) throws Exception {
        try {
            CmdEvent targetDataFlowEvent = new CmdEvent(serviceCode, dataFlowContext);

            multicastEvent(serviceCode, targetDataFlowEvent, asyn);
        } catch (Exception e) {
            logger.error("发布侦听失败，失败原因为：", e);
            //throw new BusinessException(ResponseConstant.RESULT_CODE_INNER_ERROR, e.getMessage());
            throw e;
        }

    }


    /**
     * 发布事件
     *
     * @param event
     * @param asyn  A 表示异步处理
     */
    public static void multicastEvent(String serviceCode, final CmdEvent event, String asyn) throws Exception {
        List<ServiceCmdListener> listeners = getListeners(serviceCode);
        //这里判断 serviceCode + httpMethod 的侦听，如果没有注册直接报错。
        if (listeners == null || listeners.size() == 0) {
            throw new CmdExecuteException(ResultVo.CODE_ERROR,
                    "服务【" + serviceCode + "】当前不支持");
        }
        for (final ServiceCmdListener listener : listeners) {
                invokeListener(listener, event);
        }
    }


    /**
     * Return the current task executor for this multicaster.
     */
    protected static synchronized Executor getTaskExecutor() {
        if (taskExecutor == null) {
            taskExecutor = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
        }
        return taskExecutor;
    }

    /**
     * Invoke the given listener with the given event.
     *
     * @param listener the ApplicationListener to invoke
     * @param event    the current event to propagate
     * @since 4.1
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static void invokeListener(ServiceCmdListener listener, CmdEvent event) throws Exception {
        try {
            //        //这里处理业务逻辑数据
            ICmdDataFlowContext dataFlowContext = event.getCmdDataFlowContext();
            //获取请求数据
            JSONObject reqJson = dataFlowContext.getReqJson();

            logger.debug("API服务 --- 请求参数为：{}", reqJson.toJSONString());

            listener.validate(event, dataFlowContext, reqJson);

            listener.doCmd(event, dataFlowContext, reqJson);

            //logger.debug("API服务 --- 返回报文信息：{}", dataFlowContext.getResponseEntity());
            //   listener.cmd(event);
        } catch (Throwable e) {
            logger.error("发布侦听失败" + e);
            throw e;
        }
    }


}
