import com.sun.javafx.collections.MappingChange;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author: joe
 * @date: 2018/12/7 15:28
 * @description: 纵有疾风起，人生不言弃
 */
public class TestElasticSearch {
    /**
     * 创建索引
     */
    @Test
    public void test01() throws IOException {
        //创建客户端访问对象
        /**
         * Settings表示集群的设置
         * EMPTY：表示没有集群的配置
         * Settings.EMPTY
         */
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        //创建文档对象
        //方案一：组织Document数据
//        Map<String,Object> map=new HashMap<String,Object>();
//        map.put("id",1);
//        map.put("title","ElasticSearch是一个基于Lucene的搜索服务器");
//        map.put("content","它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。");
        //方案二：组织Document数据（使用ES的api构建json）
        //{id:1,title:"aaa",content:"xxxxx"}
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject()
                .field("id", 3)
                .field("title", "搜不到结果：为什么？")
                .field("content", "临近年底，热闹了一年的手机圈纷纷偃旗息鼓，准备为明年3月的新品发力。然而今天（12月7日），恰逢节气大雪，@荣耀手机 在微博发布了一张预热海报，随后荣耀总裁赵明转发这条微博表示「关于技术，真的有很多话想说」内涵满满，或将提前点燃手机行业新一轮的技术攻坚战 我闭上眼睛就是天黑，   典型的唯心主义啊").endObject();
        //创建索引、创建文档类型、设置唯一主键。同时创建文件
        client.prepareIndex("bolg", "article", "3").setSource(builder).get();
        //关闭资源
        client.close();
    }

    @Test
    public void test02() throws Exception {
        //创建客户端访问对象
        TransportClient client=new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(
                InetAddress.getByName("127.0.0.1"),9300));
        //设置查询条件（QueryBuilders.matchAllQuery()：查询所有）
        SearchResponse searchResponse=client.prepareSearch("bolg").setTypes("article")
                .setQuery(QueryBuilders.matchAllQuery()).get();
        //处理结果
        SearchHits hits = searchResponse.getHits();//获得命中目标，即查询到了多少个对象
        System.out.println("共查询"+hits.getTotalHits()+"条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            System.out.println(searchHit.getSourceAsString());
            System.out.println(searchHit.getSource().get("title"));
        }
        //关闭资源
        client.close();
    }

    //字符串查询
    @Test
    public void test03() throws Exception{
        //创建客户端访问对象
        TransportClient client=new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9300));
        //设置查询条件
        SearchResponse searchResponse=client.prepareSearch("bolg").setTypes("article")
                //字符串查询：QueryBuilders.queryStringQuery("基服务器" ).field("title")不加field表示所有字段搜索
                //模糊查询：QueryBuilders.wildcardQuery("title","* 搜*")
                .setQuery(QueryBuilders.queryStringQuery("搜wd索" ).field("title")).get();
        SearchHits hits = searchResponse.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            System.out.println(searchHit.getSourceAsString());
            System.out.println(searchHit.getSource().get("title"));
        }
        //关闭资源
        client.close();
    }
}
