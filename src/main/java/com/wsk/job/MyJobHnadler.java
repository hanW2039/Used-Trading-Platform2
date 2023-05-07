package com.wsk.job;

import com.wsk.dao.ScoreRecordMapper;
import com.wsk.pojo.Operation;
import com.wsk.pojo.ScoreRecord;
import com.wsk.service.Impl.OperationServiceImpl;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.xxl.job.core.handler.annotation.XxlJob;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: Lenovo
 * @Date: 2023/05/06/18:50
 * @Description:
 */

@Component
@Slf4j
public class MyJobHnadler {
    @Autowired
    private OperationServiceImpl operationServiceImpl;
    @Autowired
    private ScoreRecordMapper scoreRecordMapper;

    @XxlJob("scoreHandler")
    public ReturnT<String> computeScore() {
        log.info("*****开始计算****");
        Map<Integer, List<Operation>> records = operationServiceImpl.getAllOperationRecords();
        for(List<Operation> record : records.values()) {
            Map<Integer, List<Operation>> collects =
                    record.stream().collect(Collectors.groupingBy(Operation::getUid));
            for(List<Operation> collect : collects.values()) {
                Collections.sort(collect, Comparator.comparing(Operation::getType));
                Double score = 3.0;
                Integer uid = null;
                Integer sid = null;
                for(Operation operation : collect) {
                    uid = operation.getUid();
                    sid = operation.getSid();
                    switch (operation.getType()) {
                        case "1":
                            score = score + 0.1 * operation.getCount();
                            break;
                        case "2":
                            score = score + 0.3 * operation.getCount();
                            break;
                        case "3":
                            score = score + 0.6 * operation.getCount();
                            break;
                    }
                    log.info("商品{}当前分数：{}",operation.getSid(), score);
                }
                score = Math.log10(score);
                log.info("******计算结束*****");
                // 核算评分
                ScoreRecord scoreRecord = new ScoreRecord();
                scoreRecord.setSid(sid);
                scoreRecord.setUid(uid);
                List<ScoreRecord> scoreRecords =
                        scoreRecordMapper.selectScoreRecords(scoreRecord);
                scoreRecord.setScore(score);
                if(scoreRecords.isEmpty()) {
                    scoreRecordMapper.insertScoreRecord(scoreRecord);
                }else {
                    scoreRecordMapper.updadteScoreRecord(scoreRecord);
                }
            }
        }
        return ReturnT.SUCCESS;
    }
}
