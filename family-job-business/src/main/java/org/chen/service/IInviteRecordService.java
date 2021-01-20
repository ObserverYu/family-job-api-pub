package org.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chen.domain.entity.InviteRecord;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.CheckInviteParam;
import org.chen.domain.param.InviteUserParam;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author YuChen
 * @since 2020-12-10
 */
public interface IInviteRecordService extends IService<InviteRecord> {

    InviteRecord getMyInviteRecord();

    InviteRecord inviteUser(InviteUserParam param);

    UserInfo acceptInvite(CheckInviteParam param);

    UserInfo refuseInvite(CheckInviteParam param);
}
