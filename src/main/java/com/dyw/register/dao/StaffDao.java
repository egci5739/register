package com.dyw.register.dao;

import com.dyw.register.entity.StaffEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface StaffDao {
    List<StaffEntity> getSingleStaff(@Param("staffCardNumber") String staffCardNumber);

    void insertStaff(StaffEntity staffEntity);

    void updateStaffWithCardChange(StaffEntity staffEntity);

    void updateStaff(StaffEntity staffEntity);

    void deleteStaff(@Param("staffCardNumber") String staffCardNumber);
}
