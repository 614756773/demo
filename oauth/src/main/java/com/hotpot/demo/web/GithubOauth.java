package com.hotpot.demo.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotpot.demo.model.GithubToken;
import com.hotpot.demo.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

@RestController
public class GithubOauth {
    private static final Logger log = LoggerFactory.getLogger(GithubOauth.class);

    /** clientId和clientSecret需要登录你自己的github账号去设置
     * 设置地址：https://github.com/settings/developers
     */
    private String clientId = "xxxxx";
    private String clientSecret = "xxxxx";

    /**
     * 将用户的请求重定向到github的认证页面
     */
    @GetMapping
    public void redirectGithub(HttpServletResponse response) throws IOException {
        String githubUrl = "https://github.com/login/oauth/authorize";
        String callbackUrl = "http://www.qz-hotpot.xyz:433/oauth/callback";
        response.sendRedirect(String.format("%s?client_id=%s&redirect_uri=%s&", githubUrl, clientId, callbackUrl));
    }

    @GetMapping("/callback")
    public ResponseEntity<UserInfo> callback(HttpServletRequest request) throws IOException {
        log.info("收到回调请求");
        String code = request.getParameter("code");
        GithubToken githubToken = getToken(code);
        UserInfo userInfo = getUserInfo(githubToken);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 请求github获取访问令牌
     * @param code github'返回的授权码
     */
    private GithubToken getToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s",
                clientId, clientSecret, code);
        String result = restTemplate.postForObject(url, null, String.class);
        log.info("访问github获得的访问令牌：{}", result);
        return GithubToken.buildByString(result);
    }

    /**
     * 获取用户的github账号信息
     */
    private UserInfo getUserInfo(GithubToken githubToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("accept", "application/json");
        headers.add("Authorization", githubToken.getTokenType() + " " + githubToken.getAccessToken());
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> body = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, new HttpEntity<>("body", headers), String.class);
        log.info("获得的用户账号信息：{}", body.getBody());
        return buildUserInfo(body.getBody());
    }

    private UserInfo buildUserInfo(String str) throws IOException {
        UserInfo result = new UserInfo();
        HashMap<String, Object> map = new ObjectMapper().readValue(str, HashMap.class);
        result.setLogin((String) map.get("login"));
        result.setId(String.valueOf(map.get("id")));
        result.setAvatarUrl((String) map.get("avatar_url"));
        result.setBio((String) map.get("bio"));
        result.setLocation((String) map.get("location"));
        return result;
    }
}
