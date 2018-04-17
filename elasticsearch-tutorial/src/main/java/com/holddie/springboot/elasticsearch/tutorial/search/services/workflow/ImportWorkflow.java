package com.holddie.springboot.elasticsearch.tutorial.search.services.workflow;


import com.holddie.springboot.elasticsearch.tutorial.common.http.HttpService;
import com.holddie.springboot.elasticsearch.tutorial.common.jsontool.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class ImportWorkflow extends Workflow {

	@Value("${es.host1}")
	private String esHost;
	
	@Value("${db.url}")
	private String dbUrl;
	
	@Value("${db.user}")
	private String dbUser;
	
	@Value("${db.password}")
	private String dbPass;
	
	@Autowired
	private HttpService httpService;
	
	public void importFromDB(String sql, String index, String type, String river) throws Exception {
		String url = "http://" + esHost + ":9200/_river/"+river+"/_meta";
		Map<String, String> jdbcMap = new HashMap<String, String>();
		jdbcMap.put("url", dbUrl);
		jdbcMap.put("user", dbUser);
		jdbcMap.put("password", dbPass);
		jdbcMap.put("sql", sql);
		jdbcMap.put("index", index);
		jdbcMap.put("type", type);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", "jdbc");
		params.put("jdbc", jdbcMap);
		httpService.put(url, JsonMapper.toJsonString(params));
	}
}
