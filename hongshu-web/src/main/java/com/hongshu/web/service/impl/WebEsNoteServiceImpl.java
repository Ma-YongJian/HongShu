package com.hongshu.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hongshu.common.constant.NoteConstant;
import com.hongshu.common.constant.UserConstant;
import com.hongshu.common.exception.web.HongshuException;
import com.hongshu.common.utils.DozerUtil;
import com.hongshu.common.utils.WebUtils;
import com.hongshu.web.domain.dto.EsNoteDTO;
import com.hongshu.web.domain.entity.WebNavbar;
import com.hongshu.web.domain.entity.WebLikeOrCollect;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.NoteSearchVO;
import com.hongshu.web.mapper.WebNavbarMapper;
import com.hongshu.web.mapper.WebLikeOrCollectMapper;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebEsNoteService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ES
 *
 * @Author hongshu
 */
@Service
@Slf4j
public class WebEsNoteServiceImpl extends ServiceImpl<WebNoteMapper, WebNote> implements IWebEsNoteService {

    @Autowired
    private WebUserMapper userMapper;
    @Autowired
    private WebNavbarMapper categoryMapper;
    @Autowired
    private WebNoteMapper noteMapper;
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private WebLikeOrCollectMapper likeOrCollectionMapper;


    /**
     * 搜索对应的笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param esNoteDTO   笔记
     */
    @Override
    public Page<NoteSearchVO> getNoteByDTO(long currentPage, long pageSize, EsNoteDTO esNoteDTO) {
        Page<NoteSearchVO> page = new Page<>();
        List<NoteSearchVO> noteSearchVOList = new ArrayList<>();
        try {
            SearchRequest.Builder builder = new SearchRequest.Builder().index(NoteConstant.NOTE_INDEX);
            if (StringUtils.isNotBlank(esNoteDTO.getKeyword())) {
                builder.query(q -> q.bool(b -> b
                        .should(h -> h.match(f -> f.field("title").boost(1f).query(esNoteDTO.getKeyword())))
                        .should(h -> h.match(f -> f.field("username").boost(0.5f).query(esNoteDTO.getKeyword())))
                        .should(h -> h.match(f -> f.field("content").boost(1f).query(esNoteDTO.getKeyword())))
                        .should(h -> h.match(f -> f.field("tags").boost(4f).query(esNoteDTO.getKeyword())))
                        .should(h -> h.match(f -> f.field("categoryName").boost(2f).query(esNoteDTO.getKeyword())))
                        .should(h -> h.match(f -> f.field("categoryParentName").boost(1.5f).query(esNoteDTO.getKeyword())))
                ));
            }
            if (StringUtils.isNotBlank(esNoteDTO.getCpid()) && StringUtils.isNotBlank(esNoteDTO.getCid())) {
                builder.query(q -> q.bool(b -> b
                        .must(h -> h.match(m -> m.field("cid").query(esNoteDTO.getCid())))
                        .must(h -> h.match(m -> m.field("cpid").query(esNoteDTO.getCpid())))
                ));
            } else if (StringUtils.isNotBlank(esNoteDTO.getCpid())) {
                builder.query(h -> h.match(m -> m.field("cpid").query(esNoteDTO.getCpid())));
            }

            if (esNoteDTO.getType() == 1) {
                builder.sort(o -> o.field(f -> f.field("likeCount").order(SortOrder.Desc)));
            } else if (esNoteDTO.getType() == 2) {
                builder.sort(o -> o.field(f -> f.field("time").order(SortOrder.Desc)));
            }
            builder.from((int) (currentPage - 1) * (int) pageSize);
            builder.size((int) pageSize);
            SearchRequest searchRequest = builder.build();
            SearchResponse<NoteSearchVO> searchResponse = elasticsearchClient.search(searchRequest, NoteSearchVO.class);
            TotalHits totalHits = searchResponse.hits().total();
            page.setTotal(Objects.requireNonNull(totalHits).value());
            List<Hit<NoteSearchVO>> hits = searchResponse.hits().hits();
            for (Hit<NoteSearchVO> hit : hits) {
                NoteSearchVO noteSearchVo = hit.source();
                noteSearchVOList.add(noteSearchVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HongshuException("es查找数据异常");
        }
        page.setRecords(noteSearchVOList);
        return page;
    }

    /**
     * 搜索对应的笔记
     *
     * @param esNoteDTO 笔记
     * @return
     */
    @Override
    public List<WebNavbar> getCategoryAgg(EsNoteDTO esNoteDTO) {
        List<WebNavbar> categoryList = categoryMapper.selectList(new QueryWrapper<WebNavbar>().like("title", esNoteDTO.getKeyword()));
        return categoryList;
    }

    /**
     * 分页查询笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @Override
    public Page<NoteSearchVO> getRecommendNote(long currentPage, long pageSize) {
        Page<NoteSearchVO> page = new Page<>();
        List<NoteSearchVO> noteSearchVOList = new ArrayList<>();
        //得到当前用户的浏览记录
        try {
            SearchRequest.Builder builder = new SearchRequest.Builder().index(NoteConstant.NOTE_INDEX);
            builder.size(1000);
            SearchRequest searchRequest = builder.build();
            SearchResponse<NoteSearchVO> searchResponse = elasticsearchClient.search(searchRequest, NoteSearchVO.class);
            TotalHits totalHits = searchResponse.hits().total();
            //得到所有的数据
            List<Hit<NoteSearchVO>> hits = searchResponse.hits().hits();
            if (CollectionUtil.isNotEmpty(hits)) {
                for (Hit<NoteSearchVO> hit : hits) {
                    NoteSearchVO noteSearchVo = hit.source();
                    noteSearchVOList.add(noteSearchVo);
                }

                Collections.shuffle(noteSearchVOList);
                List<List<NoteSearchVO>> partition = Lists.partition(noteSearchVOList, (int) pageSize);
                List<NoteSearchVO> noteSearchVOS = partition.get((int) currentPage - 1);
                page.setTotal(totalHits != null ? totalHits.value() : 0);
                page.setRecords(noteSearchVOS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    /**
     * 获取推荐用户
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @SneakyThrows
    @Override
    public Page<WebUser> getRecommendUser(long currentPage, long pageSize) {
        Page<WebUser> page = new Page<>();
        String userId = WebUtils.getRequestHeader(UserConstant.USER_ID);
        // 用户ID为空 默认随机加载100条数据
        List<WebUser> recommendList = null;
        if (StringUtils.isBlank(userId)) {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(NoteConstant.NOTE_INDEX)
                    .size(100));
            SearchResponse<WebUser> searchResponse = elasticsearchClient.search(searchRequest, WebUser.class);
            recommendList = searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
            // 随机排序
            Collections.shuffle(recommendList, new Random());
        } else {
            // 获取推荐列表
//            recommendList = recommendService.getRecommendUser(Long.parseLong(userId));
        }
        List<List<WebUser>> partition = Lists.partition(recommendList, (int) pageSize);
        // 如果 currentPage 超出范围，返回空记录
        if (currentPage > partition.size() || currentPage <= 0) {
            page.setTotal(0);
            page.setRecords(Collections.emptyList());
            return page;
        }
        List<WebUser> userList = partition.get((int) currentPage - 1);
        page.setTotal(recommendList.size());
        page.setRecords(userList);
        return page;
    }

    /**
     * 获取热榜笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @Override
    public Page<NoteSearchVO> getHotNote(long currentPage, long pageSize) {
        Page<NoteSearchVO> page = new Page<>(currentPage, pageSize);
        List<NoteSearchVO> noteSearchVOList = new ArrayList<>();

        try {
            // 构建搜索请求
            SearchRequest.Builder builder = new SearchRequest.Builder()
                    .index(NoteConstant.NOTE_INDEX)
                    .from(Math.toIntExact((currentPage - 1) * pageSize)) // 设置分页起始点
                    .size(Math.toIntExact(pageSize)) // 设置分页大小
                    .sort(s -> s.field(f -> f.field("likeCount").order(SortOrder.Desc))); // 按 likeCount 降序排序

            SearchRequest searchRequest = builder.build();

            // 执行搜索请求
            SearchResponse<NoteSearchVO> searchResponse = elasticsearchClient.search(searchRequest, NoteSearchVO.class);
            TotalHits totalHits = searchResponse.hits().total();

            // 获取搜索结果
            List<Hit<NoteSearchVO>> hits = searchResponse.hits().hits();
            if (CollectionUtil.isNotEmpty(hits)) {
                for (Hit<NoteSearchVO> hit : hits) {
                    NoteSearchVO noteSearchVo = hit.source();
                    noteSearchVOList.add(noteSearchVo);
                }
            }

            // 设置分页结果
            page.setTotal(totalHits != null ? totalHits.value() : 0);
            page.setRecords(noteSearchVOList);

        } catch (Exception e) {
            e.printStackTrace();
            // 这里可以进一步处理异常，比如记录日志或者抛出自定义异常
        }
        return page;
    }

    /**
     * 增加笔记
     *
     * @param noteSearchVo 笔记
     */
    @Override
    public void addNote(NoteSearchVO noteSearchVo) {
        try {
            CreateResponse createResponse = elasticsearchClient.create(e -> e.index(NoteConstant.NOTE_INDEX).id(noteSearchVo.getId()).document(noteSearchVo));
            log.info("createResponse.result{}", createResponse.result());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改笔记
     *
     * @param noteSearchVo 笔记
     */
    @Override
    public void updateNote(NoteSearchVO noteSearchVo) {
        try {
            UpdateResponse<NoteSearchVO> updateResponse = elasticsearchClient.update(e -> e.index(NoteConstant.NOTE_INDEX).id(noteSearchVo.getId()).doc(noteSearchVo), NoteSearchVO.class);
            log.info("updateResponse.result() = " + updateResponse.result());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除es中的笔记
     *
     * @param noteId 笔记 ID
     */
    @Override
    public void deleteNote(String noteId) {
        try {
            DeleteResponse deleteResponse = elasticsearchClient.delete(e -> e.index(NoteConstant.NOTE_INDEX).id(String.valueOf(noteId)));
            log.info("deleteResponse.result() ={} ", deleteResponse.result());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量增加笔记
     */
    @Override
    public void addNoteBulkData() {
        List<WebNote> noteList = noteMapper.selectList(new QueryWrapper<WebNote>().eq("audit_status", 1));
        List<NoteSearchVO> noteSearchVOList = DozerUtil.convertor(noteList, NoteSearchVO.class);
        for (NoteSearchVO noteSearchVo : noteSearchVOList) {
            WebUser user = userMapper.selectOne(new QueryWrapper<WebUser>().like("id", noteSearchVo.getUid()));
            noteSearchVo.setAvatar(user.getAvatar());
            noteSearchVo.setUsername(user.getUsername());

            // 是否点赞
            List<WebLikeOrCollect> likeOrCollections = likeOrCollectionMapper.selectList(new QueryWrapper<WebLikeOrCollect>().eq("uid", user.getId()).eq("type", 1));
            List<String> likeOrCollectionIds = likeOrCollections.stream().map(WebLikeOrCollect::getLikeOrCollectionId).collect(Collectors.toList());
            noteSearchVo.setIsLike(likeOrCollectionIds.contains(noteSearchVo.getId()));
        }
        try {
            List<BulkOperation> result = new ArrayList<>();
            for (NoteSearchVO noteSearchVo : noteSearchVOList) {
                result.add(new BulkOperation.Builder().create(
                        d -> d.document(noteSearchVo).id(noteSearchVo.getId()).index(NoteConstant.NOTE_INDEX)).build());
            }
            BulkResponse bulkResponse = elasticsearchClient.bulk(e -> e.index(NoteConstant.NOTE_INDEX).operations(result));
            log.info("createResponse.result{}", bulkResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空笔记
     */
    @Override
    public void delNoteBulkData() {
        try {
            // 删除索引
            DeleteIndexRequest deleteIndexRequest = DeleteIndexRequest.of(builder -> builder.index(NoteConstant.NOTE_INDEX));
            elasticsearchClient.indices().delete(deleteIndexRequest);
            log.info("删除索引:{}", NoteConstant.NOTE_INDEX);

            // 重新创建索引
            CreateIndexRequest createIndexRequest = CreateIndexRequest.of(builder -> builder.index(NoteConstant.NOTE_INDEX));
            elasticsearchClient.indices().create(createIndexRequest);
            log.info("创建索引:{}", NoteConstant.NOTE_INDEX);
        } catch (Exception e) {
            log.error("Error occurred while deleting and recreating index: {}", NoteConstant.NOTE_INDEX, e);
        }
    }

    /**
     * 重置
     */
    @Override
    public void refreshNoteData() {
        this.delNoteBulkData();
        this.addNoteBulkData();
    }
}
