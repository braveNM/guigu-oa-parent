package com.atguigu.common.result;

import com.sun.org.apache.regexp.internal.RE;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;//操作的状态码
    private String message;//返回信息
    private T data;//具体的数据

    //构造私有化  这个类不可以new了
    private Result(){

    }

    //封装返回数据
    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        //封装数据
        if (body != null){
            //有数据
            result.setData(body);
        }
        //设置状态码
        result.setCode(resultCodeEnum.getCode());
        //设置返回信息
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }
    //返回成功
    public static<T> Result<T> ok(){
        return build(null,ResultCodeEnum.SUCCESS);
    }
    public static<T> Result<T> ok(T data){
        return build(data,ResultCodeEnum.SUCCESS);
    }
    //返回失败
    public static<T> Result<T> fail(){
        return build(null,ResultCodeEnum.FAIL);
    }
    public static<T> Result<T> fail(T data){
        return build(data,ResultCodeEnum.FAIL);
    }

    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }


}
