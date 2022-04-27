package com.lyr.mapper;

import com.lyr.model.ArticleLikesRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2018/7/7 15:51
 * Describe: 文章点赞sql
 */
@Mapper
@Repository
public interface ArticleLikesMapper {

    @Insert("insert into article_likes_record(articleId,likerId,likeDate,isRead) values(#{articleId},#{likerId},#{likeDate},#{isRead})")
    void save(ArticleLikesRecord articleLikesRecord);

    @Select("select likeDate from article_likes_record where articleId=#{articleId} and likerId=#{likerId}")
    ArticleLikesRecord isLiked(@Param("articleId") long articleId, @Param("likerId") int likerId);

    @Delete("delete from article_likes_record where articleId=#{articleId}")
    void deleteArticleLikesRecordByArticleId(long articleId);

    @Select({"<script> " +
            "select article_likes_record.id, article_likes_record.articleId, likerId, likeDate, isRead " +
            "from article_likes_record join article " +
            "on article_likes_record.articleId=article.articleId where article.originalAuthor=#{username} " +
            "order by id desc" +
            "</script>"})
    List<ArticleLikesRecord> getArticleThumbsUpByUsername(@Param("username") String username);

    @Select({"<script> " +
            "select count(*) " +
            "from article_likes_record join article " +
            "on article_likes_record.articleId=article.articleId " +
            "where article.originalAuthor=#{username} and isRead=1" +
            "</script>"})
    int countIsReadNumByUsername(@Param("username") String username);

    @Update("update article_likes_record set isRead=0 where id=#{id}")
    void readThisThumbsUp(int id);

    @Update("update article_likes_record set isRead=0")
    void readAllThumbsUp();
}
