package com.hongshu.web.service.impl;

import cn.hutool.core.util.RandomUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.hongshu.common.constant.NoteConstant;
import com.hongshu.web.domain.dto.EsRecordDTO;
import com.hongshu.web.domain.vo.RecordSearchVO;
import com.hongshu.web.service.IWebEsRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ES
 *
 * @Author hongshu
 */
@Service
@Slf4j
public class WebEsRecordServiceImpl implements IWebEsRecordService {

    @Autowired
    ElasticsearchClient elasticsearchClient;


    /**
     * 获取搜索记录
     */
    @Override
    public List<RecordSearchVO> getRecordByKeyWord(EsRecordDTO esRecordDTO) {
        String keyword = esRecordDTO.getKeyword();
        String uid = esRecordDTO.getUid();

        List<RecordSearchVO> records = new ArrayList<>();
        try {
            // 构建搜索请求
            SearchRequest.Builder builder = new SearchRequest.Builder().index(NoteConstant.RECORD_INDEX);

            // 添加查询条件，根据uid过滤
            if (StringUtils.isNotBlank(uid)) {
                builder.query(q -> q.bool(b -> {
                    b.must(m -> m.term(t -> t.field("uid").value(uid)));
                    if (StringUtils.isNotBlank(keyword)) {
                        b.must(m -> m.match(f -> f.field("content").query(keyword)));
                    }
                    return b;
                }));
            }

            // 设置排序规则和高亮显示
            builder.sort(o -> o.field(f -> f.field("time").order(SortOrder.Desc)));
            builder.highlight(h -> h.fields("content", m -> m).preTags("<font color='black'>").postTags("</font>"));
            builder.size(10);

            // 执行搜索请求
            SearchRequest searchRequest = builder.build();
            SearchResponse<RecordSearchVO> searchResponse = elasticsearchClient.search(searchRequest, RecordSearchVO.class);

            // 获取搜索结果
            List<Hit<RecordSearchVO>> hits = searchResponse.hits().hits();

            // 处理搜索结果
            for (Hit<RecordSearchVO> hit : hits) {
                RecordSearchVO recordSearchVo = hit.source();
                records.add(recordSearchVo);
            }

            return records;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 热门搜索
     */
    @Override
    public List<RecordSearchVO> getHotRecord() {
        List<RecordSearchVO> records = new ArrayList<>();
        try {
            BooleanResponse exists = elasticsearchClient.indices().exists(e -> e
                    .index(NoteConstant.RECORD_INDEX));
            if (!exists.value()) {
                return records;
            }
            SearchRequest.Builder builder = new SearchRequest.Builder().index(NoteConstant.RECORD_INDEX);
            builder.sort(o -> o.field(f -> f.field("searchCount").order(SortOrder.Desc)));
            builder.size(10);
            SearchRequest searchRequest = builder.build();
            SearchResponse<RecordSearchVO> searchResponse = elasticsearchClient.search(searchRequest, RecordSearchVO.class);
            //得到所有的数据
            List<Hit<RecordSearchVO>> hits = searchResponse.hits().hits();
            for (Hit<RecordSearchVO> hit : hits) {
                RecordSearchVO recordSearchVo = hit.source();
                records.add(recordSearchVo);
            }
            return records;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 增加搜索记录
     */
    @Override
    public void addRecord(EsRecordDTO esRecordDTO) {
        String keyword = esRecordDTO.getKeyword();
        String uid = esRecordDTO.getUid();
        try {
            // 查询索引是否存在
            BooleanResponse exists = elasticsearchClient.indices().exists(e -> e
                    .index(NoteConstant.RECORD_INDEX));
            if (!exists.value()) {
                elasticsearchClient.indices().create(c -> c.index(NoteConstant.RECORD_INDEX));
            }
            SearchRequest.Builder builder = new SearchRequest.Builder().index(NoteConstant.RECORD_INDEX);
            if (StringUtils.isNotBlank(keyword)) {
                builder.query(q -> q.match(f -> f.field("content").query(keyword.trim())));
            }
            builder.size(10);
            SearchRequest searchRequest = builder.build();
            SearchResponse<RecordSearchVO> searchResponse = elasticsearchClient.search(searchRequest, RecordSearchVO.class);
            //得到所有的数据
            List<Hit<RecordSearchVO>> hits = searchResponse.hits().hits();

            List<String> contents = new ArrayList<>();
            // 高亮查询
            for (Hit<RecordSearchVO> hit : hits) {
                RecordSearchVO recordSearchVo = hit.source();
                recordSearchVo.setSearchCount(recordSearchVo.getSearchCount() + 1);
                UpdateResponse<RecordSearchVO> response = elasticsearchClient.update(u -> u.index(NoteConstant.RECORD_INDEX).id(hit.id()).doc(recordSearchVo), RecordSearchVO.class);
                log.info("response", response.toString());
                contents.add(recordSearchVo.getContent());
            }
            if (StringUtils.isNotBlank(keyword) && !contents.contains(keyword.trim())) {
                RecordSearchVO recordSearchVo = new RecordSearchVO();
                recordSearchVo.setContent(keyword);
                recordSearchVo.setSearchCount(1L);
                String id = RandomUtil.randomString(12);
                elasticsearchClient.create(c -> c.index(NoteConstant.RECORD_INDEX).id(id).document(recordSearchVo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除搜索记录
     */
    @Override
    public void clearRecordByUser(EsRecordDTO esRecordDTO) {
        String keyword = esRecordDTO.getKeyword();
        String uid = esRecordDTO.getUid();

        try {
            // 检查索引是否存在
            BooleanResponse exists = elasticsearchClient.indices().exists(e -> e
                    .index(NoteConstant.RECORD_INDEX));
            if (!exists.value()) {
                log.warn("Index does not exist. No records to clear.");
                return;
            }

            // 构建删除请求
            DeleteByQueryRequest.Builder deleteRequestBuilder = new DeleteByQueryRequest.Builder()
                    .index(NoteConstant.RECORD_INDEX)
                    .query(q -> q.bool(b -> {
                        b.must(m -> m.term(t -> t.field("uid").value(uid)));
                        if (StringUtils.isNotBlank(keyword)) {
                            b.must(m -> m.term(t -> t.field("content.keyword").value(keyword.trim())));
                        }
                        return b;
                    }));

            // 执行删除操作
            DeleteByQueryResponse deleteResponse = elasticsearchClient.deleteByQuery(deleteRequestBuilder.build());

            log.info("Deleted {} records for uid: {}", deleteResponse.deleted(), uid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 清空搜索记录
     */
    @Override
    public void clearAllRecord() {
        try {
            // 检查索引是否存在
            BooleanResponse exists = elasticsearchClient.indices().exists(e -> e
                    .index(NoteConstant.RECORD_INDEX));
            if (exists.value()) {
                // 删除整个索引
                elasticsearchClient.indices().delete(d -> d.index(NoteConstant.RECORD_INDEX));
                // 重新创建索引
                elasticsearchClient.indices().create(c -> c.index(NoteConstant.RECORD_INDEX));
                log.info("All search records have been cleared.");
            } else {
                log.warn("Index does not exist. No records to clear.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
