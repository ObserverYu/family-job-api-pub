package org.chen.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.chen.dao.DataDictMapper;
import org.chen.domain.entity.DataDict;
import org.chen.domain.result.TemplateIdResult;
import org.chen.service.IDataDictService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 字典 服务实现类
 * </p>
 *
 * @author YuChen
 * @since 2020-12-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DataDictServiceImpl extends ServiceImpl<DataDictMapper, DataDict> implements IDataDictService {

}
