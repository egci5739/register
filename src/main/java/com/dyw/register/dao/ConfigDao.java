package com.dyw.register.dao;

import com.dyw.register.entity.ConfigTableEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ConfigDao {
    List<ConfigTableEntity> getConfig();
}
