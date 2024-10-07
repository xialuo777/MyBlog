package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import com.example.mapper.BlogCommentMapper;
import com.example.utils.bo.BlogCommentBo;
import com.example.utils.dto.PageRequest;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/5 15:54]
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final BlogCommentMapper blogCommentMapper;
    public List<BlogCommentBo> queryCommentList(PageRequest pageRequest, Long blogId) {
        int pageSize = pageRequest.getPageSize();
        int pageNo = pageRequest.getPageNo();
        PageHelper.startPage(pageNo, pageSize);

        List<BlogCommentBo> firstCommentList = blogCommentMapper.queryFirstCommentList(blogId);
        List<BlogCommentBo> secondCommentList = blogCommentMapper.querySecondCommentList(blogId);

        return addAllNodes(firstCommentList, secondCommentList);
    }


    private List<BlogCommentBo> addAllNodes(List<BlogCommentBo> firstCommentList, List<BlogCommentBo> secondCommentList) {
        while (CollectionUtil.isNotEmpty(secondCommentList)){
            for (int i = secondCommentList.size() - 1; i >= 0; i--) {
                if (addNode(firstCommentList, secondCommentList.get(i))) {
                    secondCommentList.remove(i);
                }
            }
        }
        return firstCommentList;
    }

    private boolean addNode(List<BlogCommentBo> firstCommentList, BlogCommentBo blogCommentBo) {
        for (BlogCommentBo commentBo : firstCommentList) {
            if (commentBo.getCommentId().equals(blogCommentBo.getLastId())){
                commentBo.getNextNodes().add(blogCommentBo);
                return true;
            }else if (CollectionUtil.isNotEmpty(commentBo.getNextNodes())){
                if (addNode(commentBo.getNextNodes(), blogCommentBo)){
                    return true;
                }
            }
        }
        return false;
    }
}
