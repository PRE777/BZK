package com.iwhere.gridgeneration.utils.base.dto;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.iwhere.gridgeneration.comm.enums.ProcessFlagEnum;


/**
 * ---------------------------------------------------------------------------
 * 基础返回类型
 * ---------------------------------------------------------------------------
 * <strong>copyright</strong>： ©版权所有 成都都在哪网讯科技有限公司<br>
 * ----------------------------------------------------------------------------
 * @author: hewei
 * @time:2017/7/23 9:53
 * ---------------------------------------------------------------------------
 */
public class BaseResp {
    public static final Integer SERVER_SUCCESS = 200;   // 正常
    public static final Integer SERVER_ERROR = 500; // 通用异常

    private Integer server_status;  // 响应码
    private String server_error; // 异常返回信息

    /**
     * 构造函数
     */
    public BaseResp() {
        this.server_status = SERVER_SUCCESS;
        this.server_error = null;
    }

    /**
     * 成功状态返回（process = 1）
     * @return
     */
    public static BaseResp success() {
        return process(ProcessFlagEnum.SUCCESS);
    }

    /**
     * 失败状态返回（process = 0）
     * @return
     */
    public static BaseResp fail() {
        return process(ProcessFlagEnum.FAIL);
    }

    /**
     * 状态返回
     * @param process 状态
     * @return
     */
    public static BaseResp process(ProcessFlagEnum process) {
        return new ProcessResp(process);
    }

    public static BaseResp process(boolean process) {
        return new ProcessResp(process ? ProcessFlagEnum.SUCCESS : ProcessFlagEnum.FAIL);
    }

    /**
     * 异常返回
     * @return
     */
    public static BaseResp error() {
        return error(SERVER_ERROR, "通用异常！");
    }

    /**
     * 异常返回
     * @param error 异常信息
     * @return
     */
    public static BaseResp error(String error) {
        return error(SERVER_ERROR, error);
    }

    /**
     * 异常返回
     * @param status 异常编码
     * @param error  异常描述
     * @return
     */
    public static BaseResp error(Integer status, String error) {
        BaseResp resp = process(ProcessFlagEnum.FAIL);
        resp.setServer_status(status);
        resp.setServer_error(error);
        return resp;
    }

    /**
     * 获取构建工具
     * @return
     */
    public static BaseRespMap map() {
        return new BaseRespMap();
    }

    /**
     * Getter method for property <tt>server_status</tt>.
     * @return property value of server_status
     * @author hewei
     */
    public Integer getServer_status() {
        return server_status;
    }

    /**
     * Setter method for property <tt>server_status</tt>.
     * @param server_status value to be assigned to property server_status
     * @author hewei
     */
    public void setServer_status(Integer server_status) {
        this.server_status = server_status;
    }

    /**
     * Getter method for property <tt>server_error</tt>.
     * @return property value of server_error
     * @author hewei
     */
    public String getServer_error() {
        return server_error;
    }

    /**
     * Setter method for property <tt>server_error</tt>.
     * @param server_error value to be assigned to property server_error
     * @author hewei
     */
    public void setServer_error(String server_error) {
        this.server_error = server_error;
    }

    /**
     * 状态返回
     */
    public static class ProcessResp extends BaseResp {
        // 状态 success:00 fail:01
        private String process;

        /**
         * 构造函数
         * @param process
         */
        public ProcessResp(ProcessFlagEnum process) {
            this.process = process.value();
        }

        /**
         * Getter method for property <tt>process</tt>.
         * @return property value of process
         * @author hewei
         */
        public String getProcess() {
            return process;
        }

        /**
         * Setter method for property <tt>process</tt>.
         * @param process value to be assigned to property process
         * @author hewei
         */
        public void setProcess(String process) {
            this.process = process;
        }
    }

    /**
     * 构建工具
     */
    public static class BaseRespMap extends BaseResp implements Map<String, Object> {
        private Map<String, Object> map;    // map

        /**
         * 构造函数
         */
        public BaseRespMap() {
            super();
            // 初始化
            map = new LinkedHashMap<>();
            setServer_status(super.server_status);
            setServer_error(super.server_error);
        }

        /**
         * 附加值
         * @param key
         * @param value
         * @return
         */
        public BaseRespMap append(String key, Object value) {
            map.put(key, value);
            return this;
        }

        // ================================================ BaseResp 方法重写 ====================================

        @Override
        public void setServer_status(Integer server_status) {
            super.setServer_status(server_status);
            append("server_status", server_status);
        }

        @Override
        public void setServer_error(String server_error) {
            super.setServer_error(server_error);
            append("server_error", server_error);
        }

        // ================================================ map 接口 ====================================
        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return map.get(key);
        }

        @Override
        public Object put(String key, Object value) {
            return map.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            return map.remove(key);
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {
            map.putAll(m);
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public Set<String> keySet() {
            return map.keySet();
        }

        @Override
        public Collection<Object> values() {
            return map.values();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return map.entrySet();
        }
    }
}
