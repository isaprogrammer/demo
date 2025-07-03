package com.example.demo.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.security.User;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping(value = "/elasticsearch")
@RestController
public class ElasticsearchController {

    @RequestMapping(value = "/mock", method = RequestMethod.POST)
    @ResponseBody
    public void mock() {
        // URL and API key
        String serverUrl = "https://localhost:9200";
        String apiKey = "VnVhQ2ZHY0JDZGJrU...";
        // Create the low-level client
        RestClient restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        // And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        try {
            esClient.search(s -> s
                            .index("users")                                  // 目标索引
                            .from(0)                                         // 分页起始位置
                            .size(10)                                        // 每页数量
                            .trackTotalHits(th -> th.enabled(true))           // 精确统计总命中数
                            .query(q -> q                                    // 复合查询条件
                                    .bool(b -> b
                                            .must(m -> m                             // 必须满足条件
                                                    .term(t -> t
                                                            .field("name.keyword")            // 精确匹配
                                                            .value(v -> v.stringValue(" 张三"))
                                                    )
                                            )
                                            .should(sh -> sh                         // 可选条件（加分项）
                                                    .match(m -> m                        // 模糊匹配
                                                            .field("interests")
                                                            .query("篮球")
                                                            .boost(2.0f)                    // 权重加倍
                                                    )
                                            )
                                            .minimumShouldMatch("1")                // 至少满足1个should条件
                                    )
                            )
                            .sort(so -> so                                  // 排序规则
                                    .field(f -> f                                // 主要排序
                                            .field("create_time")
                                            .order(SortOrder.Desc)                  // 按创建时间倒序
                                    )
                            )
                            .aggregations("city_distribution", a -> a        // 聚合分析
                                    .terms(t -> t                                // 词项聚合
                                            .field("city.keyword")                   // 按城市分组
                                            .size(5)                                // 返回前5个分组
                                    )
                                    .aggregations("avg_age", a2 -> a2            // 子聚合
                                            .avg(av -> av.field("age"))              // 计算平均年龄
                                    )
                            )
                            .highlight(h -> h                               // 高亮显示
                                    .fields("name", fh -> fh                    // 高亮字段
                                            .preTags("<em>")                        // 前置标签
                                            .postTags("</em>")                      // 后置标签
                                    )
                            )
                            .source(sc -> sc                                // 结果字段过滤
                                    .filter(f -> f                              // 包含字段
                                            .includes("id", "name", "age", "city")  // 指定返回字段
                                    )
                            )
                    , User.class);
        } catch (IOException e) {
            // ignore
        }
    }

}
