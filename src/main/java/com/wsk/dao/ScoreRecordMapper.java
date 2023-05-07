package com.wsk.dao;

import com.wsk.pojo.ScoreRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Lenovo
 * @Auther: Lenovo
 * @Date: 2023/05/07/9:40
 * @Description:
 */
@Mapper
public interface ScoreRecordMapper {
    void insertScoreRecord(ScoreRecord scoreRecord);

    void updadteScoreRecord(ScoreRecord scoreRecord);

    List<ScoreRecord> selectScoreRecords(ScoreRecord scoreRecord);
}
