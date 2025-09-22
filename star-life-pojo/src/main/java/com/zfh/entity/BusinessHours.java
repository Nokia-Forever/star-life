package com.zfh.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 营业时间
 */
@Data
public class BusinessHours implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long id;
    /**
     * 营业时间
     */
    private String businessHours;
}
