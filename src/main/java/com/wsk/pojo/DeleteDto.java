package com.wsk.pojo;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Auther: Lenovo
 * @Date: 2023/05/04/15:12
 * @Description:
 */
@Data
public class DeleteDto {
    private Integer cid;
    private Integer sid;
}
