package com.tlong.center.web;

import com.tlong.center.api.dto.Result;
import com.tlong.center.api.dto.common.PageAndSortRequestDto;
import com.tlong.center.api.dto.user.PageResponseDto;
import com.tlong.center.api.dto.web.WebGoodsDetailResponseDto;
import com.tlong.center.api.web.WebGoodsApi;
import com.tlong.center.common.utils.FileUploadUtils;
import com.tlong.center.service.WebGoodsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/web/goods")
public class WebGoodsController implements WebGoodsApi {
    final WebGoodsService webGoodsService;

    public WebGoodsController(WebGoodsService webGoodsService) {
        this.webGoodsService = webGoodsService;
    }

    @Override
    public PageResponseDto<WebGoodsDetailResponseDto> findAllGoods(PageAndSortRequestDto requestDto) {
        return webGoodsService.findAllGoodsByPage(requestDto);
    }

    @Override
    public Result addGoods(@RequestParam("file") List<MultipartFile> file, WebGoodsDetailResponseDto reqDto) {
        return webGoodsService.add(FileUploadUtils.handleFileUpload(file), reqDto);
    }

    @Override
    public Result delGoods(@RequestBody String goodsId) {
        return webGoodsService.delGoods(Long.valueOf(goodsId));
    }

    @Override
    public Result updateGoods(@RequestParam("file") List<MultipartFile> file, WebGoodsDetailResponseDto reqDto) {
        return webGoodsService.updateGoods(FileUploadUtils.handleFileUpload(file), reqDto);
    }

    @Override
    public void updateGoodsState(@RequestBody String id) {
        webGoodsService.updateGoodsState(Long.valueOf(id));
    }

    @Override
    public WebGoodsDetailResponseDto findGoodsById(@RequestBody String id) {
        return webGoodsService.findGoodsById(Long.valueOf(id));
    }
}