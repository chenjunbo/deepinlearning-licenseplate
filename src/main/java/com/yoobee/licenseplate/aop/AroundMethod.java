package com.yoobee.licenseplate.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.yoobee.licenseplate.annotation.RetExclude;
import com.yoobee.licenseplate.entity.Result;
import lombok.extern.slf4j.Slf4j;

/**
 * method环绕处理
 *
 * @author jackiechan
 */
@Slf4j
public class AroundMethod implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object ret;
        ret = methodInvocation.proceed();
        // 防止出现二次封装
        if (ret instanceof Result) {
            return ret;
        }
        RetExclude re = methodInvocation.getMethod().getAnnotation(RetExclude.class);
        if (null != re) {
            log.info("api添加了封装排除注解");
            return ret;
        }
        return Result.ok(ret);
    }
}
