package com.lyr.mapper;

import com.lyr.model.Visitor;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @author: zhangocean
 * @Date: 2018/6/16 16:22
 * Describe: 访客sql
 */
@Mapper
@Repository
public interface VisitorMapper {

    @Insert("insert into visitor(visitorNum,pageName) values(0,#{pageName})")
    void save(String pageName);

    @Select("select * from visitor where pageName=#{pageName}")
    Visitor getVisitorNumByPageName(@Param("pageName") String pageName);

    @Select("<script> " +
            "select " +
            "ifnull((select visitorNum from visitor where pageName='totalVisitor'), 0)" +
            "</script>")
    long getTotalVisitor();

    @Update("update visitor set visitorNum=#{visitorNum} where pageName=#{pageName}")
    void updateVisitorNumByPageName(@Param("pageName") String pageName, @Param("visitorNum") String visitorNum);
}
