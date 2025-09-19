package com.zfh.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
分页查询返回对象
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RPage< T> {
    //总数
    private Long total;
    //数据
    private List<T> records;
}
