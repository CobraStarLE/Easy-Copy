package com.cyser.test;

import lombok.Data;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @author zjxy
 */
@Data
public class R<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 成功 */
    public static final int SUCCESS = 0;

    /** 失败 */
    public static final int FAIL = -1;


    /**
     * 返回码
     */
    private int code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    public static <T> R<T> ok()
    {
        return of(null, SUCCESS, null);
    }

    public static <T> R<T> ok(T data)
    {
        return of(data, SUCCESS, null);
    }

    public static <T> R<T> ok(T data, String msg)
    {
        return of(data, SUCCESS, msg);
    }

    public static <T> R<T> fail()
    {
        return of(null, FAIL, null);
    }

    public static <T> R<T> fail(String msg)
    {
        return of(null, FAIL, msg);
    }

    public static <T> R<T> fail(T data)
    {
        return of(data, FAIL, null);
    }

    public static <T> R<T> fail(T data, String msg)
    {
        return of(data, FAIL, msg);
    }

    public static <T> R<T> fail(int code, String msg)
    {
        return of(null, code, msg);
    }

    public static <T> R<T> of(T data, int code, String msg)
    {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public static <T> Boolean isError(R<T> ret)
    {
        return !isSuccess(ret);
    }

    public static <T> Boolean isSuccess(R<T> ret)
    {
        return R.SUCCESS == ret.getCode();
    }

    public Boolean isSuccess()
    {
        return this.code==R.SUCCESS;
    }

        /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static R warn(String msg)
    {
        return R.warn(msg, null);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static R warn(String msg, Object data)
    {
        return  R.of(data,2, msg);
    }

    /**
     * 返回错误消息
     *
     * @return 错误消息
     */
    public static R error()
    {
        return R.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 错误消息
     */
    public static R error(String msg)
    {
        return R.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 错误消息
     */
    public static R error(String msg, Object data)
    {
        return R.of(data,3, msg);
    }

    public static R error(int code,String msg)
    {
        return R.error(code,msg,null);
    }

    public static R error(int code,String msg, Object data)
    {
        return R.of(data,3, msg);
    }
}
