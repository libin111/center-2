package com.tlong.center.service;

import com.tlong.center.api.dto.Result;
import com.tlong.center.api.dto.message.MessageRequestDto;
import com.tlong.center.domain.app.Message;
import com.tlong.center.domain.repository.AppMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Transactional
public class MessageService {
    @Autowired
    private AppMessageRepository appMessageRepository;

    public Result addMessage(MessageRequestDto requestDto) {
        Message message = new Message();
        message.setTitle(requestDto.getTitle());
        message.setContent(requestDto.getContent());
        message.setUserName(requestDto.getUserName());
        message.setState(requestDto.getState());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
        message.setPublishTime(simpleDateFormat.format(new Date()));
        Message message1 = appMessageRepository.save(message);
        if (message1 == null) {
            return new Result(0, "添加失败");
        }
        return new Result(1, "添加成功");
    }

    public List<MessageRequestDto> findAllMessage() {
        List<Message> messages = appMessageRepository.findAll();
        List<MessageRequestDto> requestDtos = new ArrayList<>();
        messages.forEach(message -> {
            MessageRequestDto messageRequestDto = new MessageRequestDto();
            messageRequestDto.setId(message.getId());
            messageRequestDto.setTitle(message.getTitle());
            messageRequestDto.setContent(message.getContent());
            messageRequestDto.setUserName(message.getUserName());
            messageRequestDto.setState(message.getState());
            messageRequestDto.setPublishTime(message.getPublishTime());
            requestDtos.add(messageRequestDto);
        });
        return requestDtos;
    }

    public Result delMessage(Long id) {
        Message message = appMessageRepository.findOne(id);
        if (message == null)
            return new Result(0, "删除信息失败");
        appMessageRepository.delete(id);
        return new Result(1,"删除成功");
    }

    public Result updateMessage(@RequestBody  MessageRequestDto requestDto) {
        Message message=new Message();
        message.setId(requestDto.getId());
        message.setTitle(requestDto.getTitle());
        message.setContent(requestDto.getContent());
        message.setState(requestDto.getState());
        message.setUserName(requestDto.getUserName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
        message.setPublishTime(simpleDateFormat.format(new Date()));
        Message message1=appMessageRepository.save(message);
        if (message1 == null) {
            return new Result(0, "修改失败");
        }
        return new Result(1, "修改成功");
    }

    public MessageRequestDto findOne(Long id) {
        Message message = appMessageRepository.findOne(id);
        MessageRequestDto requestDto = new MessageRequestDto();
        requestDto.setTitle(message.getTitle());
        requestDto.setContent(message.getContent());
        requestDto.setState(message.getState());
        requestDto.setUserName(message.getUserName());
        return requestDto;
    }
}