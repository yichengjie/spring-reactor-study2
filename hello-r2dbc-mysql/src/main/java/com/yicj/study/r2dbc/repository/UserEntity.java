package com.yicj.study.r2dbc.repository;

import lombok.Data;

/**
 * @author yicj
 * @date 2023/10/1 11:36
 */
@Data
public class UserEntity {

    private Long id ;

    private String name ;

    private String password ;

    private String username ;

    private String permissions ;
}
