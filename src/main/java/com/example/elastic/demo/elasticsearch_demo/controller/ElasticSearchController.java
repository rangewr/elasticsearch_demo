/*
 * @Author: wangran
 * @Date: 2020-04-02 11:03:20
 * @LastEditors: wangran
 * @LastEditTime: 2020-08-04 14:25:09
 */
package com.example.elastic.demo.elasticsearch_demo.controller;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import com.example.elastic.demo.elasticsearch_demo.entity.ElasticResult;
import com.example.elastic.demo.elasticsearch_demo.entity.LibraryQuery;
import com.example.elastic.demo.elasticsearch_demo.entity.ResultJSON;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/elastic")
public class ElasticSearchController {

    @Resource(name = "highLevelClient")
    private RestHighLevelClient client;

    public ElasticSearchController() {
    }

    /**
     * @Author: wangran
     * @Date: 2020-04-14 14:46:41
     * @msg: 模糊查询
     * @param {type}
     * @return:
     */
    @RequestMapping("/searchMatch")
    public ResultJSON searchMatch(LibraryQuery query) throws IOException {
        String index = "es-test-query-analyzer";
        String fieldName = "shopcode";
        String queryText = query.getQueryText();
        String preTag = "<font color='#dd4b39'>";// google的色值
        String postTag = "</font>";
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        int pageNum = (query.getCurrentPage() - 1) * query.getPageSize();
        searchSourceBuilder.query(matchQuery(fieldName, queryText))
                .highlighter(new HighlightBuilder().field(fieldName).preTags(preTag).postTags(postTag));
        searchSourceBuilder.from(pageNum);// 起始记录
        searchSourceBuilder.size(query.getPageSize());// 返回结果数
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        orderAfterAggregation(response);
        List<ElasticResult> queryList = new LinkedList<>();
        for (SearchHit searchHit : response.getHits()) {
            if (response.getHits().getHits().length <= 0) {
                return null;
            }
            ElasticResult esResult = new ElasticResult();
            HighlightField esField = searchHit.getHighlightFields().get(fieldName);
            if (esField != null) {
                esResult.setShopcode(esField.fragments()[0].toString());
            }
            esResult.setIndex(index);
            queryList.add(esResult);
        }
        Long count = this.count(index, fieldName, queryText);
        return ResultJSON.success(pageNum, query.getPageSize(), count > 10000 ? 10000 : count.intValue(), queryList);
    }

    /**
     * @Author: wangran
     * @Date: 2020-04-14 14:46:41
     * @msg: 精确匹配查询
     * @param {type}
     * @return:
     */
    @RequestMapping("/searchMatchKeyWord")
    public ResultJSON searchMatchKeyWord(LibraryQuery query) throws IOException {
        String index = "es-test-query-analyzer";
        String fieldKeyWord = "shopcode.keyword";
        String queryText = query.getQueryText();
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String preTag = "<font color='#dd4b39'>";// google的色值
        String postTag = "</font>";
        int pageNum = (query.getCurrentPage() - 1) * query.getPageSize();
        searchSourceBuilder.query(matchQuery(fieldKeyWord, queryText))
                .highlighter(new HighlightBuilder().field(fieldKeyWord).preTags(preTag).postTags(postTag)); // 精确查找
        searchSourceBuilder.from(pageNum);// 起始记录
        searchSourceBuilder.size(query.getPageSize());// 返回结果数
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        List<ElasticResult> queryList = new LinkedList<>();
        for (SearchHit searchHit : response.getHits()) {
            if (response.getHits().getHits().length <= 0) {
                return null;
            }
            ElasticResult esResult = new ElasticResult();
            HighlightField esField = searchHit.getHighlightFields().get(fieldKeyWord);
            if (esField != null) {
                esResult.setShopcode(esField.fragments()[0].toString());
            }
            esResult.setIndex(index);
            queryList.add(esResult);
        }
        Long count = this.count(index, fieldKeyWord, queryText);
        return ResultJSON.success(pageNum, query.getPageSize(), count > 10000 ? 10000 : count.intValue(), queryList);
    }

    /**
     * @Author: wangran
     * @Date: 2020-04-14 15:22:27
     * @msg: 获取查询结果的总记录数
     * @param {type}
     * @return:
     */
    public Long count(String index, String name, String text) throws IOException {
        CountRequest countRequest = new CountRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQuery(name, text));
        countRequest.source(searchSourceBuilder);
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        long count = countResponse.getCount();
        return count;
    }

    // @RequestMapping("/queryCount1")
    public void orderAfterAggregation(SearchResponse sr) {
        Terms agg = sr.getAggregations().get("agg");

        // For each entry
        for (Terms.Bucket entry : agg.getBuckets()) {
            String key = entry.getKey().toString(); // bucket key
            long docCount = entry.getDocCount(); // Doc count
            // logger.info("key [{}], doc_count [{}]", key, docCount);
            System.out.println("key [{}], doc_count [{}]"+ key+"------------"+ docCount);
            // We ask for top_hits for each bucket
            TopHits topHits = entry.getAggregations().get("top");
            for (SearchHit hit : topHits.getHits().getHits()) {
                System.out.println(" -> id [{}], _source [{}]" + hit.getId() + hit.getSourceAsString());
                // logger.info(" -> id [{}], _source [{}]", hit.getId(),
                // hit.getSourceAsString());
            }
        }
    }

}