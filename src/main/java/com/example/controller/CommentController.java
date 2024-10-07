package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.example.dto.ResponseResult;
import com.example.entity.Blog;
import com.example.entity.BlogComment;
import com.example.entity.User;
import com.example.exception.BusinessException;
import com.example.mapper.BlogCommentMapper;
import com.example.mapper.BlogMapper;
import com.example.service.CommentService;
import com.example.utils.bo.BlogCommentBo;
import com.example.utils.dto.PageRequest;
import com.example.utils.dto.PageResult;
import com.example.vo.comment.CommentInVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/7 14:34]
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Api(tags = "评论功能接口")
@Slf4j
public class CommentController extends BaseController {

    private final BlogMapper blogMapper;
    private final BlogCommentMapper blogCommentMapper;
    private final CommentService commentService;

    /**
     * 添加评论
     * @param commentInVo
     * @return ResponseResult<String>
     */
    @PostMapping("/blog/comment")
    @ApiModelProperty(value = "评论文章")
    public ResponseResult<String> comment(@RequestBody CommentInVo commentInVo) {
        User user = checkUser();
        if (!validUserStatus(user)) {
            log.error("该用户已被封禁，无法评论！");
            throw new BusinessException("该用户已被封禁，无法评论！");
        }
        Blog blog = Optional.ofNullable(blogMapper.selectByPrimaryKey(commentInVo.getBlogId())).orElseThrow(() -> new BusinessException("文章不存在！"));
        if (!validBlogEnableComment(blog)) {
            log.error("该文章评论功能已关闭！");
            throw new BusinessException("该文章评论功能已关闭！");
        }
        BlogComment blogComment = new BlogComment();
        BeanUtil.copyProperties(commentInVo, blogComment);
        blogComment.setCommentId(IdUtil.getSnowflakeNextId());
        blogComment.setCommentator(user.getNickName());
        blogComment.setCommentatorId(user.getUserId());
        blogCommentMapper.insert(blogComment);
        return ResponseResult.success("评论成功");
    }

    /**
     * 删除评论
     * @param commentId
     * @return
     */
    @PostMapping("/delete/{commentId}")
    @ApiModelProperty(value = "删除评论")
    public ResponseResult<String> delete(@PathVariable Long commentId) {
        BlogComment blogComment = Optional.ofNullable(blogCommentMapper.selectByPrimaryKey(commentId)).orElseThrow(() -> new BusinessException("评论不存在！"));
        User user = checkUser();
        if (!user.getUserId().equals(blogComment.getCommentatorId())) {
            log.error("该用户没有权限删除该评论！");
            throw new BusinessException("该用户没有权限删除该评论！");
        }
        blogCommentMapper.deleteByPrimaryKey(commentId);
        return ResponseResult.success("删除成功");
    }

    /**
     * 获取评论列表
     * @param blogId 文章id
     * @param params 分页参数
     * @return ResponseResult<PageResult<BlogCommentBo>>
     */
    @GetMapping("{blogId}")
    @ApiModelProperty(value = "获取评论列表")
    public ResponseResult<PageResult<BlogCommentBo>> getCommentList(@PathVariable Long blogId, @RequestParam Map<String, Object> params) {
        if (!validPageParams(params)) {
            log.error("参数错误！");
            throw new BusinessException("参数错误！");
        }
        checkUser();
        Optional.ofNullable(blogMapper.selectByPrimaryKey(blogId)).orElseThrow(() -> new BusinessException("文章不存在！"));
        PageRequest pageRequest = new PageRequest(params);
        List<BlogCommentBo> blogCommentBos = commentService.queryCommentList(pageRequest, blogId);
        return ResponseResult.success(new PageResult<>(blogCommentBos, blogCommentBos.size()));
    }
}
