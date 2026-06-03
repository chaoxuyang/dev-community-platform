package com.devcommunity.platform.service.comment.service;

import com.devcommunity.platform.api.model.vo.comment.CommentSaveReq;
import com.devcommunity.platform.service.comment.repository.entity.CommentDO;

/**
 * 评论Service接口
 *
 * @author louzai
 * @date 2022-07-24
 */
public interface CommentWriteService {

    /**
     * 更新/保存评论
     *
     * @param commentSaveReq
     * @return
     */
    Long saveComment(CommentSaveReq commentSaveReq);

    /**
     * 删除评论
     *
     * @param commentId
     * @throws Exception
     */
    void deleteComment(Long commentId, Long userId);

}
