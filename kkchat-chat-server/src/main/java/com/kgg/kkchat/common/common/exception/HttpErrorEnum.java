package com.kgg.kkchat.common.common.exception;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONUtil;
import com.kgg.kkchat.common.common.domain.vo.resp.ApiResult;
import com.kgg.kkchat.common.common.utils.JsonUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.io.Charsets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/6
 */
@AllArgsConstructor
public enum HttpErrorEnum {
    ACCESS_DENIED(401,"登录失效，请重新登陆");

    private Integer httpCode;
    private String desc;

    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(httpCode);
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        response.getWriter().write(JsonUtils.toStr(ApiResult.fail(httpCode,desc)));
    }
}
