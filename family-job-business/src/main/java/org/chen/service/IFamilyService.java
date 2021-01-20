package org.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chen.domain.entity.Family;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.CreateFamilyParam;
import org.chen.domain.param.DeleteMemberParam;

/**
 * <p>
 * 家庭 服务类
 * </p>
 *
 * @author YuChen
 * @since 2020-11-25
 */
public interface IFamilyService extends IService<Family> {

    Family createFamily(CreateFamilyParam param);

    Family getFamily();

    UserInfo deleteMember(DeleteMemberParam param);
}
