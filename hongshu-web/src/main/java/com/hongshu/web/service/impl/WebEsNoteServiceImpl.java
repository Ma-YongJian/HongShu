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
import com.hongshu.common.exception.web.HongshuException;
import com.hongshu.common.utils.DozerUtil;
import com.hongshu.web.domain.dto.EsNoteDTO;
import com.hongshu.web.domain.entity.WebCategory;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.NoteSearchVo;
import com.hongshu.web.mapper.WebCategoryMapper;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebEsNoteService;
import com.hongshu.web.service.sys.ISysNoteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ES
 *
 * @author: hongshu
 */
@Service
@Slf4j
public class WebEsNoteServiceImpl extends ServiceImpl<WebNoteMapper, WebNote> implements IWebEsNoteService {

    @Autowired
    private WebUserMapper userMapper;
    @Autowired
    private WebCategoryMapper categoryMapper;
    @Autowired
    private ISysNoteService noteService;
    @Autowired
    private ElasticsearchClient elasticsearchClient;


    /**
     * 搜索对应的笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param esNoteDTO   笔记
     */
    @Override
    public Page<NoteSearchVo> getNoteByDTO(long currentPage, long pageSize, EsNoteDTO esNoteDTO) {
        Page<NoteSearchVo> page = new Page<>();
        List<NoteSearchVo> noteSearchVoList = new ArrayList<>();
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
            SearchResponse<NoteSearchVo> searchResponse = elasticsearchClient.search(searchRequest, NoteSearchVo.class);
            TotalHits totalHits = searchResponse.hits().total();
            page.setTotal(Objects.requireNonNull(totalHits).value());
            List<Hit<NoteSearchVo>> hits = searchResponse.hits().hits();
            for (Hit<NoteSearchVo> hit : hits) {
                NoteSearchVo noteSearchVo = hit.source();
                noteSearchVoList.add(noteSearchVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            throw new HongshuException("es查找数据异常");
        }
        page.setRecords(noteSearchVoList);
        return page;
    }

    /**
     * 搜索对应的笔记
     *
     * @param esNoteDTO 笔记
     * @return
     */
    @Override
    public List<WebCategory> getCategoryAgg(EsNoteDTO esNoteDTO) {
        List<WebCategory> categoryList = categoryMapper.selectList(new QueryWrapper<WebCategory>().like("title", esNoteDTO.getKeyword()));
        return categoryList;
    }

    /**
     * 分页查询笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @Override
    public Page<NoteSearchVo> getRecommendNote(long currentPage, long pageSize) {
        Page<NoteSearchVo> page = new Page<>();
        List<NoteSearchVo> noteSearchVoList = new ArrayList<>();
        //得到当前用户的浏览记录
        try {
            SearchRequest.Builder builder = new SearchRequest.Builder().index(NoteConstant.NOTE_INDEX);
            builder.size(1000);
            SearchRequest searchRequest = builder.build();
            SearchResponse<NoteSearchVo> searchResponse = elasticsearchClient.search(searchRequest, NoteSearchVo.class);
            TotalHits totalHits = searchResponse.hits().total();
            //得到所有的数据
            List<Hit<NoteSearchVo>> hits = searchResponse.hits().hits();
            if (CollectionUtil.isNotEmpty(hits)) {
                for (Hit<NoteSearchVo> hit : hits) {
                    NoteSearchVo noteSearchVo = hit.source();
                    noteSearchVoList.add(noteSearchVo);
                }

                Collections.shuffle(noteSearchVoList);
                List<List<NoteSearchVo>> partition = Lists.partition(noteSearchVoList, (int) pageSize);
                List<NoteSearchVo> noteSearchVos = partition.get((int) currentPage - 1);
                page.setTotal(totalHits != null ? totalHits.value() : 0);
                page.setRecords(noteSearchVos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    /**
     * 增加笔记
     *
     * @param noteSearchVo 笔记
     */
    @Override
    public void addNote(NoteSearchVo noteSearchVo) {
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
    public void updateNote(NoteSearchVo noteSearchVo) {
        try {
            UpdateResponse<NoteSearchVo> updateResponse = elasticsearchClient.update(e -> e.index(NoteConstant.NOTE_INDEX).id(noteSearchVo.getId()).doc(noteSearchVo), NoteSearchVo.class);
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
        List<WebNote> noteList = noteService.getAllNoteList();
        List<NoteSearchVo> noteSearchVoList = DozerUtil.convertor(noteList, NoteSearchVo.class);
        for (NoteSearchVo noteSearchVo : noteSearchVoList) {
            WebUser user = userMapper.selectOne(new QueryWrapper<WebUser>().like("id", noteSearchVo.getUid()));
            noteSearchVo.setAvatar(user.getAvatar());
            noteSearchVo.setUsername(user.getUsername());
        }
        try {
            List<BulkOperation> result = new ArrayList<>();
            for (NoteSearchVo noteSearchVo : noteSearchVoList) {
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
