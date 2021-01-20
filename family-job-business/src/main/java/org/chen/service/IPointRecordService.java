package org.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chen.domain.entity.PointRecord;

/**
 * <p>
 * 点数记录 服务类
 * </p>
 *
 * @author YuChen
 * @since 2020-12-15
 */
public interface IPointRecordService extends IService<PointRecord> {

    PointRecord createPointRecord(Long jobUserId, Long familyId,Long userId,Long points,Integer type);

}
