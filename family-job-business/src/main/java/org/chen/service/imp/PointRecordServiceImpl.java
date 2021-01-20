package org.chen.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.chen.dao.PointRecordMapper;
import org.chen.domain.entity.PointRecord;
import org.chen.service.IPointRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 点数记录 服务实现类
 * </p>
 *
 * @author YuChen
 * @since 2020-12-15
 */
@Slf4j
@Service
public class PointRecordServiceImpl extends ServiceImpl<PointRecordMapper, PointRecord> implements IPointRecordService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointRecord createPointRecord(Long jobUserId, Long familyId, Long userId, Long points, Integer type) {
        PointRecord pointRecord = new PointRecord();
        pointRecord.setType(type);
        pointRecord.setPoints(points);
        pointRecord.setUserId(userId);
        pointRecord.setFamilyId(familyId);
        pointRecord.setCreateTime(new Date());
        pointRecord.setJobUserId(jobUserId);
        save(pointRecord);
        return pointRecord;
    }
}
