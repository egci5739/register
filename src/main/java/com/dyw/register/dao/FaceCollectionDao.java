package com.dyw.register.dao;

import com.dyw.register.entity.FaceCollectionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface FaceCollectionDao {
    void insertFaceCollection(FaceCollectionEntity faceCollectionEntity);
}
