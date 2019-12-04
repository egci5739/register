package com.dyw.register.dao;

import com.dyw.register.entity.EquipmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface EquipmentDao {
    List<EquipmentEntity> getAllEquipment();
}
