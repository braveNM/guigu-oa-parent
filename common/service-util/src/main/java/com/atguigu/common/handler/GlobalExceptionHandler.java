package com.atguigu.common.handler;

import com.atguigu.common.execption.GuiguException;
import com.atguigu.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice//通过AOP实现
public class GlobalExceptionHandler {

    //全局异常处理，执行的方法
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail().message("全局异常执行处理");
    }

    //特定异常处理，执行的方法
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result error(ArithmeticException e) {
        e.printStackTrace();
        return Result.fail().message("ArithmeticException异常执行处理");
    }

    //自定义异常处理，执行的方法
    @ExceptionHandler(GuiguException.class)
    @ResponseBody
    public Result error(GuiguException e) {
        e.printStackTrace();
        return Result.fail().code(e.getCode()).message(e.getMsg());

    }
}
