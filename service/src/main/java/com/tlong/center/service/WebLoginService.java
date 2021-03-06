package com.tlong.center.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tlong.center.api.dto.common.TlongResultDto;
import com.tlong.center.api.dto.web.TlongPowerDto;
import com.tlong.center.api.dto.web.WebLoginRequestDto;
import com.tlong.center.api.dto.web.WebLoginResponseDto;
import com.tlong.center.common.code.CodeUtil;
import com.tlong.center.domain.app.TlongUser;
import com.tlong.center.domain.repository.TlongPowerRepository;
import com.tlong.center.domain.repository.TlongRolePowerRepository;
import com.tlong.center.domain.repository.TlongUserRepository;
import com.tlong.center.domain.web.TlongPower;
import com.tlong.center.domain.web.TlongRolePower;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.tlong.center.domain.app.QTlongUser.tlongUser;
import static com.tlong.center.domain.web.QTlongUserRole.tlongUserRole;
import static com.tlong.center.domain.web.QTlongRole.tlongRole;
import static com.tlong.center.domain.web.QTlongRolePower.tlongRolePower;
import static com.tlong.center.domain.web.QTlongPower.tlongPower;

@Component
@Transactional
public class WebLoginService {

    private static Logger logger = LoggerFactory.getLogger(WebLoginService.class);

    final EntityManager entityManager;
    final TlongUserRepository tlongUserRepository;
    final TlongPowerRepository tlongPowerRepository;
    final TlongRolePowerRepository tlongRolePowerRepository;

    final CodeUtil codeUtil;

    JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(entityManager);
    }

    public WebLoginService(EntityManager entityManager, TlongUserRepository tlongUserRepository, TlongPowerRepository tlongPowerRepository, CodeUtil codeUtil, TlongRolePowerRepository tlongRolePowerRepository) {
        this.entityManager = entityManager;
        this.tlongUserRepository = tlongUserRepository;
        this.tlongPowerRepository = tlongPowerRepository;
        this.codeUtil = codeUtil;
        this.tlongRolePowerRepository = tlongRolePowerRepository;
    }


    /**
     * 登录时自动验证账号名是否存在
     */
    public TlongResultDto checkUserName(String userName) {
        Iterable<TlongUser> all = tlongUserRepository.findAll(tlongUser.userName.eq(userName));
        if (all == null) {
            return new TlongResultDto(1, "用户名不存在");
        } else {
            return new TlongResultDto(0, "用户名正常");
        }
    }

    /**
     * 后台登录接口
     */
    public WebLoginResponseDto webLogin(WebLoginRequestDto requestDto, HttpSession session) {

        //首先验证账号密码
//        String password = MD5Util.KL(MD5Util.MD5(requestDto.getPassword()));
        TlongUser findResult = tlongUserRepository.findOne(tlongUser.userName.eq(requestDto.getUserName())
                .and(tlongUser.password.eq(requestDto.getPassword())));
        if (Objects.nonNull(findResult)) {
            session.setAttribute("tlongUser", findResult);
            logger.info("user" + requestDto.getUserName() + "Login Success!");
            List<Tuple> tuples = queryFactory.select(tlongRolePower.roleId, tlongRolePower.powerId, tlongPower.id, tlongPower.powerName, tlongPower.powerLevel, tlongPower.pid, tlongPower.url)
                    .from(tlongUser, tlongUserRole, tlongRole, tlongRolePower, tlongPower)
                    .where(tlongUser.id.eq(tlongUserRole.userId)
                            .and(tlongRole.id.eq(tlongUserRole.roleId))
                            .and(tlongRole.id.eq(tlongRolePower.roleId))
                            .and(tlongPower.id.eq(tlongRolePower.powerId))
                            .and(tlongUser.id.eq(findResult.getId())))
                    .fetch();
            WebLoginResponseDto webLoginResponseDto = new WebLoginResponseDto();
            List<TlongPowerDto> powerLevelOne = new ArrayList<>();
            List<TlongPowerDto> powerLevelTwo = new ArrayList<>();
            tuples.stream().forEach(one -> {
                TlongPowerDto dto = new TlongPowerDto(one.get(tlongPower.id), one.get(tlongPower.powerName), one.get(tlongPower.powerLevel), one.get(tlongPower.pid), one.get(tlongPower.url));
                if (one.get(tlongPower.powerLevel) == 0) {
                    powerLevelOne.add(dto);
                } else if (one.get(tlongPower.powerLevel) == 1) {
                    Iterable<TlongPower> tlongPowers = tlongPowerRepository.findAll(tlongPower.powerLevel.intValue().eq(2).and(tlongPower.pid.intValue().eq(dto.getId().intValue())));
                    Iterable<TlongRolePower> tlongRolePowers = tlongRolePowerRepository.findAll(tlongRolePower.roleId.longValue().eq(one.get(tlongRolePower.roleId)));
                    tlongPowers.forEach(three -> {
                        tlongRolePowers.forEach(tlongRolePower1 -> {
                            if (three.getId().intValue() == tlongRolePower1.getPowerId().intValue())
                                dto.getThreeLevel().add(three.getPowerName());
                        });
                    });
                    powerLevelTwo.add(dto);
                }
            });
            webLoginResponseDto.setPowersLevelOne(powerLevelOne);
            webLoginResponseDto.setPowersLevelTwo(powerLevelTwo);
            webLoginResponseDto.setUserName(requestDto.getUserName());
            return webLoginResponseDto;
        } else {
            return new WebLoginResponseDto(1, "账户名或密码不正确");
        }

        //获取权限列表

//        List<Tuple> tuples = queryFactory.select(tlongPower.id, tlongPower.powerName, tlongPower.powerLevel)
//                .from(tlongUser,tlongUserRole,tlongRole,tlongRolePower,tlongPower)
//                .leftJoin(tlongUserRole).on(tlongUser.id.eq(tlongUserRole.userId))
//                .leftJoin(tlongRole).on(tlongRole.id.eq(tlongUserRole.roleId))
//                .leftJoin(tlongRolePower).on(tlongRole.id.eq(tlongRolePower.roleId))
//                .leftJoin(tlongPower).on(tlongPower.id.eq(tlongRolePower.powerId))
//                .where(tlongUser.id.eq(findResult.getId()))
//                .fetch();
    }

}
