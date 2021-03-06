package com.lyr.controller;

import com.lyr.aspect.annotation.PermissionCheck;
import com.lyr.constant.CodeType;
import com.lyr.model.User;
import com.lyr.service.*;
import com.lyr.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

/**
 * @author: zhangocean
 * @Date: 2018/7/20 20:56
 * Describe:
 */
@RestController
@Slf4j
public class UserControl {

    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LeaveMessageService leaveMessageService;
    @Autowired
    RedisService redisService;
    @Autowired
    ArticleService articleService;
    @Autowired
    ArticleLikesRecordService articleLikesRecordService;

    /**
     * 获得个人资料
     */
    @PostMapping(value = "/getUserPersonalInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String getUserPersonalInfo(@AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        try {
            DataMap data = userService.getUserPersonalInfoByUsername(username);
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("[{}] get user personal info exception", username, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 保存个人资料
     */
    @PostMapping(value = "/savePersonalDate", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String savePersonalDate(User user, @AuthenticationPrincipal Principal principal){

        String username = principal.getName();
        try {
            DataMap data = userService.savePersonalDate(user, username);
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("[{}] save user info [{}] exception", username, user, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 获得该用户曾今的所有评论
     */
    @PostMapping(value = "/getUserComment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String getUserComment(@RequestParam("rows") int rows,
                                     @RequestParam("pageNum") int pageNum,
                                     @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        try {
            DataMap data = commentService.getUserComment(rows, pageNum, username);
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("[{}] get comment exception", username, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 获得该用户曾今的所有留言
     */
    @PostMapping(value = "/getUserLeaveWord", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String getUserLeaveMessage(@RequestParam("rows") int rows,
                                          @RequestParam("pageNum") int pageNum,
                                          @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        try {
            DataMap data = leaveMessageService.getUserLeaveMessage(rows, pageNum, username);
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("[{}] get leaveWord exception", username, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 已读一条消息
     * @param id 消息的id
     * @param msgType 消息是评论消息还是留言消息  1-评论  2--留言
     */
    @GetMapping(value = "/readThisMsg", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String readThisMsg(@RequestParam("id") int id,
                           @RequestParam("msgType") int msgType,
                           @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        try {
            redisService.readOneMsgOnRedis(userService.findIdByUsername(username), msgType);
            if(msgType == 1){
                commentService.readOneCommentRecord(id);
            } else {
                leaveMessageService.readOneLeaveMessageRecord(id);
            }
            return JsonResult.success().toJSON();
        } catch (Exception e){
            log.error("[{}] read one type [{}] message exception", username, msgType, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 已读所有消息
     * @param msgType 消息是评论消息还是留言消息  1-评论  2--留言
     */
    @GetMapping(value = "/readAllMsg", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String readAllMsg(@RequestParam("msgType") int msgType,
                                 @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        try {
            redisService.readAllMsgOnRedis(userService.findIdByUsername(username), msgType);
            if(msgType == 1){
                commentService.readAllComment(username);
            } else {
                leaveMessageService.readAllLeaveMessage(username);
            }
            return JsonResult.success().toJSON();
        } catch (Exception e){
            log.error("[{}] read all type [{}] message exception", username, msgType, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 获得用户未读消息
     */
    @PostMapping(value = "/getUserNews", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String getUserNews(@AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        try {
            DataMap data = redisService.getUserNews(username);
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("[{}] get user news exception", username, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 获得文章管理
     * @return
     */
    @PostMapping(value = "/getArticleManagementByUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String getArticleManagement(@RequestParam("rows") int rows,
                                       @RequestParam("pageNum") int pageNum,
                                       @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        try {
            DataMap data = articleService.getArticleManagementByUsername(username, rows, pageNum);
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("Get article management exception", e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 删除文章
     * @param id 文章id
     */
    @GetMapping(value = "/deleteArticleByUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String deleteArticle(@RequestParam("id") String id){
        try {
            if(StringUtil.BLANK.equals(id) || id == null){
                return JsonResult.build(DataMap.fail(CodeType.DELETE_ARTICLE_FAIL)).toJSON();
            }
            DataMap data = articleService.deleteArticle(Long.parseLong(id));
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("Delete article [{}] exception", id, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 获得用户收藏未读消息
     */
    @PostMapping(value = "/getUserCollectNews", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String getUserCollectNews(@AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        try {
            DataMap data = articleLikesRecordService.getArticleThumbsUpNumByUsername(username);
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("Get article thumbsUp num exception", e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 获得文章收藏信息
     */
    @PostMapping(value = "/getArticleThumbsUp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String getArticleThumbsUp(@RequestParam("rows") int rows,
                                     @RequestParam("pageNum") int pageNum,
                                     @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        try {
            DataMap data = articleLikesRecordService.getArticleThumbsUpByUsername(username, rows, pageNum);
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("Get article thumbsUp exception", e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 已读一条收藏信息
     */
    @GetMapping(value = "/readThisThumbsUp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String readThisThumbsUp(@RequestParam("id") int id){
        try {
            DataMap data = articleLikesRecordService.readThisThumbsUp(id);
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("Read one thumbsUp [{}] exception", id, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * 已读所有收藏信息
     */
    @GetMapping(value = "/readAllThumbsUp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PermissionCheck(value = "ROLE_USER")
    public String readAllThumbsUp(){
        try {
            DataMap data = articleLikesRecordService.readAllThumbsUp();
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("Read all thumbsUp exception", e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }
    
}
