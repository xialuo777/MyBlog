package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.example.authentication.CurrentUserHolder;
import com.example.dto.ResponseResult;
import com.example.entity.Blog;
import com.example.entity.User;
import com.example.exception.BusinessException;
import com.example.mapper.BlogMapper;
import com.example.service.BlogService;
import com.example.utils.dto.PageRequest;
import com.example.utils.dto.PageResult;
import com.example.vo.blog.BlogDesc;
import com.example.vo.blog.BlogDetail;
import com.example.vo.blog.BlogInVo;
import com.example.vo.blog.BlogUpdateVo;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/5 15:04]
 */
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "文章功能接口")
public class BlogController extends BaseController {
    private final CurrentUserHolder currentUserHolder;
    private final BlogMapper blogMapper;
    private final BlogService blogService;

    /**
     * 保存文章
     *
     * @param blogInVo 保存文章信息
     * @return ResponseResult
     */
    @PostMapping("/save")
    @ApiModelProperty(value = "保存文章")
    public ResponseResult<String> saveBlog(@RequestBody BlogInVo blogInVo) {
        User user = checkUser();
        if (!validUserStatus(user)) {
            log.error("该用户已被封禁，无法发布文章！");
            throw new BusinessException("该用户已被封禁，无法发布文章！");
        }
        Blog blog = new Blog();
        BeanUtil.copyProperties(blogInVo, blog);
        blogService.saveBlog(blog);
        return ResponseResult.success("文章保存成功");
    }

    /**
     * 更新博客
     *
     * @param blogUpdateVo 更新文章信息
     * @param blogId       文章id
     * @return ResponseResult<String>
     */
    @PostMapping("/update/{blogId}")
    @ApiModelProperty(value = "更新文章")
    public ResponseResult<String> updateBlog(@RequestBody BlogUpdateVo blogUpdateVo, @PathVariable Long blogId) {
        Blog blog = Optional.ofNullable(blogMapper.selectByPrimaryKey(blogId))
                .orElseThrow(() -> new BusinessException("文章不存在！"));
        if (!currentUserHolder.getUserId().equals(blog.getUserId())) {
            log.error("用户id与文章作者id不一致！没有权限修改该文章");
            throw new BusinessException("您没有权限修改该文章！");
        }
        User user = checkUser();
        if (!validUserStatus(user)) {
            log.error("该用户已被封禁，无法修改文章！");
            throw new BusinessException("该用户已被封禁，无法修改文章！");
        }
        BeanUtil.copyProperties(blogUpdateVo, blog, CopyOptions.create().setIgnoreNullValue(true).setIgnoreCase(true));
        blogService.updateBlog(blog);
        return ResponseResult.success("文章更新成功");
    }

    /**
     * 获取用户博客列表
     * @param params 分页参数
     * @return ResponseResult<PageResult<BlogDesc>>
     */
    @GetMapping("/list")
    @ApiModelProperty(value = "获取当前用户文章列表")
    public ResponseResult<PageResult<BlogDesc>> getCurrentUserBlogs(@RequestParam Map<String, Object> params) {
        if (!validPageParams(params)){
            return ResponseResult.fail("分页参数异常");
        }
        PageRequest pageRequest = new PageRequest(params);
        User user = checkUser();
        PageHelper.startPage(pageRequest.getPageNo(), pageRequest.getPageSize());
        List<Blog> blogList = blogMapper.selectBlogsByUserId(user.getUserId());
        List<BlogDesc> blogDescList = blogList.stream()
                .map(blog -> BeanUtil.copyProperties(blog, BlogDesc.class))
                .collect(Collectors.toList());
        int totalCount = blogDescList.size();
        PageResult<BlogDesc> pageResult = new PageResult<>(blogDescList, totalCount);
        return ResponseResult.success(pageResult);
    }

    /**
     * 获取文章详情
     * @param blogId 博客id
     * @return ResponseResult<BlogDesc>
     */
    @GetMapping("/{blogId}")
    @ApiModelProperty(value = "获取文章详情")
    public ResponseResult<BlogDetail> getBlog(@PathVariable Long blogId) {
        checkUser();
        Blog blog = Optional.ofNullable(blogMapper.selectByPrimaryKey(blogId))
                .orElseThrow(() -> new BusinessException("文章不存在！"));
        BlogDetail blogDetail = BeanUtil.copyProperties(blog, BlogDetail.class);
        return ResponseResult.success(blogDetail);
    }

    /**
     * 获取分类下的文章列表
     * @param categoryId 分类id
     * @param params 分页参数
     * @return ResponseResult<PageResult<BlogDesc>>
     */
    @GetMapping("/category/{categoryId}")
    @ApiModelProperty(value = "获取分类下的文章列表")
    public ResponseResult<PageResult<BlogDesc>> getBlogsByCategory(@PathVariable Long categoryId, @RequestParam Map<String, Object> params) {
        checkUser();
        if (!validPageParams(params)){
            return ResponseResult.fail("分页参数异常");
        }
        PageRequest pageRequest = new PageRequest(params);
        PageHelper.startPage(pageRequest.getPageNo(), pageRequest.getPageSize());
        List<Blog> blogList = blogMapper.selectBlogsByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(blogList)){
            log.error("该分类下没有文章！");
            throw new BusinessException("该分类下没有文章！");
        }
        List<BlogDesc> blogDescList = blogList.stream()
                .map(blog -> BeanUtil.copyProperties(blog, BlogDesc.class))
                .collect(Collectors.toList());
        return ResponseResult.success(new PageResult<>(blogDescList, blogDescList.size()));
    }

    /**
     * 根据文章id删除文章
     * @param blogId 文章id
     * @return ResponseResult<String>
     */
    @PostMapping("/delete/{blogId}")
    @ApiModelProperty(value = "删除文章")
    public ResponseResult<String> delete(@PathVariable Long blogId){
        Blog blog = Optional.ofNullable(blogMapper.selectByPrimaryKey(blogId)).orElseThrow(() -> new BusinessException("文章不存在！"));
        User user = checkUser();
        if (!user.getUserId().equals(blog.getUserId())){
            log.error("用户id与文章作者id不一致！没有权限删除该文章");
            throw new BusinessException("您没有权限删除该文章！");
        }
        blogMapper.deleteByPrimaryKey(blogId);
        return ResponseResult.success("删除成功");
    }

    /**
     * 获取所有博客列表
     * @param params 分页参数
     * @return ResponseResult<PageResult<BlogDesc>>
     */
    @GetMapping("/blog/list")
    @ApiModelProperty(value = "获取所有文章列表")
    public ResponseResult<PageResult<BlogDesc>> getBlogs(@RequestParam Map<String, Object> params) {
        checkUser();
        if (!validPageParams(params)){
            return ResponseResult.fail("分页参数异常");
        }
        PageRequest pageRequest = new PageRequest(params);
        PageHelper.startPage(pageRequest.getPageNo(), pageRequest.getPageSize());
        List<Blog> blogList = blogMapper.selectBlogs();
        List<BlogDesc> blogDescList = blogList.stream()
                .map(blog -> BeanUtil.copyProperties(blog, BlogDesc.class))
                .collect(Collectors.toList());
        return ResponseResult.success(new PageResult<>(blogDescList, blogDescList.size()));
    }

}
