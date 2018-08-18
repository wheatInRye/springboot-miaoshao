package com.fq.exception;

import com.fq.result.CodeMsg;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/8 11:55
 * @Description:     自定义全局异常
 */
public class GlobalException extends RuntimeException{
    static final long serialVersionUID = 1L;
    CodeMsg msg = null;

    public GlobalException(CodeMsg msg){
        super(msg.toString());
        this.msg = msg;
    }

    public CodeMsg getMsg() {
        return msg;
    }
}
