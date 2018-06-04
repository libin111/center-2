package com.tlong.center.api.web;

import com.tlong.center.api.dto.common.PageAndSortRequestDto;
import com.tlong.center.api.dto.order.OrderRequestDto;
import com.tlong.center.api.dto.user.PageResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api("消息管理接口")
public interface WebOrderApi {

    @ApiOperation("查询所有订单接口")
    @PostMapping("/findAllOrders")
    PageResponseDto<OrderRequestDto> findAllOrders(@RequestBody PageAndSortRequestDto requestDto);

}