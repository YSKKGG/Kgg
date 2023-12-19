package com.kgg.kkchat.common.websocket.domain.vo.resp;

/**
 * Description:
 * Author: Kgg
 * Date:2023-12-20
 */
public class WSBaseResp<T> {
    /**
     * @see com.kgg.kkchat.common.websocket.domain.enums.WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
