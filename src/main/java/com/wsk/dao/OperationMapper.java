package com.wsk.dao;

import com.wsk.pojo.Operation;
import java.util.List;

/**
 * @author Lenovo
 * @Auther: Lenovo
 * @Date: 2023/05/06/20:19
 * @Description:
 */
public interface OperationMapper {
    Integer insertRecord(Operation operation);

    Integer updateRecord(Operation operation);

    List<Operation> selectRecord(Operation operation);
}
