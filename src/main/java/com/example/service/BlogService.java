package com.example.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import com.example.authentication.CurrentUserHolder;
import com.example.constant.Constant;
import com.example.entity.Blog;
import com.example.entity.BlogTag;
import com.example.entity.Category;
import com.example.entity.Tag;
import com.example.enums.ResponseCodeEnum;
import com.example.exception.BusinessException;
import com.example.mapper.BlogMapper;
import com.example.mapper.BlogTagMapper;
import com.example.mapper.CategoryMapper;
import com.example.mapper.TagMapper;
import com.github.pagehelper.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/7 15:15]
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BlogService {

    private final CategoryMapper categoryMapper;

    private final BlogTagMapper blogTagMapper;

    private final TagMapper tagMapper;
    private final CurrentUserHolder currentUserHolder;
    private final BlogMapper blogMapper;
    public void saveBlog(Blog blog) {
        Long categoryId = blog.getCategoryId();
        Category categoryExist = categoryMapper.selectByPrimaryKey(categoryId);
        Optional<Category> optionalCategory = Optional.ofNullable(categoryExist);

        Long userId = currentUserHolder.getUserId();
        Long blogId = IdUtil.getSnowflakeNextId();
        String baseHomePageUrl = String.format(Constant.BLOG_BASE_PATH + "%s/%s", userId, blogId);
        blog.setSubUrl(baseHomePageUrl);
        blog.setUserId(userId);
        blog.setBlogId(blogId);

        Blog categoryAndBlog = handleCategoryAndBlog(blog, optionalCategory);
        handleTags(categoryAndBlog);
        blogMapper.insertSelective(categoryAndBlog);
    }
    public void updateBlog(Blog blog) {
        Blog blogForUpdate = blogMapper.selectByPrimaryKey(blog.getBlogId());
        BeanUtil.copyProperties(blog, blogForUpdate, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        Category categoryExist = categoryMapper.selectByPrimaryKey(blogForUpdate.getCategoryId());
        Optional<Category> optionalCategory = Optional.ofNullable(categoryExist);
        blogMapper.updateByPrimaryKeySelective(blogForUpdate);
        Blog categoryAndBlog = handleCategoryAndBlog(blogForUpdate, optionalCategory);
        handleTags(categoryAndBlog);
    }

    /**
     * 处理博客分类信息
     *
     * @param blog 博客信息
     * @param categoryExist 分类信息
     */
    private Blog handleCategoryAndBlog(Blog blog, Optional<Category> categoryExist) {
        if (!categoryExist.isPresent()) {
            long categoryId = IdUtil.getSnowflakeNextId();
            Category category = new Category(categoryId, blog.getCategoryName());
            categoryMapper.insertSelective(category);
            blog.setCategoryId(categoryId);
        } else {
            categoryMapper.increatCategoryRank(categoryExist.get());
            blog.setCategoryId(categoryExist.get().getCategoryId());
        }
        return blog;
    }

    /**
     * 处理博客标签信息
     *
     * @param blog 博客信息
     */

    private void handleTags(Blog blog) {
        if (StringUtil.isNotEmpty(blog.getBlogTags())) {
            String[] tags = blog.getBlogTags().split(",");
            if (tags.length > Constant.MAX_TAG_COUNT) {
                log.error("输入标签数量限制为{}，请重新输入", Constant.MAX_TAG_COUNT);
                throw new BusinessException("输入标签数量限制为{}，请重新输入", Constant.MAX_TAG_COUNT, ResponseCodeEnum.PARAM_ERROR);
            }
            List<String> distinctTagNames = Arrays.stream(tags).distinct().collect(Collectors.toList());

            List<Tag> tagsFromDb = tagMapper.selectListByTagNames(distinctTagNames);
            List<Tag> mutableTagsFromDb = new ArrayList<>(tagsFromDb);

            List<Tag> tagListForInsert = distinctTagNames.stream()
                    .filter(tagName -> !mutableTagsFromDb.stream().map(Tag::getTagName).collect(Collectors.toSet()).contains(tagName))
                    .map(tagName -> new Tag(IdUtil.getSnowflakeNextId(), tagName))
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(tagListForInsert)) {
                tagMapper.insertList(tagListForInsert);
                mutableTagsFromDb.addAll(tagListForInsert);
            }

            List<Tag> allTagsList = new ArrayList<>(mutableTagsFromDb);
            List<BlogTag> blogTags = setBlogTags(blog, allTagsList);

            blogTagMapper.deleteByPrimaryKey(blog.getBlogId());
            blogTagMapper.insertList(blogTags);
        }
    }

    /**
     * 处理博客标签关系，即博客与标签的关联表
     * @param blog 博客信息
     * @param tags 标签信息
     * @return List<BlogTag>
     */
    private List<BlogTag> setBlogTags(Blog blog, List<Tag> tags) {
        return tags.stream()
                .map(tag -> new BlogTag(IdUtil.getSnowflakeNextId(), blog.getBlogId(), tag.getTagId()))
                .collect(Collectors.toList());
    }
}
