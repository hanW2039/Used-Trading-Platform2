package com.wsk.service.Impl;

import com.wsk.dao.OperationMapper;
import com.wsk.pojo.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: Lenovo
 * @Date: 2023/05/06/20:40
 * @Description:
 */
@Service
public class OperationServiceImpl {
    @Autowired
    private OperationMapper operationMapper;

    public void addOperationRecord(Operation operation) {
        List<Operation> operations =
                operationMapper.selectRecord(operation);
        if(operations.isEmpty()) {
            operationMapper.insertRecord(operation);
        }else {
            operation.setCount(operations.get(0).getCount() + 1);
            operationMapper.updateRecord(operation);
        }
    }

    public Map<Integer, List<Operation>> getAllOperationRecords() {
        List<Operation> operations = operationMapper.selectRecord(new Operation());
        return operations.stream().collect(Collectors.groupingBy(Operation::getSid));
    }
}
